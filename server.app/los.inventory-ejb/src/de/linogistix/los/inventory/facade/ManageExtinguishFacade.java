/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.query.BODTO;

/**
 * @author liu
 * 
 */
@Remote
public interface ManageExtinguishFacade {

	public final static String TIME_OUT_KEY = "timeout millis";
	
	public BODTO<ExtinguishOrder> createExtinguishOrder(String lotName, String itemDataNumber, String client, 
			Date data) throws InventoryException;
	
	public void createExtinguishOrders(List<BODTO<Lot>> list) throws InventoryException;
	
	public void startExtinguishOrder(BODTO<ExtinguishOrder>  order) throws InventoryException;
	
	public void createCronJob();
	
	public List<Lot> getTooOld(Client c);
	
	public List<Lot> getNotToUse(Client c);
	
	public List<Lot> getToUseFromNow(Client c);
	
	public void processLots();
}
