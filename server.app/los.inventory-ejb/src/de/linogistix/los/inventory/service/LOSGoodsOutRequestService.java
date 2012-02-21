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

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSOrderRequest;

@Local
public interface LOSGoodsOutRequestService extends
		BasicService<LOSGoodsOutRequest> {

	public LOSGoodsOutRequest getByUnitLoad(UnitLoad ul) throws EntityNotFoundException;
	
	public LOSGoodsOutRequest getByOrder(LOSOrderRequest order) throws EntityNotFoundException;
}
