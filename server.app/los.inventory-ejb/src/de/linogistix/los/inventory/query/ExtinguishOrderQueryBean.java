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

import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.query.dto.ExtinguishOrderTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class ExtinguishOrderQueryBean extends BusinessObjectQueryBean<ExtinguishOrder> implements ExtinguishOrderQueryRemote{

  @Override
  public String getUniqueNameProp() {
    return "number";
  }
	
	private static final String[] dtoProps = new String[] { "id", "version", "number",  
		"lot.name",
		"lot.itemData.number",
		"client.number",
		"destination.name",
		"orderState", "authorizedBy.name", "delivery" };

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<ExtinguishOrderTO> getBODTOClass() {
		return ExtinguishOrderTO.class;
	}

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.name",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.itemData.name",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		ret.add(number);
		ret.add(lot);
		ret.add(itemData);
		ret.add(client);
		
		
		return ret;
	}

}
