/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;

import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
	org.mywms.globals.Role.OPERATOR_STR,
	org.mywms.globals.Role.FOREMAN_STR,
	org.mywms.globals.Role.INVENTORY_STR,
	org.mywms.globals.Role.CLEARING_STR})
public class ManageLocationFacadeBean implements ManageLocationFacade {

	private static final Logger log = Logger
			.getLogger(ManageLocationFacadeBean.class);

	@EJB
	LOSStorage storage;

	@EJB
	LOSStorageLocationService slService;

	@EJB
	ContextService contextService;

	@EJB
	LOSStorageLocationQueryRemote slQuery;

	@EJB
	LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	LOSStorageLocationQueryRemote slQueryRemote;
	
	@EJB
	LOSAreaQueryRemote areaQueryRemote;
	
	@EJB
	LOSAreaService areaService;
	
	@EJB
	LOSUnitLoadService ulService;


	@EJB
	QueryUnitLoadTypeService ulTypeService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public void releaseReservations(List<BODTO<LOSStorageLocation>> locations)
			throws LOSLocationException {

		if (locations == null) {
			return;
		}

		for (BODTO<LOSStorageLocation> storLoc : locations) {
			LOSStorageLocation sl = manager.find(LOSStorageLocation.class,
					storLoc.getId());
			if (sl == null) {
				log.warn("Not found: " + storLoc.getName());
				continue;
			}
			storage.releaseStorageLocation(sl);
		}
	}

	public void sendUnitLoadToNirwana(String labelId) throws FacadeException {

		LOSUnitLoad u = uLoadQueryRemote.queryByIdentity(labelId);
		manager.find(LOSUnitLoad.class, u.getId());
		LOSStorageLocation nirwana = slService.getNirwana();
		User user = contextService.getCallersUser();
		storage.transferUnitLoad(user.getName(), nirwana, u);

	}

	public void sendUnitLoadToNirwana(List<BODTO<LOSUnitLoad>> list)
			throws FacadeException {
		if (list == null) {
			return;
		}

		LOSStorageLocation nirwana = slService.getNirwana();
		User user = contextService.getCallersUser();
		for (BODTO<LOSUnitLoad> ul : list) {
			LOSUnitLoad u = manager.find(LOSUnitLoad.class, ul.getId());
			storage.transferUnitLoad(user.getName(), nirwana, u);
		}
	}
	
	public void removeEmptyUnitLoads() throws FacadeException{
		
		List<LOSArea> areas = areaService.getByType(LOSAreaType.STORE);
		
		for (LOSArea area : areas){
		
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area", area);
			TemplateQuery query = new TemplateQuery();
			query.addWhereToken(t);
			query.setBoClass(LOSStorageLocation.class);
			
			List<BODTO<LOSStorageLocation>> sls = slQueryRemote.queryByTemplateHandles(d, query );
			for (BODTO<LOSStorageLocation> to : sls){
				removeEmptyUnitLoads(to);
			}
		}
	}
	public void removeEmptyUnitLoads(BODTO<LOSStorageLocation> to) throws FacadeException{
		
		List<LOSArea> areas = areaService.getByType(LOSAreaType.STORE);
		UnitLoadType dummy = ulTypeService.getPickLocationUnitLoadType();
		
		for (LOSArea area : areas){
			
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area", area);
			TemplateQuery query = new TemplateQuery();
			query.addWhereToken(t);
			query.setBoClass(LOSStorageLocation.class);
			
			LOSStorageLocation sl = manager.find(LOSStorageLocation.class, to.getId());
			
			if (!sl.getArea().equals(area)){
				log.error("UNEXPECTED AREA !!! " + sl.toDescriptiveString());
				return;
			}
			if (sl.getCurrentTypeCapacityConstraint() != null){
				if (sl.getUnitLoads().size() > sl.getCurrentTypeCapacityConstraint().getCapacity()){
					log.error("Capacity exhausted !!! " + sl.toDescriptiveString());
					return;
				}
				
				if ( sl.getCurrentTypeCapacityConstraint().getUnitLoadType().equals(dummy)){
					log.info("IGNORE TYPE " + sl.getCurrentTypeCapacityConstraint().toDescriptiveString());
					return;
				}
			}
			
			if (sl.getUnitLoads().isEmpty()){
				//log.info("empty " + sl.toDescriptiveString());
				return;
			}
			List<Long> ids = new ArrayList<Long>();
			for (LOSUnitLoad ul : sl.getUnitLoads()){
				ids.add(ul.getId());
			}
			for (Long id : ids){
				LOSUnitLoad ul ;
				ul = manager.find(LOSUnitLoad.class, id);
				if (ul.getType().equals(dummy)){
					log.warn("UNEXPECTED TYPE: " + ul.toDescriptiveString());
					continue;
				}
				if (ul.getStockUnitList().size() == 0){
					log.error("GOING TO REMOVE EMPTY UNITLOAD: " + ul.toDescriptiveString());
					User user = contextService.getCallersUser();
					storage.sendToNirwana(user.getName(), ul);
				}
			}
		}
	}

	public LOSTypeCapacityConstraint checkUnitLoadSuitable(
			BODTO<LOSStorageLocation> dest, BODTO<LOSUnitLoad> ul)
			throws LOSLocationException {
		
		LOSStorageLocation storageLocation = manager.find(LOSStorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		LOSUnitLoad unitLoad = manager.find(LOSUnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});

		return storage.checkUnitLoadSuitable(storageLocation, unitLoad);
		
	}

	public void transferUnitLoad(BODTO<LOSStorageLocation> dest,
			BODTO<LOSUnitLoad> ul, int index, boolean ignoreSlLock, String info) throws LOSLocationException {
		LOSStorageLocation storageLocation = manager.find(LOSStorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		LOSUnitLoad unitLoad = manager.find(LOSUnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});
		
		User u = contextService.getCallersUser();
		
		storage.transferUnitLoad(u.getName(), storageLocation, unitLoad, -1, ignoreSlLock, info, "");
		
	}

}
