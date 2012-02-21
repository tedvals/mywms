/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.model.Zone;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.StockUnitService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.businessservice.LOSFixedLocationAssignmentComp;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.ClientQueryRemote;

/**
 * 
 * @author trautm
 */
@Stateless
public class StorageBusinessBean extends BasicFacadeBean implements
		StorageBusiness {

	private static final Logger log = Logger
			.getLogger(StorageBusinessBean.class);
	@EJB
	private LOSStorage storageLocService;
	@EJB
	private LOSInventoryComponent invComp;
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private ClientQueryRemote clQuery;
	@EJB
	private StockUnitService suService;
	@EJB
	private LOSFixedLocationAssignmentComp assignService;
	@EJB
	private QueryFixedAssignmentService fixService;
	@EJB
	private LOSInventoryComponent inventoryComponent;
	@EJB
	private QueryUnitLoadTypeService ultService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	/**
	 * 
	 * The created request contains no destination information yet
	 * 
	 * @param c
	 * @param ul
	 * @return
	 * @throws de.linogistix.los.inventory.exception.InventoryException
	 * @throws org.mywms.service.EntityNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public LOSStorageRequest getOrCreateStorageRequest(Client c, LOSUnitLoad ul)
			throws FacadeException, EntityNotFoundException {
		LOSStorageRequest ret;

		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label "
				+ " AND req.requestState = :rstate ");

		query.setParameter("label", ul.getLabelId());
		query.setParameter("rstate", LOSStorageRequestState.RAW);

		List<LOSStorageRequest> list = query.getResultList();
		if (list == null || list.size() < 1) {
			ret = new LOSStorageRequest();
			ret.setUnitLoad(ul);
			ret.setRequestState(LOSStorageRequestState.RAW);
			ret.setNumber(genService.generateStorageRequestNumber(c));
			List<LOSStorageLocation> locs = suitableDestination(ul);
			if (locs != null && locs.size() > 0) {
				LOSStorageLocation sl = locs.get(0);
				sl.setReservedCapacity(1);
				ret.setDestination(sl);
			} else {
				throw new InventoryException(
						InventoryExceptionKey.STORAGE_NO_DESTINATION_FOUND,
						new Object[] { ul.getLabelId() });
			}
			ret.setClient(c);
			manager.persist(ret);
			log.info("CREATED LOSStorageRequest for " + ul.getLabelId());
			return ret;
		} else {
			log.warn("FOUND existing LOSStorageRequest for " + ul.getLabelId());
			return list.get(0);
		}

	}

	@SuppressWarnings("unchecked")
	public LOSStorageRequest getOpenStorageRequest( String unitLoadLabel ) {

		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label "
				+ " AND req.requestState in (:raw,:processing) ");

		query.setParameter("label", unitLoadLabel);
		query.setParameter("raw", LOSStorageRequestState.RAW);
		query.setParameter("processing", LOSStorageRequestState.PROCESSING);

		List<LOSStorageRequest> list = query.getResultList();
		if( list.size() > 0 ) {
			return list.get(0);
		}
		return null;
	}
	
	public LOSStorageRequest assignStorageLocation(LOSStorageRequest r)
			throws FacadeException, LOSLocationException {
		LOSStorageLocation sl;

		if (r.getDestination() != null) {
			sl = r.getDestination();
			storageLocService.releaseStorageLocation(sl, r.getUnitLoad());
		}

		List<LOSStorageLocation> locs = suitableDestination(r.getUnitLoad());
		if (locs == null || locs.size() < 1) {
			throw new InventoryException(
					InventoryExceptionKey.NO_SUITABLE_LOCATION, r
							.toUniqueString());
		} else {
			sl = locs.get(0);
		}
		storageLocService.reserveStorageLocation(sl, r.getUnitLoad());
		r.setDestination(sl);
		r.setRequestState(LOSStorageRequestState.PROCESSING);

		return r;

	}

	public void finishStorageRequest(LOSStorageRequest req,
			LOSStorageLocation sl, boolean force) throws FacadeException,
			LOSLocationException {
		// transfer UnitLoad
		LOSUnitLoad ul;
		LOSStorageLocation assigned;
		
		ul = manager.find(LOSUnitLoad.class, req.getUnitLoad().getId());
		sl = manager.find(LOSStorageLocation.class, sl.getId());
		req = manager.find(LOSStorageRequest.class, req.getId());
		
		assigned = manager.find(LOSStorageLocation.class, req.getDestination()
				.getId());
		if (!(sl.equals(assigned))) {
			if (testSuitableDestination(sl, req.getUnitLoad())) {
				
				// Do not ask questions in front-end.
				// Do not place front-end logic into back-end
				force = true;
				
				if (force){
					transferUnitLoad( req, ul, sl );
					storageLocService.releaseStorageLocation(assigned, ul);
				} else{
					throw new InventoryException(
							InventoryExceptionKey.STORAGE_WRONG_LOCATION_BUT_ALLOWED,
							sl.getName());
				}
			} else {
				throw new InventoryException(
						InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED,
						sl.getName());
			}
		} else {
			transferUnitLoad( req, ul, assigned );
		}
		req.setRequestState(LOSStorageRequestState.TERMINATED);
		
		assignService.getAssignedLocationsForStorage(null, null);
	}

	private void transferUnitLoad(LOSStorageRequest req, LOSUnitLoad unitload, LOSStorageLocation targetLocation ) throws FacadeException {
		
		LOSFixedLocationAssignment fix = fixService.getByLocation(targetLocation);
		if( fix != null ) {
			for( StockUnit su : unitload.getStockUnitList() ) {
				if( !su.getItemData().equals(fix.getItemData()) ) {
					throw new InventoryException(InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED, targetLocation.getName());
				}
			}
			
			if (targetLocation.getUnitLoads() != null && targetLocation.getUnitLoads().size() > 0) {
				// There is already a unit load on the destination. => Add stock
				LOSUnitLoad targetUl = null;
				for( LOSUnitLoad ul : targetLocation.getUnitLoads() ) {
					if( ul.getLabelId().equals(targetLocation.getName() ) ) {
						targetUl = ul;
						break;
					}
				}
				if( targetUl == null ) {
					targetUl = targetLocation.getUnitLoads().get(0);
				}
				
				inventoryComponent.transferStock(unitload, targetUl, req.getNumber());
				storageLocService.sendToNirwana( getCallersUsername(), unitload);
				log.info("Transferred Stock to virtual UnitLoadType: "+unitload.toShortString());
			} else {
				UnitLoadType virtual = ultService.getPickLocationUnitLoadType();
				unitload.setType(virtual);
				unitload.setLabelId(targetLocation.getName());
				storageLocService.transferUnitLoadOnReservedLocation( getCallersUsername(), targetLocation, unitload, false);

			}
		} 
		else {
			storageLocService.transferUnitLoadOnReservedLocation( getCallersUsername(), targetLocation, unitload, false);
		}
	}
	
	
	public void finishStorageRequest(LOSStorageRequest req,
			LOSUnitLoad destination) throws FacadeException {
		
		// zuschuetten
		LOSUnitLoad from = manager.find(LOSUnitLoad.class, req.getUnitLoad().getId());
		destination = manager.find(LOSUnitLoad.class, destination.getId()); 
		req = manager.find(LOSStorageRequest.class, req.getId());
		// TODO make choosable from gui
		switch(destination.getPackageType()){
		case OF_SAME_LOT: 
			destination.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
			manager.flush();
			break;
		case OF_SAME_LOT_CONSOLIDATE:
			break;
		default:
			throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_CONSTRAINT_VIOLATED, destination.getLabelId());
		}
		
		invComp.transferStock(from, destination,  req.getNumber());
		
		LOSStorageLocation orig = manager.find(LOSStorageLocation.class, req.getDestination().getId());
		storageLocService.releaseStorageLocation(orig, from);
		
		// if UnitLoad empty, send to nirwana
		if (req.getUnitLoad().getStockUnitList().isEmpty()) {
			LOSUnitLoad u = manager.find(LOSUnitLoad.class, req.getUnitLoad().getId());
			storageLocService.sendToNirwana(getCallersUsername(), u);
		}
		
		req.setRequestState(LOSStorageRequestState.TERMINATED);
		
	}

	// ------------------------------------------------------------------------

	public List<LOSStorageLocation> suitableDestination(LOSUnitLoad ul) {
		List<LOSStorageLocation> ret;

		ret = suitableDestination(ul, true);
		if (ret == null || ret.size() < 1) {
			ret = suitableDestination(ul, false);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	protected List<LOSStorageLocation> suitableDestination(LOSUnitLoad ul,
			boolean ofClient) {
		ItemData itemData = null;
		Zone zone = null;
		List<StockUnit> suList = ul.getStockUnitList();
		for( StockUnit su : suList ) {
			if( itemData == null ) {
				itemData = su.getItemData();
			}
			else if( !itemData.equals(su.getItemData() )){
				itemData = null;
				break;
			}
		}
		for( StockUnit su : suList ) {
			Zone itemDataZone = su.getItemData().getZone();
			if( zone == null ) {
				zone = itemDataZone;
			}
			else if( !zone.equals(su.getItemData().getZone())) {
				zone = null;
				break;
			}
		}
		
		Client sys = clQuery.getSystemClient();
		Client c = determineClient(ul);
		
		Client c1;
		Client c2;
		
		if (ofClient) {
			c1 = c;
			c2 = c;
		} else {
			c1 = sys;
			c2 = c;
		}
		
		StringBuffer queryADDStr = new StringBuffer();
		queryADDStr.append(" SELECT DISTINCT loc FROM ");
		queryADDStr.append(LOSRackLocation.class.getName());
		queryADDStr.append(" loc JOIN loc.currentTypeCapacityConstraint tcc");
		queryADDStr.append(" WHERE loc.reservedCapacity = 0 ");
		queryADDStr.append(" AND tcc.unitLoadType =:ultype ");
		queryADDStr.append(" AND SIZE(loc.unitLoads) < tcc.capacity ");
		queryADDStr.append(" AND loc.lock = 0 ");
		queryADDStr.append(" AND loc.area.areaType = :areaTyp ");
		queryADDStr.append(" AND loc.client IN (:c1, :c2) ");

		if( zone != null ) {
			queryADDStr.append(" AND loc.zone = :zone ");
		}
		
		queryADDStr.append(" AND not exists( select 1 from " + LOSFixedLocationAssignment.class.getSimpleName() + " fix ");
		queryADDStr.append("   WHERE fix.assignedLocation = loc ");
		if( itemData != null ) {
			queryADDStr.append(" AND fix.itemData!=:itemData )");
		}
		else {
			queryADDStr.append(" )");
		}
				
				 
		log.info("Search location Query1="+queryADDStr.toString());
		Query queryADD = manager.createQuery(queryADDStr.toString());
		
		queryADD.setParameter("c1", c1);
		queryADD.setParameter("c2", c2);
		queryADD.setParameter("ultype", ul.getType());
		queryADD.setParameter("areaTyp", LOSAreaType.STORE);
		if( zone != null ) {
			queryADD.setParameter("zone", zone);
		}
		if( itemData != null ) {
			queryADD.setParameter("itemData", itemData);
		}
		
		queryADD.setMaxResults(20);
		
		List<LOSStorageLocation> rlADDList = queryADD.getResultList();
		
		if(rlADDList.size() > 0){
			return rlADDList;
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(" SELECT DISTINCT loc FROM ");
		buffer.append(LOSRackLocation.class.getName());
		buffer.append(" loc ");
		buffer.append(" WHERE ");
		buffer.append(" loc.reservedCapacity = 0");
		buffer.append(" AND loc.unitLoads IS EMPTY ");
		buffer.append(" AND loc.lock = 0 ");
		buffer.append(" AND loc.area.areaType = :areaTyp ");
		buffer.append(" AND loc.client IN (:c1, :c2) ");

		if( zone != null ) {
			buffer.append(" AND loc.zone = :zone ");
		}

		buffer.append(" AND :ultype IN ( " + "SELECT tcc.unitLoadType FROM "
				+ LOSTypeCapacityConstraint.class.getSimpleName() + " tcc "
				+ "WHERE tcc.storageLocationType=loc.type ) ");

		buffer.append(" AND not exists( select 1 from " + LOSFixedLocationAssignment.class.getSimpleName() + " fix ");
		buffer.append("   WHERE fix.assignedLocation = loc ");
		if( itemData != null ) {
			buffer.append(" AND fix.itemData!=:itemData )");
		}
		else {
			buffer.append(" )");
		}
				

		log.info("Search location Query2="+buffer.toString());
		Query query = manager.createQuery(new String(buffer));
		query = query.setParameter("ultype", ul.getType());
		query = query.setParameter("areaTyp", LOSAreaType.STORE);

		query = query.setParameter("c1", c1);
		query = query.setParameter("c2", c2);
		
		if( zone != null ) {
			query.setParameter("zone", zone);
		}
		if( itemData != null ) {
			query.setParameter("itemData", itemData);
		}

		query = query.setMaxResults(50);
		List<LOSStorageLocation> rlNewList = query.getResultList();
		if(rlNewList.size() > 0){
			return rlNewList;
		}

	
		// search for locations without constraints
		buffer = new StringBuffer();
		buffer.append(" SELECT DISTINCT loc FROM ");
		buffer.append(LOSRackLocation.class.getName());
		buffer.append(" loc ");
		buffer.append(" WHERE ");
		buffer.append(" loc.lock = 0 ");
		buffer.append(" AND loc.area.areaType = :areaTyp ");
		buffer.append(" AND loc.client IN (:c1, :c2) ");

		if( zone != null ) {
			buffer.append(" AND loc.zone = :zone ");
		}

		buffer.append(" AND not exists( SELECT tcc.unitLoadType FROM "
				+ LOSTypeCapacityConstraint.class.getSimpleName() + " tcc "
				+ "WHERE tcc.storageLocationType=loc.type ) ");

		buffer.append(" AND not exists( select 1 from " + LOSFixedLocationAssignment.class.getSimpleName() + " fix ");
		buffer.append("   WHERE fix.assignedLocation = loc ");
		if( itemData != null ) {
			buffer.append(" AND fix.itemData!=:itemData )");
		}
		else {
			buffer.append(" )");
		}
				

		log.info("Search location Query3="+buffer.toString());
		query = manager.createQuery(new String(buffer));
		query = query.setParameter("areaTyp", LOSAreaType.STORE);

		query = query.setParameter("c1", c1);
		query = query.setParameter("c2", c2);
		

		if( zone != null ) {
			query.setParameter("zone", zone);
		}
		if( itemData != null ) {
			query.setParameter("itemData", itemData);
		}

		query = query.setMaxResults(50);
		return query.getResultList();

	}

	public Client determineClient(LOSUnitLoad ul) {
		Client systemCl;

		systemCl = clQuery.getSystemClient();

		Client ret = systemCl;

		if (!ul.getClient().equals(systemCl)) {
			ret = ul.getClient();
			return ret;
		} else {
			ret = null;
			for (StockUnit u : suService.getListByUnitLoad(ul)) {
				if (ret == null || ret.equals(u.getClient())) {
					ret = u.getClient();
				} else {
					// StockUnits of different clients - take system client
					ret = systemCl;
					return ret;
				}
			}
		}

		if (ret == null) {
			ret = systemCl;
		}

		return ret;
	}

	protected boolean testSuitableDestination(LOSStorageLocation sl, LOSUnitLoad ul) throws LOSLocationException{
		boolean ret;

		LOSTypeCapacityConstraint constr = storageLocService.checkUnitLoadSuitable(sl, ul);
		if (constr != null){
			int occupied = sl.getUnitLoads().size()+sl.getReservedCapacity();			
			if(constr.getCapacity()<(occupied+1)){
				throw new LOSLocationException(LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL, new String[]{sl.getName()});	
			} else{
				ret = true;
			}
		} else{
			ret = true;
		}
		if (!sl.getClient().isSystemClient()) {
			ret = ret && sl.getClient().equals(determineClient(ul));
		} else{
//			throw new LOSLocationException(
//					LOSLocationExceptionKey.WRONG_CLIENT, 
//					new String[]{sl.getClient().getNumber(), ul.getClient().getNumber()});
		}

		return ret;

    }

	public void cancelStorageRequest(LOSStorageRequest req) throws FacadeException {

		if( req.getDestination() != null && req.getUnitLoad() != null ) {
			storageLocService.releaseStorageLocation(req.getDestination(), req.getUnitLoad());
		}
		else {
			log.warn("cancelStorageRequest: cannot release reservation of location");
		}
		
		req.setRequestState(LOSStorageRequestState.CANCELED);
	}

	public void removeStorageRequest(LOSStorageRequest req) throws FacadeException {

		if( req.getDestination() != null && (req.getRequestState() == LOSStorageRequestState.RAW || req.getRequestState() == LOSStorageRequestState.PROCESSING) ) {
			storageLocService.releaseStorageLocation(req.getDestination(), null );
		}
		else {
			log.warn("cancelStorageRequest: cannot release reservation of location");
		}
		
		manager.remove(req);
	}

}
