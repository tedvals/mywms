/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.Local;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;


@Local
public interface ManageReceiptService {

	/**
	 * UserExit
	 * This method is called at the beginning of the GoodsReceipt posting
	 * 
	 * @param gr
	 * @throws InventoryException
	 */
	public void finishGoodsReceiptStart(LOSGoodsReceipt gr) throws InventoryException, InventoryTransferException;
	
	/**
	 * UserExit
	 * This method is called at the end of the GoodsReceipt posting
	 * 
	 * @param gr
	 * @throws InventoryException
	 */
	public void finishGoodsReceiptEnd(LOSGoodsReceipt gr) throws InventoryException;
	
}
