/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.businessservice;

import java.math.BigDecimal;
import java.util.List;

import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.exception.NullAmountNoOtherException;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.exception.PickingSubstitutionException;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.report.businessservice.ReportException;

/**
 * A facade for the picking process within the warehouse.
 * 
 * Returns all {@link LOSPickRequest} that are in state raw, i.e. that can be
 * selected by a worker.
 * 
 * Workflow (for gui or web frontend) is as follows
 * <ul>
 * <li> {@link #getRawPickingRequest} to get a list of raw
 * {@link LOSPickRequest}
 * <li> select {@link LOSPickRequest} from above list and accept it for worker
 * via {@link #accept}
 * <li> TODO: do something with it
 * <li> finish accepted {@link LOSPickRequest} via {@link #finish}
 * </ul>
 * 
 * @author trautm
 */
public interface PickOrderBusiness {

	String CONFKEY_PRINTER = "printer name";

	/**
	 * processes all {@link LOSOrderRequest} that are in state raw.
	 * 
	 * Creates {@link LOSPickRequest} .
	 * 
	 */
	void processPickWave();

	/**
	 * processes given {@link LOSOrderRequest} that has to be in state raw.
	 * 
	 * Creates one or more {@link LOSPickRequest} .
	 * 
	 * @param order
	 * @return the resulting list of {@link LOSPickRequest}
	 * @throws de.linogistix.los.inventory.pick.businessservice.PickException
	 */
	List<LOSPickRequest> processOrderRequest(LOSOrderRequest order)
			throws InventoryException, LOSLocationException;

	/**
	 * processes given {@link LOSOrderRequest} that has to be in state raw.
	 * 
	 * The {@link StockUnits} to be used are precalculated and will be taken as whole.
	 * 
	 * @param order
	 * @param sus
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	List<LOSPickRequest> processOrderRequest(LOSOrderRequest order, List<StockUnit> sus, BigDecimal amount)
			throws InventoryException, LOSLocationException;
	
	/**
	 * Returns all {@link LOSPickRequest} that are in state raw, i.e. that can
	 * be selected by a worker.
	 * 
	 */
	List<PickingRequestTO> getRawPickingRequest();

	/**
	 * Accepts a {@link LOSPickRequest} for the worker
	 * 
	 * 
	 * @return
	 */
	void accept(LOSPickRequest request) throws InventoryException;

	/**
	 * Finishes an accepted {@link LOSPickRequest} for the worker
	 * 
	 * @return
	 * @throws PickingException 
	 */
	void finish(LOSPickRequest request, boolean force) throws InventoryException, PickingException;

	/**
	 * Processes a single {@link LOSPickRequestPosition}: The worker is in
	 * front of the rack/StorageLocation and has picked an amount of goods.
	 * <p>
	 * {@link InventoryException}/ {@link InventoryExceptionKey} that need special treatment:
	 * <ul>
	 * <li>InventoryExceptionKey.PICK_UNEXPECTED_NULL: 
	 * <li>InventoryExceptionKey.PICK_WRONG_AMOUNT
	 * <li>InventoryExceptionKey.PICK_WRONG_SOURCE
	 * </ul>
	 * 
	 * @param r
	 * @param position
	 * @param unexpectedNullAmount
	 * @param sl
	 * @param amount
	 * @throws InventoryException
	 * @throws LOSLocationException
	 * @throws FacadeException 
	 */
	void processPickRequestPosition(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws PickingException, InventoryException,
			LOSLocationException, FacadeException;
	
	/**
	 * Similar to {@link #processPickRequestPosition(LOSPickRequest, LOSPickRequestPosition, boolean, LOSStorageLocation, BigDecimal)}
	 * <p>
	 * But caller knows (previous call to {@link #testCanProcess(LOSPickRequest, LOSPickRequestPosition, boolean, LOSStorageLocation, BigDecimal)})
	 * that it is in expected null amount turn. 
	 * <p>
	 * If the {@link StockUnit} does not become empty after picking
	 * <code>discoveredAmount</code> should contain this newly discovered amount.
 	 * 
	 * @see #processPickRequestPosition
	 * @param r
	 * @param position
	 * @param sl
	 * @param amount
	 * @param discoveredAmount
	 * @throws FacadeException
	 */
	public void processPickRequestPositionExpectedNull(LOSPickRequest r,
			LOSPickRequestPosition position, LOSStorageLocation sl, 
			BigDecimal amount, BigDecimal discoveredAmount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException;
	
	/**
	 * Similar to {@link #processPickRequestPosition(LOSPickRequest, LOSPickRequestPosition, boolean, LOSStorageLocation, BigDecimal)}
	 * <p>
	 * But caller knows (previous call to {@link #testCanProcess(LOSPickRequest, LOSPickRequestPosition, boolean, LOSStorageLocation, BigDecimal)})
	 * that {@link StockUnit} is a substitute. 
	 * <p>
	 * @see #processPickRequestPosition	 
	 * @param r
	 * @param position
	 * @param unexpectedNullAmount
	 * @param sl
	 * @param amount
	 * @throws FacadeException
	 */
	public void processPickRequestPositionSubstitution(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException ;
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
	 * @throws ReportException 
	 * @throws FacadeException 
	 */
	LOSPickRequest finishCurrentUnitLoads(LOSPickRequest req,
			LOSStorageLocation transfer) throws PickingException,
			LOSLocationException, ReportException, FacadeException;

	/**
	 * Remove the picking request (cancellation)
	 * 
	 * @param pickingRequest
	 * @throws InventoryException
	 */
	void removePickingRequest(LOSPickRequest pickingRequest)
			throws InventoryException, PickingException;

	/**
	 * Resets the {@link LOSPickRequest}. Another operator can use this.
	 * 
	 * @param pickingRequest
	 * @throws PickingException with key MUST_GOTO_DESTINATION if operator cannot cancel before he brought the current UnitLoad to its destination
	 * @throws FacadeException
	 */
	void cancel(LOSPickRequest pickingRequest) throws PickingException, FacadeException;

	/**
	 * @param r
	 * @param position
	 * @param unexpectedNullAmount
	 * @param sl
	 * @param amount
	 * @return
	 * @throws FacadeException
	 * @throws {@link PickingExpectedNullException} if the Stockunit will become empty (can be used to let the operator aknowledge this)
	 * @throws PickingSubstitutionException if another {@link LOSStorageLocation} has been scanned but it is ok to take from there
	 */
	public boolean testCanProcess(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount) throws FacadeException ;
	
	/**
	 * 
	 * @param r
	 * @param position
	 * @param unexpectedNullAmount
	 * @param sl
	 * @param amount
	 * @param serialNumber might be null
	 * @return
	 * @throws FacadeException
	 */
	public boolean testCanProcess(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount, String serialNumber) throws FacadeException ;
	
	/**
	 * Creates a {@link LOSPickRequestPosition} per {@link StockUnit} of @link{PickingWithdrawalType.TAKE_UNITLOAD}
	 *  
	 * @param orderPos
	 * @param name
	 * @param sus
	 * @return
	 * @throws FacadeException
	 */
	public LOSPickRequest createPickRequest(LOSOrderRequestPosition orderPos, String name, List<CreatePickRequestPositionTO> sus) throws FacadeException;

		
	public void createPickRequestPosition(CreatePickRequestPositionTO posTO) throws FacadeException;

	/**
	 * Assigns a number of serial numbers to the picked position.
	 * Each picked stockunit will be splitted in serials.size() StockUnits. Each gets a serial number,
	 * 
	 * @param position
	 * @param serials
	 */
	void assignSerialNumbers(LOSPickRequestPosition position,
			List<String> serials) throws FacadeException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param pos
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	public List<StockUnit> suitableStockUnitsByLot(LOSOrderRequestPosition pos)
			throws InventoryException, LOSLocationException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param lot
	 * @param toPick
	 * @return
	 * @throws NullAmountNoOtherException
	 */
	public List<StockUnit> suitableStockUnitsByLotAndAmountNoException(Lot lot,
			BigDecimal toPick) throws NullAmountNoOtherException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param lot
	 * @param toPick
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	public List<StockUnit> suitableStockUnitsByLotAndAmount(Lot lot, BigDecimal toPick)
			throws InventoryException, LOSLocationException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param lots
	 * @param toPick
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	public List<StockUnit> suitableStockUnitsByLotsAndAmount(List<Lot> lots, BigDecimal toPick)
			throws InventoryException, LOSLocationException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param pos
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	public List<StockUnit> suitableStockUnitsByItemData(LOSOrderRequestPosition pos, BigDecimal amount)
			throws InventoryException, LOSLocationException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param itemData
	 * @param toPick
	 * @return
	 * @throws InventoryException
	 * @throws LOSLocationException
	 */
	public List<StockUnit> suitableStockUnitsByItemDataWithoutLot(ItemData itemData,
			BigDecimal toPick) throws InventoryException, LOSLocationException;

	/**
	 * Returns List of suitable StockUnits for picking from
	 * @param itemData
	 * @param toPick
	 * @return
	 * @throws NullAmountNoOtherException
	 */
	public List<StockUnit> suitableStockUnitsByItemDataWithoutLotNoException(
			ItemData itemData, BigDecimal toPick, boolean ignoreMissingAmount) throws NullAmountNoOtherException;	
	
}	