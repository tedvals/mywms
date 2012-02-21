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

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * 
 * @author artur, liyu
 */
@Remote
public interface ExtinguishFacade {

	/**
	 * Returns all {@link LOSPickRequest} that are in state raw, i.e. that can
	 * be selected by a worker.
	 * 
	 * 
	 * @return
	 */
	List<ExtinguishRequest> getRawExtinguishRequest();

	/**
	 * load from database (eagerly) to prevent lazyInitializationException
	 * 
	 * @param req
	 * @return
	 * @throws de.linogistix.los.query.exception.BusinessObjectNotFoundException
	 */
	ExtinguishRequest loadExtinguishRequest(ExtinguishRequest req)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException;

	/**
	 * Returns StorageLocation from where to Pick for given
	 * LOSPickRequestPosition
	 * 
	 * @param position
	 * @return StorageLocation from where to Pick
	 */
	LOSStorageLocation getStorageLocation(LOSPickRequestPosition position)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException;

	/**
	 * Accepts a {@link LOSPickRequest} for the worker
	 * 
	 * 
	 * @return
	 */
	ExtinguishRequest accept(ExtinguishRequest request)
			throws PickingException, InventoryException,
			BusinessObjectNotFoundException, BusinessObjectSecurityException;

	/**
	 * Process a single position.
	 * <ul>
	 * <li>go to StorgaLocation
	 * <li>pick amount of items
	 * <li>finish {@link LOSPickRequest} via {@link #processPickRequestPosition}
	 * </ul>
	 * 
	 * @param unexpectedNullAmount
	 *            the worker detects that less items are in the box than he
	 *            should pick.
	 * @throws InventoryException
	 * @throws FacadeException 
	 */
//	LOSPickRequestPosition processPickRequestPosition(
//			LOSPickRequestPosition position, boolean unexpectedNullAmount,
//			String label, BigDecimal amount) throws PickingException,
//			BusinessObjectNotFoundException, BusinessObjectSecurityException,
//			InventoryException, LOSLocationException, FacadeException;

	/**
	 * 
	 * Terminates given LOSPickRequest at the end of process.
	 * 
	 * @param req
	 * @return
	 * @throws de.linogistix.los.inventory.pick.exception.PickingException
	 * @throws LOSLocationException
	 * @throws FacadeException 
	 */
	ExtinguishRequest finishExtinguishRequest(ExtinguishRequest req,
			String labelID) throws PickingException, InventoryException,
			BusinessObjectNotFoundException, BusinessObjectSecurityException,
			LOSLocationException, ReportException, FacadeException;

	/**
	 * Returns the destination where the picked goods have to be transported to
	 * at the end of the process.
	 * 
	 * @param pickingRequest
	 * @return
	 * @throws PickingException
	 */
	LOSStorageLocation getDestination(ExtinguishRequest request)
			throws PickingException;

	/**
	 * At the end of the picking process or whenever the UnitLoad to which goods
	 * are picked becomes full, it is transported to the transfer or goods out
	 * location.
	 * 
	 * @param req
	 * @param transfer
	 * @return
	 * @throws PickingException
	 * @throws LOSLocationException
	 * @throws FacadeException 
	 */
	ExtinguishRequest finishCurrentUnitLoad(ExtinguishRequest req,
			String transfer) throws PickingException, LOSLocationException,
			ReportException, FacadeException;

}
