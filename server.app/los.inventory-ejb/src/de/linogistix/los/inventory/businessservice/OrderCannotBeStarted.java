/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
@ApplicationException(rollback=false)
public class OrderCannotBeStarted extends FacadeException {

	public OrderCannotBeStarted(String orderNo) {
		super("OrderCannotBeStarted: " + orderNo, InventoryExceptionKey.ORDER_CANNOT_BE_STARTED.name(), new String[]{orderNo});
		setBundleResolver(InventoryBundleResolver.class);
	}
	public OrderCannotBeStarted(InventoryExceptionKey key, String orderNo) {
		super("OrderCannotBeStarted: " + orderNo, key.name(), new String[]{orderNo});
		setBundleResolver(InventoryBundleResolver.class);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
