/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;


public class ManagePickServiceBean implements ManagePickService {

	public boolean createPickReceipt(LOSPickRequest req, LOSUnitLoad ul) {
		return true;
	}

	public void finishCurrentUnitLoadsEnd(LOSPickRequest pick)
			throws InventoryException {
	}

	public void putOnDestinationEnd(LOSPickRequest r, LOSUnitLoad u,
			LOSStorageLocation destination) throws InventoryException {
	}
	
	public boolean createLabels(LOSOrderRequest req, LOSUnitLoad ul) {
		return false;
	}

	public void finishEnd(LOSPickRequest req) throws FacadeException {
	}

}
