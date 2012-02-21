/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.annotation.IgnoreDependency;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;
import org.mywms.globals.PickingRequestState;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnitType;
import org.mywms.model.Lot;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.Request;
import org.mywms.model.StockUnit;
import org.mywms.model.SubstitutionType;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.LotService;
import org.mywms.service.StockUnitService;
import org.mywms.service.UserService;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.businessservice.OrderBusiness;
import de.linogistix.los.inventory.customization.ManagePickService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrder;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestPositionState;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.pick.exception.NullAmountNoOtherException;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.exception.PickingExceptionKey;
import de.linogistix.los.inventory.pick.exception.PickingExpectedNullException;
import de.linogistix.los.inventory.pick.exception.PickingSubstitutionException;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.pick.model.LOSPickPropertyKey;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.inventory.pick.report.PickReport;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;
import de.linogistix.los.inventory.service.QueryStockService;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.businessservice.LOSFixedLocationAssignmentComp;
import de.linogistix.los.location.businessservice.LOSRackLocationComparatorByName;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 * 
 * @author trautm
 */
@Stateless
public class PickOrderBusinessBean implements PickOrderBusiness {

	Logger log = Logger.getLogger(PickOrderBusinessBean.class);
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	@EJB
	private LOSPickRequestService pickService;
	@EJB
	private LOSPickRequestPositionService pickPosService;
	@EJB
	private StockUnitService suService;
	@EJB
	private LOSStorageLocationService slService;
	@EJB
	private LotService lotService;
	@EJB
	private UserService userService;
	@EJB
	private LOSInventoryComponent invComp;
	@EJB
	private LOSStorageLocationTypeService locTypeService;
	@EJB
	private LOSUnitLoadService losUlService;
	@EJB
	private LOSStorage storage;
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private LOSFixedLocationAssignmentComp fixedAssignmentComp;
	@EJB
	private QueryStockService stockService;
	@EJB
	private ContextService contextService;
	@EJB
	private PickReport pickReport;
	@EJB
	private ReportService reportService;
	@EJB
	private LOSServicePropertyService serviceConfig;
	@EJB
	private LOSPickRequestQueryRemote pickReqQuery;
	@EJB
	private QueryUnitLoadTypeService ulTypeService;
	@EJB
	private ManagePickService managePickService;
	@EJB
	private ReportService repService;;
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private LOSStockUnitRecordService recordService;
	
	@EJB
	@IgnoreDependency
	OrderBusiness orderBusiness;
	

	// public static final String NUMBER_PREFIX = "PICK";
	public static final double OFFSET_FACTOR = 1.2;

	public void processPickWave() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<LOSPickRequest> processOrderRequest(LOSOrderRequest order)
			throws InventoryException, LOSLocationException {
		if( order == null ) {
			log.error("processOrderRequest order=null. Abort");
			return new ArrayList<LOSPickRequest>();
		}
		log.debug("processOrderRequest order=" + order.getNumber() );
		List<LOSPickRequest> ret;
		LOSPickRequest req;
		Client client;
		List<LOSPickRequestPosition> positions = new ArrayList<LOSPickRequestPosition>();

		ret = new ArrayList<LOSPickRequest>();

		client = order.getClient();
		String seq = genService.generatePickOrderNumber(client);
		if (seq == null) {
			throw new IllegalArgumentException("seq. number must not be null");
		}

		LOSStorageLocationType type;
		LOSStorageLocation sl;
		// LOSTypeCapacityConstraint capacity;

		type = locTypeService.getNoRestrictionType();
		if (type == null ) throw new RuntimeException("Type without restriction not found ");
		sl = slService.createStorageLocation(client, seq, type);

		manager.flush();

		log.debug("Going to create Pick Order in the name of "
				+ client.getNumber() + " with " + seq);

		try {
			req = pickService.createPickRequest(order, seq);
		} catch (FacadeException e) {
			log.error("Exception in createPickRequest: "+ e.getMessage());
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}
		req.setCart(sl);
		
		manager.flush();

		for (LOSOrderRequestPosition oPos : order.getPositions()) {
			List<LOSPickRequestPosition> l = processOrderPosition(req, oPos);
			positions.addAll(l);
			oPos.setPositionState(LOSOrderRequestPositionState.PROCESSING);
		}

		orderLOSPickRequestPositions(positions);
		ret.add(req);

		order.setOrderState(LOSOrderRequestState.PROCESSING);

//		logService.create(order.getClient(), "SERVER",
//				PickOrderBusinessBean.class.getSimpleName(), contextService
//						.getCallersUser().getName(), LogItemType.LOG,
//				"de.linogistix.los.imventory.res.Bundle",
//				"CREATED LOSPickRequest: " + req.getId(),
//				InventoryLogKeys.CREATED_PICKINGREQUEST.name(),
//				new Object[] { req.getId() });

		return ret;

	}

	public List<LOSPickRequest> processOrderRequest(LOSOrderRequest order, List<StockUnit> sus, BigDecimal amount)
			throws InventoryException, LOSLocationException {
		log.debug("processOrderRequest order=" + order.getNumber() + ", size=" + sus.size() + "; amount=" + amount);

		if (order.getPositions().size() != 1){
			throw new IllegalArgumentException("Intended for just one Order LOSOrderRequestPosition");
		}
		
		List<LOSPickRequest> ret;
		LOSPickRequest req;
		Client client;
		List<LOSPickRequestPosition> positions = new ArrayList<LOSPickRequestPosition>();

		ret = new ArrayList<LOSPickRequest>();

		client = order.getClient();
		String seq = null;
		if( order instanceof LOSReplenishRequest ) {
			seq = genService.generateReplenishNumber(client);
		}
		else {
			seq = genService.generatePickOrderNumber(client);
		}
		if (seq == null) {
			throw new IllegalArgumentException("seq. number must not be null");
		}

		LOSStorageLocationType type;
		LOSStorageLocation sl;

		type = locTypeService.getNoRestrictionType();
		if (type == null ) throw new RuntimeException("Type without restriction not found ");
		sl = slService.createStorageLocation(client, seq, type);

		manager.flush();

		log.debug("Going to create Pick Order in the name of "
				+ client.getNumber() + " with " + seq);

		try {
			req = pickService.createPickRequest(order, seq);
		} catch (FacadeException e) {
			log.error("Exception in createPickRequest: "+ e.getMessage());
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}
		req.setCart(sl);

		manager.flush();

		if( amount == null ) {
			log.info("Take complete available amount");
			amount = BigDecimal.valueOf(Double.MAX_VALUE);
		}
		BigDecimal amountRemain = amount;
		LOSOrderRequestPosition oPos = order.getPositions().get(0);
		for( StockUnit su : sus ) {
			LOSPickRequestPosition pick;
			List<StockUnit> oneSu = new ArrayList<StockUnit>();
			oneSu.add(su);
			
			BigDecimal amountPick = su.getAvailableAmount();
			if( amountPick.compareTo(amountRemain) > 0) {
				amountPick = amountRemain;
			}
			pick = createLOSPickRequestPositions(req, oneSu, oPos, amountPick).get(0);
			positions.add(pick);
			
			amountRemain = amountRemain.subtract(amountPick);
			if( BigDecimal.ZERO.compareTo(amountRemain) >= 0 ) {
				break;
			}
		}
		
		oPos.setPositionState(LOSOrderRequestPositionState.PROCESSING);
		orderLOSPickRequestPositions(positions);
		ret.add(req);

		order.setOrderState(LOSOrderRequestState.PROCESSING);


		return ret;

	}

	@SuppressWarnings("unchecked")
	public List<PickingRequestTO> getRawPickingRequest() {
		// Client c;
		User user = contextService.getCallersUser();
		Client c = user.getClient();
		c = manager.find(Client.class, c.getId());

		StringBuffer b = new StringBuffer();

		b.append("SELECT NEW ");
		b.append(PickingRequestTO.class.getName());
		b.append("(");

		b.append("o.id,");
		b.append("o.version,");
		b.append("o.number,");
		b.append("o.created,");
		b.append("o.client.number,");
		b.append("o.state,");
		b.append("o.parentRequestNumber,");
		b.append("o.parentRequest.parentRequestNumber");
		b.append(")");

		b.append(" FROM ");
		b.append(LOSPickRequest.class.getName());
		b.append(" o ");
		b.append(" WHERE ( o.state=:state ");
		b
				.append(" OR ( o.user=:user AND o.state IN (:stateAccepted, :statePocessing, :statePicked, :statePartial ) ) )");
		if (!c.isSystemClient()) {
			b.append(" AND o.client=:client ");
		}
		b.append("ORDER BY o.created ASC");

		Query query = manager.createQuery(new String(b));
		if (!c.isSystemClient()) {
			query.setParameter("client", c);
		}
		query.setParameter("state", PickingRequestState.RAW);
		query.setParameter("stateAccepted", PickingRequestState.ACCEPTED);
		query.setParameter("statePocessing", PickingRequestState.PICKING);
		query.setParameter("statePicked", PickingRequestState.PICKED);
		query.setParameter("statePartial", PickingRequestState.PICKED_PARTIAL);
		query.setParameter("user", user);

		return query.getResultList();

	}

	public void accept(LOSPickRequest request) throws InventoryException {
		log.debug("accept request=" + request.getNumber() );

		request = manager.find(request.getClass(), request.getId());
		User user;

		try {
			user = userService.getByUsername(contextService.getCallersUser().getName());
			if( !request.getState().equals(PickingRequestState.RAW) ) {
				if( !user.equals(request.getUser()) ) {
					log.error("Request " + request.getNumber() + " is already started by other user: " + request.getUser().getName());
					throw new InventoryException(InventoryExceptionKey.ORDER_CANNOT_BE_CREATED, request.getNumber());
				}
			}

			request.setUser(user);
			if (request.getState().equals(PickingRequestState.RAW)) {
				request.setState(PickingRequestState.ACCEPTED);
			} else {
				log.warn("state not RAW " + request);
			}

			sanityCheckPickRequest(request);

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.NOT_ACCEPTED,
					request.getNumber());
		}

	}

	public void sanityCheckPickRequest(LOSPickRequest request) throws InventoryException {
		// still ok?
//		boolean allCanceled = true;
//		boolean allSolved = true;
		for (LOSPickRequestPosition p : request.getPositions()) {
			
			if (p.isCanceled()) {
				log.warn("Position is already cancled: " + p.toShortString());
//				allCanceled = allCanceled && true;
				continue;
			} else if (p.isSolved()){
				log.warn("Position is already solved: " + p.toShortString());
//				allSolved = allSolved && true;
				continue;
			}
//			//TODO
//			allCanceled = false;
//			allSolved = false;
//			
			p = manager.find(p.getClass(), p.getId());
			switch (p.getParentRequest().getParentRequest().getOrderType()) {
			case TO_REPLENISH:
				if (p.getStockUnit().getAvailableAmount().compareTo(
						p.getAmount()) < 0) {
					log.error("PICKPOSITION FOR REPL need recalculation");
					if (p.getStockUnit().getAvailableAmount().compareTo(
							new BigDecimal(0)) > 0) {
						p.setAmount(p.getStockUnit().getAvailableAmount());
					} else {
						log
								.error("Stock unit does not have enough available amount: "
										+ p.getStockUnit()
												.toDescriptiveString());
						throw new InventoryException(
								InventoryExceptionKey.PICK_POSITION_CONTRAINT_VIOLATED,
								p.toUniqueString());
					}
				}
				break;
			default:
				// if (p.getStockUnit().isLocked()) {
				// log.error("Stock unit is locked: "
				// + p.getStockUnit().toDescriptiveString());
				// throw new InventoryException(
				// InventoryExceptionKey.STOCKUNIT_IS_LOCKED,
				// p.toUniqueString());
				// }
				if (p.getStockUnit().getReservedAmount().compareTo(p.getAmount()) < 0) {
//					log.error("Stock unit does not have enough reserved amount: "
//									+ p.getStockUnit().toDescriptiveString());
//					throw new InventoryException(
//							InventoryExceptionKey.PICK_POSITION_CONTRAINT_VIOLATED,
//							p.toUniqueString());
					// Do not cancel operation if some redundant information is a little bit corrupt
					log.warn("Stock unit does not have enough reserved amount: " + p.getStockUnit().toShortString());
				}
			}
		}

//		if (allCanceled) {
//			throw new InventoryException(
//					InventoryExceptionKey.PICK_POSITION_CONTRAINT_VIOLATED,
//					"All positions cancled - nothing to do");
//		}
//		if (allSolved) {
//			throw new InventoryException(
//					InventoryExceptionKey.PICK_POSITION_CONTRAINT_VIOLATED,
//					"All positions solved - nothing to do");
//		}
	}

	public LOSPickRequest finishCurrentUnitLoads(LOSPickRequest r,
			LOSStorageLocation transfer) throws FacadeException,
			PickingException, LOSLocationException, ReportException {
		log.debug("finishCurrentUnitLoads request=" + r.getNumber()+", location="+transfer.getName() );

		LOSStorageLocation from;
		LOSPickRequest req = manager.find(LOSPickRequest.class, r.getId());

		from = req.getCart();

		if (from == null) {
			throw new PickingException(PickingExceptionKey.NO_CART, (String)null);
		}


		List<LOSUnitLoad> uls = req.getCart().getUnitLoads();
		Vector<Long> uIds = new Vector<Long>();

		for (LOSUnitLoad u : uls) {
			uIds.add(u.getId());
		}
		for (Long id : uIds) {

			LOSUnitLoad u = manager.find(LOSUnitLoad.class, id);
			u = (LOSUnitLoad) BusinessObjectHelper.eagerRead(u);
			List<StockUnit> sus = new ArrayList<StockUnit>();
			sus.addAll(u.getStockUnitList());
			boolean hasStock = false;
			for( StockUnit su : u.getStockUnitList() ) {
				if( BigDecimal.ZERO.compareTo(su.getAmount()) < 0 ) {
					hasStock = true;
					break;
				}
			}
			if( ! hasStock ) {
				// no stock => trash
				log.info("finishCurrentUnitLoads: trash empty unit load");
				User user = contextService.getCallersUser();
				String userName = user != null ? user.getName() : "UNKNOWN";
				storage.sendToNirwana(userName, u);
				continue;
			}

			if (!transfer.equals(req.getDestination())) {
				throw new PickingException(PickingExceptionKey.WRONG_DESTINATION,
						req.getDestination().getName());
			}

			switch (getOrderType(req)) {
			case TO_REPLENISH:
				if (fixedAssignmentComp.isFixedLocation(transfer)) {
					putOnFixedAssigned(r, u, transfer);
				} else {
					putOnDestination(r, u, transfer);
				}
				for (StockUnit su : sus) {
					su = manager.find(StockUnit.class, su.getId());
					su.setLock(BusinessObjectLockState.NOT_LOCKED.getLock());
				}
				break;
			case TO_PRODUCTION:
			case TO_EXTINGUISH:
			case INTERNAL:
				putOnDestination(r, u, transfer);
				log.info("Create Receipt");
				createPickReceipt(req, u);
				createLabels(req, u);
				break;
			case TO_CUSTOMER:
			case TO_OTHER_SITE:
				putOnDestination(r, u, transfer);
				log.info("Create Receipt");
				createPickReceipt(req, u);
				createLabels(req, u);
				break;
			default:
				log.error("Unhandled type for " + req.toDescriptiveString());
			}
		}

		managePickService.finishCurrentUnitLoadsEnd(req);
		
		return req;
	}

	protected void putOnDestination(LOSPickRequest r, LOSUnitLoad u,
			LOSStorageLocation destination) throws InventoryException,
			FacadeException {
		User user = contextService.getCallersUser();
		String userName = user != null ? user.getName() : "!UNKNOWN!";

		try {
			storage.transferUnitLoad(userName, destination, u);
		} catch (LOSLocationException ex) {
			switch (ex.getLocationExceptionKey()) {
			case STORAGELOCATION_ALLREADY_FULL:
			case UNITLOAD_NOT_SUITABLE:
			case STORAGELOCATION_ALREADY_RESERVED_FOR_DIFFERENT_TYPE:
				// log.warn("Going to transfer stock and send Unitload to
				// nirwana: "
				// + u.getLabelId() + ". Error was: " + ex.getMessage());
				//				
				// LOSUnitLoad existing = destination.getUnitLoads().get(0);
				// existing = manager.find(LOSUnitLoad.class, existing.getId());
				//				
				// invComp.transferStock(u, existing);
				// storage.sendToNirwana(contextService.getCallersUser()
				// .getName(), u);
				// break;
			default:
				log.error(ex.getMessage(), ex);
				throw ex;
			}
		}
		
		managePickService.putOnDestinationEnd(r, u, destination);

	}

	// differs between FixedAssigned StorageLocations of virtual UL Type or
	// others
	protected void putOnFixedAssigned(LOSPickRequest r, LOSUnitLoad u,
			LOSStorageLocation destination) throws InventoryException,
			FacadeException {
		// To picking? (implicitly if virtual ul type)
		UnitLoadType virtual = ulTypeService.getPickLocationUnitLoadType();
		if (destination.getUnitLoads() != null
				&& destination.getUnitLoads().size() > 0) {
			LOSUnitLoad onDestination = destination.getUnitLoads().get(0);
			onDestination = manager.find(onDestination.getClass(),
					onDestination.getId());
			if (onDestination.getType().equals(virtual)) {
				invComp.transferStock(u, onDestination, r.getNumber());
				storage.sendToNirwana(
						contextService.getCallersUser().getName(), u);
				log.info("Transferred Stock to virtual UnitLoadType: "
						+ u.toShortString());
			} else {
				putOnDestination(r, u, destination);
			}
		} else {
			// throw new
			// InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, "");
			u.setType(virtual);
			putOnDestination(r, u, destination);
		}
	}

	protected void createPickReceipt(LOSPickRequest r, LOSUnitLoad u)
			throws ReportException {
		if( !managePickService.createPickReceipt(r, u) ) {
			log.info("No Receipt for PickRequest " + r.getNumber());
			return;
		}

		PickReceipt receipt;

		receipt = pickReport.generatePickReceipt(r.getClient(),
				DocumentTypes.APPLICATION_PDF.toString(), u,
				containingPositions(u, r));

		if (receipt != null) {
			try {
				String printer = null;
				try {
					printer = serviceConfig.getValue(PickOrderBusiness.class, r
							.getClient(), PickOrderBusiness.CONFKEY_PRINTER);
				} catch (EntityNotFoundException e) {
					serviceConfig.create(PickOrderBusiness.class, r.getClient(), PickOrderBusiness.CONFKEY_PRINTER,
							ReportService.NO_PRINTER);
					log.error(e.getMessage(), e);
					printer = ReportService.NO_PRINTER;
				}
				reportService.print(printer, receipt.getDocument(),
						DocumentTypes.APPLICATION_PDF.toString());
			} catch (ReportException rex) {
				log.error(rex.getMessage(), rex);
//				logService.create(r.getClient(), "SERVER",
//						PickOrderBusinessBean.class.getSimpleName(),
//						contextService.getCallersUser().getName(),
//						LogItemType.ERROR,
//						"de.linogistix.los.imventory.pick.res.Bundle",
//						"NOT PRINTED " + receipt.getName(),
//						PickingExceptionKey.PRINT_LABEL_FAILED.name(),
//						new Object[] { receipt.getName() });
			}
		}
	}

	protected List<LOSPickRequestPosition> containingPositions(LOSUnitLoad ul,
			LOSPickRequest r) {

		List<LOSPickRequestPosition> ret = new ArrayList<LOSPickRequestPosition>();

		for (LOSPickRequestPosition pos : r.getPositions()) {
			if (pos.getStockUnit().getUnitLoad().equals(ul)) {
				ret.add(pos);
			}
		}
		return ret;
	}

	/**
	 * Sets {@link PickingRequestState} to {@link PickingRequestState#PICKED} or
	 * {@link PickingRequestState#PICKED_PARTIAL} or
	 * {@link PickingRequestState#PICKING} or {@link PickingRequestState#FAILED}
	 * depending on the positions.
	 * 
	 * @param request
	 */
	protected void setRequestStateAfterPicking(LOSPickRequest request) {
		int countFinished = 0;
		int countPending = 0;
		int countCanceled = 0;
		int countRaw = 0;

		List<LOSPickRequestPosition> canceled = new ArrayList<LOSPickRequestPosition>();

		for (LOSPickRequestPosition pos : request.getPositions()) {
			if (pos.isPicked()) {
				countFinished++;
			} else if (pos.isCanceled()) {
				countCanceled++;
				canceled.add(pos);
			} else if (pos.getPickedAmount().compareTo(new BigDecimal(0)) > 0) {
				countPending++;
			} else {
				countRaw++;
			}
		}

		// RAW
		if (countFinished == 0 && countCanceled == 0 && countPending == 0
				&& countRaw > 0) {
			request.setState(PickingRequestState.RAW);
			return;
		}
		if (countFinished == 0 && countCanceled > 0 && countPending == 0
				&& countRaw > 0) {
			request.setState(PickingRequestState.RAW);
			return;
		}
		// PICKING
		if (countFinished == 0 && countCanceled == 0 && countPending == 0
				&& countRaw > 0) {
			request.setState(PickingRequestState.PICKING);
			return;
		}
		if (countFinished == 0 && countCanceled == 0 && countPending > 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.PICKING);
			return;
		}
		if (countFinished == 0 && countCanceled == 0 && countPending > 0
				&& countRaw > 0) {
			request.setState(PickingRequestState.PICKING);
			return;
		}
		if (countFinished == 0 && countCanceled > 0 && countPending > 0
				&& countRaw > 0) {
			request.setState(PickingRequestState.PICKING);
			return;
		}
		if (countFinished == 0 && countCanceled > 0 && countPending > 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.PICKING);
			return;
		}

		// PICKED
		if (countFinished > 0 && countCanceled == 0 && countPending == 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.PICKED);
			return;
		}

		// PICKED or PICKED_PARTIAL
		if (countFinished > 0 && countCanceled > 0 && countPending == 0
				&& countRaw == 0) {
			// One of the finished healing the canceled???
			for (LOSPickRequestPosition canceledPos : canceled) {
				for (LOSPickRequestPosition complement : canceledPos
						.getComplements()) {
					if (complement.isSolved()) {
						request.setState(PickingRequestState.PICKED);
					} else {
						request.setState(PickingRequestState.PICKED_PARTIAL);
						break;
					}
				}
				if (request.getState().equals(
						PickingRequestState.PICKED_PARTIAL))
					break;
			}
			if (!request.getState().equals(PickingRequestState.PICKED)) {
				request.setState(PickingRequestState.PICKED_PARTIAL);
			}
			return;
		}

		// FAILED
		if (countFinished == 0 && countCanceled > 0 && countPending == 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.FAILED);
			return;
		}

		// PICKED_PARTIAL
		request.setState(PickingRequestState.PICKED_PARTIAL);

	}

	/**
	 * Sets {@link PickingRequestState} to {@link PickingRequestState#FINISHED}
	 * or {@link PickingRequestState#FINISHED_PARTIAL} or
	 * {@link PickingRequestState#FAILED} depending on the positions.
	 * 
	 * @param request
	 */
	protected void setRequestStateAfterFinish(LOSPickRequest request) {
		int countFinished = 0;
		int countPending = 0;
		int countCanceled = 0;
		int countRaw = 0;

		List<LOSPickRequestPosition> canceled = new ArrayList<LOSPickRequestPosition>();

		for (LOSPickRequestPosition pos : request.getPositions()) {
			if (pos.isSolved()) {
				countFinished++;
			} else if (pos.isCanceled()) {
				countCanceled++;
				canceled.add(pos);
			} else if (pos.getPickedAmount().compareTo(new BigDecimal(0)) > 0) {
				countPending++;
			} else {
				throw new RuntimeException("There are raw postions");
			}
		}

		// FINISHED
		if (countFinished > 0 && countCanceled == 0 && countPending == 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.FINISHED);
			return;
		}

		// FINISHED or FINISHED_PARTIAL
		if (countFinished > 0 && countCanceled > 0 && countPending == 0
				&& countRaw == 0) {
			// One of the finished healing the canceled???
			for (LOSPickRequestPosition canceledPos : canceled) {
				for (LOSPickRequestPosition complement : canceledPos
						.getComplements()) {
					if (complement.isSolved()) {
						request.setState(PickingRequestState.FINISHED);
					} else {
						request.setState(PickingRequestState.FINISHED_PARTIAL);
						break;
					}
				}
				if (request.getState().equals(
						PickingRequestState.FINISHED_PARTIAL))
					break;
			}
			if (!request.getState().equals(PickingRequestState.FINISHED)) {
				request.setState(PickingRequestState.FINISHED_PARTIAL);
			}
			return;
		}

		// FAILED
		if (countFinished == 0 && countCanceled > 0 && countPending == 0
				&& countRaw == 0) {
			request.setState(PickingRequestState.FAILED);
			return;
		}

		// PICKED_PARTIAL
		request.setState(PickingRequestState.FINISHED_PARTIAL);
	}

	public void finish(LOSPickRequest request, boolean force)
			throws InventoryException, PickingException {
		log.debug("finish. pick request=" + request.getNumber()+", force="+force );

		ReportException reportException = null;
		LOSPickRequest r = (LOSPickRequest) request;

		try {

			for (LOSPickRequestPosition pos : request.getPositions()) {
				pos = manager.find(pos.getClass(), pos.getId());
				if (pos.isSolved()) {
					finishOrderPosition(pos);
				} else if (pos.isCanceled()) {
					finishOrderPosition(pos);
				} else if (pos.isPicked()) {
					if (force) {
						pos.setCanceled(true);
						finishOrderPosition(pos);
					} else {
						finishOrderPosition(pos);
					}
				} else {
					if (force) {
						
						finishPickRequestPosition(r, pos, pos.getAmount(),
								false, false, false);
						pos = manager.find(LOSPickRequestPosition.class, pos
								.getId());
						if (!pos.isSolved()) {
							throw new PickingException(
									PickingExceptionKey.UNFINISHED_POSITIONS,
									(String)null);
						}
						finishOrderPosition(pos);
					} else {
						throw new PickingException(
								PickingExceptionKey.UNFINISHED_POSITIONS, (String)null);
					}
				}

				if (r.getCart().getUnitLoads().size() > 0) {
					try {
						finishCurrentUnitLoads(r, r.getDestination());
					} catch (ReportException ex) {
						log.error(ex.getMessage(), ex);
						reportException = ex;
					}
				}
			}

			setRequestStateAfterFinish(request);


			if( request.getState() == PickingRequestState.FINISHED || request.getState() == PickingRequestState.FINISHED_PARTIAL || request.getState() == PickingRequestState.FAILED ) {
				// Try to eliminate the cart-location
				LOSStorageLocation cart = request.getCart();
				request.setCart(null);
				if( cart != null ) {
					if( cart.getUnitLoads().size() == 0 ) {
						releaseCart(cart);
					}
				}
				
				managePickService.finishEnd(request);
			}
			
			log.info("finish. done. pick request=" + request.getNumber());

			if (reportException != null) {
				throw reportException;
			}

		} catch (PickingException e) {
			throw e;
		} catch (InventoryException ex) {
			throw ex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.NOT_ACCEPTED,
					request.getNumber());
		}
	}

	protected void finishPickRequestPosition(LOSPickRequest r,
											 LOSPickRequestPosition pos, 
											 BigDecimal amount,
											 boolean unexpectedNull,
											 boolean takeWholeUnitLoad,
											 boolean stockEmptyConfirmed) 
		throws FacadeException 
	{
		log.debug("finishPickRequestPosition: user="+contextService.getCallersUser().getName()+", pos="+pos.getParentRequest().getNumber()+", amount="+amount+", unexpectedNull="+unexpectedNull+", wholeUnitLoad="+takeWholeUnitLoad);
		
		LOSUnitLoad to;
		LOSUnitLoad from;
		
		StockUnit su = manager.find(pos.getStockUnit().getClass(), pos
				.getStockUnit().getId());
		
		if (su.isLocked()){
			log.warn("is locked: " + su.toDescriptiveString());
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, "" + su.getLock());
		}
		
		UnitLoadType virtual = null;

		virtual = ulTypeService.getPickLocationUnitLoadType();
		if (virtual == null) log.warn("No DUMMY_KOMM_ULTYPE_NAME found");
		
		if (pos.isSolved()) {
			log.warn("Already solved: " + pos.toShortString());
			return;
		} else if (pos.isPicked()) {
			log.warn("Already picked but not solved: "
					+ pos.toShortString());
		} else if (pos.isCanceled()) {
			log.warn("Was canceled: " + pos.toShortString());
		}

		from = manager.find(LOSUnitLoad.class, pos.getStockUnit().getUnitLoad().getId());

		if ( takeWholeUnitLoad && (!from.getType().equals(virtual)) ) {

			if (from.getStockUnitList().size() > 1) {
				log.warn("unexpected null: " + pos.toShortString());
				throw new PickingException( PickingExceptionKey.UNITLOAD_HAS_MORE_STOCKS, "" );
			}
			
			if (su.getAmount().compareTo(amount) > 0 && !unexpectedNull) {
				log.warn("unexpected null: " + pos.toShortString());
				throw new PickingException( PickingExceptionKey.STOCK_HAS_MORE_AMOUNT, "" );
			}

			if (su.getAmount().compareTo(amount) < 0) {
				log.warn("unsufficient amount: " + pos.toShortString() + " *** on *** " + su.toShortString());
				throw new InventoryException(
						InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
						new Object[] { "" + su.getAmount(),
								pos.getItemData().toUniqueString() });
			}

			finishPickRequestPositionTakeUnitLoad(r, pos, amount, unexpectedNull, su, from, stockEmptyConfirmed);

			return;
		}

		to = determinePickToUnitLoad(r, pos, su);

		if (amount.compareTo(pos.getAmount()) == 0) {
			if (su.getAmount().compareTo(amount) > 0) {
				finishPickRequestPositionTakeFromStockUnit(r, pos, amount,
						unexpectedNull, su, to);
			} else if (su.getAmount().compareTo(amount) == 0) {
				finishPickRequestPositionEmptyFromStockUnit(r, pos, amount,
						unexpectedNull, su, to, stockEmptyConfirmed);
			} else {
				throw new InventoryException(
						InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
						new Object[] { "" + su.getAmount(),
								pos.getItemData().toUniqueString() });
			}
		} else if (amount.compareTo(pos.getAmount()) < 0) {
			if (unexpectedNull) {
				finishPickRequestPositionUnexpectedNull(r, pos, amount,
						unexpectedNull, su, to, stockEmptyConfirmed);
			} else {
				throw new PickingException(
						PickingExceptionKey.PICK_UNEXPECTED_NULL, "");
			}
		} else {
			throw new PickingException(PickingExceptionKey.PICK_WRONG_AMOUNT, ""+pos.getDisplayAmount());
		}
	}

	private LOSUnitLoad determinePickToUnitLoad(LOSPickRequest r, LOSPickRequestPosition pos, StockUnit su) {

		LOSUnitLoad to;

		for (LOSUnitLoad ul : r.getCart().getUnitLoads()) {
			if (invComp.testSuiable(su, ul)) {
				to = ul;
				return to;
			}
		}

		// create new
		UnitLoadType t;
		try {
			t = ulTypeService.getDefaultUnitLoadType();
			if (t == null) {
				throw new RuntimeException(
						"Default type could not be found");
			}
			
        	// do not use the label twice
        	String labelId = null;
        	int i = 0;
        	while( i++ < 100 ) {
    			labelId = genService.generateUnitLoadLabelId(r.getClient(), t);
        		try {
					losUlService.getByLabelId(r.getClient(), labelId);
				} catch (EntityNotFoundException e) {
					break;
				}
        		log.info("UnitLoadLabel " + labelId + " already exists. Try the next");
        	}

			to = storage.createUnitLoad(r.getClient(), labelId, t, r.getCart(), r.getNumber());
			to.setPackageType(LOSUnitLoadPackageType.MIXED);
//			
//			to = losUlService.createLOSUnitLoad(r.getClient(), r.getNumber()
//					+ "/" + pos.getIndex(), t, r.getCart());
			return to;
		} catch (LOSLocationException lex) {
			throw new RuntimeException("Cannot create on StorageLocation "
					+ r.getCart().toUniqueString());
		}
	}

	protected void finishPickRequestPositionUnexpectedNull(LOSPickRequest r,
			LOSPickRequestPosition pos, BigDecimal amount,
			boolean unexpectedNull, StockUnit su, LOSUnitLoad to, boolean stockEmptyConfirmed)
			throws FacadeException {
		log.debug("finishPickRequestPositionUnexpectedNull: user="+contextService.getCallersUser().getName()+", pos="+pos.getParentRequest().getNumber()+", amount="+amount+ ", stockUnit=" + su.toShortString());

		// su.setReservedAmount(0);
		LOSUnitLoad from = (LOSUnitLoad) su.getUnitLoad();
		LOSStorageLocation sourceLoc = from.getStorageLocation();
		invComp.changeAmount(su, amount, true, r.getNumber());
		invComp.transferStockUnit(su, to, r.getNumber());
		invComp.sendUnitLoadToNirwanaIfEmpty(from);

		pos.setCanceled(pos.getAmount().compareTo(amount)>0);
		pos.setPicked(amount, su);
		OrderType t = getOrderType(r);
		switch (t) {
		case INTERNAL:
		case TO_EXTINGUISH:
		case TO_PRODUCTION:
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			su.setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
			su.releaseReservedAmount(amount);
			break;
		case TO_REPLENISH:
			break;
		default:
			log.error("unknown state: " + t.name());
			break;
		}


		if( stockEmptyConfirmed ) {
			if( sourceLoc.getUnitLoads().size() == 0 ) {
				setStocktakingDone( sourceLoc, null, null, r.getNumber() );
			}
		}

		List<LOSPickRequestPosition> affected = null;
		try {
			// do this here to prevent having newly created positions in the
			// next query
			affected = pickPosService.getByStockUnit(su);
			createLOSPickRequestPositionAfterNull(r, pos, su, pos.getAmount().subtract(amount));
			setRequestStateAfterPicking(r);
		} catch (NullAmountNoOtherException ex) {
			log.error(ex.getMessage());
			setRequestStateAfterPicking(r);
			throw ex;
		} finally {

			// TODO: Separate via Event (JMS?)
			if (affected != null) {
				for (LOSPickRequestPosition p : affected) {
					if (p.equals(pos))
						continue;
					p = manager.find(p.getClass(), p.getId());
					p.setCanceled(true);
					p.setPicked(new BigDecimal(0), su);
					LOSPickRequestPosition affectedPos = (LOSPickRequestPosition) p;
					LOSPickRequest affectedRequest = affectedPos
							.getPickRequest();
					try {
						log
								.warn("Recalculate a new pick position to substitute "
										+ affectedPos.toDescriptiveString());
						createLOSPickRequestPositionAfterNull(affectedRequest,
								affectedPos, su, p.getAmount());
						setRequestStateAfterPicking(affectedRequest);
					} catch (NullAmountNoOtherException ex) {
						log.error(ex.getMessage(), ex);
						setRequestStateAfterPicking(affectedRequest);
						continue;
					}
				}
			}
		}
	}

	protected void finishPickRequestPositionEmptyFromStockUnit(
			LOSPickRequest r, LOSPickRequestPosition pos, BigDecimal amount,
			boolean unexpectedNull, StockUnit su, LOSUnitLoad to, boolean stockEmptyConfirmed)
			throws InventoryException, FacadeException {
		// will become empty
		LOSUnitLoad from = (LOSUnitLoad) su.getUnitLoad();
		LOSStorageLocation sourceLoc = from.getStorageLocation();
		
		log.debug("finishPickRequestPositionEmptyFromStockUnit (unitload will become empty): user="+contextService.getCallersUser().getName()+
				", pos="+pos.getParentRequest().getNumber()+" *** transfer " + su.toShortString() + "*** on " + from.toShortString() + " *** to " + to.toShortString());
		
		invComp.transferStockUnit(su, to, r.getNumber());
		invComp.sendUnitLoadToNirwanaIfEmpty(from);
		pos.setPicked(amount, su);
		OrderType t = getOrderType(r);
		switch (t) {
		case INTERNAL:
		case TO_EXTINGUISH:
		case TO_PRODUCTION:
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			su.setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
			su.releaseReservedAmount(amount);
			break;
		case TO_REPLENISH:
			break;
		default:
			log.error("unknown state: " + t.name());
			break;
		}

		if( stockEmptyConfirmed ) {
			if( sourceLoc.getUnitLoads().size() == 0 ) {
				setStocktakingDone( sourceLoc, null, null, r.getNumber() );
			}
		}
		
		setRequestStateAfterPicking(r);

	}

	/**
	 * Creates a new StockUnit on Pickpallet and transfers Stock.
	 * 
	 * @param r
	 * @param pos
	 * @param amount
	 * @param unexpectedNull
	 * @param su
	 * @param from
	 * @throws FacadeException
	 * @throws InventoryException
	 */
	protected void finishPickRequestPositionTakeFromStockUnit(LOSPickRequest r,
			LOSPickRequestPosition pos, BigDecimal amount,
			boolean unexpectedNull, StockUnit su, LOSUnitLoad to)
			throws InventoryException, FacadeException {

		StockUnit toSu;
		OrderType t = getOrderType(r);
		switch (t) {
		case INTERNAL:
		case TO_EXTINGUISH:
		case TO_PRODUCTION:
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			su.releaseReservedAmount(amount);
			toSu = invComp.splitStock(su, to, amount, r.getNumber());
			toSu.setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
			pos.setPicked(amount, toSu);
			log.info("finishPickRequestPositionTakeFromStockUnit: user="+contextService.getCallersUser().toShortString() + " pos=" + pos.toShortString() 
					+ ", source=" + su.toShortString() + ", target=" + toSu.toShortString() + ", " + to.toShortString());
			break;
		case TO_REPLENISH:
			// su.releaseReservedAmount(amount);
			toSu = invComp.splitStock(su, to, amount, r.getNumber());
			pos.setPicked(amount, toSu);
			log.info("finishPickRequestPositionTakeFromStockUnit: user="+contextService.getCallersUser().toShortString() + ", pos=" + pos.toShortString() 
					+ ", source=" + su.toShortString() + ", target=" + toSu.toShortString() + ", " + to.toShortString());
			break;
		default:
			log.error("finishPickRequestPositionTakeFromStockUnit: unknown state: " + t.name());
			break;
		}
		

		setRequestStateAfterPicking(r);
	}

	protected void finishPickRequestPositionTakeUnitLoad(LOSPickRequest r,
			LOSPickRequestPosition pos, BigDecimal amount,
			boolean unexpectedNull, StockUnit su, LOSUnitLoad from, boolean stockEmptyConfirmed)
			throws LOSLocationException, FacadeException {
		log.debug("finishPickRequestPositionTakeUnitLoad: user="+contextService.getCallersUser().getName()+", pos="+pos.getParentRequest().getNumber()+", amount="+amount+", unexpectedNull="+unexpectedNull);

		LOSStorageLocation sourceLoc = from.getStorageLocation();

		if (r.getCart().getUnitLoads().size() > 0) {
			log.warn("Cart has already one ore more UnitLoads "
					+ r.getCart().toShortString());
		}


		if (unexpectedNull) {
			try {
				finishPickRequestPositionUnexpectedNull(r, pos, amount, unexpectedNull, su, (LOSUnitLoad) su.getUnitLoad(), stockEmptyConfirmed);
			}
			catch( NullAmountNoOtherException e ) {
				// IGNORE
			}
		} else {
			OrderType t = getOrderType(r);
			switch (t) {
			case INTERNAL:
			case TO_EXTINGUISH:
			case TO_PRODUCTION:
			case TO_CUSTOMER:
			case TO_OTHER_SITE:
				pos.setPicked(amount, su);
				su.setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
				su.releaseReservedAmount(amount);
				log.info(contextService.getCallersUser().toShortString() + " finish (take unit load): " + pos.toShortString() 
						+ " *** take " + su.toShortString() + "*** on " + from.toShortString());
				break;
			case TO_REPLENISH:
				pos.setPicked(amount, su);
				log.info(contextService.getCallersUser().toShortString() + " finish (take unit load) for replenish: " + pos.toShortString() 
						+ " *** take " + su.toShortString() + "*** on " + from.toShortString());
				break;
			default:
				log.error("unknown state: " + t.name());
				break;
			}
		}

		storage.transferUnitLoad(contextService.getCallersUser().getName(), r.getCart(), from);
		
		
		if( stockEmptyConfirmed ) {
			if( sourceLoc.getUnitLoads().size() == 0 ) {
				setStocktakingDone( sourceLoc, null, null, r.getNumber() );
			}
		}

		setRequestStateAfterPicking(r);
	}

	private void createLOSPickRequestPositionAfterNull(LOSPickRequest r,
			LOSPickRequestPosition pos, StockUnit su, BigDecimal missingAmount)
			throws NullAmountNoOtherException {
		log.debug("createLOSPickRequestPositionAfterNull: user="+contextService.getCallersUser().getName()+", pos="+pos.getParentRequest().getNumber()+", missingAmount="+missingAmount+ ", stockUnit=" + su.toShortString());
		if( missingAmount == null || BigDecimal.ZERO.compareTo(missingAmount) >= 0 ) {
			log.debug("createLOSPickRequestPositionAfterNull: Do not search for zero amount");
			return;
		}
		
		LOSOrderRequestPosition orderPos = manager.find(
				LOSOrderRequestPosition.class, pos.getParentRequest().getId());
		List<StockUnit> sus;

		if (orderPos.getLot() != null) {
			sus = suitableStockUnitsByLotAndAmountNoException(
					orderPos.getLot(), missingAmount);
		} else {
			try {
				sus = suitableStockUnitsByItemData(orderPos, missingAmount);
			} catch (InventoryException e) {
				log.error(e.getMessage());
				throw new NullAmountNoOtherException(e.getInventoryExceptionKey(), new BigDecimal(-99));
			} catch (LOSLocationException e) {
				log.error(e.getMessage());
				throw new NullAmountNoOtherException(InventoryExceptionKey.STORAGELOCATION_CONSTRAINT_VIOLATED, new BigDecimal(-99));
			}
//			throw new UnsupportedOperationException(
//					"Not configured for substitution of lots");
		}

		List<LOSPickRequestPosition> positions = createLOSPickRequestPositions(
				r, sus, orderPos, missingAmount);

		for (LOSPickRequestPosition p : positions) {
			p = manager.find(p.getClass(), p.getId());
			p.setComplementOn(pos);
		}

		orderLOSPickRequestPositions(r.getPositions());

	}
	

	// -------------------------------------------------------------------------
	protected List<LOSPickRequestPosition> processOrderPosition(
			LOSPickRequest req, LOSOrderRequestPosition pos)
			throws InventoryException, LOSLocationException {
		log.debug("processOrderPosition START (" + pos.getNumber() + ")");
		// List<LOSPickRequestPosition> ret = new
		// ArrayList<LOSPickRequestPosition>();
		List<StockUnit> sus;

		if (pos.getItemData().isLocked()) {
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_ISLOCKED, pos.getItemData()
							.getName());
		}

		if (pos.getLot() != null) {
			if (pos.getLot().isLocked()) {

				switch (pos.getParentRequest().getOrderType()) {
				case INTERNAL:
					sus = suitableStockUnitsByLot(pos);
					return createLOSPickRequestPositions(req, sus, pos,
							new BigDecimal(-1));
				default: {
					switch (pos.getItemData().getLotSubstitutionType()) {
					case ANY:
						sus = suitableStockUnitsByItemData(pos, pos.getAmount());
						return createLOSPickRequestPositions(req, sus, pos,
								new BigDecimal(-1));
					case NOT_ALLOWED:
					case NEWER:
					default:
						throw new InventoryException(
								InventoryExceptionKey.LOT_ISLOCKED, pos
										.getLot().getName());
					}
				}
				}
			} else {
				sus = suitableStockUnitsByLot(pos);
				return createLOSPickRequestPositions(req, sus, pos,
						new BigDecimal(-1));
			}

		} else {

			sus = suitableStockUnitsByItemData(pos, pos.getAmount());
			return createLOSPickRequestPositions(req, sus, pos, new BigDecimal(
					-1));
		}
	}

	public List<StockUnit> suitableStockUnitsByLot(
			LOSOrderRequestPosition pos) throws InventoryException,
			LOSLocationException {
		List<StockUnit> list = suitableStockUnitsByLotAndAmount(pos.getLot(),
				pos.getAmount());
		List<StockUnit> suitable = new ArrayList<StockUnit>();

		LOSOrderRequest r = (LOSOrderRequest) pos.getParentRequest();
		if (r instanceof LOSReplenishRequest) {
			// don't take from pick area
			for (StockUnit su : list) {
				LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
				LOSStorageLocation sl = ul.getStorageLocation();
				if (sl.getArea() != null) {
					switch (sl.getArea().getAreaType()) {
					case STORE:
					case REPLENISHMENT:
						log.debug("suitable for replenish "
								+ su.toUniqueString());
						suitable.add(su);
						break;
					case GOODS_IN:
					case PICKING:
					case GOODS_OUT:
					case GENERIC:
					case QUALITY_ASSURANCE:
					case TOLL:
					default:
						log.warn("not suitable for replenish "
								+ su.toUniqueString());
						continue;
					}
				}

			}
		} else {
			suitable = list;
		}
		return suitable;

	}

	public List<StockUnit> suitableStockUnitsByLotAndAmountNoException(
			Lot lot, BigDecimal toPick) throws NullAmountNoOtherException {
		List<StockUnit> list = new ArrayList<StockUnit>();
		List<StockUnit> suitable = new ArrayList<StockUnit>();
		BigDecimal amount = new BigDecimal(0);
		log.info("try to find suitable stockunits for Lot " + lot.getName()
				+ " and amount  " + toPick);

		list = stockService.getListByLot(lot, true);

		for (StockUnit listSu : list) {
			if (!isSuitable(listSu)) {

				continue;
			}
			log.debug("found SU: " + listSu.toString());
			suitable.add(listSu);
			amount = amount.add(listSu.getAvailableAmount());
		}

		if (suitable.size() < 1) {
			throw new NullAmountNoOtherException(
					InventoryExceptionKey.NO_STOCKUNIT, amount, lot
							.toUniqueString());
		}

		if (amount.compareTo(toPick) < 0) {
			throw new NullAmountNoOtherException(
					InventoryExceptionKey.UNSUFFICIENT_AMOUNT, amount, lot
							.toUniqueString());
		}

		Collections.sort(suitable, getSuitableStockUnitComparator());
		return suitable;
	}

	public List<StockUnit> suitableStockUnitsByLotAndAmount(Lot lot,
			BigDecimal toPick) throws InventoryException, LOSLocationException {
		try {
			return suitableStockUnitsByLotAndAmountNoException(lot, toPick);
		} catch (NullAmountNoOtherException ex) {
			log.error(ex.getMessage());
			if (ex.getKey().equals(InventoryExceptionKey.NO_STOCKUNIT.name())) {
				throw new InventoryException(
						InventoryExceptionKey.NO_STOCKUNIT, lot
								.toUniqueString());
			} else if (ex.getKey().equals(
					InventoryExceptionKey.UNSUFFICIENT_AMOUNT.name())) {
				throw new InventoryException(
						InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
						new Object[] { "", lot.toUniqueString() });
			} else {
				throw new InventoryException(
						InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
						new Object[] { "", lot.toUniqueString() });
			}
		}
	}

	// private List<StockUnit> getStockUnitsOnFixedAssignment(Lot lot)
	// throws LOSLocationException, InventoryException {
	// List<StockUnit> list = new ArrayList<StockUnit>();
	// List<LOSStorageLocation> locs = fixedAssignmentComp
	// .getAssignedLocationsForStorage(lot.getItemData(), null);
	// for (LOSStorageLocation loc : locs) {
	// List<LOSUnitLoad> uls = loc.getUnitLoads();
	// for (LOSUnitLoad ul : uls) {
	// for (StockUnit u : ul.getStockUnitList()) {
	// if (u.getLot().equals(lot)) {
	// list.add(u);
	// }
	// }
	// }
	// }
	//
	// if (list.size() == 0) {
	// throw new InventoryException(
	// InventoryExceptionKey.NO_STOCKUNIT_ON_FIXED_ASSIGNED_LOC,
	// lot.getName());
	// }
	// return list;
	// }
	//
	// private boolean hasFixedLocationAssignment(Lot lot) {
	// return fixedAssignmentComp
	// .hasFixedLocationAssignment(lot.getItemData());
	// }

	public List<StockUnit> suitableStockUnitsByLotsAndAmount(List<Lot> lots,
			BigDecimal toPick) throws InventoryException, LOSLocationException {
		List<StockUnit> list;
		List<StockUnit> ret = new ArrayList<StockUnit>();

		BigDecimal amount = new BigDecimal(0);

		for (Lot lot : lots) {
			List<StockUnit> suitable = new ArrayList<StockUnit>();
			// if (hasFixedLocationAssignment(lot)) {
			// log.info("Take from Fix assigend");
			// try {
			// list = getStockUnitsOnFixedAssignment(lot);
			// } catch (InventoryException ex) {
			// if (ex
			// .getInventoryExceptionKey()
			// .equals(
			// InventoryExceptionKey.NO_STOCKUNIT_ON_FIXED_ASSIGNED_LOC)) {
			// log.warn(ex.getMessage());
			// list = suExService.getByLot(lot);
			// } else {
			// throw ex;
			// }
			// }
			// } else {
			// log.info("Cannot take from Fix assigend");
			// list = suExService.getByLot(lot);
			// }
			list = stockService.getListByLot(lot, true);

			for (StockUnit listSu : list) {
				if (!isSuitable(listSu)) {
					continue;
				}
				suitable.add(listSu);
				amount = amount.add(listSu.getAvailableAmount());
// With this algorithm, we will never be able to find the correct stock-unit. 
// The list is not ordered in the way, we will use the stock-units
//				BigDecimal d = toPick.multiply(new BigDecimal(OFFSET_FACTOR));
//				if (amount.compareTo(d) > 0) {
//					break;
//				}
			}
			Collections.sort(suitable, getSuitableStockUnitComparator());
			ret.addAll(suitable);
		}

		if (ret.size() < 1) {
			throw new InventoryException(InventoryExceptionKey.NO_STOCKUNIT, "");
		}

		return ret;

	}

	public List<StockUnit> suitableStockUnitsByItemData(
			LOSOrderRequestPosition pos, BigDecimal amount) throws InventoryException,
			LOSLocationException {
		List<Lot> lots;
		List<Lot> suitableLots = new ArrayList<Lot>();
		List<StockUnit> ret = new ArrayList<StockUnit>();

		if (pos.getItemData().isLotMandatory()){
			lots = lotService.getListByItemData(pos.getItemData());
	
			for (Lot lot : lots) {
				if (!isSuitable(lot)) {
					continue;
				}
				if (!isSuitable(lot.getItemData())) {
					continue;
				}
				suitableLots.add(lot);
			}

			if (suitableLots.size() < 1 ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUITABLE_LOT, "");
			} 
			else{
				Collections.sort(suitableLots, getSuitableLotComparator());
				ret = suitableStockUnitsByLotsAndAmount(suitableLots, amount);
			}
		}
		else {
			ret = suitableStockUnitsByItemDataWithoutLot(pos.getItemData(), amount);
			
		}
		return ret;

	}
	
	public List<StockUnit> suitableStockUnitsByItemDataWithoutLot(
			ItemData itemData, BigDecimal toPick) throws InventoryException, LOSLocationException {
		try {
			return suitableStockUnitsByItemDataWithoutLotNoException(itemData, toPick, false);
		} catch (NullAmountNoOtherException ex) {
			log.error(ex.getMessage());
			if (ex.getKey().equals(InventoryExceptionKey.NO_STOCKUNIT.name())) {
				throw new InventoryException(
						InventoryExceptionKey.NO_STOCKUNIT, itemData
								.toUniqueString());
			} else if (ex.getKey().equals(
					InventoryExceptionKey.UNSUFFICIENT_AMOUNT.name())) {
				throw new InventoryException(
						InventoryExceptionKey.CUSTOM_TEXT, ex.getLocalizedMessage() );
			} else {
				throw new InventoryException(
						InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
						new Object[] { "", itemData.toUniqueString() });
			}
		}
	}

	
	public List<StockUnit> suitableStockUnitsByItemDataWithoutLotNoException(
			ItemData itemData, BigDecimal toPick, boolean ignoreMissingAmount) throws NullAmountNoOtherException {
		List<StockUnit> list = new ArrayList<StockUnit>();
		List<StockUnit> suitable = new ArrayList<StockUnit>();
		BigDecimal amount = new BigDecimal(0);
		log.info("try to find suitable stockunits for ItemData " + itemData.getNumber() + " and amount  " + toPick);

		list = stockService.getListByItemData(itemData, true);

		log.info("Found "+list.size()+" Stock Units");

		for (StockUnit listSu : list) {
			if (!isSuitable(listSu)) {
				continue;
			}
			log.debug("found SU: " + listSu.toString());
			suitable.add(listSu);
			amount = amount.add(listSu.getAvailableAmount());
		}

		if (suitable.size() < 1) {
			throw new NullAmountNoOtherException(
					InventoryExceptionKey.NO_STOCKUNIT, toPick, itemData
							.toUniqueString());
		}

		if (!ignoreMissingAmount && amount.compareTo(toPick) < 0) {
			throw new NullAmountNoOtherException(
					InventoryExceptionKey.UNSUFFICIENT_AMOUNT, amount, itemData
							.toUniqueString());
		}

		Collections.sort(suitable, getSuitableStockUnitComparator());
		return suitable;
	}

	protected boolean isSuitable(StockUnit su) {

		if (su.isLocked()) {
//			log.info("Check SU: " + su.toUniqueString() + " is locked");
			return false;
		} else if (su.getAvailableAmount().compareTo(BigDecimal.ZERO) <= 0) {
//			log.info("Check SU: " + su.toUniqueString() + " no amount available");
			return false;
		} 

		LOSStorageLocation sl = ((LOSUnitLoad) (su.getUnitLoad()))
				.getStorageLocation();
		LOSStorageLocation clearing = slService.getClearing();
		LOSStorageLocation nirwana = slService.getNirwana();

		if (sl.equals(clearing) || sl.equals(nirwana)) {
			log.info("Check SU: " + su.toUniqueString() + " Won't take from location: " + sl.getName());
			return false;
		}

		if (sl.getArea() != null) {
			switch (sl.getArea().getAreaType()) {
			case GOODS_OUT:
			case GENERIC:
			case QUALITY_ASSURANCE:
			case TOLL:
				log.info("Check SU: " + su.toUniqueString() + " Won't take from area: " + sl.getArea().getAreaType());
				return false;
			}
		}

		return true;

	}

	protected void testStillSuitable(LOSPickRequestPosition pos)
			throws InventoryException {

		// LOSPickRequest pick = (LOSPickRequest) pos.getParentRequest();
		// LOSOrderRequest order = null;
		//		
		// if (pick != null){
		// order = (LOSOrderRequest) pick.getParentRequest();
		// }
		//		
		// if (order != null){
		// if (order instanceof ExtinguishOrder){
		//				
		// }
		// }

		if (pos.getStockUnit().getLot() != null) {
			Lot l = pos.getStockUnit().getLot();
			if (!isSuitable(l)) {
				throw new InventoryException(
						InventoryExceptionKey.LOT_ISLOCKED, l.getName());
			}
		}
		if (!isSuitable(pos.getItemData())) {
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_ISLOCKED, pos.getItemData()
							.getNumber());
		}
	}

	protected boolean isSuitable(Lot lot) {

		lot = manager.find(Lot.class, lot.getId());
		if (lot.isLocked()) {
			log.warn("is locked: " + lot.toDescriptiveString());
			return false;
		}

		return true;

	}

	protected boolean isSuitable(ItemData idat) {

		idat = manager.find(ItemData.class, idat.getId());

		if (idat.isLocked()) {
			log.warn("is locked: " + idat.toDescriptiveString());
			return false;
		}

		return true;

	}

	/**
	 * 
	 * @param req
	 * @param sus
	 * @param pos
	 * @param substituteAmount
	 *            don't pick the amount given in pos but this amount (usually
	 *            less, i.e. after unexpected null). Ignore if
	 *            <code>substituteAmount < 0 </code>
	 * @return
	 */
	protected List<LOSPickRequestPosition> createLOSPickRequestPositions(
			LOSPickRequest req, List<StockUnit> sus,
			LOSOrderRequestPosition pos, BigDecimal substituteAmount) {
		List<LOSPickRequestPosition> ret;
		ret = new ArrayList<LOSPickRequestPosition>();
		BigDecimal toReserve;
		BigDecimal toPick;
		boolean doReservation = true;

		switch (pos.getParentRequest().getOrderType()) {
		case TO_REPLENISH:
			doReservation = false;
			break;
		default:
			doReservation = true;
			break;
		}
		if (substituteAmount != null && substituteAmount.compareTo(new BigDecimal(0)) > 0) {
			toPick = substituteAmount;
		}
		else if (pos.getPickedAmount().compareTo(new BigDecimal(0)) == 0) {
			toPick = pos.getAmount();
		}
		else if (pos.getPickedAmount().compareTo(new BigDecimal(0)) > 0) {
			log.warn("Recalculate OrderPosition (Null amount before???): "
					+ pos.toDescriptiveString());
			toPick = pos.getAmount().subtract(pos.getPickedAmount());
		} 
		else {
			throw new IllegalArgumentException(
					"Order Position with negative picked amount");
		}
		
		LOSPickRequestPosition pickPos;
		for (StockUnit su : sus) {
			su = manager.find(su.getClass(), su.getId());
			if (toPick.compareTo(new BigDecimal(0)) == 0) {
				break;
			} else if (toPick.compareTo(su.getAvailableAmount()) < 0) {
				// Amount satisfied
				toReserve = toPick;
				PickingWithdrawalType w = determineWithdrawalType(su, toReserve);
				if (doReservation)
					su.addReservedAmount(toReserve);
				pickPos = pickPosService.create(req, pos, su, toReserve,
						SubstitutionType.SUBSTITUTION_SAME_LOT, w);
				ret.add(pickPos);
				break;
			} else {
				// take whole UnitLoad
				toReserve = su.getAvailableAmount();
				PickingWithdrawalType w = determineWithdrawalType(su, toReserve);
				if (doReservation)
					su.addReservedAmount(toReserve);
				pickPos = pickPosService.create(req, pos, su, toReserve,
						SubstitutionType.SUBSTITUTION_SAME_LOT, w);
				toPick = toPick.subtract(toReserve);
				ret.add(pickPos);
			}
		}

		return ret;
	}

	/**
	 * Sorts the {@link LOSPickRequestPosition}s according to the process
	 * 
	 * @param picks
	 * @return
	 */
	protected List<LOSPickRequestPosition> orderLOSPickRequestPositions(
			List<LOSPickRequestPosition> picks) {

		LOSPickRequestPosition[] array = picks
				.toArray(new LOSPickRequestPosition[0]);
		Arrays.sort(array, getPickingRequestPostionComparator());
		int index = 0;
		for (LOSPickRequestPosition p : array) {
			p = manager.find(LOSPickRequestPosition.class, p.getId());
			p.setIndex(index);
			index++;
		}

		return Arrays.asList(array);

	}

	protected PickingWithdrawalType determineWithdrawalType(StockUnit su, BigDecimal toReserve) {
		LOSUnitLoad ul = manager.find(LOSUnitLoad.class, su.getUnitLoad().getId());

		try {
			// On PickLocationUnitLoadTypes only picking is allowed
			UnitLoadType virtualLhm = ulTypeService.getPickLocationUnitLoadType();
			if (ul.getType().equals(virtualLhm)) {
				return PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT;
			}
			
			// If not the complete amount is for the pick, we try to pick the amount
			if( su.getAmount().compareTo(toReserve) != 0 ) {
				return PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT;
			}
			
			// If there are more than one StockItems on the UnitLoad, we pick
			if (ul.getStockUnitList().size() != 1) {
				return PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT;
			}
			
			return PickingWithdrawalType.TAKE_UNITLOAD;

		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
			return  PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT;
		}
	}

	public Comparator<StockUnit> getSuitableStockUnitComparator() {
		return new SuitableStockUnitComparator();
	}

	public Comparator<Lot> getSuitableLotComparator() {
		return new SuitableLotComparator();
	}

	public Comparator<LOSPickRequestPosition> getPickingRequestPostionComparator() {
		return new PickingRequestPostionComparator();
	}

	final static class PickingRequestPostionComparator implements
			Comparator<LOSPickRequestPosition> {

		public int compare(LOSPickRequestPosition o1, LOSPickRequestPosition o2) {
			if (o1 == null || o2 == null) {
				throw new NullPointerException();
			}
			if (o1 == o2) {
				return 0;
			}
			if (o1.equals(o2)) {
				return 0;
			}
			if (!o1.getWithdrawalType().equals(o2.getWithdrawalType())) {
				if (o1.getWithdrawalType().equals(
						PickingWithdrawalType.TAKE_UNITLOAD)) {
					return -1;
				} else {
					return 1;
				}
			}
			if( (o1.isCanceled() || o1.isPicked()) != (o2.isCanceled() || o2.isPicked()) ) {
				if(o1.isCanceled() || o1.isPicked()) {
					return -1;
				}
				else {
					return 1;
				}
			}
			LOSUnitLoad ul1 = (LOSUnitLoad) o1.getUnitLoad();
			LOSUnitLoad ul2 = (LOSUnitLoad) o2.getUnitLoad();

			LOSRackLocationComparatorByName c = new LOSRackLocationComparatorByName();

			try {
				int ret = c.compare((LOSRackLocation) ul1.getStorageLocation(),
						(LOSRackLocation) ul2.getStorageLocation());
				if (ret != 0) {
					return ret;
				}
			} catch (ClassCastException ex) {
				//
			}
			if (!ul1.getStorageLocation().getName().equals(
					ul2.getStorageLocation().getName())) {
				return ul1.getStorageLocation().getName().compareTo(
						ul2.getStorageLocation().getName());
			} else {
				return o1.getId().compareTo(o2.getId());
			}

		}

	}

	public void createPickRequestPosition(CreatePickRequestPositionTO posTO) throws FacadeException{
		log.debug( "createPickRequestPosition posTO=" + posTO.orderPosition.getName() + ", amount=" + posTO.amountToPick );

		LOSOrderRequestPosition orderPos = manager.find(LOSOrderRequestPosition.class, posTO.orderPosition.getId());
		
		LOSOrder order = (LOSOrder) orderPos.getParentRequest();
		order = manager.find(LOSOrder.class, order.getId());
		
		LOSPickRequest req;
		try{
			req = pickReqQuery.queryByIdentity(posTO.pickRequestNumber);
			switch(req.getState()){
			case RAW:
				break;
			default:
				throw new PickingException(PickingExceptionKey.PICKREQUEST_WRONG_STATE, req.getState().toString());
			}
		} catch (BusinessObjectNotFoundException ex){
			req = pickService.createPickRequest(order, posTO.pickRequestNumber);

			if(posTO.targetPlace != null){
				LOSStorageLocation target = manager.find(LOSStorageLocation.class, posTO.targetPlace.getId());
				req.setDestination(target);
				req = manager.merge(req);
			}
		}
				
		req = manager.find(LOSPickRequest.class, req.getId());
		
		if(req.getCart() == null){
			LOSStorageLocation cart;
			LOSStorageLocationType type;

			type = locTypeService.getNoRestrictionType();
			if (type == null ) throw new RuntimeException("Type without restriction not found ");
			cart = slService.createStorageLocation(order
					.getClient(), req.getNumber(), type);
		
				
			
			req.setCart(cart);
		}
		
		StockUnit su = manager.find(StockUnit.class, posTO.stock.getId());
		
		BigDecimal amount = posTO.amountToPick;
		PickingWithdrawalType withdrawalType = determineWithdrawalType(su, amount);
		log.info("PWT="+withdrawalType);
		
		if (su.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0){
			log.error("--- !!! Stock : amount = "+su.getAmount()+" Reserved = "+su.getReservedAmount());
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, 
					new String[]{""+su.getAvailableAmount(), su.toUniqueString()});
		}
		
		order.setOrderState(LOSOrderRequestState.PROCESSING);
		orderPos.setPositionState(LOSOrderRequestPositionState.PROCESSING);
		manager.merge(orderPos);
		
		LOSPickRequestPosition prp = pickPosService.create(req, orderPos,su,
				amount,SubstitutionType.SUBSTITUTION_SAME_LOT, withdrawalType );
		if(req.getPositions() == null){
            prp.setIndex(1);
            req.setPositions(new ArrayList<LOSPickRequestPosition>());
        }
        else{
            prp.setIndex(req.getPositions().size()+1);
            req.getPositions().add(prp);
        }
		
		manager.merge(req);
		
		orderLOSPickRequestPositions(req.getPositions());

		log.info(contextService.getCallersUser().getName() + " created: " + prp.toDescriptiveString() + " *** for *** " + orderPos.toDescriptiveString());
		
		manager.flush();
		manager.clear();
		
	}
	
	//---------------------------------------------------------------
	
	public LOSPickRequest createPickRequest(LOSOrderRequestPosition orderPos, String name, List<CreatePickRequestPositionTO> sus) throws FacadeException{

		LOSStorageLocation cart;
		LOSStorageLocationType type;

		orderPos = manager.find(LOSOrderRequestPosition.class, orderPos.getId());
		
		LOSOrder order = (LOSOrder) orderPos.getParentRequest();
		order = manager.find(LOSOrder.class, order.getId());
		
		log.debug( "createPickRequest orderPos=" + orderPos.getNumber() );

		
		LOSPickRequest req;
		try{
			req = pickReqQuery.queryByIdentity(name);
			switch(req.getState()){
			case RAW:
				break;
			default:
				throw new PickingException(PickingExceptionKey.PICKREQUEST_WRONG_STATE, req.getState().toString());
			}
		} catch (BusinessObjectNotFoundException ex){
			req = pickService.createPickRequest(order, name);
		}
		
		req = manager.find(LOSPickRequest.class, req.getId());
		
		type = locTypeService.getNoRestrictionType();
		if (type == null ) throw new RuntimeException("Type without restriction not found ");
		
		cart = slService.createStorageLocation(order
				.getClient(), req.getNumber(), type);
		
		req.setCart(cart);
		
		int index = 0;
		
		if (sus.size() < 1){
			log.warn("nothing to do. No affected StockUnits to pick");
			req.setState(PickingRequestState.FINISHED);
		} else{
			Set<Long> already = new HashSet<Long>();
			for (CreatePickRequestPositionTO dto : sus) {
				if (already.contains(dto.stock.getId())){
					continue;
				} 
				StockUnit su = manager.find(StockUnit.class, dto.stock.getId());
				
				BigDecimal amount = dto.amountToPick;
				PickingWithdrawalType withdrawalType = determineWithdrawalType(su, amount);

				log.debug("--- !!! Stock : amount = "+su.getAmount()+" Reserved = "+su.getReservedAmount());
				
				if (su.getAvailableAmount().compareTo(amount) < 0){
					log.error("--- !!! Stock : amount = "+su.getAmount()+" Reserved = "+su.getReservedAmount());
					throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, 
							new String[]{""+su.getAvailableAmount(), su.toUniqueString()});
				}

				su.addReservedAmount(amount);
				orderPos.setPositionState(LOSOrderRequestPositionState.PROCESSING);
				
				LOSPickRequestPosition prp = pickPosService.create(req, orderPos,su,
						amount,SubstitutionType.SUBSTITUTION_SAME_LOT, withdrawalType );
				prp.setIndex(index);
				
				already.add(dto.stock.getId());
				index++;
			}
		}

		manager.persist(req);
		manager.flush();

		log.info("created " + req.toDescriptiveString());
		
		return req;

	}
	
	
	//---------------------------------------------------------------
	
	/**
	 * First: Take from StorageLocations within PickArea. Then: from StockUnit
	 * with smaller amount, Last: from older StockUnit.
	 * 
	 * @author trautm
	 * 
	 */
	public final static class SuitableStockUnitComparator implements
			Comparator<StockUnit> {

		// LOSFixedLocationAssignmentComp fixAssComp;
		//		
		// public SuitableStockUnitComparator(LOSFixedLocationAssignmentComp
		// fixAssComp){
		// this.fixAssComp = fixAssComp;
		// }

		public int compare(StockUnit su1, StockUnit su2) {

			if (su1 == null || su2 == null) {
				throw new NullPointerException();
			}
			if (su1 == su2) {
				return 0;
			}
			if (su1.equals(su2)) {
				return 0;
			}

			LOSUnitLoad ul1 = (LOSUnitLoad) su1.getUnitLoad();
			LOSUnitLoad ul2 = (LOSUnitLoad) su2.getUnitLoad();
			LOSStorageLocation sl1 = ul1.getStorageLocation();
			LOSStorageLocation sl2 = ul2.getStorageLocation();

			if ((sl1.getArea() != null && sl1.getArea().getAreaType().equals(LOSAreaType.PICKING))
					&& (sl2.getArea() == null || !(sl2.getArea().getAreaType()
							.equals(LOSAreaType.PICKING)))) {
				return -1;
			}
			
			if ((sl2.getArea() != null && sl2.getArea().getAreaType().equals(LOSAreaType.PICKING))
					&& (sl1.getArea() == null || !(sl1.getArea().getAreaType()
							.equals(LOSAreaType.PICKING)))) {
				return 1;
			}
			
			long sameAgeDiff = 43200000; // 12 hours
			if (su1.getCreated().getTime() < (su2.getCreated().getTime() - sameAgeDiff) ) {
				return -1;
			}
			if (su1.getCreated().getTime() > (su2.getCreated().getTime() + sameAgeDiff) ) {
				return 1;
			}
			
			
			
			if (su1.getAvailableAmount()
					.compareTo(su2.getAvailableAmount()) < 0) {
				return -1;
			}
			
			if (su1.getAvailableAmount().compareTo(
					su2.getAvailableAmount()) > 0) {
				return 1;
			}
			
//			if (su1.getCreated().before(su2.getCreated())) {
//				return -1;
//			}
//			
//			if (su2.getCreated().before(su1.getCreated())) {
//				return 1;
//			}
			
			return su1.getId().compareTo(su2.getId());
		}
	}

	final static class SuitableLotComparator implements Comparator<Lot> {

		public int compare(Lot o1, Lot o2) {

			if (o1 == null || o2 == null) {
				throw new NullPointerException();
			}
			if (o1 == o2) {
				return 0;
			}
			if (o1.equals(o2)) {
				return 0;
			}

			//both best before end dates set?
			if (o1.getBestBeforeEnd() != null && o2.getBestBeforeEnd() != null){
				if (o1.getBestBeforeEnd().before(o2.getBestBeforeEnd())){
					return -1;
				} else if (o2.getBestBeforeEnd().before(o1.getBestBeforeEnd())){
					return 1;
				} else{
					//go to next if then statement...
				}
			}
			
			// best before end date not sufficent?
			if (o1.getCreated().before(o2.getCreated())) {
				return -1;
			} else if (o2.getCreated().before(o1.getCreated())) {
				return 1;
			} else {
				return o1.getId().compareTo(o2.getId());
			}
		}
	}
			
	public void assignSerialNumbers(LOSPickRequestPosition pos, List<String> serialNumbers) throws FacadeException{
		
		StockUnit picked = manager.find(StockUnit.class, pos.getStockUnit().getId());
		pos = manager.find(LOSPickRequestPosition.class, pos.getId());
		LOSPickRequest req = manager.find(LOSPickRequest.class, pos.getPickRequest().getId());
		LOSOrderRequestPosition orderPos = manager.find(LOSOrderRequestPosition.class, pos.getParentRequest().getId());
		
		log.debug( "assignSerialNumbers pos=" + orderPos.getNumber()+", numSerials="+(serialNumbers==null?"NULL":serialNumbers.size()) );
		
		if (pos == null || serialNumbers == null){
			throw new NullPointerException("Neither position nor serial number list must be null");
		}
		if (picked.getItemUnit().getUnitType() != ItemUnitType.PIECE){
			throw new IllegalArgumentException("Wrong unit type (must be piece): " + picked.getId());
		}
		if (serialNumbers.size() != pos.getPickedAmount().intValue()){
			throw new IllegalArgumentException("Missing serialNumbers");
		}
		if (! (pos.isPicked() || pos.isSolved())) {
			throw new IllegalArgumentException("position ist not picked nor solved");
		}
		if (orderPos.getItemData().getSerialNoRecordType() == SerialNoRecordType.NO_RECORD){
			throw new IllegalArgumentException("no record of serialNo necessary");
		}
		try{	
			if (serialNumbers.size() > 1){
				
			    picked.setSerialNumber(serialNumbers.remove(0));
			    
			    List<LOSPickRequestPosition> posList = req.getPositions();
			    
			    // for inserting new pick positions, 
			    // it is necessary to raise the index of the following
			    for(int i = posList.indexOf(pos)+1; i < posList.size(); i++){
			    	LOSPickRequestPosition next = posList.get(i);
			    	next.setIndex(next.getIndex()+serialNumbers.size());
			    	manager.merge(next);
			    }
			    
			    int index = pos.getIndex();
			    
				for (String s: serialNumbers){
					
						StockUnit created = invComp.splitStock(picked, (LOSUnitLoad) picked.getUnitLoad(), BigDecimal.ONE, "SER " + pos.getId());	
						created.setSerialNumber(s);
						manager.merge(created);
						
						LOSPickRequestPosition createdPos = pickPosService.create(req, orderPos, created, BigDecimal.ONE, SubstitutionType.SUBSTITUTION_NOT_ALLOWED, PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT);
						createdPos.setIndex(++index);
						createdPos.setComplementOn(pos);
						createdPos.setPicked(BigDecimal.ONE, created);
						
						pos.setPicked(pos.getPickedAmount().subtract(BigDecimal.ONE), picked);
						pos.setAmount(pos.getAmount().subtract(BigDecimal.ONE));
						
						manager.flush();
					
				}
			} else{
				picked.setSerialNumber(serialNumbers.get(0));
			}
		} catch (Throwable ex){
			log.error(ex.getMessage(),ex);
			if (ex instanceof FacadeException) throw (FacadeException)ex;
			else throw new InventoryException(InventoryExceptionKey.INVENTORY_CREATE_STOCKUNIT_ON_TOP ,new String[0]);
		}
	}
	
	public void processPickRequestPosition(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException {

		position = manager.find(LOSPickRequestPosition.class, position.getId());
		r = manager.find(LOSPickRequest.class, r.getId());
		
		log.info("processPickRequestPosition user="+contextService.getCallersUser().getName() + " pos=" + position.toUniqueString() + 
				", amount=" + amount + ", unexpectedNull=" + unexpectedNullAmount + ", location=" + sl.getName());
		
		try {
			if (!testCanProcess(r, position, unexpectedNullAmount, sl, amount)) {
				log.warn("cannot process " + position.toUniqueString());
				return;
			}
		} catch (PickingSubstitutionException ex) {
			log.error(ex.getMessage(), ex);
			processPickRequestPositionSubstitution(r, position,
					unexpectedNullAmount, sl, amount, takeWholeUnitLoad, stockEmptyConfirmed);
			return;
		}

		r.setState(PickingRequestState.PICKING);
		finishPickRequestPosition(r, position, amount, unexpectedNullAmount, takeWholeUnitLoad, stockEmptyConfirmed);

	}
	
	public void processPickRequestPosition(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			StockUnit su, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException {

		position = manager.find(LOSPickRequestPosition.class, position.getId());
		
		r = manager.find(LOSPickRequest.class, r.getId());
		
		log.info("processPickRequestPosition user="+contextService.getCallersUser().getName() + ", pos=" + position.toUniqueString() + 
				", amount=" + amount + ", unexpectedNull=" + unexpectedNullAmount + ", stochUnit=" + su.getLabelId());
		
		try {
			if (!testCanProcess(r, position, unexpectedNullAmount, ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation(), amount)) {
				log.warn("cannot process " + position.toUniqueString());
				return;
			}
		} catch (PickingSubstitutionException ex) {
			log.error(ex.getMessage(), ex);
			processPickRequestPositionSubstitution(r, position,
					unexpectedNullAmount, ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation(), amount, takeWholeUnitLoad, stockEmptyConfirmed);
			return;
		}

		r.setState(PickingRequestState.PICKING);
		finishPickRequestPosition(r, position, amount, unexpectedNullAmount, takeWholeUnitLoad, stockEmptyConfirmed);

	}

	public void processPickRequestPositionSubstitution(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException {

		position = manager.find(LOSPickRequestPosition.class, position.getId());
		LOSUnitLoad ul = (LOSUnitLoad) position.getUnitLoad();
		r = manager.find(LOSPickRequest.class, r.getId());

		log.info("processPickRequestPositionSubstitution user="+contextService.getCallersUser().getName() + ", pos=" + position.toUniqueString() + 
				", amount=" + amount + ", unexpectedNull=" + unexpectedNullAmount + ", location=" + sl.getName());
		
		
		try {
			if (!testCanProcess(r, position, false, sl, amount)) {
				return;
			}
			// reaching this: it is not a substitution
			log
					.error("Call to processPickRequestPositionSubstitution but it is not: "
							+ position.toDescriptiveString());
			r.setState(PickingRequestState.PICKING);
			finishPickRequestPosition(r, position, amount, false, takeWholeUnitLoad, stockEmptyConfirmed);
		} catch (PickingSubstitutionException ex) {
			// as expected
		}

		r.setState(PickingRequestState.PICKING);
		StockUnit subs;

		if ((subs = substitution(position, sl)) != null) {
			LOSOrderRequest order = r.getParentRequest();
			switch (order.getOrderType()) {
			case TO_REPLENISH:
				break;
			default:
				position.getStockUnit().releaseReservedAmount(
						position.getAmount());
				subs.addReservedAmount(position.getAmount());
			}

			position.setStockUnit(subs);
			finishPickRequestPosition(r, position, amount, unexpectedNullAmount, takeWholeUnitLoad, stockEmptyConfirmed);
		} else {
			PickingException ex = new PickingException(
					PickingExceptionKey.PICK_WRONG_SOURCE, ul
							.getStorageLocation().getName());
			throw ex;
		}

	}

	public void processPickRequestPositionExpectedNull(LOSPickRequest r,
			LOSPickRequestPosition position, LOSStorageLocation sl,
			BigDecimal amount, BigDecimal discoveredAmount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed)
			throws FacadeException {

		if (discoveredAmount.compareTo(new BigDecimal(0)) < 0) {
			PickingException ex = new PickingException(
					PickingExceptionKey.AMOUNT_MUSTBE_POSITIVE, ""
							+ discoveredAmount);
			log.error(ex.getMessage(), ex);
			throw ex;
		}


		position = manager.find(LOSPickRequestPosition.class, position.getId());
		
		r = manager.find(LOSPickRequest.class, r.getId());

		log.info("processPickRequestPositionExpectedNull user="+contextService.getCallersUser().getName() + ", pos=" + position.toUniqueString() + 
				", amoount=" + discoveredAmount + ", location=" + sl.getName());
		
		try {
			if (!testCanProcess(r, position, false, sl, amount)) {
				log.error("Could not be processed: "
						+ position.toDescriptiveString());
				return;
			}
			// reaching this: it is not an expected null turn
			log
					.error("Call to processPickRequestPositionExpectedNull but it is not: "
							+ position.toDescriptiveString());
			r.setState(PickingRequestState.PICKING);
			finishPickRequestPosition(r, position, amount, false, takeWholeUnitLoad, stockEmptyConfirmed);
			return;
		} catch (PickingExpectedNullException ex) {
			// as expected
		}

		r.setState(PickingRequestState.PICKING);

		if (discoveredAmount.compareTo(new BigDecimal(0)) > 0) {
			StockUnit su = position.getStockUnit();
			invComp.changeAmount(su, su.getAmount().add(discoveredAmount),
					false, r.getNumber());
			position.setStockUnit(su);
			
			if( stockEmptyConfirmed ) {
				LOSUnitLoad sourceUl = (LOSUnitLoad)su.getUnitLoad();
				if( sourceUl.getStockUnitList().size() == 1 ) {
					LOSStorageLocation sourceLoc = sourceUl.getStorageLocation();
					if( sourceLoc.getUnitLoads().size() == 1 ) {
						setStocktakingDone( sourceLoc, sourceUl, su, r.getNumber() );
					}
					else {
						setStocktakingDone( sourceLoc, null, null, r.getNumber() );
					}
				}
			}
		}

		finishPickRequestPosition(r, position, amount, false, takeWholeUnitLoad, stockEmptyConfirmed);

	}

	/**
	 * 
	 * @return the {@link OrderType} of the parent order
	 * @throws IllegalArgumentException
	 *             if OrderType cannot be determined
	 */
	protected OrderType getOrderType(LOSPickRequest pick) {

		if (pick == null) {
			throw new NullPointerException();
		}

		pick = manager.find(LOSPickRequest.class, pick.getId());

		if (pick.getParentRequest() == null) {
			log.error("No parentRequest: " + pick.toDescriptiveString());
			return OrderType.INTERNAL;
		}

		LOSOrderRequest order = null;

		order = manager.find(LOSOrderRequest.class, pick.getParentRequest()
				.getId());
		return order.getOrderType();

	}

	// protected boolean testSubstitution(LOSPickRequestPosition pos,
	// LOSStorageLocation sl) throws PickingException {
	//		 
	// switch(pos.getSubstitutionType()){
	// case SUBSTITUTION_NOT_ALLOWED:
	// return false;
	// case SUBSTITUTION_SAME_ARTICLE:
	// for (LOSUnitLoad u : sl.getUnitLoads()){
	// if (invComp.testSameItemData(pos.getStockUnit().getItemData(), u)){
	// return true;
	// } else{
	// continue;
	// }
	// }
	// return false;
	// case SUBSTITUTION_SAME_LOT:
	// for (LOSUnitLoad u : sl.getUnitLoads()){
	// if (invComp.testSameLot(pos.getStockUnit().getLot(), u)){
	// return true;
	// } else{
	// continue;
	// }
	// }
	// return false;
	// default:
	// return false;
	// }
	//
	// }
	//	
	protected StockUnit substitution(LOSPickRequestPosition pos,
			LOSStorageLocation sl) throws PickingException {

		switch (pos.getSubstitutionType()) {
		case SUBSTITUTION_NOT_ALLOWED:
			return null;
		case SUBSTITUTION_SAME_ARTICLE:
			for (LOSUnitLoad u : sl.getUnitLoads()) {
				if (invComp.testSameItemData(pos.getStockUnit().getItemData(),
						u)) {
					for (StockUnit su : u.getStockUnitList()) {
						if (isSuitable(su)
								&& su.getAvailableAmount().compareTo(
										pos.getAmount()) >= 0) {
							return su;
						}
					}
				} else {
					continue;
				}
			}
			return null;
		case SUBSTITUTION_SAME_LOT:
			for (LOSUnitLoad u : sl.getUnitLoads()) {
				log.info("search for substitution  " + u.toShortString());
				if (invComp.testSameLot(pos.getStockUnit().getLot(), u)) {
					log.info("Test for same lot OK " + u.toShortString());
					for (StockUnit su : u.getStockUnitList()) {
						log.info("Test for suitable StockUnit " + su.toShortString());
						if (isSuitable(su)
								&& su.getAvailableAmount().compareTo(
										pos.getAmount()) >= 0) {
							log.info("OK for " + su.toShortString());
							return su;
						}
					}
				} else {
					continue;
				}
			}
			return null;
		default:
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness#
	 *      removePickingRequest
	 */
	public void removePickingRequest(LOSPickRequest pickingRequest)
			throws InventoryException, PickingException {
		if( pickingRequest == null ) {
			log.error("removePickingRequest pickingRequest=NULL. Abort");
			return;
		}
		log.info("removePickingRequest: pickingRequest="+pickingRequest.getNumber());

		LOSPickRequest pickRequest = manager.find(LOSPickRequest.class, pickingRequest.getId());

		// check state of the picking-request.
		switch (pickingRequest.getState()) {
		case FINISHED:
		case FINISHED_PARTIAL:
		case PICKED_PARTIAL:
		case FAILED:
		case ACCEPTED:
		case RAW:
			break;
		default:
			throw new PickingException(
					PickingExceptionKey.PICKREQUEST_NOT_FINISHED, pickRequest
							.toUniqueString());
		}

		LOSStorageLocation cart = pickRequest.getCart();

		// delete the picking-request
		try {
			releaseStockUnits(pickingRequest);
			pickService.delete(pickRequest);
			releaseCart(cart);
		} catch (ConstraintViolatedException e) {
			throw new PickingException(
					PickingExceptionKey.PICKREQUEST_CONSTRAINT_VIOLATED,
					pickRequest.toUniqueString());
		}
	}

	protected void releaseStockUnits(LOSPickRequest r) {
		r = manager.find(LOSPickRequest.class, r.getId());

		LOSOrderRequest order = r.getParentRequest();
		switch (order.getOrderType()) {
		case TO_REPLENISH:
			return;
		default:
			//
		}

		for (LOSPickRequestPosition p : r.getPositions()) {
			p = manager.find(LOSPickRequestPosition.class, p.getId());
			StockUnit su = p.getStockUnit();
			if (su.getReservedAmount().compareTo(p.getAmount()) >= 0) { 
				su = manager.find(StockUnit.class, su.getId());
				su.releaseReservedAmount(p.getAmount());
			} else {
				su.releaseReservedAmount(su.getReservedAmount()); 
			}
		}
	}

	protected void releaseCart(LOSStorageLocation cart)
			throws InventoryException {
		if (cart != null && cart.getUnitLoads().size() > 0) {
			for (LOSUnitLoad cu : cart.getUnitLoads()) {
				for (StockUnit su : cu.getStockUnitList()) {
					try {
						suService.delete(su);
					} catch (ConstraintViolatedException e) {
						throw new InventoryException(
								InventoryExceptionKey.STOCKUNIT_CONSTRAINT_VIOLATED,
								new Long[] { su.getId() });
					}
				}

				try {
					losUlService.delete(cu);
				} catch (ConstraintViolatedException e) {
					throw new InventoryException(
							InventoryExceptionKey.UNIT_LOAD_CONSTRAINT_VIOLATED,
							new Long[] { cu.getId() });
				}
			}
		}

		if (cart != null) {
			try {
				slService.delete(cart);
			} catch (ConstraintViolatedException e) {
				throw new InventoryException(
						InventoryExceptionKey.STORAGELOCATION_CONSTRAINT_VIOLATED,
						new Long[] { cart.getId() });
			}
		}
	}

	protected void finishOrderPosition(LOSPickRequestPosition pos)
			throws FacadeException {

		LOSOrderRequestPosition orderPos;
		Request prox = pos.getParentRequest();

		orderPos = manager.find(LOSOrderRequestPosition.class, prox.getId());

		if (!pos.getItemData().equals(orderPos.getItemData())) {
			throw new RuntimeException("ItemData mismatch:"
					+ pos.getItemData().toShortString());
		}

		boolean cancled = false;

		orderBusiness.processOrderPositionPicked(orderPos, pos
				.getPickedAmount(), cancled);
	}

	public void cancel(LOSPickRequest pickingRequest) throws PickingException,
			FacadeException {

		User user = contextService.getCallersUser();
		log.info(user.toShortString() + " is going to cancel: "
				+ pickingRequest.toDescriptiveString());

		switch (pickingRequest.getState()) {
		case ACCEPTED:
			pickingRequest = manager.find(pickingRequest.getClass(),
					pickingRequest.getId());
			pickingRequest.setState(PickingRequestState.RAW);
			log.info(user.toShortString() + " resets state to RAW: "
					+ pickingRequest.toDescriptiveString());
			return;
		case RAW:
			return;
		default:
			for( LOSPickRequestPosition pos : pickingRequest.getPositions() ) {
				if( pos.isPicked() && pos.missingMandatorySerialNo() ) {
					throw new PickingException(
							PickingExceptionKey.PICK_MISSING_SERIALNO, "");
				}
			}
			boolean hasStock = false;
			if( pickingRequest.getCart() != null ) {
				LOSStorageLocation cart = manager.find(LOSStorageLocation.class, pickingRequest.getCart().getId());
				// Why is this done in the backend?
				for( LOSUnitLoad ul : cart.getUnitLoads() ) {
					for( StockUnit su : ul.getStockUnitList() ) {
						if( BigDecimal.ZERO.compareTo(su.getAmount()) < 0 ) {
							hasStock = true;
							break;
						}
					}
					if( hasStock ) {
						break;
					}
				}
			}
			log.debug("Check for stock value="+hasStock);
			if (hasStock) {
				throw new PickingException(
						PickingExceptionKey.MUST_GOTO_DESTINATION, "");
			}
		}
	}
	
	public boolean testCanProcess(LOSPickRequest r,
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			LOSStorageLocation sl, BigDecimal amount) throws FacadeException {
		return testCanProcess(r, position, unexpectedNullAmount, sl, amount, null);
	}

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
			LOSStorageLocation sl, BigDecimal amount, String serialNumber) throws FacadeException {
		LOSUnitLoad ul = (LOSUnitLoad) position.getUnitLoad();

		if (r.getState().equals(PickingRequestState.FINISHED)) {
			log.warn("Request already finished "
					+ position.toDescriptiveString());
			return false;
		}
		if (position.isSolved()) {
			log.warn("Position already solved "
					+ position.toDescriptiveString());
			return false;
		}
		if (position.isCanceled()) {
			log.warn("Position already cancled "
					+ position.toDescriptiveString());
			return false;
		}
		if (position.isPicked()) {
			log.warn("Position already picked "
					+ position.toDescriptiveString());
			return false;
		}

		OrderType t = getOrderType(r);
		switch (t) {
		case INTERNAL:
		case TO_EXTINGUISH:
			break;
		case TO_PRODUCTION:
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
		case TO_REPLENISH:
			testStillSuitable(position);
			break;
		default:
			log.error("unknown state: " + t.name());
			return false;
		}

		if (sl.equals(ul.getStorageLocation())) {
			if ((amount).compareTo(position.getAmount()) == 0) {
				StockUnit su = position.getStockUnit();
				if (su.getAmount().compareTo(amount) == 0) {
					// The dialog should go to the Location-Empty-Question
					boolean confirmEmpty = false;
					
					LOSUnitLoad sourceUl = (LOSUnitLoad)su.getUnitLoad();
					if( sourceUl.getStockUnitList().size() == 1 ) {
						LOSStorageLocation sourceLoc = sourceUl.getStorageLocation();
						if( sourceLoc.getUnitLoads().size() == 1 ) {
							
							// ask for confirmation only after a couple of days
							long countingInterval = propertyService.getLongDefault(LOSPickPropertyKey.PICK_LOCATION_EMPTY_CHECK_DAYS, 0L);
							if( countingInterval < 0 ) {
								confirmEmpty = false;
							}
							else if( countingInterval == 0 ) {
								confirmEmpty = true;
							}
							else if( countingInterval > 0 ) {
								Date lastCounted = sourceLoc.getStockTakingDate();
								Date now = new Date();
								if( lastCounted == null ) {
									confirmEmpty = true;
									log.debug("Location has never been counted.");
								}
								else {
									long diffTime = now.getTime() - lastCounted.getTime();
									long diffDays = diffTime / 86400000;
									log.debug("Location has not been counted in the last "+diffDays+" days. Limit="+countingInterval);
									if( diffDays >= countingInterval ) {
										confirmEmpty = true;
									}
								}
							}
						}
					}
					
					if( confirmEmpty ) {
						PickingExpectedNullException ex = new PickingExpectedNullException(	su);
						throw ex;
					}
					
				} else if (su.getAmount().compareTo(amount) > 0) {
					// OK
				} else {
					log.warn("testCanProcess: unsufficient amount: " + amount + " *** " + su.toDescriptiveString());
					throw new InventoryException(
							InventoryExceptionKey.UNSUFFICIENT_AMOUNT,
							new Object[] { "" + su.getAmount(),
									position.getItemData().toUniqueString() });
				}
			} else if (amount.compareTo(position.getAmount()) < 0) {
				if (unexpectedNullAmount) {
					// OK
				} else {
					PickingException ex = new PickingException(
							PickingExceptionKey.PICK_UNEXPECTED_NULL, position
									.getStockUnit().toUniqueString());
					log.warn("testCanProcess: unexpected null !");
					throw ex;
				}
			} else {
				log.warn("testCanProcess: wrong amount: " + amount );
				PickingException ex = new PickingException(
						PickingExceptionKey.PICK_WRONG_AMOUNT, ""+position.getDisplayAmount());
				throw ex;
			}
		} else {
			StockUnit subs;
			if ((subs = substitution(position, sl)) != null) {
				PickingSubstitutionException ex = new PickingSubstitutionException(
						ul.getStorageLocation().getName(), sl.getName());
				ex.setSubstitution(subs);
				log.warn(ex.getMessage());
				throw ex;
			} else {
				PickingException ex = new PickingException(
						PickingExceptionKey.PICK_WRONG_SOURCE, ul
								.getStorageLocation().getName());
				log.warn(ex.getMessage());
				throw ex;
			}
		}
		
		if (serialNumber != null){
			StockUnit su = position.getStockUnit();
			if (su.getSerialNumber() != null && su.getSerialNumber().equals(serialNumber)){
				log.error("testCanProcess: Wrong serial number! expected:" + su.getSerialNumber() + " was: " + serialNumber);
				throw new PickingException(PickingExceptionKey.PICK_WRONG_SERIALNO, new String[]{su.getSerialNumber(), serialNumber});
			}
			if(position.getAmount().compareTo(new BigDecimal(1)) > 0){
				log.error("testCanProcess: No amount > 1 for serials");
				throw new PickingException(PickingExceptionKey.PICK_POSITION_CONTRAINT_VIOLATED, "");
			}
		}

		return true;

	}

	private void createLabels(LOSPickRequest req, LOSUnitLoad ul) {
		LOSOrderRequest orderReq = req.getParentRequest();
		if( orderReq == null ) {
			log.info("No Labels without OrderRequest");
			return;
		}
		if( !managePickService.createLabels(orderReq, ul) ) {
			log.info("No Labels for OrderRequest " + req.getNumber());
			return;
		}

		byte[] labels;
		try {
			if (orderReq.getLabelUrl() == null || orderReq.getLabelUrl().length() < 1){
				log.warn("No labels found at "  );
				return;
			}
			labels = repService.httpGet(orderReq.getLabelUrl());
			log.info(" Got labels from "+ orderReq.getLabelUrl());
			
			if (labels != null){
				String printer = null;
				try {
					printer = serviceConfig.getValue(PickOrderBusiness.class, req
							.getClient(), PickOrderBusiness.CONFKEY_PRINTER);
				} catch (EntityNotFoundException e) {
					log.error(e.getMessage(), e);
				}
				repService.print(printer, labels,
						DocumentTypes.APPLICATION_PDF.toString());
			}
		} catch (ReportException rex) {
			log.error(rex.getMessage(), rex);
		}

	}

	private void setStocktakingDone( LOSStorageLocation loc, LOSUnitLoad ul, StockUnit su, String activityCode ) {
		// TODO: Use StockTaking service
		Date countingDate = new Date();
		if( su != null ) {
			recordService.recordCounting(su, ul, loc, activityCode, null, null);
		}
		if( loc != null ) {
			loc.setStockTakingDate(countingDate);
			recordService.recordCounting(null, null, loc, activityCode, null, null);
		}
		if( ul != null ) {
			ul.setStockTakingDate(countingDate);
			recordService.recordCounting(null, ul, loc, activityCode, null, null);
		}
	}
}
