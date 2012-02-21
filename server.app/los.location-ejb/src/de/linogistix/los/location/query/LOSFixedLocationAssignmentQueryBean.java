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
package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.query.dto.LOSFixedLocationAssignmentTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSFixedLocationAssignmentQueryBean extends BusinessObjectQueryBean<LOSFixedLocationAssignment> implements LOSFixedLocationAssignmentQueryRemote {

	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"id",
		"assignedLocation.name",
		"itemData.number",
		"itemData.name",
		"itemData.scale",
		"desiredAmount"
	};
    public String getUniqueNameProp() {
        return "id";
    }
    
    @Override
	public Class<LOSFixedLocationAssignmentTO> getBODTOClass() {
    	return LOSFixedLocationAssignmentTO.class;
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	// TODO Auto-generated method stub
    	return dtoProps;
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
		
		TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, getUniqueNameProp(), id);
		idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number", value);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name", value);
		itemName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken loc = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "assignedLocation.name", value);		
		loc.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(idt);
		ret.add(item);
		ret.add(itemName);
		ret.add(loc);
		
		return ret;
    }
    
}
