/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.Local;

//import org.mywms.facade.Inventory;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSAdvice;

/**
 * A component for managing {@link LOSInventory} entities and 
 * retrieving inventory information.
 *  
 * @author trautm
 */
@Local
public interface InventoryBusiness {
	
	/**
	 * Creates an empty {@link Inventory} entity when a new {@link ItemData} has been created.
	 * @param itemData
	 * @throws InventoryException 
	 */
	public abstract void itemDataCreated(ItemData itemData) throws InventoryException;

	/**
	 * Deletes {@link Inventory} entity when the corresponding {@link ItemData} has been removed 
	 * @param itemData
	 * @throws InventoryException 
	 */
	public abstract void itemDataRemoved(ItemData itemData) throws InventoryException;
	
	
	/**
	 * Updates {@link Inventory} entities by evaluating all {@link StockUnit} and {@link LOSAdvice}.
	 * 
	 * Very time consuming!
	 * 
	 * @param c
	 * @throws InventoryException 
	 */
	public abstract void updateInventory(Client c) throws InventoryException;
	
	
	/**
	 * Returns an array of {@link InventoryTO}. 
	 * One entry per lot i.e. {@link Lot} if consolidateLot is false. One entry per article otherwise.
	 * 
	 * @param c
	 * @return Array of {@link InventoryTO}
	 * @throws InventoryException 
	 */
	public InventoryTO[] getInventory(Client c, boolean consolidateLot) throws InventoryException;
	
	/**
	 * Returns an array of {@link InventoryTO}, one entry per lot i.e. {@link Lot} of given {@link ItemData}.
	 * 
	 * @param c
	 * @return Array of {@link InventoryTO}
	 * @throws InventoryException 
	 */
	public InventoryTO[] getInventory(Client c, ItemData idat, boolean consolidateLot) throws InventoryException;
	
	

	
}