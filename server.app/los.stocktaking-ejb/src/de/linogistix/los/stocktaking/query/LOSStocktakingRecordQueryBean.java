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

package de.linogistix.los.stocktaking.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.query.dto.StockTakingRecordTO;

/**
 *
 * @author krane
 */
@Stateless
public class LOSStocktakingRecordQueryBean extends BusinessObjectQueryBean<LOSStocktakingRecord> implements LOSStocktakingRecordQueryRemote{
    
	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"id",
		"clientNo",
		"unitLoadLabel",
		"itemNo",
		"lotNo",
		"plannedQuantity",
		"countedQuantity",
		"state"
	};
	
	@Override
    public String getUniqueNameProp() {
        return "id";
    }
    
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<StockTakingRecordTO> getBODTOClass() {
		return StockTakingRecordTO.class;
	}
	
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = Long.valueOf(-1);
		}
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken oid = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "stocktakingOrder.id", id);
		oid.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken clientNo = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "clientNo",
				value);
		clientNo.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken label = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoadLabel",
				value);
		label.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemNo = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemNo",
				value);
		itemNo.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken lotNo = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lotNo",
				value);
		lotNo.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(name);
		ret.add(oid);
		ret.add(clientNo);
		ret.add(label);
		ret.add(itemNo);
		ret.add(lotNo);
		
		return ret;
	}

}
