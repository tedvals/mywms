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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestPositionTO;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSPickRequestPositionQueryBean extends BusinessObjectQueryBean<LOSPickRequestPosition> implements LOSPickRequestPositionQueryRemote{

	@EJB
	LOSPickRequestPositionService posService;
	
	private static final String[] dtoProps = new String[] { 
		"id", 
		"version",
		"id", 
		"stockUnit.itemData.number",
		"stockUnit.itemData.name",
		"stockUnit.itemData.scale",
		"amount",
		"solved",
		"pickRequest.number"
		};

	
    @Override
    public String getUniqueNameProp() {
        return "id";
    }
    
    public List<LOSPickRequestPosition> queryByPickRequest(LOSPickRequest r){
    	return posService.getByPickRequest(r);
    }
    
    @Override
	public Class<PickingRequestPositionTO> getBODTOClass() {
    	return PickingRequestPositionTO.class;
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return dtoProps;
    }
    
    @Override
    public LOSPickRequestPosition queryById(Long ID)
    		throws BusinessObjectNotFoundException,
    		BusinessObjectSecurityException {
    	LOSPickRequestPosition o =  super.queryById(ID);
    	
    	// Read all elements to prevent lazy load error in the client!
    	for (LOSPickRequestPosition p : o.getComplements()){
    		p.toDescriptiveString();
    	}
    	
    	if (o.getComplementOn() != null){
	    	LOSPickRequestPosition complement = (LOSPickRequestPosition) BusinessObjectHelper.eagerRead(o.getComplementOn());
	    	for (LOSPickRequestPosition p : complement.getComplements()){
	    		p.toDescriptiveString();
	    	}
    	}
    	
    	return o;
    }
    
    public List<BODTO<LOSPickRequestPosition>> queryByNotSolved(QueryDetail qd) 
    	throws BusinessObjectNotFoundException, BusinessObjectQueryException
    {
    	qd.addOrderByToken("pickRequest.number", true);
    	TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_FALSE, "solved", false);
    	TemplateQuery q = new TemplateQuery();
    	q.setBoClass(LOSPickRequestPosition.class);
        q.addWhereToken(t);
    
    	return queryByTemplateHandles(qd, q);
 
    }
    

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {

		Long id = null;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = null;
		}
		
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "stockUnit.itemData.number",
				value);
		ret.add(item);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "stockUnit.itemData.name",
				value);
		ret.add(itemName);
		itemName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken pickRequest = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "pickRequest.number",
				value);
		ret.add(pickRequest);
		pickRequest.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		if (id != null){
			
			TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "id",
					id);
			idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(idt);
		}
			
		return ret;
	}
    
}
