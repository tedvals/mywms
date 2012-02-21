/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.StockUnit;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.UserService;

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;

@Stateless
public class LOSGoodsReceiptPositionServiceBean
        extends BasicServiceBean<LOSGoodsReceiptPosition>
        implements LOSGoodsReceiptPositionService {

    @Resource
    SessionContext ctx;
    @EJB
    UserService userService;
    @Resource
    EJBContext context;
    
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceiptPosition> getByStockUnit(StockUnit su) {
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSGoodsReceiptPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.stockUnit=:su ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("su", su);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceiptPosition> getByStockUnit(String stockUnitStr){
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSGoodsReceiptPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.stockUnitStr=:stockUnitStr ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("stockUnitStr", stockUnitStr);
		return q.getResultList();
	}
}
