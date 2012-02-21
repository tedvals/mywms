/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class LOSGoodsOutRequestQueryBean extends BusinessObjectQueryBean<LOSGoodsOutRequest> implements
		LOSGoodsOutRequestQueryRemote {

	public final static String[] dtoProps = new String[] { "id", "version",
		"number", "number","parentRequest.number", "outState", "client.number"};

	
	@Override
	public String getUniqueNameProp() {
		return "number";
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<LOSGoodsOutRequestTO> getBODTOClass() {
		return LOSGoodsOutRequestTO.class;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken parent = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parentRequest.number",
				value);
		parent.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		ret.add(parent);
		ret.add(number);
		ret.add(client);
		
		
		return ret;
	}

}
