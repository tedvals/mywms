/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
	package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;

import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.businessservice.LOSFixedLocationAssignmentComp;
import de.linogistix.los.location.businessservice.LOSRackLocationNameUtil;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class InventoryProcessFacadeBean implements InventoryProcessFacade {

	private static final Logger log = Logger
			.getLogger(InventoryProcessFacadeBean.class);

	@EJB
	ManageInventoryFacade manageInv;

	@EJB
	LOSInventoryComponent inventoryComponent;

	@EJB
	LOSStorageLocationQueryRemote slQuery;

	@EJB
	LOSUnitLoadQueryRemote ulQueryRemote;

	@EJB
	LOSStorage storage;

	@EJB
	ContextService contextService;
	
	@EJB
	LotQueryRemote lotQuery;
	
	@EJB
	ItemDataService itemService;

	@EJB
	LOSFixedLocationAssignmentService assService;
	
	@EJB
	StockUnitQueryRemote suQuery;
	
	@EJB
	InventoryGeneratorService genService;
	
	@EJB
	ClientService clientService;
	
	@EJB
	LOSFixedLocationAssignmentComp assComp;
		
	@EJB
	LOSStorageLocationService slService;
	
	@EJB
	LOSUnitLoadService ulService;
	
	@EJB
	UnitLoadTypeQueryRemote uTypeQuery;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public List<UnitLoadType> getUnitLoadTypes(){
		try {
			return uTypeQuery.queryAll(new QueryDetail(0,Integer.MAX_VALUE));
		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage(), e);
			return new ArrayList<UnitLoadType>();
		}
	}
	
	public void doInventoyForStorageLocationFromScratch(
			String client,
			String slName, 
			String ulName,
			UnitLoadType t,
			String suId,
			String itemData, 
			String lot,
			BigDecimal amount
			)
			throws FacadeException{
		
		
		LOSUnitLoad ul;
		
		if (ulName != null && ulName.length() > 0) {
			try {
				ul = ulQueryRemote.queryByIdentity(ulName);
				ul = manager.find(LOSUnitLoad.class, ul.getId());
				Vector<Long> sus = new Vector<Long>();
				for (StockUnit su : ul.getStockUnitList()){
					sus.add(su.getId());
				}
				for (Long id : sus){
					StockUnit su = manager.find(StockUnit.class,id);
					inventoryComponent.changeAmount(su, new BigDecimal(0), true, "IMAN", null, null, true);
					inventoryComponent.sendStockUnitsToNirwana(su, "IMAN");
				}
				ul.setStockUnitList(new ArrayList<StockUnit>());
				ul.setLock(0);
				ul.setAdditionalContent("");
				manager.flush();
			} catch (BusinessObjectNotFoundException ex) {
				log.error(ex.getMessage(), ex);
				ul = null;
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, "");
		}
		this.doInventoyForStorageLocation(client, slName, ulName, t, suId, itemData, lot, amount, true, true, true);
	}
	
	public void doInventoyForStorageLocation(
			String clientNumber,
			String slName, 
			String ulName,
			UnitLoadType t,
			String suId,
			String itemNumber, 
			String lot,
			BigDecimal amount,
			boolean forceUlNotEmpty,
			boolean forceExistingUnitLoad,
			boolean forceFullStorageLocation
			)
			throws FacadeException{

		
		LOSStorageLocation sl = null;
		LOSUnitLoad ul = null;
		
		StockUnit su;
		
		LOSRackLocationNameUtil nu;
		int index = -1;
		
		if (clientNumber == null) throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, "");
		if (slName == null) throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, "");
		if (ulName == null) throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, "");
		if (itemNumber == null) throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, "");
	
		Client c = clientService.getByNumber(clientNumber);

		if(c == null){
			throw new BusinessObjectNotFoundException(clientNumber);
		}

		ItemData idat = itemService.getByItemNumber(c, itemNumber);
		
		if(idat == null){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, itemNumber);
		}
		
		Lot l; 
		
		try {
			l = lotQuery.queryByIdentity(c, lot).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			l = new Lot();
			l.setName(lot);
			l.setItemData(idat);
			l.setClient(idat.getClient());
			l.setDate(new Date());
			
			manager.persist(l);
			manager.flush();
		}
		try{
			sl = slQuery.queryByIdentity(slName);
		} catch (BusinessObjectNotFoundException ex){
			log.warn("No found: " + slName + ". Check for index");
			nu = new LOSRackLocationNameUtil(slName);
			slName = nu.extractSlName();
			index = nu.extractIndex();
			sl = slQuery.queryByIdentity(slName);
		}
		
		sl = manager.find(LOSStorageLocation.class, sl.getId());
		
		if (ulName != null && ulName.length() > 0) {
			try {
				ul = ulQueryRemote.queryByIdentity(ulName);
				ul = manager.find(LOSUnitLoad.class, ul.getId());
			} catch (BusinessObjectNotFoundException ex) {
				log.error(ex.getMessage(), ex);
				ul = null;
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, "");
		}
		
		if (t == null){
			t = uTypeQuery.getDefaultUnitLoadType();
		}
		
		if (ul == null){
			log.info("WARN: no Unitload " + ulName
					+ ". Create on StrageLocaton " + sl.getName());
			
			ul = ulService.createLOSUnitLoad(idat.getClient(), ulName, t, slService.getClearing());
			manager.flush();

		} else{
			if (!forceExistingUnitLoad){
				throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, new String[]{ul.getLabelId(), ul.getStorageLocation().getName()});
			}
		}
		
		correctUnitLoad(ul, sl, index, forceFullStorageLocation);
		
		if (suId != null && suId.length() > 0) {
			try {
				LOSResultList<StockUnit> sus = suQuery.queryByLabelId(new QueryDetail(0,Integer.MAX_VALUE),suId);
				if (sus == null || sus.size()<1){
					throw new BusinessObjectNotFoundException();
				}
				su = sus.get(0);
				su = manager.find(StockUnit.class, su.getId());
			} catch (BusinessObjectNotFoundException ex) {
				su = null;
			}
		} else {
			su = null;
		}
		
		try{
			su = correctStockUnit(su,suId, ul, idat, l, amount, forceUlNotEmpty);
		} catch (EntityNotFoundException ex){
			throw new BusinessObjectNotFoundException();
		}
		inventoryComponent.changeAmount(su, amount, true, "IMAN", null, null, true);		
	}

	
	private StockUnit correctStockUnit( StockUnit su, String labelId,LOSUnitLoad ul, ItemData idat, Lot l,
			BigDecimal amount, boolean force) throws FacadeException, EntityNotFoundException{

		User u = contextService.getCallersUser();
		
		switch (ul.getPackageType()) {
		case MIXED_CONSOLIDATE:
		case OF_SAME_ITEMDATA_CONSOLIDATE:
		case OF_SAME_LOT_CONSOLIDATE:
			inventoryComponent.consolidate(ul,genService.generateManageInventoryNumber( u) );
			break;
		default:
			break;
		}	
		
		if (su != null && ul.getStockUnitList().size() > 0){
			if (! su.getUnitLoad().equals(ul)){
				if (force){
					inventoryComponent.transferStockUnit(su, ul, "IMAN");
				} else{
					String s = getStockUnitListAsString(ul);
					throw new InventoryException( InventoryExceptionKey.INVENTORY_CREATE_STOCKUNIT_ON_TOP, s);
				}
			} else{
				log.info("Stockunit already on ul " + su.toDescriptiveString());
				return su;
			}
		}
		
		else if (su == null && ul.getStockUnitList().size() > 0){
			if (force){
				su = inventoryComponent.createStock(idat.getClient(),
						l, idat, amount, ul, "IMAN", null);
				su.setLabelId(labelId);
				inventoryComponent.transferStockUnit(su, ul, "IMAN");
			} else{
				String s = getStockUnitListAsString(ul);
				throw new InventoryException( InventoryExceptionKey.INVENTORY_CREATE_STOCKUNIT_ON_TOP, s);
			}
		} else if (su == null){
			su = inventoryComponent.createStock(idat.getClient(),
					l, idat, amount, ul, "IMAN", null);
			su.setLabelId(labelId);
			inventoryComponent.transferStockUnit(su, ul, "IMAN");
		} else{ // su != null , ul.getStockUnitList().size() < 1
			inventoryComponent.transferStockUnit(su, ul, "IMAN");
		}

		return su;
		
	}

	private String getStockUnitListAsString(LOSUnitLoad ul) {
		String s = "";
		for (StockUnit exists : ul.getStockUnitList()){
			s += exists.getId() +"; "+ exists.getLabelId() +"; "+ exists.getLot() == null ? "" : exists.getLot().toUniqueString() +"; "+ exists.getAmount();
			s += "\\n";
		}
		return s;
	}

	public void changeAmount(BODTO<StockUnit> su, BigDecimal amount) throws FacadeException{
		StockUnit u = manager.find(StockUnit.class, su.getId());
		User user = contextService.getCallersUser();
		inventoryComponent.changeAmount(u, amount, false, genService.generateManageInventoryNumber( user), null, null, true);

	}
	private void correctUnitLoad(LOSUnitLoad ul, LOSStorageLocation sl, int index, boolean forceFullStorageLocation) throws FacadeException {
		
		User user = contextService.getCallersUser();
		
		if (sl.getUnitLoads().contains(ul)) {
			log.info("OK: Unitload " + ul.getLabelId()
					+ " already on StorageLocaton " + sl.getName());
			ul.setIndex(index);
			
		} else {
			log.error("ERROR: Unitload " + ul.getLabelId()
					+ " not  on StorageLocaton " + sl.getName()
					+ ". Will be transferred");
			try {
				storage.checkUnitLoadSuitableNoRollback(sl, ul);
			} catch (LOSLocationAlreadyFullException ex){
				if (forceFullStorageLocation){
					Vector<Long> ids = new Vector<Long>();
					for (LOSUnitLoad existing : sl.getUnitLoads()) {
						log.warn("WARN: send existing to clearing: "
								+ existing.getLabelId());
						ids.add(existing.getId());
					}
					for (Long id : ids){
						LOSUnitLoad existing = manager.find(LOSUnitLoad.class, id);
						storage.sendToClearing(user.getName(), existing);
					}
				} else{
					throw ex.createRollbackException();
				}
			} catch (LOSLocationReservedException ex){
				throw ex.createRollbackException();
			} catch (LOSLocationWrongClientException ex){
				throw ex.createRollbackException();
			} catch (LOSLocationNotSuitableException ex){
				throw ex.createRollbackException();
			} catch (LOSLocationException ex) {
				log.error(ex.getMessage(), ex);
				throw ex;
			}
			
			try{
				storage.transferUnitLoad(user.getName(), sl, ul, index, false);	
			} catch (LOSLocationException ex) {
				log.error(ex.getMessage(), ex);
				throw ex;
			}
		}
		
	}

}
