/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class InfoFacadeBean implements InfoFacade {
	Logger log = Logger.getLogger(InfoFacadeBean.class);
	
	@EJB
	private QueryUnitLoadService queryUlService;
	
	@EJB
	private QueryStorageLocationService queryLocationService;
	
	@EJB
	private QueryItemDataService queryItemData;
	
	@EJB
	private LOSPickRequestPositionService pickService;
	
	@EJB
	private QueryFixedAssignmentService fixService;
	
	@EJB
	private ClientService clientService;
	
	@EJB
	private ContextService contextService;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public Client getDefaultClient() {
		
		// Only one client
		Client systemClient;
		systemClient = clientService.getSystemClient();
		List<Client> clients = clientService.getList(systemClient);
		if( clients.size() == 1 ) {
			log.info("Only one client in system");
			return systemClient;
		}
		
		
		// Callers client not system-client
		Client callersClient = contextService.getCallersClient();
		if( !systemClient.equals(callersClient) ) {
			log.info("Caller is not system-client => only one client to use");
			return callersClient; 
		}
		
		
		log.info("Plenty clients");
		return null;
	}

	
	public InfoItemDataTO readItemData( String code ) {
		log.info("readItemData code="+code);
		ItemData item = null;
		List<ItemData> itemList = queryItemData.getListByItemNumber( code );
		if( itemList == null || itemList.size() < 1 ) {
			return null;
		}
		item = itemList.get(0);
		
		List<LOSFixedLocationAssignment> fixList = fixService.getByItemData(item);

		return new InfoItemDataTO(item, fixList);
	}
	
	public InfoLocationTO readLocation( String name ) {
		log.info("readLocation name="+name);
		LOSStorageLocation loc = null;
		try {
			loc = queryLocationService.getByName(name);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( loc == null ) {
			return null;
		}
		
		List<LOSFixedLocationAssignment> fixList = new ArrayList<LOSFixedLocationAssignment>();
		LOSFixedLocationAssignment fix = fixService.getByLocation(loc);
		if( fix != null ) {
			fixList.add(fix);
		}

		InfoLocationTO locto = new InfoLocationTO( loc, fixList );
		if( locto.getNumUnitLoads() == 1 ) {
			readOrder( locto.getUnitLoad(), loc.getUnitLoads().get(0) );
		}
		
		return locto;
	}

	public List<InfoUnitLoadTO> readUnitLoadList( String locationName ) {
		log.info("readUnitLoadList locationName="+locationName);
		List<InfoUnitLoadTO> toList = new ArrayList<InfoUnitLoadTO>();
		LOSStorageLocation loc = null;
		try {
			loc = queryLocationService.getByName( locationName );
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( loc == null ) {
			return toList;
		}
		
		for( LOSUnitLoad ul : loc.getUnitLoads() ) {
			InfoUnitLoadTO ulto = new InfoUnitLoadTO(ul);
			readOrder(ulto, ul);
			
			toList.add(ulto);
			
		}
		
		return toList;
	}
	
	
	public InfoUnitLoadTO readUnitLoad( String label ) {
		log.info("readUnitLoad label="+label);
		LOSUnitLoad ul = null;
		try {
			ul = queryUlService.getByLabelId(label);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( ul == null ) {
			return null;
		}

		InfoUnitLoadTO ulto = new InfoUnitLoadTO(ul);
		readOrder(ulto, ul);
		return ulto;

	}


	private void readOrder( InfoUnitLoadTO ulto, LOSUnitLoad ul ) {
		HashSet<LOSOrderRequest> orderSetUl = new HashSet<LOSOrderRequest>();
		HashSet<LOSOrderRequest> pickSetUl = new HashSet<LOSOrderRequest>();
		
		for( StockUnit su : ul.getStockUnitList() ) {
			HashSet<LOSOrderRequest> orderSetSu = new HashSet<LOSOrderRequest>();
			HashSet<LOSOrderRequest> pickSetSu = new HashSet<LOSOrderRequest>();
			
			List<LOSPickRequestPosition> pickList = pickService.getByStockUnit(su);
			for( LOSPickRequestPosition pick : pickList ) {
				if( pick.isPicked() ) {
					orderSetSu.add( pick.getParentRequest().getParentRequest());
					orderSetUl.add( pick.getParentRequest().getParentRequest());
				}
				else {
					pickSetSu.add( pick.getParentRequest().getParentRequest());
					pickSetUl.add( pick.getParentRequest().getParentRequest());
				}
			}
			
			InfoStockUnitTO suto = new InfoStockUnitTO( su ) ;
			
			for( LOSOrderRequest order : orderSetSu ) {
				suto.getOrderList().add( new InfoOrderTO(order) );
			}
			for( LOSOrderRequest order : pickSetSu ) {
				suto.getPickList().add( new InfoOrderTO(order) );
			}
			
			ulto.getStockUnitList().add(suto);
		}

		for( LOSOrderRequest order : orderSetUl ) {
			ulto.getOrderList().add( new InfoOrderTO(order) );
		}
		for( LOSOrderRequest order : pickSetUl ) {
			ulto.getPickList().add( new InfoOrderTO(order) );
		}
		
	}
}
