/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.UnitLoad;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequest;


@Stateless
public class LOSGoodsOutRequestServiceBean extends
		BasicServiceBean<LOSGoodsOutRequest> implements
		LOSGoodsOutRequestService {

	@EJB 
	LOSGoodsOutRequestPositionService posService;
	
	public LOSGoodsOutRequest getByUnitLoad(UnitLoad ul) throws EntityNotFoundException {

		LOSGoodsOutRequest out;
		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT out FROM ");
		b.append(LOSGoodsOutRequest.class.getName());
		b.append(" out, ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		
		b.append(" WHERE pos.source=:ul ");
		b.append(" AND pos.goodsOutRequest=out ");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("ul", ul);
		try{
			out = (LOSGoodsOutRequest) q.getSingleResult();
			return out;
		} catch (Throwable t){
			throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_ID);
		}

	}
	
	
	@Override
	public void delete(LOSGoodsOutRequest entity)
			throws ConstraintViolatedException {
		
		entity.setPositions(new ArrayList<LOSGoodsOutRequestPosition>());
		super.delete(entity);
		
		for (LOSGoodsOutRequestPosition pos : entity.getPositions()){
			posService.delete(pos);
		}
		
	}

	public LOSGoodsOutRequest getByOrder(LOSOrderRequest order) throws EntityNotFoundException {
		LOSGoodsOutRequest out;
		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT out FROM ");
		b.append(LOSGoodsOutRequest.class.getName());
		b.append(" out ");
		
		b.append(" WHERE out.parentRequest.id = :parentid");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("parentid", order.getId());
		try{
			out = (LOSGoodsOutRequest) q.getSingleResult();
			return out;
		} catch (Throwable t){
			throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_ID);
		}
	}
	

}
