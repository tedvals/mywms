/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;
import org.mywms.model.SubstitutionType;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;

@Stateless
public class LOSPickRequestPositionServiceBean extends BasicServiceBean<LOSPickRequestPosition> implements LOSPickRequestPositionService{
	Logger log = Logger.getLogger(LOSPickRequestPositionServiceBean.class);

	
	@SuppressWarnings("unchecked")
	public List<LOSPickRequestPosition> getByPickRequest(LOSPickRequest r) {
		
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSPickRequestPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.pickRequest=:r ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("r", r);
		return q.getResultList();
		
	}

	public LOSPickRequestPosition create(LOSPickRequest req, LOSOrderRequestPosition orderPos, StockUnit su,
			BigDecimal amount, SubstitutionType substitutionType, PickingWithdrawalType withdrawalType) {
		log.debug("create (Pos=" + orderPos.getNumber() + ", SU=" + su.toString() + ", Amount=" + amount + ", PWT="+withdrawalType);

		LOSPickRequestPosition p = new LOSPickRequestPosition();
		p.setParentRequest(orderPos);
		p.setStockUnit(su);
		p.setAmount(amount);
		p.setPickRequest(req);
		p.setSubstitutionType(substitutionType);
		p.setWithdrawalType(withdrawalType);
		manager.persist(p);
		manager.flush();
		
		return p;
		
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickRequestPosition> getByOrderPosition(LOSOrderRequestPosition p) {
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSPickRequestPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.parentRequest.id=:parentid ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("parentid", p.getId());
		
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickRequestPosition> getByStockUnit(StockUnit su) {
		StringBuffer b = new StringBuffer();
		Query q;
		 
		b.append(" SELECT pos FROM ");
		b.append(LOSPickRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.stockUnit=:su");
		
		q = manager.createQuery(new String(b))
			.setParameter("su", su);
		
		return q.getResultList();
		
	}

}
