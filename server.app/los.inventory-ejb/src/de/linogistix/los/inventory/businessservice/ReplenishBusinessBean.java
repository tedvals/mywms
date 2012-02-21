/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.io.NotSerializableException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.User;
import org.mywms.service.ClearingItemService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.facade.ReplenishFacade;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.pick.exception.NullAmountNoOtherException;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.inventory.service.ReplenishRequestService;
import de.linogistix.los.location.businessservice.LOSFixedLocationAssignmentComp;
import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSServicePropertyService;

@Stateless
public class ReplenishBusinessBean implements ReplenishBusiness {

	private final static Logger log = Logger
			.getLogger(ReplenishBusinessBean.class);

	@EJB
	OrderRequestService orderService;
	@EJB
	LOSFixedLocationAssignmentComp assComp;
	@EJB
	LOSFixedLocationAssignmentService assService;
	@EJB
	UnitLoadTypeQueryRemote ulTypeQuery;
	@EJB
	LOSInventoryComponent invService;
	@EJB
	LOSAreaService areaService;
	@EJB
	InventoryGeneratorService genService;
	@EJB
	PickOrderBusiness pickOrderBusiness;
	@EJB
	ReplenishRequestService replenishService;
	@EJB
	ContextService context;
	@EJB
	LOSServicePropertyService configService;
	@EJB
	ClearingItemService clearingService;
	@EJB
	QueryInventoryBusiness queryInventoryBusiness;
	
//	@Resource
//	TimerService timerService;

	@Resource
	SessionContext ctx;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@SuppressWarnings("unchecked")
	public List<LOSReplenishRequest> createReplenishmentIfNeeded()
			throws InventoryException, FacadeException {
		List<LOSReplenishRequest> ret = new ArrayList<LOSReplenishRequest>();
		LOSAreaType pickType = LOSAreaType.PICKING;
		// find locations with UnitLoads on and free places left
		StringBuffer sb = new StringBuffer("SELECT ass FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName() + " ass ");
		sb.append(" WHERE ass.assignedLocation.area.areaType = :pick");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("pick", pickType);

		List<LOSFixedLocationAssignment> fixed = query.getResultList();

		for (LOSFixedLocationAssignment ass : fixed) {
			log.info("Check location " + ass.getAssignedLocation().getName());
			
			BigDecimal amount = invService.getAmountOfStorageLocation(ass
					.getItemData(), ass.getAssignedLocation());
			
			if (ass.getDesiredAmount() == null || ass.getDesiredAmount().compareTo(new BigDecimal(0)) == 0) {
				log.warn("Desired amount not configured for "
						+ ass.getAssignedLocation().getName());
				continue;
			}
			
			BigDecimal d = ass.getDesiredAmount().multiply(new BigDecimal(0.25));
			if( amount.compareTo(d) >= 0 ) {
				log.info("There is still enough material on location " + ass.getAssignedLocation().getName());
				continue;
			}

			try {
				create(ass.getAssignedLocation(), null);
			} catch (InventoryTransactionException ex) {
				log.warn(ex.getMessage());
				continue;
			}
		}

		return ret;
	}

	public LOSReplenishRequest create(LOSStorageLocation sl, BigDecimal amount)
			throws FacadeException {
		LOSReplenishRequest r = null;
		String orderRef;
		Lot lot = null;
		BigDecimal locationAmount = BigDecimal.ZERO;
		
		LOSFixedLocationAssignment ass = assService.getByLocation(sl);
		if( ass == null ) {
			log.info("Location " + sl.getName() + " is not fix assigned");
			throw new InventoryTransactionException(InventoryExceptionKey.NOT_A_FIXED_ASSIGNED_LOCATION, sl.getName());
		}
			
		List<LOSOrderRequest> active = orderService.getActiveByDestination(sl);
		if (active != null && active.size() > 0) {
			log.info("There is already replenishment ordered for location " + sl.getName());
			throw new InventoryTransactionException(InventoryExceptionKey.REPLENISH_ALREADY_COMES);
		}

		List<LOSUnitLoad> uls = ass.getAssignedLocation().getUnitLoads();
		for( LOSUnitLoad ul : uls ) {
			for (StockUnit su : ul.getStockUnitList()) {
				if( !su.getItemData().equals(ass.getItemData()) ) {
					throw new InventoryTransactionException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{su.getItemData().getNumber(),ass.getItemData().getNumber()});
				}
				lot = su.getLot();
				locationAmount = locationAmount.add(su.getAmount());
			}
		}
		if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
			amount = ass.getDesiredAmount().subtract(locationAmount);
		}
		if( BigDecimal.ZERO.compareTo(amount) >= 0 ) {
			log.info("No Replenish needed for location " + sl.getName());
			throw new InventoryTransactionException(InventoryExceptionKey.REPLENISH_NOT_NEEDED);
		}
		
		log.info("Going to create replenish for " + sl.getName());
		
		Client client = sl.getClient();
		orderRef = genService.generateReplenishNumber(client);
		
		
		
		List<StockUnit> sus1 = null;
		
		try{
			 sus1 = pickOrderBusiness.suitableStockUnitsByItemDataWithoutLotNoException(ass.getItemData(), amount, true);
		} catch (NullAmountNoOtherException ex){
			log.warn("Exception during replenishment: " + ex.getMessage());
			clearNoStockUnit(ass);
			if (ex.getKey().equals(InventoryExceptionKey.NO_STOCKUNIT.toString())){
				log.info("No Stock available for ItemData " + ass.getItemData().getNumber());
				throw new InventoryTransactionException(InventoryExceptionKey.NO_STOCKUNIT, ass.getItemData().getNumber() );
			}
		}
		
		List<StockUnit> sus = new ArrayList<StockUnit>();
		for( StockUnit su : sus1 ) {
			LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
			if( ul.getStorageLocation().equals(ass.getAssignedLocation()) ) {
				log.info("Do not take replenish from targetlocation");
				continue;
			}
			if( lot != null && su.getLot() != null && !lot.equals(su.getLot())) {
				log.info("Lot mismatch. On location is " + lot.getName() + ", Stock has " + su.getLot().getName());
				continue;
			}

			sus.add(su);
			
			if( lot == null && su.getLot() != null ) {
				lot = su.getLot();
			}
		}
		if( sus.size() == 0 ) {
			log.info("No Stock available for ItemData " + ass.getItemData().getNumber());
			throw new InventoryTransactionException(InventoryExceptionKey.NO_STOCKUNIT, ass.getItemData().getNumber() );
		}
		
		LOSReplenishRequest order = replenishService.create(client,
				orderRef, new Date(), sl, null, null);
		int index = replenishService.addPosition(order, ass.getItemData(),
				lot, amount, true);
		order = manager.find(LOSReplenishRequest.class, order.getId());
		LOSOrderRequestPosition pos = order.getPositions().get(index);
		log.info("CREATED REPLENISHMENT " + order.toDescriptiveString());
		log.info("... WITH POSITION:" + pos.toDescriptiveString());

		if (sus != null){
			pickOrderBusiness.processOrderRequest(order, sus, amount);
		} else{
			pickOrderBusiness.processOrderRequest(order);
		}

		return r;

	}

	private String getClearingSource(LOSFixedLocationAssignment ass){
		String source = CLEARING_REPL_NOSTOCKUNIT_SOURCEPREF + ass.getItemData().getNumber() + "(" + ass.getAssignedLocation().getName() + ")";
		return source;
	}
	
	private void unclearNoStockUnit(LOSFixedLocationAssignment ass){
		
		String source = getClearingSource(ass);
		List<ClearingItem> l = clearingService.getNondealChronologicalList(null, null, source, null, -1);
		
		if (l != null && l.size() > 0){
			for (ClearingItem i : l){
				try {
					clearingService.delete(i);
				} catch (ConstraintViolatedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private void clearNoStockUnit(LOSFixedLocationAssignment ass){
		User u = context.getCallersUser();
		Client c = u.getClient();
		
		String host;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "?";
		}
		
		String source = getClearingSource(ass);
		
		String available;
		try {
			QueryInventoryTO[] tos = queryInventoryBusiness.getInventory(c, ass.getItemData(), true);
			if (tos == null || tos.length == 0){
				available = BigDecimal.ZERO.toString();
			} else{
				available = tos[0].available.toString();
			}
		} catch (InventoryException e1) {
			available = "?";
		}

		List<ClearingItem> l = clearingService.getNondealChronologicalList(null, null, source, null, -1);
		
		if (l != null && l.size() > 0){
			// clearing already exists!
			return;
		}
		
		String resourceBundleName = "de.linogistix.los.inventory.res.Bundle";
		String[] shortMessageParameters = new String[]{ass.getItemData().getNumber(), ass.getAssignedLocation().getName()};
		String[] messageParameters = new String[]{ass.getItemData().getNumber(), ass.getDesiredAmount().toString(), available, ass.getItemData().getHandlingUnit().toUniqueString(), ass.getAssignedLocation().getName()};
		
		ArrayList<ClearingItemOption> options = new ArrayList<ClearingItemOption>();
		ClearingItemOption confirm = new ClearingItemOption();
		confirm.setMessageResourceKey(CLEARING_REPL_NOSTOCKUNIT_CONFIRM);
		options.add(confirm);
		
		try {
			clearingService.create(
					c,host, source, u.getName(),
					CLEARING_REPL_NOSTOCKUNIT_MSG, CLEARING_REPL_NOSTOCKUNIT_SHORT, resourceBundleName, 
					InventoryBundleResolver.class, shortMessageParameters, messageParameters, options);
		} catch (NotSerializableException e) {
			log.error(e.getMessage());
		}
	}
	
	public void createCronJob() {
		
		Long intervall = 10 * 1000L;

		String value;
		try {
			value = configService
					.getValue(ReplenishFacade.class, context.getCallersUser()
							.getClient(), ReplenishFacade.TIME_OUT_KEY);
			if (value != null) {
				try {
					intervall = Long.parseLong(value);
				} catch (NumberFormatException ex) {
					log.warn(ex.getMessage());
					intervall = 10 * 1000L;
				}
			}
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		if (getTimer() != null){
			cancelCronJob();
		}
		TimerService timerService = ctx.getTimerService();
	    timerService.createTimer(new Date(System.currentTimeMillis() + intervall), intervall,
				ReplenishFacade.TIME_OUT_INFO);

	}

	@SuppressWarnings("unchecked")
	public void cancelCronJob() {
		TimerService timerService = ctx.getTimerService();
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(ReplenishFacade.TIME_OUT_INFO)) {
					timer.cancel();
					log.info("Cancel timer: " + timer.toString());
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Timer getTimer() {
		TimerService timerService = ctx.getTimerService();
		for (Timer timer : (Collection<Timer>) timerService.getTimers()) {
			if (timer.getInfo() instanceof String) {
				if (((String) timer.getInfo())
						.equals(ReplenishFacade.TIME_OUT_INFO)) {
					log.info(" " + timer.toString());
					return timer;
				}
			}
		}
		
		return null;
	}
	
	public String statusCronJob() {
		String stat = null;
		Timer timer = getTimer();
		if (timer != null){
			log.info(" " + timer.toString());
			stat = "Running. Remaining: " + timer.getTimeRemaining();
			return stat;
		}
		
		return "Not running";
	}

	@Timeout
	public void timeout(Timer timer) {
		try {
			log.info("create replenishment if needed");
			createReplenishmentIfNeeded();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

}
