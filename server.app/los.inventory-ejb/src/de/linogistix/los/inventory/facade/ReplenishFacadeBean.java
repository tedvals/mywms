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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.businessservice.ReplenishBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;
@Stateless
public class ReplenishFacadeBean implements ReplenishFacade{

	@EJB
	ReplenishBusiness replenishBusiness;

	@EJB
	ContextService contextService;
	
	@EJB
	LOSStorageLocationService slService;
	
	@EJB
	LOSStorageLocationQueryRemote slQuery;

	
	@EJB
	LOSFixedLocationAssignmentService fixService;
	
	@EJB
	OrderRequestService orderService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	public void replenish(List<BODTO<LOSStorageLocation>> locs)
			throws FacadeException {

		for (BODTO<LOSStorageLocation> loc : locs) {
			LOSStorageLocation l = manager.find(LOSStorageLocation.class, loc
					.getId());
			replenishBusiness.create(l, new BigDecimal(-1));
		}
	}

	public void replenish(String storageLocation, BigDecimal amount)
			throws FacadeException {
		LOSStorageLocation sl = slService.getByName(storageLocation);
		if( sl == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, storageLocation);
		}
		replenishBusiness.create(sl, amount);
	}

	public void createCronJob() {
		replenishBusiness.createCronJob();
		
	}

	public void createReplenishmentIfNeeded() throws FacadeException{
		replenishBusiness.createReplenishmentIfNeeded();
		
	}

	public void cancelCronJob() throws Exception {
		replenishBusiness.cancelCronJob();
		
	}

	public String statusCronJob() {
		return replenishBusiness.statusCronJob();
	}

	public ItemDataTO getItemDataByLocation(String storageLocationName) throws FacadeException{

		LOSStorageLocation loc = slService.getByName(storageLocationName);
		if( loc == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, storageLocationName);
		}
		LOSFixedLocationAssignment fix = fixService.getByLocation(loc);
		if( fix == null ) {
			throw new InventoryException(InventoryExceptionKey.NOT_A_FIXED_ASSIGNED_LOCATION, new Object[]{});
		}
		
		
		List<LOSOrderRequest> active = orderService.getActiveByDestination(loc);
		if (active != null && active.size() > 0) {
			throw new InventoryException(InventoryExceptionKey.REPLENISH_ALREADY_COMES, new Object[]{});
		}

		ItemDataTO item = new ItemDataTO(fix.getItemData());

		return item;
	}
	
}
