/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.Request;
import org.mywms.model.StockUnit;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestPositionState;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.inventory.report.OrderReport;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.inventory.service.OrderService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

/**
 * 
 * @author trautm
 */
@Stateless
public class OrderBusinessBean implements OrderBusiness {

	Logger log = Logger.getLogger(OrderBusinessBean.class);


	@EJB
	private PickOrderBusiness pickService;

	@EJB
	private OrderRequestService orderrequestService;

	@EJB
	private ItemDataService idatService;

	@EJB
	private LOSLotService lotService;

	@EJB
	private InventoryGeneratorService genService;

	@EJB
	private LOSGoodsOutBusiness outBusiness;

	@EJB
	private LOSServicePropertyService serviceConfig;

	@EJB
	private LOSPickRequestService pickRequestService;
	
	@EJB
	private LOSStorageLocationService slService;
	
	@EJB
	private LOSInventoryComponent invComp;
	
	@EJB
	private OrderService orderService;
	
	@EJB
	private ReportService repService;;

	@EJB
	private OrderReport orderReport;

	@EJB
	private ManageOrderService manageOrderService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public LOSOrderRequest createOrder(Client c, String orderRef,
			OrderPositionTO[] positions, String documentUrl, String labelUrl,
			String destination, Date delivery, OrderType type) throws FacadeException {

		LOSOrderRequest r;
		ItemData idat;
		Lot lot = null;
		OrderCannotBeStarted orderCannotBeStarted = null;
		try {

			r = orderService.create(c.getNumber(), orderRef, type, delivery,
					destination, documentUrl, labelUrl);

			if (r == null) {
				throw new RuntimeException("OrderRequest could not be created");
			}

			for (OrderPositionTO to : positions) {
				
				log.info("createOrder position: " + to.toString());
				idat = idatService.getByItemNumber(c, to.articleRef);
				if( idat == null ) {
					log.error("ItemData " + to.articleRef + " does not exists");
					throw new InventoryException(
							InventoryExceptionKey.NO_SUCH_ITEMDATA, to.articleRef + "("+c.getNumber()+")");
				}
				
				if (to.batchRef == null || to.batchRef.length() == 0) {
					testItemData(idat);
				} 
				else {
					
					try {
						lot = lotService.getByNameAndItemData(c, to.batchRef, idat.getNumber());
					} catch (EntityNotFoundException ex) {
						log.warn(ex.getMessage());
						throw new InventoryException(
								InventoryExceptionKey.NO_SUCH_LOT, to.batchRef + "("+c.getNumber()+")");
					}
					
					try{
						testLot(r,lot);
					} catch (OrderCannotBeStarted ex){
						log.error("Lot is not suitable: " + lot.getName());
						log.error(ex.getMessage());
						orderCannotBeStarted = ex;
					}
				}
				orderrequestService.addPosition(r, idat, lot, to.amount, true);
			}
			
			if (orderCannotBeStarted == null){
//				process(r);
			} else {
				throw orderCannotBeStarted;
			}
		} catch (OrderCannotBeStarted ex){
			throw  ex;
		} catch (FacadeException t) {
			log.error(t.getMessage());
			log.error("CREATE OrderRequest FAILED: " + orderRef);
			throw t;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(
					InventoryExceptionKey.ORDER_CANNOT_BE_CREATED, t.getLocalizedMessage());

		}
		log.info("CREATED OrderRequest: " + r.getRequestId());
//		logService.create(c, "SERVER", OrderFacadeBean.class.getSimpleName(),
//				contextService.getCallersUser().getName(), LogItemType.LOG,
//				"de.linogistix.los.imventory.res.Bundle",
//				"CREATED OrderRequest: " + r.getRequestId(),
//				InventoryLogKeys.CREATED_ORDERREQUEST.name(), new Object[] { r
//						.getRequestId() });
		return r;
	}
	
	public LOSOrderRequest create(Client c, Date startDate, LOSStorageLocation destination, OrderType orderType) {
		LOSOrderRequest order;
		
		order = new LOSOrderRequest();
		order.setOrderType(orderType);
		order.setDestination(destination);
		order.setDelivery(startDate==null?new Date():startDate);
		order.setClient(c);
		order.setNumber(genService.generateOrderNumber(c));
		order.setOrderState(LOSOrderRequestState.RAW);
		order.setRequestId(order.getNumber());

		manager.persist(order);
		manager.flush();

//		logService.create(order.getClient(), "SERVER",
//				OrderBusinessBean.class.getSimpleName(),
//				contextService.getCallersUser().getName(), LogItemType.LOG,
//				"de.linogistix.los.imventory.res.Bundle",
//				"CREATED ExtinguishOrder: " + order.getRequestId(),
//				InventoryLogKeys.CREATED_ORDERREQUEST.name(),
//				new Object[] { order.getRequestId() });
		return order;

	}

	
	protected void testLot(LOSOrderRequest order, Lot lot) throws InventoryException, OrderCannotBeStarted {
		
		switch(order.getOrderType()){
		case  INTERNAL:
			// everything goes
			return;
		default: break;
		}
		
		if (lot.isLocked()) {
			if (lot.getLock() == LotLockState.LOT_TOO_YOUNG.getLock()){
				log.error("Lot too young: " + lot.getName());
				throw new OrderCannotBeStarted(InventoryExceptionKey.LOT_TOO_YOUNG, lot.getName());
			} else{
				log.error("Lot is locked: " + lot.getName() + " with " + lot.getLock());
				throw new InventoryException(InventoryExceptionKey.LOT_ISLOCKED,
						lot.getName());
			}
		} else if (lot.getItemData().isLocked()) {
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_ISLOCKED, lot.getName());
		}

	}

	protected void testItemData(ItemData idat) throws InventoryException {
		if (idat.isLocked()) {
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_ISLOCKED, idat.getNumber());
		}
	}

	public void finishOrder(LOSOrderRequest req, boolean force)
			throws FacadeException {
		
		req = manager.find(LOSOrderRequest.class, req.getId());
		log.debug("finishOrder. order="+req.getNumber());
		
		switch(req.getOrderState()){
		case FINISHED: 
		case FAILED:
			log.warn("finishOrder. already has state FINISHED/FAILED: " + req.toString());
			return;
		}
		
		manageOrderService.processOrderFinishedStart(req);
		

		for (LOSOrderRequestPosition pos : req.getPositions()) {
			switch (pos.getPositionState()) {
			case FINISHED:
			case PICKED: // TODO don't waterfall
				req.setOrderState(LOSOrderRequestState.FINISHED);
				break;
			case PICKED_PARTIAL: {
				if (force || pos.isPartitionAllowed()){
					req.setOrderState(LOSOrderRequestState.FINISHED);
					break;
				} else{
					throw new InventoryException(InventoryExceptionKey.ORDER_NOT_FINIHED, pos.getNumber());
				}
			}
			case PENDING:
			case RAW:
			default:
				if (force || pos.isPartitionAllowed()){
					req.setOrderState(LOSOrderRequestState.FAILED);
				}
				else {
					throw new InventoryException(InventoryExceptionKey.ORDER_NOT_FINIHED, pos.getNumber());
				}
			}
		}
		

		switch(req.getOrderType()){
		case INTERNAL:
		case TO_PRODUCTION:
			req.setOrderState(LOSOrderRequestState.FINISHED);
			cleanup(req);
			break;
		case TO_CUSTOMER:
		case TO_OTHER_SITE:
			req.setOrderState(LOSOrderRequestState.FINISHED);
			cleanup(req);
			break;
		case TO_EXTINGUISH:
			req.setOrderState(LOSOrderRequestState.FINISHED);
			cleanup(req);
			//TODO: Create Vernichtungsreport
			break;
		case TO_REPLENISH:
			req.setOrderState(LOSOrderRequestState.FINISHED);
			break;
		default:
			log.warn("Unhandled state: " + req.getOrderType());
			return;
		}
		
		if( manageOrderService.createReceiptOnFinish(req) ) {
			createReceipt(req, manageOrderService.printReceiptOnFinish(req));
		}
		
		if( manageOrderService.printLabelOnFinish(req) ) {
			createLabels(req, true);
		}
			
		manageOrderService.processOrderFinishedEnd(req);

	}

	protected void cleanup(LOSOrderRequest req) throws FacadeException {
		
		switch (req.getOrderState()) {
		case FINISHED:
		case FAILED:
			log.info("cleanup: OK state is " + req.getOrderState());
			break;
		default:
			log.warn("cleanup: Wrong state: " + req.toDescriptiveString());
			return;
		}
		
		List<LOSPickRequest> picks = pickRequestService.getListByOrder(req); 
		
		for (LOSPickRequest pick : picks){
			
			pick = manager.find(pick.getClass(), pick.getId());
			
			for (LOSPickRequestPosition pos : pick.getPositions()){
				StockUnit su = pos.getStockUnit();
				LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
				LOSStorageLocation sl = ul.getStorageLocation();
				if ( ! sl.equals(pick.getDestination())){
					LOSStorageLocation nirwana = slService.getNirwana();
					if (su.getAmount().compareTo(new BigDecimal(0)) == 0 && sl.equals(nirwana)){
						log.info("Stockunit has been transferred to Nirwana: " + su.toDescriptiveString());
					} else{
						log.error("Wrong destination - expectecd " + pick.getDestination().getName() + ", but was: " + sl.getName() + " for " + pos.toDescriptiveString() );
						throw new InventoryException(InventoryExceptionKey.STORAGELOCATION_CONSTRAINT_VIOLATED, pick.getDestination().getName()+" / "+sl.getName());
					}
				}
				
				invComp.changeAmount(su, new BigDecimal(0), true, req.getNumber(), null, null, false);
				invComp.sendStockUnitsToNirwana(su, req.getNumber());
				invComp.sendUnitLoadToNirwanaIfEmpty(ul);
			}
			
//			try {
//				pickRequestService.delete(pick);
//			} catch (ConstraintViolatedException e) {
//				log.error(e.getMessage(), e);
//				throw new InventoryException(InventoryExceptionKey.ORDER_CONSTRAINT_VIOLATED, req.getNumber());
//			}
		}

		//		LOSStorageLocation sl = slService.getByName(req.getClient(), req.getNumber());
//		if (sl != null){
//			try {
//				slService.delete(sl);
//			} catch (ConstraintViolatedException e) {
//				log.error(e.getMessage(), e);
//				throw new LOSLocationException(LOSLocationExceptionKey.LOCATION_CONSTRAINT_VIOLATION, new String[]{sl.getName()});
//			}
//		}
	}


	public void remove(LOSOrderRequest req) throws PickingException, FacadeException {
		log.info("remove: ORDER="+(req == null ? "null" : req.getNumber()));
		if( req == null ) {
			log.error("remove: LOSOrderRequest=null. Abort");
			return;
		}
		
		switch(req.getOrderState()){
		case FAILED:
		case FINISHED:
		case RAW:
		case PROCESSING:
			break;
		default:
			throw new InventoryException(InventoryExceptionKey.ORDER_CONSTRAINT_VIOLATED, "");
		}
		
		for (LOSPickRequest pick : pickRequestService.getListByOrder(req)){
			pickService.removePickingRequest(pick);
		}
		
		try {
			orderrequestService.delete(req);
		} catch (ConstraintViolatedException e) {
			log.error(e.getMessage(), e);
			throw new InventoryException(InventoryExceptionKey.ORDER_CONSTRAINT_VIOLATED, "");
		}
	}

	private void createReceipt(LOSOrderRequest req, boolean print) {
		OrderReceipt receipt = null;
		try {
			
			receipt = orderReport.generateOrderReceipt(req.getClient(),
					DocumentTypes.APPLICATION_PDF.toString(), req);
			
			if (req.getDocumentUrl() != null && req.getDocumentUrl().length() > 0){
				byte[] erpReceipt = repService.httpGet(req.getDocumentUrl());
				if (erpReceipt != null){
					receipt.setDocument(erpReceipt);
					log.info("Got receipt from " + req.getDocumentUrl());
				} else{
					log.error("Cannot obtain ERP receipt at " + req.getDocumentUrl());
				}
			}
			
			if (print){
				String printer = null;
				try {
					printer = serviceConfig.getValue(PickOrderBusiness.class, req
							.getClient(), PickOrderBusiness.CONFKEY_PRINTER);
				} catch (EntityNotFoundException e) {
					log.error(e.getMessage(), e);
				}
				// TODO make configurable
				for (int i=0;i<5;i++){
					repService.print(printer, receipt.getDocument(),
							DocumentTypes.APPLICATION_PDF.toString());
				}
			}
		} catch (ReportException rex) {
			log.error(rex.getMessage(), rex);
//			logService.create(req.getClient(), "SERVER",
//					PickOrderBusinessBean.class.getSimpleName(), contextService
//							.getCallersUser().getName(), LogItemType.ERROR,
//					"de.linogistix.los.imventory.pick.res.Bundle",
//					"NOT PRINTED " + name,
//					PickingExceptionKey.PRINT_LABEL_FAILED.name(),
//					new Object[] { name });
		}

	}
	
	
	private void createLabels(LOSOrderRequest req, boolean print) {

		byte[] labels;
		try {
			if (req.getLabelUrl() == null || req.getLabelUrl().length() < 1){
				log.warn("No labels found at "  );
				return;
			}
			labels = repService.httpGet(req.getLabelUrl());
			log.info(" Got labels from "+ req.getLabelUrl());
			
			if (print && labels != null){
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
//			logService.create(req.getClient(), "SERVER",
//					PickOrderBusinessBean.class.getSimpleName(), contextService
//							.getCallersUser().getName(), LogItemType.ERROR,
//					"de.linogistix.los.imventory.pick.res.Bundle",
//					"NOT PRINTED ",
//					PickingExceptionKey.PRINT_LABEL_FAILED.name(),
//					new Object[] { });
		}

	}

	public void processOrderPositionPicked(LOSOrderRequestPosition pos,
			BigDecimal amount, boolean canceled) throws FacadeException {

		pos = manager.find(LOSOrderRequestPosition.class, pos.getId());
		log.debug("processOrderPositionPicked. pos="+pos.getNumber()+", state="+pos.getPositionState()+", amount="+amount+", cancelled="+canceled);
		
		Request prox = pos.getParentRequest();
		LOSOrderRequest r;
		r = manager.find(LOSOrderRequest.class, prox.getId());

		// Replenish orders are closed in every case
		if( OrderType.TO_REPLENISH.equals(r.getOrderType())) {
			canceled = true;
		}
		
		switch (pos.getPositionState()) {
		case PROCESSING:
		case PENDING:{
			
			pos.setPickedAmount(pos.getPickedAmount().add(amount));
			
			if (pos.getAmount().compareTo(pos.getPickedAmount()) == 0 ) {
				pos.setPositionState(LOSOrderRequestPositionState.PICKED);
				processOrderPicked(r);
			} else if (pos.getAmount().compareTo(pos.getPickedAmount()) > 0) {
				if (canceled) {
					pos.setPositionState(LOSOrderRequestPositionState.PICKED_PARTIAL);
					processOrderPicked(r);
				} else {
					pos.setPositionState(LOSOrderRequestPositionState.PENDING);
					processOrderPicked(r);
				}
			} else {
				pos.setPositionState(LOSOrderRequestPositionState.PICKED);
				processOrderPicked(r);
//				log.error("Picked too many: ");
//				logService.create(pos.getClient(), "SERVER",
//						OrderBusinessBean.class.getSimpleName(), contextService
//								.getCallersUser().getName(), LogItemType.ERROR,
//						"de.linogistix.los.imventory.res.Bundle",
//						"PICKED TOO MANY: " + pos.getNumber(),
//						InventoryExceptionKey.PICKED_TOO_MANY.name(),
//						new Object[] { pos.getNumber() });
			}
			break;
		}
		default: {
			log.error("processOrderPositionPicked. transition not allowed: " + pos.getNumber());
			return;
		}
		}

	}

	public void processOrderPicked(LOSOrderRequest order) throws FacadeException {
		processOrderPicked(order, false);
	}
	
	public void processOrderPicked(LOSOrderRequest order, boolean force)
			throws FacadeException {

		order = manager.find(LOSOrderRequest.class, order.getId());
		log.debug("processOrderPicked: order="+order.getNumber()+", state="+order.getOrderState()+", force="+force);

		switch (order.getOrderState()) {
		case PROCESSING:
		case RAW:
		case PENDING:
			break;
		default:
//			throw new RuntimeException("processOrderPicked: state not allowed. order="+order.getNumber()+", state="+order.getOrderState());
			log.error("processOrderPicked: state not allowed. order="+order.getNumber()+", state="+order.getOrderState());
			return;
		}

		for (LOSOrderRequestPosition pos : order.getPositions()) {
			pos = manager.find(LOSOrderRequestPosition.class, pos.getId());
			log.debug("processOrderPicked: check pos="+pos.getNumber()+", state="+pos.getPositionState());
			
			switch (pos.getPositionState()) {
			case PICKED_PARTIAL:
				order.setOrderState(LOSOrderRequestState.PICKED_PARTIAL);
				break;
			case FINISHED:
			case PICKED: {
				if (!order.getOrderState().equals(LOSOrderRequestState.PICKED_PARTIAL)){
					order.setOrderState(LOSOrderRequestState.PICKED);
				} else{
					order.setOrderState(LOSOrderRequestState.PICKED_PARTIAL);
				}
				break;
			}
			case RAW:
			case PROCESSING: {
				if( force ) {
					log.info("processOrderPicked: force closing unfinished position. pos=" + pos.getNumber());
					order.setOrderState(LOSOrderRequestState.PICKED_PARTIAL);
					pos.setPositionState(LOSOrderRequestPositionState.FAILED);
				}
				else {
					order.setOrderState(LOSOrderRequestState.PROCESSING);
					log.info("processOrderPicked: still unfinished positions. pos="	+ pos.getNumber());
					return;
				}
				break;
			}
			case PENDING: {
				if( force ) {
					log.info("processOrderPicked: force closing pending position. pos="	+ pos.getNumber());
					order.setOrderState(LOSOrderRequestState.PICKED_PARTIAL);
					pos.setPositionState(LOSOrderRequestPositionState.FAILED);
				}
				else {
					order.setOrderState(LOSOrderRequestState.PENDING);
					log.info("processOrderPicked: still pending positions. pos="	+ pos.getNumber());
					return;
				}
				break;
			}
			default: {
				log.error("processOrderPicked: transition not allowed. pos="+ pos.getNumber());
				return;
			}
			}
		}

		
		switch(order.getOrderState()){
		case PICKED:
		case PICKED_PARTIAL:
			log.info("processOrderPicked: order is picked. Order=" + order.getNumber());
			switch(order.getOrderType()){
			case INTERNAL:
				finishOrder(order, force);
				break;
			case TO_PRODUCTION:
			case TO_REPLENISH:
			case TO_EXTINGUISH:
				LOSGoodsOutRequest out = outBusiness.create(order);
				outBusiness.finish(out, true);
				finishOrder(order, force);
				break;
			case TO_CUSTOMER:
			case TO_OTHER_SITE:
				if( force ) {
					finishOrder(order, force);
				}
				else {
					outBusiness.create(order);
				}
				break;
			default:
				log.warn("Unhandled type - going to finish anyway: " + order.getOrderType());
				finishOrder(order, force);
			}
			
			if( manageOrderService.createReceiptOnPicked(order) ) {
				createReceipt(order, manageOrderService.printReceiptOnPicked(order));
			}
			
			if( manageOrderService.printLabelOnPicked(order) ) {
				createLabels(order, true);
			}

			break;	
		default: 
			log.info("Unhandled state - Nothing to to do: " + order.toDescriptiveString());
		}

		
		manageOrderService.processOrderPickedEnd(order);
	}

	public void process(LOSOrderRequest r) throws FacadeException, OrderCannotBeStarted {

		r = manager.find(LOSOrderRequest.class, r.getId());
		
		if (!r.getOrderState().equals(LOSOrderRequestState.RAW)){
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_STARTED, r.getNumber());
		}
		
		ItemData idat;
		Lot lot;
		OrderCannotBeStarted orderCOrderCannotBeStarted = null;
		try {

			
			for (LOSOrderRequestPosition to : r.getPositions()) {
				if (to.getLot() == null ) {
					lot = null;
					idat = to.getItemData();
					testItemData(idat);
				} else {
					
					lot = to.getLot();
					
					idat = lot.getItemData();
					try{
						testLot(r,lot);
					} catch (OrderCannotBeStarted ex){
						log.error(ex.getMessage());
						orderCOrderCannotBeStarted = ex;
					}
				}
			}
			
			if (orderCOrderCannotBeStarted == null){
				pickService.processOrderRequest(r).get(0);
			} else{
				switch (r.getOrderType()) {
				case INTERNAL:
					pickService.processOrderRequest(r).get(0);
					break;
				default:
					throw orderCOrderCannotBeStarted;
				}
			}
		} catch (FacadeException t) {
			log.error(t.getMessage());
			log.error("CREATE OrderRequest FAILED: " + r.getNumber());
			throw t;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(
					InventoryExceptionKey.PICKORDER_CANNOT_BE_CREATED, t.getLocalizedMessage());

		}
	}


}
