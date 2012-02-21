/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.dto.LOSOrderRequestTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class OrderRequestQueryBean extends
		BusinessObjectQueryBean<LOSOrderRequest> implements
		OrderRequestQueryRemote {

	private final static Logger log = Logger.getLogger(OrderRequestQueryBean.class);
	public final static String[] dtoProps = new String[] { "id", "version",
			"number", "client.number", "parentRequestNumber", "delivery", "orderState", "destination.name"};

	@Override
	public String getUniqueNameProp() {
		return "number";
	}

	@Override
	public Class<LOSOrderRequestTO> getBODTOClass() {
		return LOSOrderRequestTO.class;
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@SuppressWarnings("unchecked")
	public List<BODTO<LOSOrderRequest>> queryZombies(QueryDetail d)
			throws BusinessObjectQueryException {
		
		Client c = contextService.getCallersUser().getClient();
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.HOUR, -6);
		
		Date ago = cal.getTime();
		
		List<BODTO<LOSOrderRequest>> orders;
		StringBuffer b = new StringBuffer();
		b.append("SELECT " );
		b.append(TemplateQuery.getConstructorPropsStatement(getBODTOClass(), getBODTOConstructorProps()));
		b.append(" FROM ");
		b.append(LOSOrderRequest.class.getName());
		b.append(" o  ");
		b.append(" WHERE o.orderState NOT IN (:failed, :finished) " );
		b.append( " AND o.modified < :ago ");
		if (c.isSystemClient()){
			b.append( " AND o.client < :client ");
		}
		b.append( " AND o.modified < :ago ");
		
		Query q = manager.createQuery(new String(b));
		q.setParameter("failed", LOSOrderRequestState.FAILED);
		q.setParameter("finished", LOSOrderRequestState.FINISHED);
		q.setParameter("ago", ago);
		if (c.isSystemClient()){
			q.setParameter("client", c);
		}
		q.setMaxResults(d.getMaxResults());
		q.setFirstResult(d.getStartResultIndex());
		orders = q.getResultList();
		
		return orders;
		
		
	}

	public LOSOrderRequest queryByRequestId(Client c, String requestId) {
		List<LOSOrderRequest> ret;

		long start = System.currentTimeMillis();

		try {

			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE,
					"requestId", true);

			TemplateQueryWhereToken identityToken = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL,
					"requestId", requestId);
			
			TemplateQueryWhereToken clientToken;
			
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(identityToken);

			q.setBoClass(getBoClass());

			clientToken = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client", c);
			q.addWhereToken(clientToken);
			
			ret = queryByTemplate(d, q);
			
			if (ret == null || ret.size() != 1) {
				return null;
			} else {
				eagerRead(ret.get(0));
				return ret.get(0); // first element
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return null;
		} finally {
			long stop = System.currentTimeMillis();
			log.info("query took " + (stop - start));
		}
	}

	public LOSResultList<BODTO<LOSOrderRequest>> autoCompletionByState(String typed,
																	   BODTO<Client> clientTO,
			   														   LOSOrderRequestState orderState, 
			   														   QueryDetail detail)
	{	
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		Client client = null;
		
		if(clientTO != null){
			client = manager.find(Client.class, clientTO.getId());
		}
		
		if(orderState != null){
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderState", orderState));
		}
		
		return autoCompletion(typed, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken parent = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parentRequestNumber",
				value);
		parent.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken destination = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "destination.name",
				value);
		destination.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(number);
		ret.add(parent);
		ret.add(client);
		ret.add(destination);

		
		return ret;
	}

}
