/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.AreaService;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;

/**
 * @see de.linogistix.los.location.entityservice.LOSRackService
 *  
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Stateless 
public class LOSAreaServiceBean 
	extends BasicServiceBean<LOSArea> 
	implements LOSAreaService 
{	
	
	@EJB
	AreaService delegate;
	
    public LOSArea createLOSArea(Client c, String name, LOSAreaType type) {
        LOSArea a = new LOSArea();
        a.setClient(c);
        a.setName(name);
        a.setAreaType(type);
                
        manager.persist(a);
        manager.flush();
        
        return a;
    }

	@SuppressWarnings("unchecked")
	public List<LOSArea> getByType(LOSAreaType type) {
		
		StringBuffer sb = new StringBuffer("SELECT a FROM ");
		sb.append(LOSArea.class.getSimpleName()+" a ");
		sb.append("WHERE a.areaType=:t");
		
		Query query = manager.createQuery(sb.toString());
		query.setParameter("t", type);
		
	
		return query.getResultList();

	}
	
	public LOSArea getByName(Client c, String name) throws EntityNotFoundException {
		return (LOSArea) delegate.getByName(c, name);
	}
	
    
    
}
