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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.globals.PickingRequestState;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;
import org.mywms.model.SubstitutionType;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.ManageExtinguishFacade;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestPositionState;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.ExtinguishRequestService;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.service.ExtinguishOrderService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.inventory.service.QueryStockService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

@Stateless
public class ExtinguishBusinessBean implements ExtinguishBusiness {

	Logger log = Logger.getLogger(ExtinguishBusinessBean.class);

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	@EJB
	private LOSStorageLocationService loStorageLocationService;

	@EJB
	private LOSPickRequestPositionService pickingPositionService;

	@EJB
	private ExtinguishRequestService exRequestService;

	@EJB
	private QueryStockService stockService;
	
	@EJB
	private ClientService clientService;

	@EJB
	private ContextService contextService;

	@EJB
	private LOSLotService lotService;

	@EJB
	private ExtinguishOrderService extinguishOrderService;

	@EJB
	private ContextService context;

	@EJB
	private LOSServicePropertyService configService;

	@EJB
	private OrderRequestService orderService;
	
	@EJB
	private LOSStorageLocationTypeService locTypeService;
	
	@Resource
	private TimerService timerService;

	

	
	public ExtinguishRequest process(ExtinguishRequest req)
			throws InventoryException {

		LOSStorageLocation cart;
		LOSStorageLocationType type;
		Lot lot = req.getLot();

		type = locTypeService.getNoRestrictionType();
		if (type == null ) throw new RuntimeException("Type without restriction not found ");
		cart = loStorageLocationService.createStorageLocation(lot
				.getClient(), req.getNumber(), type);
		
		req.setCart(cart);
		LOSStorageLocation nirwana = loStorageLocationService.getNirwana();
		
		int index = 0;
		List<StockUnit> sus = getAffectedStockUnit(lot);
		if (sus.size() < 1){
			log.warn("nothing to do. No affected StockUnits to extinguish");
			req.setState(PickingRequestState.FINISHED);
		} else{
			for (StockUnit su : sus) {
				
				su = manager.find(StockUnit.class, su.getId());
				
				LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
				if (ul.getStorageLocation().equals(nirwana)){
					log.info("Already on Nirwana: " + su.toDescriptiveString());
					continue;
				}
	
				if (su.getAvailableAmount().compareTo(new BigDecimal(0)) > 0){
					su.addReservedAmount(su.getAvailableAmount());
				} else if (su.getAmount().compareTo(new BigDecimal(1)) < 0){
					log.error("Stockunit without amount " + su.toDescriptiveString());
					continue;
				}
				ExtinguishOrder order = (ExtinguishOrder) req.getParentRequest();
				order = manager.find(ExtinguishOrder.class, order.getId());
				
				int posI = orderService.addPosition(order, lot.getItemData(), lot, su.getAmount(), false);
				order = manager.find(ExtinguishOrder.class, order.getId());
				LOSOrderRequestPosition pos = order.getPositions().get(posI);
				pos = manager.find(LOSOrderRequestPosition.class, pos.getId());
				pos.setPositionState(LOSOrderRequestPositionState.PROCESSING);
				LOSPickRequestPosition prp = pickingPositionService.create(req, pos,su,
						su.getAmount(),SubstitutionType.SUBSTITUTION_NOT_ALLOWED, PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT );
				prp.setIndex(index);
				
				manager.merge(prp);
				index++;
			}
		}

		req.setUser(contextService.getCallersUser());
		manager.persist(req);
		manager.flush();

		return req;

	}

	public ExtinguishRequest create(ExtinguishOrder order) throws InventoryException {
		Lot lot  = order.getLot();
		
		Client client = lot.getClient();
		if (client == null) {
			log.error("NullpointerException from the Client of Lot");
			throw new NullPointerException("The client of Lot is null.");
		}

		LOSStorageLocation sl = loStorageLocationService.getNirwana();

		if (sl == null) {
			log.error("Nirwana not found ");
			throw new NullPointerException("Nirwana not found");
		}

		// create an extinguish request
		ExtinguishRequest req = exRequestService.create(client, order, lot, sl);
		req = process(req);
		return req;
	}

	public List<StockUnit> getAffectedStockUnit(Lot lot)
			throws InventoryException {
		return stockService.getListByLot(lot, false);
	}

	public ExtinguishOrder createExtinguishOrder(Lot lot, Date startDate)
			throws InventoryException {
		ExtinguishOrder order;

		try {
			order = extinguishOrderService.getByLot(lot);
			log.warn("EXISTS: no new ExtinguishOrder is created for lot " + lot.toUniqueString());
		} catch (EntityNotFoundException ex) {
			order = extinguishOrderService.create(lot, startDate);
		}

		return order;

	}

	public ExtinguishOrder startExtinguishOrder(ExtinguishOrder order)
			throws InventoryException {

		order = manager.find(ExtinguishOrder.class, order.getId());
		
		if (!order.getOrderState().equals(LOSOrderRequestState.RAW)){
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_STARTED, order.getNumber());
		}
				
		ExtinguishRequest ext = create(order);
		
		ext.setParentRequest(order);

		order.setAuthorizedBy(contextService.getCallersUser());
		order.setOrderState(LOSOrderRequestState.PROCESSING);
		
		Lot lot = manager.find(Lot.class,order.getLot().getId());
		lot.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
		
//		manager.merge(order);
		manager.flush();

		return order;
	}

	public void createCronJob() {
		
		Long intervall = 3600 * 1000L;

		String value;
		try {
			value = configService.getValue(ExtinguishBusiness.class, context
					.getCallersUser().getClient(),
					ManageExtinguishFacade.TIME_OUT_KEY);
			if (value!= null){
				try{
					intervall = Long.parseLong(value);
				} catch (NumberFormatException ex){
					log.warn(ex.getMessage());
					intervall = 3600 * 1000L;
				}
			} 
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}

		timerService.createTimer(intervall,	"going to lock extinguishable Lots");

	}

	@Timeout
	public void timeout(Timer timer) {
		log.info(this.getClass().getSimpleName() + ": Timeout occurred");
		processLots();
	}

	public void processLots() {

		Client c = clientService.getSystemClient();
		// List<ExtinguishOrder> orders = extinguishOrderService.getRipe();
		//
		// for (ExtinguishOrder order : orders) {
		// Lotlot = order.getLot();
		// lot.setLock(LotLockState.LOT_EXPIRED.getLock());
		// log.info("Timeout occurred:" + "Lot" + lot.getName()
		// + " is locked");
		// }

		List<Lot> lots = getTooOld(c);

		for (Lot l : lots) {
			l = manager.find(Lot.class, l.getId());
			log.warn("Will be locked with LotLockState.LOT_EXPIRED: "
					+ l.toDescriptiveString());
			l.setLock(LotLockState.LOT_EXPIRED.getLock());
		}

		lots = getNotToUse(c);

		for (Lot l : lots) {
			l = manager.find(Lot.class, l.getId());
			log.warn("Will be locked with LotLockState.LOT_TOO_YOUNG: "
					+ l.toDescriptiveString());
			l.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
		}

		lots = getToUseFromNow(c);

		for (Lot l : lots) {
			l = manager.find(Lot.class, l.getId());
			if (l.getLock() != LotLockState.LOT_TOO_YOUNG.getLock()) {
				log.warn("has not been in lotLockState.LOT_TOO_YOUNG ");
				continue;
			}
			log.info("Will be unlocked from LotLockState.LOT_TOO_YOUNG: "
					+ l.toDescriptiveString());
			l.setLock(BusinessObjectLockState.NOT_LOCKED.getLock());
		}
	}

	public List<Lot> getNotToUse(Client c) {
		return lotService.getNotToUse(c);
	}

	public List<Lot> getToUseFromNow(Client c) {
		return lotService.getToUseFromNow(c);
	}

	public List<Lot> getTooOld(Client c) {
		return lotService.getTooOld(c);
	}

}
