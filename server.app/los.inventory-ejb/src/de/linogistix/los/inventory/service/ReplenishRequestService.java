/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 *  A Service for {@link OrderRequest}.
 * 
 * @author trautm
 */
@Local
public interface ReplenishRequestService extends BasicService<LOSReplenishRequest> {

    /**
     * Creates a new {@link OrderRequest}.
     * 
     * @param client
     * @param requestId
     * @param delivery the date when the order has to be delivered
     * @param destination the destination where the order has to be handed over 
     * @return the created {@link OrderRequest}
     * @throws InventoryException 
     */
    LOSReplenishRequest create(Client client, String requestId, Date delivery, LOSStorageLocation destination, String documentUrl, String labelUrl) throws InventoryException;

     /**
     * Creates a {@link OrderRequestPosition} and adds it to the {@link OrderRequest}.
     * 
     * @param requestId of the  {@link OrderRequest}
     * @param itemDataRef references {@link ItemData}
     * @param lotRef references {@link Lot}. Must not be null!
     * @param amount the ordered amount
     * @param partitionAllowed if the {@link OrderRequestPosition} might be delivered altough the available amount is smaller than the ordered
     * @return the index of the {@link OrderRequestPosition}within the {@link OrderRequest}.
     */
    int addPosition(LOSReplenishRequest request, ItemData idat, Lot lot, BigDecimal amount, boolean partitionAllowed);
    
    /**
     * 
     * @param requestId
     * @return 
     * @throws org.mywms.service.EntityNotFoundException
     */
    public LOSReplenishRequest getByRequestId(String requestId) throws EntityNotFoundException;
	
	
}
