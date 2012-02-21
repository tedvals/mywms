/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;

@Local
public interface ExtinguishBusiness {

	public ExtinguishRequest create(ExtinguishOrder order) throws InventoryException;

	public List<StockUnit> getAffectedStockUnit(Lot lot) throws InventoryException;

	public ExtinguishRequest process(ExtinguishRequest req) throws InventoryException;
	
	public ExtinguishOrder startExtinguishOrder(ExtinguishOrder order) throws InventoryException;

	public ExtinguishOrder createExtinguishOrder(Lot lot, Date startDate) throws InventoryException;
	
	public void createCronJob();

	public List<Lot> getTooOld(Client c);

	public List<Lot> getNotToUse(Client c);

	public List<Lot> getToUseFromNow(Client c);
	
	public void processLots();
}
