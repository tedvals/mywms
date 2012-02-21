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

import javax.ejb.Local;

import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;
import org.mywms.model.SubstitutionType;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;

@Local
public interface LOSPickRequestPositionService extends BasicService<LOSPickRequestPosition>{
	
	List<LOSPickRequestPosition> getByPickRequest(LOSPickRequest r);

	LOSPickRequestPosition create(LOSPickRequest req, LOSOrderRequestPosition orderPos, StockUnit su,
			BigDecimal amount, SubstitutionType substitutionType, PickingWithdrawalType w);

	List<LOSPickRequestPosition> getByOrderPosition(LOSOrderRequestPosition p);
	
	List<LOSPickRequestPosition> getByStockUnit(StockUnit su);
	
}
