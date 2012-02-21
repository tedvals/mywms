/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
@Remote
public interface ReplenishFacade {

	public final static String TIME_OUT_KEY = "timeout millis";
	
	public final static String TIME_OUT_INFO = "timeout for Replenish";
	
	void replenish(List<BODTO<LOSStorageLocation>> locs) throws FacadeException;
	
	void replenish(String storageLocation, BigDecimal amount) throws FacadeException;
	
	void createCronJob();
	
	void cancelCronJob() throws Exception;
	
	String statusCronJob();
	
	void createReplenishmentIfNeeded() throws FacadeException;
	
	public ItemDataTO getItemDataByLocation(String storageLocationName) throws FacadeException;
}
