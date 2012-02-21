/*
 * LOSLocationCluster
 *
 * Created on 2009
 *
 * Copyright (c) 2009 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.model.LOSLocationCluster;

/**
 * @see de.linogistix.los.location.entityservice.LOSLocationClusterService
 *  
 * @author krane
 */
@Stateless 
public class LOSLocationClusterServiceBean 
	extends BasicServiceBean<LOSLocationCluster> 
	implements LOSLocationClusterService 
{	

	public LOSLocationCluster createLocationCluster(String name) {
		LOSLocationCluster cluster = new LOSLocationCluster();
        cluster.setName(name);
        
        manager.persist(cluster);
        manager.flush();
        
        return cluster;
	}

   
	public LOSLocationCluster getByName(String name) throws EntityNotFoundException {
		String sql = "SELECT cl FROM " + LOSLocationCluster.class.getSimpleName() + " cl " +
        	"WHERE cl.name=:name";
		Query query = manager.createQuery(sql);

		query.setParameter("name", name);
		
		try {
			LOSLocationCluster cluster = (LOSLocationCluster) query.getSingleResult();
		    return cluster;
		}
		catch (NoResultException ex) {
		    throw new EntityNotFoundException(
		            ServiceExceptionKey.NO_ENTITY_WITH_NAME);
		}
	}
    
    
}
