/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.ItemData;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSInventory;
import org.mywms.model.Lot;

@Local
public interface InventoryService extends BasicService<LOSInventory>{
	
	LOSInventory create(ItemData itemData);
	
	LOSInventory create(Lot Batch);
	
	public List<LOSInventory> getByItemData(ItemData data) throws EntityNotFoundException;
	
	public LOSInventory getByBatch(Lot Batch) throws EntityNotFoundException;
	
	public LOSInventory getByBatchName(String BatchName) throws EntityNotFoundException;
}
