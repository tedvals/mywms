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

package de.linogistix.los.inventory.pick.query;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.globals.PickingRequestState;
import org.mywms.model.Client;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSPickRequestQueryBean extends
		BusinessObjectQueryBean<LOSPickRequest> implements
		LOSPickRequestQueryRemote {


	public static final String[] dtoProps = new String[] { "id", "version",
			"number", "created", "client.number", "state", "parentRequestNumber", 
			"parentRequest.parentRequestNumber"
			};

	@Override
	public String getUniqueNameProp() {
		return "number";
	}

	@Override
	public Class<PickingRequestTO> getBODTOClass() {
		return PickingRequestTO.class;
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	//-------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<LOSPickRequest> queryByParentRequest(String parentRequestNumber){
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT r FROM ");
		buffer.append(LOSPickRequest.class.getName());
		buffer.append(" r ");
		buffer.append(" WHERE ");
		buffer.append(" r.parentRequest.number=:number");
		Query q = manager.createQuery(new String(buffer));
		q = q.setParameter("number", parentRequestNumber);
		
		return q.getResultList();

	}
	
	//------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public List<BODTO<LOSPickRequest>> queryZombies(QueryDetail d)
			throws BusinessObjectQueryException {
		
		Client c = contextService.getCallersUser().getClient();
		
		GregorianCalendar ago = new GregorianCalendar();
		ago.add(GregorianCalendar.MINUTE, -45);
		
		List<BODTO<LOSPickRequest>> orders;
		StringBuffer b = new StringBuffer();
		b.append("SELECT " );
		b.append(TemplateQuery.getConstructorPropsStatement(getBODTOClass(), getBODTOConstructorProps()));
		b.append(" FROM ");
		b.append(LOSPickRequest.class.getName());
		b.append(" o  ");
		b.append(" WHERE o.state NOT IN (:failed, :finished, :partial) " );
		b.append( "AND o.modified < :ago");
		if (c.isSystemClient()){
			b.append( "AND o.client < :client");
		}
		b.append( "AND o.modified < :ago");
		
		Query q = manager.createQuery(new String(b));
		q.setParameter("failed", PickingRequestState.FAILED);
		q.setParameter("finished", PickingRequestState.FINISHED);
		q.setParameter("partial", PickingRequestState.FINISHED_PARTIAL);
		q.setParameter("ago", ago);
		if (c.isSystemClient()){
			q.setParameter("client", c);
		}
		
		q.setMaxResults(d.getMaxResults());
		q.setFirstResult(d.getStartResultIndex());
		
		orders = q.getResultList();
		
		return orders;
		
		
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickRequest> queryByOrderReference(String parentRequestNumber) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT r FROM ");
		buffer.append(LOSPickRequest.class.getName());
		buffer.append(" r ");
		buffer.append(" WHERE ");
		buffer.append(" r.parentRequest.parentRequestNumber=:number");
		Query q = manager.createQuery(new String(buffer));
		q = q.setParameter("number", parentRequestNumber);
		
		return q.getResultList();
	}
	
	public LOSPickRequest queryByOrderReference(Client c, String string){
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT r FROM ");
		buffer.append(LOSPickRequest.class.getName());
		buffer.append(" r ");
		buffer.append(" WHERE ");
		buffer.append(" r.parentRequest.parentRequestNumber=:number");
		buffer.append(" AND r.client=:client");
		Query q = manager.createQuery(new String(buffer));
		
		q = q.setParameter("number", string);
		q = q.setParameter("client", c);
		try{
			return (LOSPickRequest) q.getSingleResult();
		} catch (javax.persistence.NoResultException ex){
			return null;
		}
	}
	
	//---------------------------------------------------------------
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret  = new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number", value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken parentRequestNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parentRequestNumber", value);
		parentRequestNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken parentRequestParentRequestNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parentRequest.parentRequestNumber", value);
		parentRequestParentRequestNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(number);
		ret.add(parentRequestNumber);
		ret.add(parentRequestParentRequestNumber);
		ret.add(client);
		
		return ret;
		
	}

}
