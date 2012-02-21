/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.exception;

import javax.ejb.ApplicationException;

import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.pick.res.BundleResolver;

@ApplicationException(rollback=false)
public class PickingExpectedNullException extends PickingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StockUnit stockUnit;
	
	public PickingExpectedNullException(StockUnit su) {
		super(PickingExceptionKey.PICK_EXPECTED_NULL, "" + su.getId());
		setBundleResolver(BundleResolver.class);
	}

	public void setStockUnit(StockUnit stockUnit) {
		this.stockUnit = stockUnit;
	}

	public StockUnit getStockUnit() {
		return stockUnit;
	}


}
