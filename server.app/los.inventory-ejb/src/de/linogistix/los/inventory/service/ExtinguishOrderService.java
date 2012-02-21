/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Lot;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.ExtinguishOrder;
@Remote
public interface ExtinguishOrderService extends BasicService<ExtinguishOrder> {
	
	public ExtinguishOrder create(Lot lot, Date startDate);
	
	public ExtinguishOrder getByLot(Lot lot) throws EntityNotFoundException;
	
	public List<ExtinguishOrder> getRipe();
}
