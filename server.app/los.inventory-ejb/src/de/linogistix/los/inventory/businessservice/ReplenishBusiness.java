/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Timer;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * Services for replenish (refilling empty pick locations)
 * 
 * @author trautm
 *
 */
@Local
public interface ReplenishBusiness {

	String CLEARING_REPL_NOSTOCKUNIT_MSG = "CLEARING_REPL_NOSTOCKUNIT_MSG";
	
	String CLEARING_REPL_NOSTOCKUNIT_SHORT = "CLEARING_REPL_NOSTOCKUNIT_SHORT";
	
	String CLEARING_REPL_NOSTOCKUNIT_CONFIRM = "CLEARING_REPL_NOSTOCKUNIT_CONFIRM";
	
	String CLEARING_REPL_NOSTOCKUNIT_SOURCEPREF =  "REPL_NOSTOCKUNIT_";
	
	List<LOSReplenishRequest> createReplenishmentIfNeeded() throws InventoryException, FacadeException;
	
	LOSReplenishRequest create(LOSStorageLocation sl, BigDecimal amount) throws FacadeException;
	
	public void createCronJob();
	
	public void timeout(Timer timer) ;
	
	public String statusCronJob();
	
	public void cancelCronJob();
}
