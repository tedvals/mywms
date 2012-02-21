/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface ManagePickService {
	
	/**
	 * UserExit
	 * This method is called at the end of finishing the UnitLoads
	 * 
	 * @param LOSPickRequest
	 * @throws InventoryException
	 */
	public void finishCurrentUnitLoadsEnd(LOSPickRequest pick) throws InventoryException;
	
	
	/**
	 * User Exit
	 * This method is called before creating a PickReceipt.
	 * 
	 * @param LOSPickRequest
	 * @param LOSUnitLoad
	 */
	public boolean createPickReceipt(LOSPickRequest req, LOSUnitLoad ul);
	
	/**
	 * User Exit
	 * This method is called before creating a PickReceipt.
	 * 
	 * @param LOSPickRequest
	 * @param LOSUnitLoad
	 * @param LOSStorageLocation the new location
	 */
	public void putOnDestinationEnd(LOSPickRequest r, LOSUnitLoad u, LOSStorageLocation destination)  throws InventoryException;

	
	/**
	 * User Exit
	 * This method is called before creating a Label.
	 * 
	 * @param LOSOrderRequest
	 * @param LOSUnitLoad
	 */
	public boolean createLabels(LOSOrderRequest req, LOSUnitLoad ul);

	
	/**
	 * User Exit
	 * This method is called after finishing the picking order.
	 * 
	 * @param LOSOrderRequest
	 */
	public void finishEnd(LOSPickRequest req)  throws FacadeException;

}
