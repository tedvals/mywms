/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.UnitLoad;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;


@Stateless
public class LOSGoodsOutRequestPositionServiceBean extends
		BasicServiceBean<LOSGoodsOutRequestPosition> implements
		LOSGoodsOutRequestPositionService{

	public LOSGoodsOutRequestPosition getByUnitLoad(UnitLoad ul) throws EntityNotFoundException {

		LOSGoodsOutRequestPosition out;
		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		
		b.append(" WHERE pos.source=:ul ");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("ul", ul);
		try{
			out = (LOSGoodsOutRequestPosition) q.getSingleResult();
			return out;
		} catch (Throwable t){
			throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_ID);
		}

	}

}
