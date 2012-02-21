/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.linogistix.los.inventory.pick.facade;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.exception.PickingExpectedNullException;
import de.linogistix.los.inventory.pick.exception.PickingSubstitutionException;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * Workflow (for gui or web frontend) is as follows
 * <ul>
 * <li> {@link #getRawPickingRequest} to get a list of raw
 * {@link LOSPickRequest}
 * <li> select {@link LOSPickRequest} from above list and accept it for worker
 * via {@link #accept}
 * </ul>
 * 
 * Then the worker steps through the positions. Typically:
 * <ul>
 * <li> go to StorgaLocation
 * <li> pick amount of items
 * <li> finish {@link LOSPickRequestPosition} via
 * {@link #processPickRequestPosition}
 * <li> continue with next position
 * </ul>
 * 
 * After all positions have been terminated:
 * <ul>
 * <li> finish {@link LOSPickRequest} via {@link #finishPickingRequest}
 * </ul>
 * 
 * @see de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness
 * 
 * @author trautm
 */
@Remote
public interface PickOrderFacade {

	/**
	 * Returns all {@link LOSPickRequest} that are in state raw, i.e. that can
	 * be selected by a worker.
	 * 
	 * 
	 * @return
	 */
	List<PickingRequestTO> getRawPickingRequest();

	/**
	 * load from database (eagerly) to prevent lazyInitializationException
	 * 
	 * @param req
	 * @return
	 * @throws de.linogistix.los.query.exception.BusinessObjectNotFoundException
	 */
	LOSPickRequest loadPickingRequest(LOSPickRequest req)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException;

	LOSPickRequest loadPickingRequest(PickingRequestTO req)
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
	LOSPickRequest accept(LOSPickRequest request) throws PickingException,
			InventoryException, BusinessObjectNotFoundException,
			BusinessObjectSecurityException;

	/**
	 * Tests whether the given {@link LOSPickRequestPosition} can be processes with the given parameters.
	 * <p>
	 * Pay special attention to {@link PickingExpectedNullException} and {@link PickingSubstitutionException}.
	 * Those indicate that a call to {@link #processPickRequestPosition(LOSPickRequestPosition, boolean, String, BigDecimal)}
	 * will fail and special handling is required.
	 * <p>
	 * Special handling is done via 
	 * 
	 * @param position
	 * @param unexpectedNullAmount
	 * @param label
	 * @param amount
	 * @return
	 * @throws FacadeException
	 * @throws {@link PickingExpectedNullException} if the Stockunit will become empty (can be used to let the operator aknowledge this)
	 * @throws PickingSubstitutionException if another {@link LOSStorageLocation} has been scanned but it is ok to take from there
	 */
	public boolean testCanProcess(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount) throws FacadeException ;
			
	/**
	 * Process a single position.
	 * <ul>
	 * <li> go to StorgaLocation
	 * <li> pick amount of items
	 * <li> finish {@link LOSPickRequest} via
	 * {@link #processPickRequestPosition}
	 * </ul>
	 * 
	 * @param unexpectedNullAmount
	 *            the worker detects that less items are in the box than he
	 *            should pick.
	 * @throws InventoryException
	 * @throws FacadeException
	 */
	LOSPickRequestPosition processPickRequestPosition(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws PickingException,
			BusinessObjectNotFoundException, BusinessObjectSecurityException,
			InventoryException, LOSLocationException, FacadeException;
	
	/**
	 * Similar to {@link #processPickRequestPosition(LOSPickRequestPosition, boolean, String, BigDecimal)}
	 * <p>
	 * But caller knows (previous call to {@link #testCanProcess(LOSPickRequest, LOSPickRequestPosition, boolean, String, int)}
	 * that it is in expected null amount turn. 
	 * <p>
	 * If the {@link StockUnit} does not become empty after picking
	 * <code>discoveredAmount</code> should contain this newly discovered amount.
 	 * 
	 * @see #processPickRequestPosition
	 * @param position
	 * @param label
	 * @param amount
	 * @param discoveredAmount this amount is still on the stockUnit after it should have become empty
	 * @throws FacadeException
	 */
	public LOSPickRequestPosition processPickRequestPositionExpectedNull(
			LOSPickRequestPosition position, String label, 
			BigDecimal amount, BigDecimal discoveredAmount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException;
	
	
	/**
	 * Similar to {@link #processPickRequestPosition(LOSPickRequestPosition, boolean, String, BigDecimal)}
	 * <p>
	 * But caller knows (previous call to {@link #testCanProcess(LOSPickRequest, LOSPickRequestPosition, boolean, String, int)}
	 * that {@link StockUnit} is a substitute. 
	 * <p>
	 * @see #processPickRequestPosition	 
	 * @param position
	 * @param unexpectedNullAmount
	 * @param label
	 * @param amount
	 * @throws FacadeException
	 */
	public LOSPickRequestPosition processPickRequestPositionSubstitution(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException ;
	/**
	 * 
	 * Terminates given LOSPickRequest at the end of process.
	 * 
	 * @param req
	 * @return
	 * @throws de.linogistix.los.inventory.pick.exception.PickingException
	 * @throws LOSLocationException
	 * @throws ReportException
	 * @throws FacadeException
	 */
	LOSPickRequest finishPickingRequest(LOSPickRequest req, String labelID)
			throws PickingException, InventoryException,
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
	LOSStorageLocation getDestination(LOSPickRequest pickingRequest)
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
	 * @throws ReportException
	 * @throws FacadeException
	 */
	LOSPickRequest finishCurrentUnitLoad(LOSPickRequest req, String transfer)
			throws PickingException, LOSLocationException, ReportException,
			FacadeException;

	/**
	 * Cancels the given LOSPickingRequest for the moment (gives it free).
	 * 
	 * If at least one Position has been processed (i.e. there is a unit load
	 * with picked goods) then the operator has to transport the unit load to
	 * the destination.
	 * 
	 * @param r
	 * @throws org.mywms.facade.FacadeException
	 * @throws de.linogistix.los.inventory.pick.exception.PickingException
	 *             with key MUST_GOTO_DESTINATION if at least one position has
	 *             been processed.
	 */
	void cancel(LOSPickRequest r) throws FacadeException, PickingException;

	/**
	 * Sets {@link PickingWithdrawalType} to
	 * {@link PickingWithdrawalType#TAKE_UNITLOAD} if finishPaletCheckBox is
	 * true
	 * 
	 * @param finishPaletCheckBox
	 */
	void setPickRequestPositionTakeUnitLoad(LOSPickRequestPosition p,
			boolean finishPaletCheckBox);


	List<BODTO<StockUnit>> getSuitableStockUnits(BODTO<Client> client, 
			BODTO<ItemData> idat, BODTO<Lot> lot, BigDecimal amount) throws FacadeException;
	
	/**
	 * Creates a PickRequest with one {@link LOSPickRequestPosition} per {@link StockUnit}
	 * 
	 * @param orderpos
	 * @param pickreqnumber
	 * @param sus
	 * @throws FacadeException 
	 */
	void createPickRequests(BODTO<LOSOrderRequestPosition> orderpos, String pickreqnumber, List<BODTO<StockUnit>> sus ) throws FacadeException;
	
	void createPickRequests(List<CreatePickRequestPositionTO> chosenStocks) throws FacadeException;

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
     * Check, whether there is already picked material on the cart.
     * @param req
     * @return
     */
    public boolean hasPickedStock(LOSPickRequest req);

    /**
     * Check, whether the pick uses the complete UnitLoad
     * @param pos
     * @return
     */
    public boolean isCompleteUnitLoad(LOSPickRequestPosition pos);

    /**
     * Check, whether it is allowed to take the whole UnitLoad
     * @param pos
     * @return
     */
    public boolean isWholeUnitLoadAllowed(LOSPickRequestPosition pos);

    /**
     * Check, whether the serialNumber is already used
     * @param itemData
     * @param serialNumber
     * @return
     */
    public boolean isSerialNumberUnique(ItemData itemData, String serialNumber );

	/**
	 * Deleting on LOSPickRequest
	 * 
	 * @param pick
	 * @throws FacadeException
	 */
	public void remove(BODTO<LOSPickRequest> pick) throws FacadeException;
	
	
    /**
     * Check whether something is currently picked to the cart.
     * 
     * @param request
     * @throws FacadeException
     */
    public boolean isSomethingPicked( LOSPickRequest request );

}
