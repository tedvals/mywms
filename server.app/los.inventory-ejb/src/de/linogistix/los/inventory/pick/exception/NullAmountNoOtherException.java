/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.exception;

import java.math.BigDecimal;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.res.InventoryBundleResolver;

@ApplicationException(rollback=false)
public class NullAmountNoOtherException extends FacadeException {

	private static final long serialVersionUID = 1L;
	
	private static final String resourceBundle = "de.linogistix.los.inventory.res.Bundle";
	
	public NullAmountNoOtherException(InventoryExceptionKey key, BigDecimal amount) {
		this(key, amount, "?");
		setBundleResolver(InventoryBundleResolver.class);
	}
	
	public NullAmountNoOtherException(InventoryExceptionKey key, BigDecimal amount, String forItem) {
		super(key.name(), key.name(), new String[]{"" +amount, forItem}, resourceBundle);
		setBundleResolver(InventoryBundleResolver.class);
	}

	

}
