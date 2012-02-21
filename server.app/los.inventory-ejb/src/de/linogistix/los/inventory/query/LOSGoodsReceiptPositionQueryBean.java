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

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptPositionTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSGoodsReceiptPositionQueryBean extends BusinessObjectQueryBean<LOSGoodsReceiptPosition> implements LOSGoodsReceiptPositionQueryRemote{

	public final static String props[] = new String[]{
		"id", "version", "positionNumber",
		"orderReference", "amount", "itemData", "lot", "scale"
	};
	
    @Override
    public String getUniqueNameProp() {
        return "positionNumber";
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	public Class<LOSGoodsReceiptPositionTO> getBODTOClass() {
    	return LOSGoodsReceiptPositionTO.class; 
    }
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken positionNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "positionNumber",
				value);
		positionNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderReference = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderReference",
				value);
		orderReference.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(positionNumber);
		ret.add(orderReference);
		ret.add(itemData);
		ret.add(lot);
		
		
		return ret;
	}
    
}
