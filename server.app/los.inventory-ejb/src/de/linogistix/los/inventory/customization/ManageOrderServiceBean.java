/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequest;


public class ManageOrderServiceBean implements ManageOrderService {
	
	public void processOrderPickedEnd(LOSOrderRequest order) throws InventoryException {
	}
	
	public void processOrderFinishedStart(LOSOrderRequest order) throws InventoryException {
	}
	public void processOrderFinishedEnd(LOSOrderRequest order) throws InventoryException {
	}
	
	public boolean createReceiptOnFinish(LOSOrderRequest req) {
		switch(req.getOrderType()){
		case INTERNAL:
		case TO_PRODUCTION:
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
		case TO_EXTINGUISH:
			return true;
		case TO_REPLENISH:
			return false;
		}
		return false;
	}
	
	public boolean printReceiptOnFinish(LOSOrderRequest req) {
		switch(req.getOrderType()){
		case INTERNAL:
		case TO_PRODUCTION:
			return false;
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			return true;
		case TO_EXTINGUISH:
			return false;
		case TO_REPLENISH:
			return false;
		}
		return false;
	}
	
	public boolean printLabelOnFinish(LOSOrderRequest req) {
		switch(req.getOrderType()){
		case INTERNAL:
		case TO_PRODUCTION:
			return false;
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			return true;
		case TO_EXTINGUISH:
			return false;
		case TO_REPLENISH:
			return false;
		}
		return false;
	}
	
	public boolean createReceiptOnPicked(LOSOrderRequest req) {
		return false;
	}
	
	public boolean printReceiptOnPicked(LOSOrderRequest req) {
		return false;
	}
	
	public boolean printLabelOnPicked(LOSOrderRequest req) {
		return false;
	}
	
}
