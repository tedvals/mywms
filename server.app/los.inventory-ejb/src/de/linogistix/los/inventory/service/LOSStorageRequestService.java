/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import javax.ejb.Local;
import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;


@Local
public interface LOSStorageRequestService
	extends BasicService<LOSStorageRequest>
{
	/**
     * 
     * @param label labelId of StockUnit or UnitLoad
     * @return
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    LOSStorageRequest getRawOrCreateByLabel(Client c, String label) throws InventoryException, EntityNotFoundException;
    
    List<LOSStorageRequest> getListByLabelId(String label);
}
