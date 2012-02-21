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

@Local
public interface ManageOrderService {
	
	/**
	 * UserExit
	 * This method is called at the end of processing the pick
	 * 
	 * @param order
	 * @throws InventoryException
	 */
	public void processOrderPickedEnd(LOSOrderRequest order) throws InventoryException;
	

	/**
	 * User Exit
	 * This method is called before finishing the OrderRequest
	 * 
	 * @param order
	 * @throws InventoryException
	 */
	public void processOrderFinishedStart(LOSOrderRequest order) throws InventoryException;
	
	/**
	 * User Exit
	 * This method is called after finishing the OrderRequest
	 * 
	 * @param order
	 * @throws InventoryException
	 */
	public void processOrderFinishedEnd(LOSOrderRequest order) throws FacadeException;

	
	/**
	 * User Exit
	 * If TRUE the receipt is created on finishing the order
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean createReceiptOnFinish(LOSOrderRequest req);
	
	/**
	 * User Exit
	 * If TRUE the receipt is printed on finishing the order
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean printReceiptOnFinish(LOSOrderRequest req);
	
	/**
	 * User Exit
	 * If TRUE labels are printed on finishing the order
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean printLabelOnFinish(LOSOrderRequest req);
	
	/**
	 * User Exit
	 * If TRUE the receipt is created after picking the last position
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean createReceiptOnPicked(LOSOrderRequest req);

	/**
	 * User Exit
	 * If TRUE the receipt is printed after picking the last position
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean printReceiptOnPicked(LOSOrderRequest req);
	
	/**
	 * User Exit
	 * If TRUE labels are printed after picking the last position
	 * 
	 * @param LOSOrderRequest
	 */
	public boolean printLabelOnPicked(LOSOrderRequest req);

}
