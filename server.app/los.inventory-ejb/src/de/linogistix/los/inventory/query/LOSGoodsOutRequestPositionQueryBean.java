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

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;


@Stateless
public class LOSGoodsOutRequestPositionQueryBean extends BusinessObjectQueryBean<LOSGoodsOutRequestPosition> implements
		LOSGoodsOutRequestPositionQueryRemote {
	
	private static final String[] dtoProps = new String[] { "id", "version", "id",  
		"outState",
		"source.labelId",
		"goodsOutRequest.number"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<LOSGoodsOutPositionTO> getBODTOClass() {
		return LOSGoodsOutPositionTO.class;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = new Long(-1);
		}
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken unitLoad = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "source.labelId",
				value);
		unitLoad.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "goodsOutRequest.number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(name);
		ret.add(unitLoad);
		ret.add(number);
		
		return ret;
	}

}
