/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.LogItem;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.crud.LOSGoodsOutRequestCRUDRemote;
import de.linogistix.los.inventory.crud.LOSGoodsOutRequestPositionCRUDRemote;
import de.linogistix.los.inventory.crud.LOSStorageRequestCRUDRemote;
import de.linogistix.los.inventory.crud.OrderRequestCRUDRemote;
import de.linogistix.los.inventory.crud.OrderRequestPositionCRUDRemote;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgStock;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.pick.crud.LOSPickRequestCRUDRemote;
import de.linogistix.los.inventory.pick.crud.LOSPickRequestPositionCRUDRemote;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.query.LOSPickRequestPositionQueryRemote;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.query.OrderReceiptQueryRemote;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.businessservice.LOSFixedLocationAssignmentComp;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.crud.LOSUnitLoadCRUDRemote;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.util.DateHelper;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSInventoryComponentBean extends BasicFacadeBean implements LOSInventoryComponent {

	private static final Logger log = Logger.getLogger(LOSInventoryComponentBean.class);
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@EJB
	InventoryGeneratorService generatorService;

	@EJB
	StockUnitService stockUnitService;

	@EJB
	StockUnitQueryRemote suQuery;

	@EJB
	LOSStockUnitRecordService recordService;

	@EJB
	LOSUnitLoadQueryRemote ulQuery;

	@EJB
	LOSUnitLoadService ulService;

	@EJB
	LOSStockUnitRecordService recService;

	@EJB
	LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	LOSStorage storage;

	@EJB
	OrderRequestQueryRemote orderQuery;

	@EJB
	UnitLoadTypeQueryRemote ulTypeQueryRemote;

	@EJB
	ContextService contextService;

	@EJB
	LOSFixedLocationAssignmentComp fixAssComp;

	@EJB
	LOSPickRequestPositionService pickPosService;

	@EJB
	LOSPickRequestPositionQueryRemote pickPosQuery;

	@EJB
	LOSGoodsReceiptPositionQueryRemote grPosQueryRemote;

	@EJB
	OrderReceiptQueryRemote orderReceiptQuery;

	@EJB
	LOSAdviceQueryRemote adviceQuery;

	@EJB
	ClientService clientService;

	@EJB
	ItemDataService itemDataService;

	@EJB
	LOSStorageLocationService slService;

	@EJB
	LOSStorageLocationTypeService slTypeService;

	@EJB
	LOSLotService lotService;

	@EJB
	QueryUnitLoadTypeService ulTypeService;

	@EJB
	LOSUnitLoadCRUDRemote ulCrud;

	@EJB
	LOSStorageRequestCRUDRemote storageCrud;

	@EJB
	LOSStorageRequestQueryRemote storageQuery;

	@EJB
	LOSGoodsOutRequestPositionCRUDRemote outPosCRUD;

	@EJB
	LOSGoodsOutRequestCRUDRemote outCRUD;

	@EJB
	LOSGoodsOutRequestPositionService outPosService;

	@EJB
	OrderRequestService orderReqService;

	@EJB
	OrderRequestCRUDRemote orderReqCrud;

	@EJB
	OrderRequestPositionCRUDRemote orderReqPosCrud;

	@EJB
	LOSPickRequestPositionCRUDRemote pickPosCrud;

	@EJB
	LOSPickRequestCRUDRemote pickCrud;
	
	@EJB
	HostMsgService hostService;

	public StockUnit createStock(Client client, Lot batch, ItemData item, BigDecimal amount, LOSUnitLoad unitLoad, String activityCode, String serialNumber) throws InventoryException {
		return createStock(client, batch, item, amount, unitLoad, activityCode, serialNumber, null, true);
	}
	public StockUnit createStock(Client client, Lot batch, ItemData item, BigDecimal amount, LOSUnitLoad unitLoad, String activityCode, String serialNumber, String operator, boolean sendNotify) throws InventoryException {

		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new InventoryException(InventoryExceptionKey.AMOUNT_MUST_BE_GREATER_THAN_ZERO, "");
		}

		if (serialNumber != null) {
			serialNumber = serialNumber.trim();
			if (serialNumber.length() == 0) {
				serialNumber = null;
			}
		}

		if (serialNumber != null) {
			List<StockUnit> list = stockUnitService.getBySerialNumber(item, serialNumber);
			for (StockUnit su : list) {
				if (BigDecimal.ZERO.compareTo(su.getAmount()) < 0) {
					throw new InventoryException(InventoryExceptionKey.SERIAL_ALREADY_EXISTS, serialNumber);
				}
			}
		}

		StockUnit su = stockUnitService.create(client, unitLoad, item, amount);
		su.setLot(batch);
		su.setUnitLoad(unitLoad);
		su.setSerialNumber(serialNumber);
		unitLoad.getStockUnitList().add(su);

		manager.persist(su);

		if( BigDecimal.ZERO.compareTo(amount) != 0 ) {
			recService.recordCreation(amount, su, activityCode, null, operator);
			if( sendNotify ) {
				try {
					hostService.sendMsg( new HostMsgStock( su, amount, operator, LOSStockUnitRecordType.STOCK_CREATED, activityCode) );
				} catch (FacadeException e) {
					// TODO Do not declare so special throws declarations. A throws FacadeException would be enough.
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
		}
		return su;
	}

	public void transferStock(LOSUnitLoad src, LOSUnitLoad dest, String activityCode) throws FacadeException {

		boolean destAllowed = false;
		Vector<Long> suIds = new Vector<Long>();

		List<StockUnit> sus = src.getStockUnitList();

		for (StockUnit su : sus) {
			if (testSuiable(su, dest)) {
				destAllowed = true;
				suIds.add(su.getId());
			} else {
				destAllowed = false;
				break;
			}
		}

		if (destAllowed) {
			// remove from src
			for (Long suId : suIds) {
				StockUnit su = manager.find(StockUnit.class, suId);
				transferStockUnit(su, dest, activityCode);
			}
			src.setStockUnitList(new ArrayList<StockUnit>());
		} else {
			log.error("Transfer of stock unit from unit load="+src.getLabelId()+" to unit load="+dest.getLabelId()+" is not allowed");
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { src.getLabelId(), dest.getLabelId() });
		}
	}

	public void consolidate(LOSUnitLoad ul, String activityCode) throws FacadeException {

		HashMap<Lot, StockUnit> lots;
		HashMap<ItemData, StockUnit> is;

		StockUnit existing;

		lots = new HashMap<Lot, StockUnit>();
		is = new HashMap<ItemData, StockUnit>();

		ul = manager.merge(ul);
		List<StockUnit> sus = ul.getStockUnitList();

		if (ul.getStorageLocation().getArea() != null) {
			switch (ul.getStorageLocation().getArea().getAreaType()) {

			case PICKING:
			case STORE:
			case REPLENISHMENT:
				break;
			default:
				log.warn("WON'T CONSOLIDATE on storage location in Area " + ul.getStorageLocation().getArea().getAreaType());
				return;
			}
		} else {
			log.warn("WON'T CONSOLIDATE on storage location " + ul.getStorageLocation().getName());
			return;
		}

		if (sus == null || sus.size() < 1) {
			return;
		}

		List<Long> suIds = new ArrayList<Long>();

		for (StockUnit su : sus) {
			if (su.getSerialNumber() != null) {
				log.warn("Won't consolidate because has serialnumber: " + su.toDescriptiveString());
			}
			suIds.add(su.getId());
		}

		for (Long id : suIds) {

			StockUnit su = manager.find(StockUnit.class, id);

			if (su.getItemData().isLotMandatory() && LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE.equals(ul.getPackageType())) {

				if (su.getLot() == null) {
					throw new InventoryException(InventoryExceptionKey.STOCKUNIT_NO_LOT, su.getId().toString());
				}
				existing = lots.get(su.getLot());
				if (existing != null && !existing.equals(su)) {
					if (existing.getLock() != 0) {
						log.warn("CANNOT CONSOLIDATE existing su is locked " + existing.toDescriptiveString());
						continue;
					}

					if (su.getLock() != 0) {
						log.warn("CANNOT CONSOLIDATE to su is locked " + su.toDescriptiveString());
						continue;
					}
					// existing = manager.find(StockUnit.class, su.getId());
					combineStock(su, existing, activityCode);
					lots.put(su.getLot(), existing);
				} else {
					lots.put(su.getLot(), su);
				}

			} else if (LOSUnitLoadPackageType.MIXED_CONSOLIDATE.equals(ul.getPackageType()) || LOSUnitLoadPackageType.OF_SAME_ITEMDATA_CONSOLIDATE.equals(ul.getPackageType())
					|| LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE.equals(ul.getPackageType())) {

				existing = is.get(su.getItemData());
				if (existing != null && !existing.equals(su)) {
					combineStock(su, existing, activityCode);
					is.put(su.getItemData(), existing);
				} else {
					is.put(su.getItemData(), su);
				}
			} else {
				log.warn("noting to do. Type is" + ul.getPackageType());
			}
		}
	}

	public void transferStockFromReserved(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws FacadeException {

		boolean destAllowed = false;

		if (su.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, su.getId() + "-" + su.getLock());
		}
		if (dest.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, dest.getId() + "-" + dest.getLock());
		}

		if (su.getReservedAmount().compareTo(amount) < 0) {
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, "" + amount);
		}

		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				log.warn("lot mismatch! Stockunit is of " + su.getLot().toUniqueString() + ", destination of " + dest.getLot().toUniqueString());
				destAllowed = false;
			}
		} else if (su.getItemData().equals(dest.getItemData())) {
			destAllowed = true;
		} else {
			log.warn("itemData mismatch! Stockunit is of " + su.getItemData().toUniqueString() + ", destination of " + dest.getItemData().toUniqueString());
			destAllowed = false;
		}

		if (destAllowed) {
			// transfer amount
			dest.setAmount(dest.getAmount().add(amount));
			su.setAmount(su.getAmount().subtract(amount));
			su.releaseReservedAmount(amount);

			recordService.recordRemoval(amount.negate(), su, activityCode);
			recordService.recordCreation(amount, dest, activityCode);

			if (su.getAmount().compareTo(new BigDecimal(0)) == 0) {
				sendStockUnitsToNirwana(su, activityCode);
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public void transferStock(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws FacadeException {

		su = manager.merge(su);
		dest = manager.merge(dest);

		boolean destAllowed = false;

		if (su.isLocked()) {
			if (su.getLock() != StockUnitLockState.PICKED_FOR_GOODSOUT.getLock() || su.getLock() != StockUnitLockState.UNEXPECTED_NULL.getLock()) {
				// ok
			} else {
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, su.getId() + "-" + su.getLock());
			}

		}
		if (dest.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, dest.getId() + "-" + dest.getLock());
		}

		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				destAllowed = false;
			}
		} else if (su.getItemData().equals(dest.getItemData())) {
			destAllowed = true;
		} else {
			destAllowed = false;
		}

		if (destAllowed) {
			// transfer amount
			dest.setAmount(dest.getAmount().add(amount));
			su.setAmount(su.getAmount().subtract(amount));

			recordService.recordRemoval(amount.negate(), su, activityCode);
			recordService.recordCreation(amount, dest, activityCode);

			if (su.getAmount().compareTo(new BigDecimal(0)) == 0) {
				sendStockUnitsToNirwana(su, activityCode);
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public StockUnit splitStock(StockUnit stockToSplit, LOSUnitLoad destUl, BigDecimal takeAwayAmount, String activityCode) throws FacadeException {

		stockToSplit = manager.merge(stockToSplit);

		if (stockToSplit.isLocked()) {
			if (stockToSplit.getLock() != StockUnitLockState.PICKED_FOR_GOODSOUT.getLock() || stockToSplit.getLock() != StockUnitLockState.UNEXPECTED_NULL.getLock()) {
				// ok
			} else {
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, stockToSplit.getId() + "-" + stockToSplit.getLock());
			}
		}

		if (destUl.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.DESTINATION_UNITLOAD_LOCKED, destUl.getLabelId());
		}

		StockUnit newStock = createStock(stockToSplit.getClient(), stockToSplit.getLot(), stockToSplit.getItemData(), new BigDecimal(0), destUl, activityCode, null);

		transferStock(stockToSplit, newStock, takeAwayAmount, activityCode);
		newStock.setLock(stockToSplit.getLock());

		return newStock;
	}

	public void combineStock(StockUnit su, StockUnit dest, String activityCode) throws FacadeException {

		boolean destAllowed = false;

		su = manager.merge(su);
		dest = manager.merge(dest);

		if (su.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, new Integer[] { su.getLock() });
		}

		if (dest.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, new Integer[] { dest.getLock() });
		}

		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				destAllowed = false;
			}
		} else if (su.getItemData().equals(dest.getItemData())) {
			destAllowed = true;
		} else {
			destAllowed = false;
		}

		if (destAllowed) {
			// check reservations
			if (BigDecimal.ZERO.compareTo(su.getReservedAmount()) < 0) {
				// there seems to a reservation
				// Try all picks to switch them to the new stock unit
				List<LOSPickRequestPosition> pickList = null;
				try {
					pickList = pickPosService.getByStockUnit(su);
				} catch (Throwable t) {
					// ignore
				}
				if (pickList != null) {
					for (LOSPickRequestPosition pick : pickList) {
						if (!pick.isCanceled() && !pick.isPicked()) {
							pick.setStockUnit(dest);
						}
					}
				}
			}
			// transfer amount
			BigDecimal amount = su.getAmount();
			dest.setAmount(dest.getAmount().add(amount));
			dest.setReservedAmount(dest.getReservedAmount().add(su.getReservedAmount()));

			su.setAmount(new BigDecimal(0));
			su.setReservedAmount(new BigDecimal(0));
			recordService.recordRemoval(amount.negate(), su, activityCode);
			recordService.recordCreation(amount, dest, activityCode);
			sendStockUnitsToNirwana(su, activityCode);
			manager.flush();
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public void sendStockUnitsToNirwana(StockUnit su, String activityCode) throws FacadeException {
		sendStockUnitsToNirwana(su, activityCode, null);
	}

	public void sendStockUnitsToNirwana(StockUnit su, String activityCode, String operator) throws FacadeException {
		LOSUnitLoad ul = ulService.getNirwana();

		if (su.getReservedAmount().compareTo(new BigDecimal(0)) > 0) {
			log.error("Cannot be deleted: " + su.toDescriptiveString());
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_HAS_RESERVATION, "" + su.getId());
		}
		changeAmount(su, new BigDecimal(0), true, activityCode);
		transferStockUnit(su, ul, activityCode, operator);
		su.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
	}

	public void sendStockUnitsToNirwana(LOSStorageLocation sl, String activityCode) throws FacadeException {

		sl = manager.merge(sl);

		for (LOSUnitLoad ul : (List<LOSUnitLoad>) sl.getUnitLoads()) {
			sendStockUnitsToNirwana(ul, activityCode);
		}

	}

	public void sendStockUnitsToNirwana(LOSUnitLoad ul, String activityCode) throws FacadeException {

		List<Long> sus = new ArrayList<Long>();
		// List<Long> uls = new ArrayList<Long>();

		for (StockUnit su : ul.getStockUnitList()) {
			sus.add(su.getId());
			// su = manager.find(StockUnit.class, su.getId());
			// manager.remove(su);
		}

		for (Long id : sus) {
			StockUnit su = manager.find(StockUnit.class, id);
			if (su == null) {
				continue;
			}
			sendStockUnitsToNirwana(su, activityCode);
		}
		
		sendUnitLoadToNirwanaIfEmpty(ul);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode) throws InventoryException {
		changeAmount(su, amount, forceRelease, activityCode, null, null, true);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment) throws InventoryException {
		changeAmount(su, amount, forceRelease, activityCode, comment, null, true);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator) throws InventoryException {
		changeAmount(su, amount, forceRelease, activityCode, comment, operator, true);
	}
	
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator, boolean sendNotify) throws InventoryException {

		if( operator == null ) {
			operator = contextService.getCallersUser().getName();
		}
		BigDecimal diffAmount;

		su = manager.merge(su);

		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}

		diffAmount = amount.subtract(su.getAmount());

		if (BigDecimal.ZERO.compareTo(diffAmount) == 0) {
			log.info("No need to change amount of StockUnit " + su.toDescriptiveString());
		} else if (BigDecimal.ZERO.compareTo(diffAmount) < 0) {
			log.info("GOING TO SET amount of StockUnit " + su.toDescriptiveString() + " *** to *** " + amount);
			su.setAmount(amount);
			recordService.recordChange(diffAmount, su, activityCode, comment, operator);
			if( sendNotify ) {
				try{
					hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, LOSStockUnitRecordType.STOCK_ALTERED, activityCode));
				}
				catch( FacadeException e ) {
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
			
		} else if (amount.compareTo(su.getReservedAmount()) >= 0 || forceRelease) {
			if (forceRelease && amount.compareTo(su.getReservedAmount()) < 0) {
				log.info("GOING TO SET FORCE RELEASED reservedamount of StockUnit " + su.toDescriptiveString() + " *** to *** " + amount);
				su.setReservedAmount(amount);
			}
			log.info("GOING TO SET amount of StockUnit " + su.toDescriptiveString() + " *** to *** " + amount);
			su.setAmount(amount);
			try{
				if (BigDecimal.ZERO.compareTo(amount) < 0) {
					recordService.recordChange(diffAmount, su, activityCode, comment, operator);
					if( sendNotify ) {
						hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, LOSStockUnitRecordType.STOCK_ALTERED, activityCode));
					}
				} else {
					recordService.recordRemoval(diffAmount, su, activityCode, comment, operator);
					if( sendNotify ) {
						hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, LOSStockUnitRecordType.STOCK_REMOVED, activityCode));
					}
				}
			}
			catch( FacadeException e ) {
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
			}

		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_HAS_RESERVATION, "");
		}
	}

	public void changeReservedAmount(StockUnit su, BigDecimal reservedAmount, String activityCode) throws InventoryException {
		if (reservedAmount.compareTo(new BigDecimal(0)) < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}

		if (reservedAmount.compareTo(su.getAmount()) <= 0) {
			log.info("GOING TO SET reservedamount of StockUnit " + su.toDescriptiveString() + " *** to *** " + reservedAmount);
			su.setReservedAmount(reservedAmount);
		} else {
			throw new InventoryException(InventoryExceptionKey.CANNOT_RESERVE_MORE_THAN_AVAILABLE, "" + su.getAvailableAmount());
		}
	}

	public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode) throws FacadeException {
		transferStockUnit(su, dest, activityCode, null, null);
	}
	public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode, String comment) throws FacadeException {
		transferStockUnit(su, dest, activityCode, null, null);
	}
	public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode, String comment, String operator) throws FacadeException {

		boolean destAllowed = false;
		Vector<Long> suIds = new Vector<Long>();

		if (testSuiable(su, dest)) {
			destAllowed = true;
			suIds.add(su.getId());
		} else {
			destAllowed = false;
		}

		if (destAllowed) {
			LOSUnitLoad old = (LOSUnitLoad) su.getUnitLoad();

			old.getStockUnitList().remove(su);

			// add to destination
			su.setUnitLoad(dest);
			dest.getStockUnitList().add(su);

			recordService.recordTransfer(su, old, dest, activityCode, comment, operator);

			switch (dest.getPackageType()) {
			case MIXED_CONSOLIDATE:
			case OF_SAME_ITEMDATA_CONSOLIDATE:
			case OF_SAME_LOT_CONSOLIDATE:
				log.info("Going to consolidate " + dest.getLabelId());
				consolidate(dest, activityCode);
				break;
			default:
				log.info("No consolidation for " + dest.getLabelId());
				break;
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), dest.getLabelId() });
		}
	}

	public void sendUnitLoadToNirwanaIfEmpty(LOSUnitLoad ul) throws FacadeException {
		ul = manager.merge(ul);
		if (ul.getStockUnitList() == null || ul.getStockUnitList().size() == 0) {
			log.info("A UnitLoad has become empty: " + ul.toDescriptiveString());
			try {
				UnitLoadType type = ulTypeQueryRemote.getPickLocationUnitLoadType();
				if (ul.getType().equals(type)) {
					log.debug("Skip: UnitLoad of type " + type.toUniqueString());
				} else {
					storage.sendToNirwana(contextService.getCallersUser().getName(), ul);
				}
			} catch (BusinessObjectNotFoundException ex) {
				log.error(ex.getMessage(), ex);
				return;
			}
		} else {
			log.debug("No UnitLoad has become empty");
		}

	}

	public boolean testSuiable(StockUnit su, LOSUnitLoad ul) {
		boolean ret;

		if (ul.isLocked()) {
			log.warn("UnitLoad is locked: " + ul.toDescriptiveString());
			ret = false;
		} else {
			ret = true;
		}

		LOSFixedLocationAssignment ass = fixAssComp.getAssignment(ul.getStorageLocation());
		if (ass != null && !ass.getItemData().equals(su.getItemData())) {
			log.warn("ItemData has fixed location assignment but itemdata of stockunit " + su.getItemData().getNumber() + " doesn't match: " + ass.getItemData().getNumber());
			return false;
		}

		switch (ul.getPackageType()) {
		case MIXED:
		case MIXED_CONSOLIDATE:
			ret = ret && true;
			break;
		case OF_SAME_ITEMDATA:
		case OF_SAME_ITEMDATA_CONSOLIDATE:
			ret = ret && testSameItemData(su, ul);
			break;
		case OF_SAME_LOT:
		case OF_SAME_LOT_CONSOLIDATE:
			ret = ret && testSameLot(su, ul);
			break;
		default:
			log.error("Unknown type: Assuming true for : " + ul.getPackageType());
			ret = ret && true;
		}

		return ret;

	}

	public boolean testSameItemData(StockUnit su, LOSUnitLoad ul) {

		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(su.getItemData())) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + su.getItemData().toUniqueString());
					ret = false;
					return ret;
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public boolean testSameItemData(ItemData idat, LOSUnitLoad ul) {

		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(idat)) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + idat.toUniqueString());
					ret = false;
					return ret;
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public boolean testSameLot(StockUnit su, LOSUnitLoad ul) {
		boolean ret = false;
		// ul = manager.find(LOSUnitLoad.class, ul.getId());
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(su.getItemData())) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + su.getItemData().toUniqueString());
					ret = false;
					return ret;
				}

				if (s.getLot() != null && su.getLot() != null) {
					if (s.getLot().equals(su.getLot())) {
						ret = true;
					} else {
						log.warn("testSameItemData: " + s.getLot().toUniqueString() + " != " + su.getLot().toUniqueString());
						ret = false;
						return ret;
					}
				}
			}
		} else {
			log.warn("found empty UnitLoad: " + ul.toDescriptiveString());
			ret = true;
		}

		return ret;
	}

	public BigDecimal getAmountOfUnitLoad(ItemData idat, LOSUnitLoad ul) throws InventoryException {
		BigDecimal amount = new BigDecimal(0);
		boolean contains = false;
		for (StockUnit su : ul.getStockUnitList()) {
			if (su.getItemData().equals(idat)) {
				amount = amount.add(su.getAvailableAmount());
				contains = true;
			}
		}
		if (!contains) {
			throw new InventoryException(InventoryExceptionKey.ITEMDATA_NOT_ON_UNITLOAD, new String[] { idat.getNumber(), ul.getLabelId() });
		}
		return amount;
	}

	public BigDecimal getAmountOfStorageLocation(ItemData idat, LOSStorageLocation sl) throws InventoryException {
		BigDecimal amount = new BigDecimal(0);

		for (LOSUnitLoad ul : sl.getUnitLoads()) {
			for (StockUnit su : ul.getStockUnitList()) {
				if (su.getItemData().equals(idat)) {
					amount = amount.add(su.getAvailableAmount());
				}
			}
		}

		return amount;
	}

	public boolean testSameLot(Lot lot, LOSUnitLoad ul) {
		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getLot() != null && lot != null) {
					if (s.getLot().equals(lot)) {
						ret = true;
					} else {
						ret = false;
						return ret;
					}
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public void processLotDates(Lot lot, Date bestBeforeEnd, Date useNotBefore) {

		lot = manager.merge(lot);

		Date today = DateHelper.endOfDay(new Date());

		if (useNotBefore != null && (lot.getUseNotBefore() == null || lot.getUseNotBefore().compareTo(useNotBefore) != 0)) {
			lot.setUseNotBefore(useNotBefore);
		}
		if (useNotBefore != null && lot.getUseNotBefore() != null && lot.getUseNotBefore().after(today)) {
			log.warn("Set Lot to LotLockState.LOT_TOO_YOUNG: " + lot.toDescriptiveString());
			lot.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
		}

		today = DateHelper.beginningOfDay(new Date());
		if (bestBeforeEnd != null && (lot.getBestBeforeEnd() == null || lot.getBestBeforeEnd().compareTo(bestBeforeEnd) != 0)) {
			lot.setBestBeforeEnd(bestBeforeEnd);
		}

		if (bestBeforeEnd != null && lot.getBestBeforeEnd() != null && lot.getBestBeforeEnd().before(today)) {
			log.warn("Set Lot to LotLockState.LOT_EXPIRED: " + lot.toDescriptiveString());
			lot.setLock(LotLockState.LOT_EXPIRED.getLock());
		}

		manager.flush();
	}

	// -------------------------------------------------------------------------
	// Sanity Checks
	// -------------------------------------------------------------------------
	public List<LogItem> sanityCheck() {

		ArrayList<LogItem> ret = new ArrayList<LogItem>();

		ret.addAll(sanityCheckStockUnit());
		ret.addAll(sanityCheckStockUnitToDelete());
		ret.addAll(sanityCheckOrder());
		ret.addAll(sanityCheckOrderReceipt());
		// ret.addAll(sanityCheckGoodsReceiptPosition());
		ret.addAll(sanityCheckAdviceReassign());
		return ret;
	}

	public List<LogItem> sanityCheckStockUnit() {

		ArrayList<LogItem> ret = new ArrayList<LogItem>();

		try {

			List<LOSUnitLoad> uls = ulQuery.queryAll(new QueryDetail(0, Integer.MAX_VALUE));
			int i = 0;
			for (LOSUnitLoad ul : uls) {
				i++;
				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				ul = manager.merge(ul);
				if (ul.getStockUnitList().size() > 0) {
					log.info("found UnitLoad with at least one StockUnit: " + ul.toDescriptiveString());
				} else if (ul.getStockUnitList().size() == 0) {
					log.warn("found UnitLoad with no StockUnit: " + ul.toDescriptiveString());
					continue;

				}
				int k = 0;
				for (StockUnit su : ul.getStockUnitList()) {
					k++;
					if (k % 30 == 0) {
						manager.flush();
						manager.clear();
					}
					if ((ul.getPackageType().equals(LOSUnitLoadPackageType.OF_SAME_LOT) || ul.getPackageType().equals(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE)) && su.getLot() != null
							&& !testSameLot(su.getLot(), ul)) {

						log.error("found UnitLoad with StockUnits of different lot: " + ul.toDescriptiveString());

					}
					if ((ul.getPackageType().equals(LOSUnitLoadPackageType.OF_SAME_ITEMDATA) || ul.getPackageType().equals(LOSUnitLoadPackageType.OF_SAME_ITEMDATA_CONSOLIDATE))
							&& !testSameItemData(su.getItemData(), ul)) {

						log.error("found UnitLoad with StockUnits of different lot: " + ul.toDescriptiveString());

					}

					if (su.getReservedAmount().compareTo(new BigDecimal(0)) > 0) {
						List<LOSPickRequestPosition> pickList = pickPosService.getByStockUnit(su);
						if (pickList.size() < 1) {
							log.error("found StockUnit with reservation but no pick request position " + su.toDescriptiveString());
						}
					}
				}
			}

		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	public List<LogItem> sanityCheckStockUnitToDelete() {
		ArrayList<LogItem> ret = new ArrayList<LogItem>();

		User user = contextService.getCallersUser();
		try {

			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", new Integer(100));
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_GREATER, "amount", BigDecimal.ZERO);

			TemplateQuery q = new TemplateQuery();
			q.setBoClass(StockUnit.class);
			q.addWhereToken(t);
			q.addWhereToken(t2);

			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);

			List<BODTO<StockUnit>> sus = suQuery.queryByTemplateHandles(d, q);

			if (sus.size() > 0) {
				log.warn("Found some locked StockUnits");
			} else {
				log.warn("Found NO locked StockUnits");
				return ret;
			}
			int i = 0;
			for (BODTO<StockUnit> su : sus) {

				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				StockUnit sunit = manager.find(StockUnit.class, su.getId());
				LOSUnitLoad ul = (LOSUnitLoad) sunit.getUnitLoad();
				// On Goods out location?

				if (ul.getStorageLocation().getArea() == null) {
					continue;
				}

				switch (ul.getStorageLocation().getArea().getAreaType()) {
				case GOODS_IN_OUT:
				case GOODS_OUT:
				case PRODUCTION:
					break;
				default:
					log.error("StockUnit with lock 100 but not on goods out location: " + sunit.toDescriptiveString());
					continue;
				}

				TemplateQueryWhereToken bySu = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "stockUnit", sunit);
				q = new TemplateQuery();
				q.addWhereToken(bySu);
				q.setBoClass(LOSPickRequestPosition.class);

				List<BODTO<LOSPickRequestPosition>> picks = pickPosQuery.queryByTemplateHandles(d, q);
				if (picks.size() < 1) {
					log.error("StockUnit with lock 100 and on goods out location but no pick request: " + sunit.toDescriptiveString());
					continue;
				}

				LOSPickRequestPosition pos = manager.find(LOSPickRequestPosition.class, picks.get(0).getId());

				if (pos.getPickedAmount().compareTo(sunit.getAmount()) != 0) {
					// OK
					log.info("pickrequest has differnt amount - skip: " + sunit.toDescriptiveString());
					continue;
				}

				if (pos.isSolved() || pos.isPicked() || pos.isCanceled()) {
					// OK
					log.error("SET to 0: " + sunit.toDescriptiveString());
					changeAmount(sunit, new BigDecimal(0), true, generatorService.generateManageInventoryNumber(user));
				} else {
					log.error("Nothing to to: " + pos.toDescriptiveString());
				}
			}

		} catch (Exception t) {
			log.error(t.getMessage(), t);
		}

		return ret;
	}

	public List<LogItem> sanityCheckOrder() {
		ArrayList<LogItem> ret = new ArrayList<LogItem>();

		List<BODTO<LOSOrderRequest>> orders;
		try {
			orders = orderQuery.queryZombies(new QueryDetail(0, Integer.MAX_VALUE));

			for (BODTO<LOSOrderRequest> dto : orders) {
				LOSOrderRequest o = manager.find(LOSOrderRequest.class, dto.getId());

				log.error("Found zombie order: " + o.toDescriptiveString());

			}
		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}

	public List<LogItem> sanityCheckOrderReceipt() {

		List<LogItem> ret = new ArrayList<LogItem>();

		try {
			TemplateQuery q;
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "destination", "");
			q = new TemplateQuery();
			q.setBoClass(OrderReceipt.class);
			q.addWhereToken(t);

			List<BODTO<OrderReceipt>> ors = orderReceiptQuery.queryByTemplateHandles(new QueryDetail(0, Integer.MAX_VALUE), q);

			int i = 0;
			for (BODTO<OrderReceipt> bto : ors) {
				i++;
				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				OrderReceipt rec = manager.find(OrderReceipt.class, bto.getId());
				if (!rec.getDestination().equals("")) {
					continue;
				}

				log.error("FOUND receipt with no destination " + rec.toDescriptiveString());
				t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "number", rec.getOrderNumber());
				q = new TemplateQuery();
				q.setBoClass(LOSOrderRequest.class);
				q.addWhereToken(t);
				LOSOrderRequest order = orderQuery.queryByTemplate(new QueryDetail(0, Integer.MAX_VALUE), q).get(0);
				order = manager.find(LOSOrderRequest.class, order.getId());

				rec.setDestination(order.getDestination().getName());

			}

		} catch (Exception t) {
			log.error(t.getMessage(), t);
		}
		return ret;
	}

	public List<LogItem> sanityCheckAdviceReassign() {
		List<LogItem> ret = new ArrayList<LogItem>();

		try {
			int i = 0;
			for (BODTO<LOSAdvice> ad : adviceQuery.queryAllHandles(new QueryDetail(0, Integer.MAX_VALUE))) {

				i++;
				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				LOSAdvice a = manager.find(LOSAdvice.class, ad.getId());
				BigDecimal old = a.getReceiptAmount();
				List<LOSGoodsReceiptPosition> list;
				list = a.getGrPositionList();

				a.setReceiptAmount(new BigDecimal(0));
				a.setGrPositionList(new ArrayList<LOSGoodsReceiptPosition>());

				for (LOSGoodsReceiptPosition pos : list) {
					pos = manager.find(pos.getClass(), pos.getId());
					a.addGrPos(pos);
				}

				BigDecimal newA = a.getReceiptAmount();

				if (newA.compareTo(old) != 0) {
					log.error("AMOUNT OF ADVICE CHANGED " + ad.toString() + "    ***   from " + old + " to " + newA);
				} else {
					log.info("NO CHANGE of amount for " + a.toDescriptiveString());
				}
			}

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return ret;
	}

	public List<LogItem> sanityCheckGoodsReceiptPosition() {
		List<LogItem> ret = new ArrayList<LogItem>();

		try {

			List<LOSGoodsReceiptPosition> list = grPosQueryRemote.queryAll(new QueryDetail(0, Integer.MAX_VALUE));
			int i = 0;
			for (LOSGoodsReceiptPosition pos : list) {
				i++;
				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				pos = manager.find(pos.getClass(), pos.getId());
				if (pos.getStockUnit() != null) {
					if (pos.getItemData() == null || !pos.getItemData().equals(pos.getStockUnit().getItemData().getNumber())) {

						log.error("GOING TO CORRECT ITEM DATA REFERENCE - expected " + pos.getStockUnit().getItemData().getNumber() + " but was: " + pos.getItemData());
						pos.setStockUnit(pos.getStockUnit());
					}

					if (pos.getStockUnit().getLot() != null && (pos.getLot() == null || !pos.getLot().equals(pos.getStockUnit().getLot().getName()))) {
						log.error("GOING TO CORRECT LOT REFERENCE - expected " + pos.getStockUnit().getLot().getName() + " but was: " + pos.getLot());
						pos.setStockUnit(pos.getStockUnit());
					}
				}

				if (pos.getStockUnitStr() == null) {
					StockUnit su = manager.find(StockUnit.class, pos.getStockUnit().getId());
					log.error("GOING TO CORRECT StockUnitStr - expected " + su.toDescriptiveString() + " but was null ");
					pos.setStockUnit(su);
				}

				if (pos.getAmount().compareTo(new BigDecimal(0)) == 0) {
					StockUnit su = manager.find(StockUnit.class, pos.getStockUnit().getId());
					log.error("FOUND receipt position with null amount ");
					List<LOSStockUnitRecord> suRecords = recService.getByStockUnitAndType(su, LOSStockUnitRecordType.STOCK_CREATED);
					if (suRecords.size() != 1) {
						log.error("NOT ONE CREATION RECORD FOUND - skip: " + pos.toDescriptiveString());
						continue;
					}

					pos.setAmount(suRecords.get(0).getAmount());
					log.info("CORRECTED null amount: " + pos.toDescriptiveString());
				}
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return ret;
	}

	public void cleanup() throws FacadeException {
		try {
			cleanupUnitLoads();
			cleanUpOrderRequest();
			cleanupStockUnitsOnNirwana();
		} catch (RuntimeException ex) {
			log.error(ex.getMessage(), ex);
			return;
		}
	}

	public void cleanupUnitLoads() throws FacadeException {

		List<LOSStorageLocation> sls = slService.getListByAreaType(getCallersClient(), LOSAreaType.GOODS_IN_OUT);
		sls.addAll(slService.getListByAreaType(getCallersClient(), LOSAreaType.GOODS_OUT));
		sls.addAll(slService.getListByAreaType(getCallersClient(), LOSAreaType.PRODUCTION));

		int i = 1;
		for (LOSStorageLocation sl : sls) {
			manager.flush();
			manager.clear();
			sl = manager.find(LOSStorageLocation.class, sl.getId());
			for (LOSUnitLoad ul : sl.getUnitLoads()) {
				if (i % 30 == 0) {
					manager.flush();
					manager.clear();
				}
				if (ulService.getNirwana().equals(ul))
					continue;
				ul = manager.find(LOSUnitLoad.class, ul.getId());
				List<Long> susIds = new ArrayList<Long>();
				for (StockUnit su : ul.getStockUnitList()) {
					su = manager.find(StockUnit.class, su.getId());
					if (!checkStockUnitDelete(su)) {
						log.warn("skip: " + su.toDescriptiveString());
						continue;
					}
					// delete Order
					// ... Later
					// delete PickRequests
					// ... Later
					// delete StockUnit
					// removeStockUnit(su, "CLS");
					susIds.add(su.getId());
				}

				for (Long id : susIds) {
					StockUnit su = manager.find(StockUnit.class, id);
					sendStockUnitsToNirwana(su, "CLS");
					manager.flush();
				}

				// delete UnitLoad
				if (ul.getStockUnitList().size() == 0) {
					TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "unitLoad", ul);
					TemplateQuery q = new TemplateQuery();
					q.addWhereToken(t);
					q.setBoClass(LOSStorageRequest.class);
					QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
					List<BODTO<LOSStorageRequest>> stors;
					stors = storageQuery.queryByTemplateHandles(d, q);
					for (BODTO<LOSStorageRequest> dto : stors) {
						LOSStorageRequest sr = manager.find(LOSStorageRequest.class, dto.getId());
						storageCrud.delete(sr);
						manager.flush();
					}

					LOSGoodsOutRequestPosition gOutPos;
					try {
						gOutPos = outPosService.getByUnitLoad(ul);
						LOSGoodsOutRequest oreq = gOutPos.getGoodsOutRequest();
						oreq = manager.find(LOSGoodsOutRequest.class, oreq.getId());
						oreq.getPositions().remove(oreq);
						outPosCRUD.delete(gOutPos);
						if (oreq.getPositions().isEmpty()) {
							outCRUD.delete(oreq);
						}
						manager.flush();
					} catch (EntityNotFoundException e) {
						log.error(e.getMessage(), e);
					}

					ulCrud.delete(ul);
					manager.flush();
					i++;
				}
				if (i > 250) {
					log.warn("******* STOP *** REACHED LIMIT OF 250 ************ ");
					throw new RuntimeException("******* STOP *** REACHED LIMIT OF 250 ************ ");
				}
			}
		}
	}

	public void cleanUpOrderRequest() throws FacadeException {

		TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSOrderRequestState.FINISHED);
		TemplateQuery q = new TemplateQuery();
		q.addWhereToken(t);
		q.setBoClass(LOSOrderRequest.class);
		QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
		List<BODTO<LOSOrderRequest>> reqs;

		reqs = orderQuery.queryByTemplateHandles(d, q);
		int i = 0;
		for (BODTO<LOSOrderRequest> to : reqs) {

			LOSOrderRequest req = manager.find(LOSOrderRequest.class, to.getId());
			if (!req.getOrderState().equals(LOSOrderRequestState.FINISHED)) {
				log.warn("wrong state: " + req.getOrderState());
			}

			List<Long> posIds = new ArrayList<Long>();

			for (LOSOrderRequestPosition p : req.getPositions()) {

				List<LOSPickRequestPosition> picks = pickPosService.getByOrderPosition(p);
				for (LOSPickRequestPosition pickPos : picks) {
					pickPos = manager.find(LOSPickRequestPosition.class, pickPos.getId());
					if (pickPos.isCanceled() || pickPos.isSolved()) {
						pickPosCrud.delete(pickPos);
						LOSPickRequest pickReq = pickPos.getPickRequest();
						pickReq = manager.find(LOSPickRequest.class, pickReq.getId());
						pickReq.getPositions().remove(pickPos);
						if (pickReq.getPositions().isEmpty()) {
							pickCrud.delete(pickReq);
						}
					} else {
						log.error("wrong state: " + pickPos.toDescriptiveString());
						break;
					}
				}
				posIds.add(p.getId());
			}

			for (Long id : posIds) {
				LOSOrderRequestPosition pos = manager.find(LOSOrderRequestPosition.class, id);
				orderReqPosCrud.delete(pos);
				req.getPositions().remove(pos);
			}

			orderReqCrud.delete(req);
			manager.flush();
			manager.clear();
			if (i++ > 250) {
				log.warn("******* STOP *** REACHED LIMIT OF 250 ************ ");
				throw new RuntimeException("******* STOP *** REACHED LIMIT OF 250 ************ ");
			}

		}
	}

	public void cleanupStockUnitsOnNirwana() throws FacadeException {

		LOSUnitLoad ul = ulService.getNirwana();
		ul = manager.find(LOSUnitLoad.class, ul.getId());
		List<Long> susIds = new ArrayList<Long>();
		for (StockUnit su : ul.getStockUnitList()) {
			if (!checkStockUnitDelete(su)) {
				log.warn("skip: " + su.toDescriptiveString());
				continue;
			}
			susIds.add(su.getId());
		}

		int i = 1;
		for (Long id : susIds) {
			StockUnit su = manager.find(StockUnit.class, id);
			removeStockUnit(su, "CLS", true);
			if (i % 30 == 0) {
				manager.flush();
				manager.clear();
			}

		}
	}

	private boolean checkStockUnitDelete(StockUnit su) {

		LOSUnitLoad ul;

		if (su.getAmount().compareTo(new BigDecimal(0)) != 0) {
			log.error("pickrequest has amount - skip: " + su.toDescriptiveString());
			return false;
		} else if (su.getLock() != BusinessObjectLockState.GOING_TO_DELETE.getLock() && su.getLock() != StockUnitLockState.PICKED_FOR_GOODSOUT.getLock()) {
			log.error("pickrequest has wrong lock - skip: " + su.toDescriptiveString());
			return false;
		}

		ul = manager.find(LOSUnitLoad.class, su.getUnitLoad().getId());

		if (ul.getStorageLocation().getArea() == null) {
			log.error("unit load is on storage location without area: " + ul.getStorageLocation().toDescriptiveString());
			return false;
		}

		switch (ul.getStorageLocation().getArea().getAreaType()) {
		case GOODS_IN_OUT:
		case GOODS_OUT:
		case PRODUCTION:
			break;
		default:
			log.error("StockUnit not on goods out location: " + su.toDescriptiveString());
			return false;
		}

		TemplateQueryWhereToken bySu = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "stockUnit", su);
		TemplateQuery q = new TemplateQuery();
		q.addWhereToken(bySu);
		q.setBoClass(LOSPickRequestPosition.class);
		QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);

		List<BODTO<LOSPickRequestPosition>> picks;
		try {
			picks = pickPosQuery.queryByTemplateHandles(d, q);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}

		for (BODTO<LOSPickRequestPosition> dto : picks) {
			LOSPickRequestPosition pos = manager.find(LOSPickRequestPosition.class, dto.getId());
			if (pos == null)
				continue;
			if (!(pos.isSolved() || pos.isCanceled())) {
				log.error("StockUnit has unsolved pick request position: " + pos.toDescriptiveString());
				return false;
			}
			LOSOrderRequest req = pos.getParentRequest().getParentRequest();
			req = manager.find(LOSOrderRequest.class, req.getId());
			switch (req.getOrderState()) {
			case FINISHED:
			case FAILED:
				// OK
				break;
			default:
				log.error("StockUnit has unfinished order: " + req.toDescriptiveString());
				return false;
			}

		}

		return true;
	}

	// ---------------------------------------------------------------------------------------

	public StockUnit createStockUnitOnStorageLocation(String clientRef, String slName, String articleRef, String lotRef, BigDecimal amount, String unitLoadRef, String activityCode, String serialNumber)
			throws EntityNotFoundException, InventoryException, FacadeException {

		Client c;
		LOSStorageLocation sl;
		Lot lot = null;
		StockUnit su;
		UnitLoad ul;
		Client sys;

		try {
			c = clientService.getByNumber(clientRef);
			sys = clientService.getSystemClient();

			if ((!getCallersClient().equals(c)) && (!getCallersClient().isSystemClient())) {
				throw new EJBAccessException();
			}

			sl = slService.getByName(c, slName);

			if (sl == null) {
				sl = slService.getByName(sys, slName);
			}

			if (sl == null) {
				log.warn("NOT FOUND. Going to CREATE StorageLocation " + slName);

				LOSStorageLocationType type;
				try {
					type = slTypeService.getDefaultStorageLocationType();
					if (type == null)
						throw new NullPointerException("No default location type found.");
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
				sl = slService.createStorageLocation(c, slName, type);
			}

			ItemData idat = itemDataService.getByItemNumber(c, articleRef);

			if (idat == null) {
				log.error("--- !!! NO ITEM WITH NUMBER " + articleRef + " !!! ---");
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
			}

			if ((lotRef != null && lotRef.length() > 0) || idat.isLotMandatory()) {
				try {
					lot = getOrCreateLot(c, lotRef, idat);
					ul = getOrCreateUnitLoad(c, idat, sl, unitLoadRef);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED, slName);
				}
			}
			try {
				ul = getOrCreateUnitLoad(c, idat, sl, unitLoadRef);
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED, slName);
			}

			su = createStock(c, lot, idat, amount, (LOSUnitLoad) ul, activityCode, serialNumber);

			consolidate((LOSUnitLoad) su.getUnitLoad(), activityCode);

			return su;

		} catch (FacadeException ex) {
			throw ex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ONSTOCK, "");
		}
	}

	protected Lot getOrCreateLot(Client c, String lotRef, ItemData idat) {
		Lot lot;
		if (lotRef != null && lotRef.length() != 0) {
			try {
				lot = lotService.getByNameAndItemData(c, lotRef, idat.getNumber());
				if (!lot.getItemData().equals(idat)) {
					throw new RuntimeException("ItemData does not match Lot");
				}
			} catch (EntityNotFoundException ex) {
				log.warn("CREATE Lot: " + ex.getMessage());
				lot = lotService.create(c, idat, lotRef, new Date(), null, null);
			}
		} else {
			throw new IllegalArgumentException("Missing orderRef");
		}
		return lot;
	}

	protected UnitLoad getOrCreateUnitLoad(Client c, ItemData idat, LOSStorageLocation sl, String ref) throws LOSLocationException {
		UnitLoad ul;
		UnitLoadType type;

		if (c == null)
			throw new NullPointerException("Client must not be null");
		if (idat == null)
			throw new NullPointerException("Article must not be null");
		if (sl == null)
			throw new NullPointerException("StorageLocation must not be null");
		if (ref == null)
			throw new NullPointerException("Reference must not be null");

		if (ref != null && ref.length() != 0) {
			try {
				ul = ulService.getByLabelId(c, ref);
			} catch (EntityNotFoundException ex) {
				try {
					log.warn("CREATE UnitLoad: " + ex.getMessage());

					type = idat.getDefaultUnitLoadType();
					if (type == null) {
						type = ulTypeService.getDefaultUnitLoadType();
					}
					if (type == null) {
						throw new RuntimeException("Cannot retrieve default UnitLoadType");
					}
					ul = ulService.createLOSUnitLoad(c, ref, type, sl);
				} catch (LOSLocationException lex) {
					throw lex;
				}
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}
		return ul;
	}

	public void deleteStockUnitsFromStorageLocation(LOSStorageLocation sl, String activityCode) throws FacadeException {

		List<Long> sus = new ArrayList<Long>();
		List<Long> uls = new ArrayList<Long>();

		sl = manager.find(LOSStorageLocation.class, sl.getId());

		for (LOSUnitLoad ul : (List<LOSUnitLoad>) sl.getUnitLoads()) {
			for (StockUnit su : ul.getStockUnitList()) {
				sus.add(su.getId());
				// su = manager.find(StockUnit.class, su.getId());
				// manager.remove(su);
			}
			uls.add(ul.getId());
		}

		for (Long id : sus) {
			StockUnit su = manager.find(StockUnit.class, id);
			if (su == null) {
				continue;
			}

			removeStockUnit(su, activityCode, true);
		}

		for (Long id : uls) {
			UnitLoad ul = manager.find(UnitLoad.class, id);
			if (ul == null) {
				continue;
			}
			manager.remove(ul);
		}
	}

	public void removeStockUnit(StockUnit su, String activityCode, boolean sendNotify) throws FacadeException {

		su = manager.find(StockUnit.class, su.getId());
		log.warn("Going to remove stock Unit: " + su.toDescriptiveString());
		BigDecimal amountOld = su.getAmount();
		su.setAmount(BigDecimal.ZERO);
		if (BigDecimal.ZERO.compareTo(amountOld) < 0) {
			recordService.recordRemoval(amountOld.negate(), su, activityCode);
			if( sendNotify ) {
				try{
					hostService.sendMsg( new HostMsgStock(su, amountOld.negate(), null, LOSStockUnitRecordType.STOCK_REMOVED, activityCode) );
				}
				catch( FacadeException e ) {
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
		}
		manager.remove(su);
		manager.flush();

	}

}
