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
import java.util.List;

import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSOrderRequestPositionTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;


/** 
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class OrderRequestPositionQueryBean extends BusinessObjectQueryBean<LOSOrderRequestPosition> implements OrderRequestPositionQueryRemote{
	
	public final static String[] dtoProps = new String[]{
		"id", "version", "number","itemData.number", "itemData.name", "lot.name", "amount", "itemData.scale",
		"positionState","parentRequest.number"
	};
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public String getUniqueNameProp() {
		return "number";
	}
	
	@Override
	public Class<LOSOrderRequestPositionTO> getBODTOClass() {
		return LOSOrderRequestPositionTO.class;
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("number", false));
		propList.add(new BODTOConstructorProperty("itemData.number", false));
		propList.add(new BODTOConstructorProperty("itemData.name", false));
		propList.add(new BODTOConstructorProperty("lot", true));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("itemData.scale", false));
		propList.add(new BODTOConstructorProperty("positionState", false));
		propList.add(new BODTOConstructorProperty("parentRequest.number", false));
		
		return propList;
		
	}

	public LOSResultList<BODTO<LOSOrderRequestPosition>> autoCompletionByOrderRequest(
																	String typed, 
																	BODTO<LOSOrderRequest> orderTO,
																	QueryDetail detail)
	{	
		String typedToUpper = typed.toUpperCase();
		
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		Client client = null;
		
		if(orderTO != null){
			LOSOrderRequest order = manager.find(LOSOrderRequest.class, orderTO.getId());
			client = order.getClient();
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "parentRequest", order));
		}
		
		return autoCompletion(typedToUpper, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(number);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.name",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemData);
		
		TemplateQueryWhereToken itemDataName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemDataName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemDataName);

		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		return ret;
	}

}
