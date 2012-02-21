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
public class PickingSubstitutionException extends PickingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StockUnit subs;
	
	public PickingSubstitutionException(String location1, String location2) {
		super(PickingExceptionKey.PICK_FROM_WRONG_LOCATION, new Object[]{location1, location2});
		setBundleResolver(BundleResolver.class);
	}


	public void setSubstitution(StockUnit subs) {
		this.subs = subs;
		
	}
	
	public StockUnit getSubstitution(){
		return this.subs;
	}
	
}
