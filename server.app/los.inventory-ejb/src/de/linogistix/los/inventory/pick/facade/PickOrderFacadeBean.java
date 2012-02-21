/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.exception.PickingExceptionKey;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.query.LOSPickRequestPositionQueryRemote;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.facade.LocationSanityCheckFacade;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * 
 * @author trautm
 */
@Stateless
public class PickOrderFacadeBean implements PickOrderFacade {

	private static final Logger log = Logger.getLogger(PickOrderFacadeBean.class);
	
	@EJB
	ClientService clService;
	
	@EJB
	PickOrderBusiness pickOrderService;

	@EJB
	LOSPickRequestQueryRemote pickReqQuery;

	@EJB
	LOSPickRequestPositionQueryRemote pickPosQuery;

	@EJB
	LOSPickRequestService pickService;
	
	@EJB
	LOSStorageLocationQueryRemote slQueryX;
	
	@EJB
	LOSInventoryComponent inventoryComponent;
	
	@EJB
	LocationSanityCheckFacade locationSanity;
	
	@EJB
	ContextService context;
	
	@EJB
	LOSStorageLocationService locService;
	
	@EJB
	StockUnitService suService;
	@EJB
	QueryUnitLoadTypeService ulTypeService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public List<PickingRequestTO> getRawPickingRequest() {
		return pickOrderService.getRawPickingRequest();
	}

	public LOSPickRequest accept(LOSPickRequest request)
			throws PickingException, InventoryException,
			BusinessObjectNotFoundException, BusinessObjectSecurityException {
		request = manager.find(LOSPickRequest.class, request.getId());
		pickOrderService.accept(request);
		return (LOSPickRequest) BusinessObjectHelper.eagerRead(request);
	}

	public LOSPickRequestPosition processPickRequestPosition(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException 
	{
		position = manager.find(LOSPickRequestPosition.class, position.getId());
		LOSPickRequest r = position.getPickRequest();
		LOSStorageLocation sl = resolveSource(r, label);
		sl = manager.find(LOSStorageLocation.class, sl.getId());
		pickOrderService.processPickRequestPosition(r, position, unexpectedNullAmount, sl, amount, takeWholeUnitLoad, stockEmptyConfirmed);
			
		return (LOSPickRequestPosition) BusinessObjectHelper
				.eagerRead(position);
	}
	

	public LOSPickRequestPosition processPickRequestPositionExpectedNull(
			LOSPickRequestPosition position, String label, BigDecimal amount,
			BigDecimal discoveredAmount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException {
		position = manager.find(LOSPickRequestPosition.class, position.getId());
		LOSPickRequest r = position.getPickRequest();
		LOSStorageLocation sl = locService.getByName(label);
		pickOrderService.processPickRequestPositionExpectedNull(r, position, sl, amount, discoveredAmount, takeWholeUnitLoad, stockEmptyConfirmed);
		
		return (LOSPickRequestPosition) BusinessObjectHelper
				.eagerRead(position);
		
	}

	public LOSPickRequestPosition processPickRequestPositionSubstitution(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount, boolean takeWholeUnitLoad, boolean stockEmptyConfirmed) throws FacadeException {
		position = manager.find(LOSPickRequestPosition.class, position.getId());
		LOSPickRequest r = position.getPickRequest();
		LOSStorageLocation sl = locService.getByName(label);
		pickOrderService.processPickRequestPositionSubstitution(r, position, unexpectedNullAmount, sl, amount, takeWholeUnitLoad, stockEmptyConfirmed);
		return (LOSPickRequestPosition) BusinessObjectHelper
				.eagerRead(position);
	}

	public boolean testCanProcess(
			LOSPickRequestPosition position, boolean unexpectedNullAmount,
			String label, BigDecimal amount) throws FacadeException {
		position = manager.find(LOSPickRequestPosition.class, position.getId());
		LOSPickRequest r = position.getPickRequest();
		LOSStorageLocation sl = resolveSource(r, label);
		sl = manager.find(LOSStorageLocation.class, sl.getId());
		return pickOrderService.testCanProcess(r, position, unexpectedNullAmount, sl, amount);
	}
	public LOSStorageLocation getStorageLocation(LOSPickRequestPosition position) {

		LOSStorageLocation sl;
		LOSUnitLoad ul;

		position = manager.find(LOSPickRequestPosition.class, position.getId());
		ul = (LOSUnitLoad) position.getUnitLoad();
		sl = ul.getStorageLocation();
		return (LOSStorageLocation) BusinessObjectHelper.eagerRead(sl);
	}

	public LOSPickRequest finishPickingRequest(LOSPickRequest req,
			String labelID) throws FacadeException {

		finishCurrentUnitLoad(req, labelID);

		req = manager.find(LOSPickRequest.class, req.getId());
		pickOrderService.finish(req, false);
		return (LOSPickRequest) BusinessObjectHelper.eagerRead(req);
	}

	public LOSPickRequest loadPickingRequest(LOSPickRequest req)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException {
		return pickReqQuery.queryById(req.getId());
	}

	public LOSPickRequest loadPickingRequest(PickingRequestTO req)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException {
		return pickReqQuery.queryById(req.getId());
	}

	public LOSStorageLocation getDestination(LOSPickRequest pickingRequest)
			throws PickingException {
		LOSStorageLocation dest;

		dest = ((LOSPickRequest) pickingRequest).getDestination();
		return dest;

	}

	public LOSPickRequest finishCurrentUnitLoad(LOSPickRequest req,
			String transfer) throws FacadeException {
		LOSStorageLocation to = null;
		LOSPickRequest r = manager.find(LOSPickRequest.class, req.getId());
		
		to = resolveTargetLocation(r, transfer);

		LOSOrderRequest order = manager.find(LOSOrderRequest.class, r.getParentRequest().getId());
		if (order == null) {
			throw new PickingException(PickingExceptionKey.NO_PARENT_ORDER, r.getNumber());
		} else {
			pickOrderService.finishCurrentUnitLoads(r, to);
			return r;
		}
	}       


	private LOSStorageLocation resolveTargetLocation(LOSPickRequest r,
			String transfer)  throws FacadeException {
		LOSStorageLocation sl = null;
		Client sys = clService.getSystemClient();
		Client cl = context.getCallersUser().getClient();
		if (cl.equals(sys) || cl.equals(r.getClient())){
			sl =  locService.getByName(r.getClient(),transfer);
		} else{
			throw new InventoryException(InventoryExceptionKey.CLIENT_MISMATCH, cl.getName());
		}
		
		if( sl == null ) {
			sl = locService.getByName(sys,transfer);
		}
		if( sl == null ) {
			throw new PickingException(PickingExceptionKey.TARGET_NOT_FOUND, transfer);
		}

		return sl;
	}
	
	private LOSStorageLocation resolveSource(LOSPickRequest r,
			String transfer)  throws FacadeException {
		LOSStorageLocation sl = null;
		Client sys = clService.getSystemClient();
		Client cl = context.getCallersUser().getClient();
		if (cl.equals(sys) || cl.equals(r.getClient())){
			sl =  locService.getByName(r.getClient(),transfer);
		} else{
			throw new InventoryException(InventoryExceptionKey.CLIENT_MISMATCH, cl.getName());
		}
		
		if( sl == null ) {
			sl = locService.getByName(sys,transfer);
		}
		if( sl == null ) {
			throw new PickingException(PickingExceptionKey.SOURCE_NOT_FOUND, transfer);
		}

		return sl;
	}

	public void cancel(LOSPickRequest pickingRequest) throws PickingException,
			FacadeException {
		
		pickOrderService.cancel(pickingRequest);
		
	}
	
	public void remove(BODTO<LOSPickRequest> pick) throws FacadeException{
		
		LOSPickRequest r = manager.find(LOSPickRequest.class, pick.getId());
		if (r != null){
			pickOrderService.removePickingRequest(r);
		} else{
			throw new BusinessObjectNotFoundException();
		}
	}

	public void setPickRequestPositionTakeUnitLoad(LOSPickRequestPosition p,
			boolean finishPaletCheckBox) {
		
		p = manager.find(p.getClass(), p.getId());
		if (finishPaletCheckBox){
			p.setWithdrawalType(PickingWithdrawalType.TAKE_UNITLOAD);
		} else{
			p.setWithdrawalType(PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT);
		}
		
	}

	public List<BODTO<StockUnit>> getSuitableStockUnits(BODTO<Client> client, 
			BODTO<ItemData> idat, BODTO<Lot> lot, BigDecimal amount) throws FacadeException{
		
		Lot l;
		List<BODTO<StockUnit>> ret = new ArrayList<BODTO<StockUnit>>();
		if (lot != null){
			l = manager.find(Lot.class, lot.getId());	
			List<StockUnit> sus = pickOrderService.suitableStockUnitsByLotAndAmount(l, amount);
			for (StockUnit su : sus){
				BODTO<StockUnit> dto = new BODTO<StockUnit>(su.getId(), su.getVersion(), su.getId());
				ret.add(dto);
			}
			
			return ret;
			
		} else{
			l = null;
			return ret;
		}		
	}
	
	public void createPickRequests(BODTO<LOSOrderRequestPosition> orderpos,
			String pickreqnumber, List<BODTO<StockUnit>> sus) throws FacadeException {
		
		try{
			for(BODTO<StockUnit> to:sus){
				
				CreatePickRequestPositionTO  posTo = new CreatePickRequestPositionTO();
				posTo.orderPosition = orderpos;
				posTo.pickRequestNumber = pickreqnumber;
				posTo.stock = to;
				List<CreatePickRequestPositionTO> creates = new ArrayList<CreatePickRequestPositionTO>();
				creates.add(posTo);
				LOSOrderRequestPosition pos = manager.find(LOSOrderRequestPosition.class, orderpos.getId());
				pickOrderService.createPickRequest(pos, pickreqnumber, creates);	
			}
		} catch (FacadeException ex){
			log.error(ex.getMessage());
		}
		
	}

	public void createPickRequests(List<CreatePickRequestPositionTO> chosenStocks)
			throws FacadeException 
	{
				
		for(CreatePickRequestPositionTO to:chosenStocks){	
			pickOrderService.createPickRequestPosition(to);
		}
	}

	public void assignSerialNumbers(LOSPickRequestPosition position,
			List<String> serials) throws FacadeException {
		
		pickOrderService.assignSerialNumbers(position, serials);
	}


    public boolean hasPickedStock(LOSPickRequest req) {
    	if(req == null){
    		return false;
    	}
    	else{
			req = manager.find(LOSPickRequest.class, req.getId());
			if( req.getCart() == null ) {
				log.error("Constraint violated! LOSPickRequest has no cart" );
				return false;
			}
			if( req.getCart().getUnitLoads() == null ) {
				return false;
			}
    		for( LOSUnitLoad ul : req.getCart().getUnitLoads() ) {
    			if( ul.getStockUnitList().size()>0) {
    				return true;
    			}
    		}
    		return false;
    	}
    }

    public boolean isCompleteUnitLoad(LOSPickRequestPosition pos) {

    	pos = manager.find(LOSPickRequestPosition.class, pos.getId());
    	StockUnit su = pos.getStockUnit();
    	if( su.getAmount().compareTo(pos.getAmount()) > 0 ) {
    		return false;
    	}
    	LOSUnitLoad ul = (LOSUnitLoad)su.getUnitLoad();
    	if( ul.getStockUnitList().size() > 1 ) {
    		return false;
    	}
		UnitLoadType virtual = ulTypeService.getPickLocationUnitLoadType();
		if( virtual != null && virtual.equals(ul.getType()) ) {
			return false;
		}
		return true;

//    	if( su.getAmount().compareTo(pos.getAmount()) <= 0 ) {
//        	UnitLoad ul = su.getUnitLoad();
//        	List<StockUnit> suList = suService.getListByUnitLoad(ul);
//        	return suList.size() == 1;
//    	}
//    	return false;
    }
    
    public boolean isWholeUnitLoadAllowed(LOSPickRequestPosition pos) {
    	
    	pos = manager.find(LOSPickRequestPosition.class, pos.getId());
    	StockUnit su = pos.getStockUnit();
    	if( su.getReservedAmount().compareTo(pos.getAmount()) > 0 ) {
    		return false;
    	}
    	LOSUnitLoad ul = (LOSUnitLoad)su.getUnitLoad();
    	if( ul.getStockUnitList().size() > 1 ) {
    		return false;
    	}
		UnitLoadType virtual = ulTypeService.getPickLocationUnitLoadType();
		if( virtual != null && virtual.equals(ul.getType()) ) {
			return false;
		}

		
    	return true;
    }

    public boolean isSerialNumberUnique(ItemData itemData, String serialNumber ) {
    	if( serialNumber == null || serialNumber.trim().length() == 0 ) {
    		return false;
    	}
    	List<StockUnit> suList = suService.getBySerialNumber(itemData, serialNumber);
    	if( suList == null ) {
    		return true;
    	}
    	for( StockUnit su : suList ) {
    		if( BigDecimal.ZERO.compareTo(su.getAmount()) < 0 ) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean isSomethingPicked( LOSPickRequest request ) {
		
		LOSPickRequest req = manager.find(LOSPickRequest.class, request.getId());

		if (req.getCart() == null) {
			return false;
		}

		for( LOSUnitLoad ul : req.getCart().getUnitLoads() ) {
			for( StockUnit su : ul.getStockUnitList() ) {
				if( BigDecimal.ZERO.compareTo(su.getAmount()) < 0 ) {
					log.debug("isSomethingPicked request=" + request.getNumber()+", TRUE" );
					return true;
				}
			}
		}
		
		log.debug("isSomethingPicked request=" + request.getNumber()+", FALSE" );
		return false;
    }

}
