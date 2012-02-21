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

import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSGoodsReceiptQueryBean extends BusinessObjectQueryBean<LOSGoodsReceipt> implements LOSGoodsReceiptQueryRemote{

	public final static String props[] = new String[]{
		"id", "version", "goodsReceiptNumber",
		"deliveryNoteNumber", "receiptDate", "receiptState", "client.number"
	};
	
    @Override
    public String getUniqueNameProp() {
        return "goodsReceiptNumber";
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	public Class<LOSGoodsReceiptTO> getBODTOClass() {
    	return LOSGoodsReceiptTO.class; 
    }
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken goodsReceiptNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "goodsReceiptNumber",
				value);
		goodsReceiptNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken deliveryNoteNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "deliveryNoteNumber",
				value);
		deliveryNoteNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		ret.add(goodsReceiptNumber);
		ret.add(deliveryNoteNumber);
		ret.add(client);
		
		
		return ret;
	}

    
}
