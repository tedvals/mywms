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

import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.query.dto.LOSOrderRequestTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class ReplenishRequestQueryBean extends BusinessObjectQueryBean<LOSReplenishRequest> implements ReplenishRequestQueryRemote{

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


	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken destination = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "destination.name",
				value);
		destination.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(number);
		ret.add(client);
		ret.add(destination);
	
		
		return ret;
	}
}
