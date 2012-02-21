/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.model.UnitLoad;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;

@Local
public interface LOSGoodsOutRequestPositionService extends
		BasicService<LOSGoodsOutRequestPosition> {

	public LOSGoodsOutRequestPosition getByUnitLoad(UnitLoad ul) throws EntityNotFoundException;
	
}
