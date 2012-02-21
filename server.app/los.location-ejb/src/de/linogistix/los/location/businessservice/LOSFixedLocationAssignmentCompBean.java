/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.ItemData;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.service.QueryFixedAssignmentService;

@Stateless
public class LOSFixedLocationAssignmentCompBean implements
		LOSFixedLocationAssignmentComp {

	@EJB
	private QueryFixedAssignmentService flaService;
	
	@EJB
	private LOSAreaService areaService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public void createFixedLocationAssignment(LOSStorageLocation sl, ItemData item) 
					throws LOSLocationException 
	{
		LOSFixedLocationAssignment fla = flaService.getByLocation(sl); 
		
		// if the assignment already exists, nothing to do
		if(fla!=null && fla.getItemData().equals(item)){
			return;
			
		}
		// if the location is already assigned to a different item, that is a problem
		else if(fla!=null && !(fla.getItemData().equals(item))){
			throw new LOSLocationException(
					LOSLocationExceptionKey.LOCATION_ALLREADY_ASSIGNED_TO_DIFFEREND_ITEM, 
					new Object[]{sl.getName(), item.getNumber()});
		}
		
		fla = new LOSFixedLocationAssignment();
		fla.setAssignedLocation(sl);
		fla.setItemData(item);
		
		manager.persist(fla);
		
		return;
		
	}

	@SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getAssignedLocationsForStorage(ItemData item, UnitLoadType ult) throws LOSLocationException {
		
		
		if (areaService.getByType(LOSAreaType.STORE).size() == 0){
			throw new LOSLocationException(LOSLocationExceptionKey.NO_STORE_AREA, new Object[]{});
		}
		
		// if there are no locations assigned to the item, we could return none
		if(!flaService.existsFixedLocationAssignment(item)){
			return new ArrayList<LOSStorageLocation>();
		}
		
		// find locations with UnitLoads on and free places left
		StringBuffer sb = new StringBuffer("SELECT sl FROM ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" sl ");
		sb.append(" WHERE sl.area.type IN (:store, :pick, :replenish) ");
		sb.append(" AND sl.lock=0 ");
		sb.append(" AND sl.currentTypeCapacityConstraint IS NOT NULL ");
		sb.append(" AND :item IN ( ");
		sb.append(" SELECT fla.itemData FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName()+" fla ");
		sb.append(" WHERE fla.assignedLocation=sl ) ");
        if (ult != null){
            sb.append(" AND sl.currentTypeCapacityConstraint.unitLoadType =:ultype");
        }
		sb.append(" AND (sl.reservedCapacity + SIZE(sl.unitLoads)) < sl.currentTypeCapacityConstraint.capacity");
		
		Query query = manager.createQuery(sb.toString());
		query.setParameter("store", LOSAreaType.STORE);
		query.setParameter("pick", LOSAreaType.PICKING);
		query.setParameter("replenish", LOSAreaType.REPLENISHMENT);
		query.setParameter("item", item);
		if (ult != null){
            query.setParameter("ultype", ult);
        }
		
		List<LOSStorageLocation> slList = query.getResultList();
		
		if(slList.size()>0){
			return slList;
		}else{
			
			// find total free locations
			
			sb = new StringBuffer("SELECT sl FROM ");
			sb.append(LOSStorageLocation.class.getSimpleName()+" sl ");
			sb.append(" WHERE sl.area.type IN (:store, :pick, :replenish) ");
			sb.append(" AND sl.lock=0 ");
			sb.append(" AND sl.currentTypeCapacityConstraint IS NULL ");
			sb.append(" AND :item IN ( ");
			sb.append(" SELECT fla.itemData FROM ");
			sb.append(LOSFixedLocationAssignment.class.getSimpleName()+" fla ");
			sb.append(" WHERE fla.assignedLocation=sl ) ");
			sb.append(" AND :ultype IN ( ");
    		sb.append(" SELECT tcc.unitLoadType FROM ");
    		sb.append(LOSTypeCapacityConstraint.class.getSimpleName()+" tcc ");
    		sb.append(" WHERE tcc.storageLocationType=sl.type ) ");
			
			query = manager.createQuery(sb.toString());
			query.setParameter("store", LOSAreaType.STORE);
			query.setParameter("pick", LOSAreaType.PICKING);
			query.setParameter("replenish", LOSAreaType.REPLENISHMENT);
			query.setParameter("item", item);
			query.setParameter("ultype", ult);
			
			slList = query.getResultList();
			
			return slList;
		}
		
	}

	public boolean hasFixedLocationAssignment(ItemData item) {
		
		return flaService.existsFixedLocationAssignment(item);
	}

	public LOSFixedLocationAssignment getAssignment(LOSStorageLocation sl) {
		return flaService.getByLocation(sl);
	}

	public boolean isFixedLocation(LOSStorageLocation sl) {
		return getAssignment(sl) != null;
	}

}
