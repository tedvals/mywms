/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

@Stateless
public class OrderRequestPositionServiceBean extends
		BasicServiceBean<LOSOrderRequestPosition> implements OrderRequestPositionService {
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	
	public LOSOrderRequestPosition getByIndex( LOSOrderRequest order, int positionIndex ) {
		
		Query query = manager.createQuery(
				"SELECT o FROM " + LOSOrderRequestPosition.class.getSimpleName() + " o " +
				" WHERE o.parentRequest=:order and positionIndex=:index");
		query.setParameter("order", order);
		query.setParameter("index", positionIndex);
		
		LOSOrderRequestPosition pos = null;
		try {
			pos = (LOSOrderRequestPosition)query.getSingleResult();
		}
		catch( NoResultException e ) {
			return null;
		}
		
		return pos;
	}
}
