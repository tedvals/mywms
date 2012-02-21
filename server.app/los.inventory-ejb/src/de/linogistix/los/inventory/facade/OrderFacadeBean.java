/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.LogItemService;

import de.linogistix.los.inventory.businessservice.OrderBusiness;
import de.linogistix.los.inventory.businessservice.OrderCannotBeStarted;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;

/**
 * A Webservice for ordering items from stock
 * 
 * @see de.linogistix.los.inventory.connector.OrderFacade
 * 
 * @author trautm
 * 
 */
@Stateless
public class OrderFacadeBean extends BasicFacadeBean implements OrderFacade {

	@EJB
	private LOSStorageLocationService slService;

	Logger log = Logger.getLogger(OrderFacadeBean.class);
	
	@EJB
	ClientService clientService;
	@EJB
	OrderRequestService orderService;
	
	@EJB
	ItemDataService idatService;
	@EJB
	LogItemService logService;
	@EJB
	PickOrderBusiness pickOrderService;
	@EJB
	OrderBusiness orderBusiness;
	@EJB
	LOSStorageLocationQueryRemote slQueryRemote;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.los.inventory.connector.OrderRemote#order(java.lang.String,
	 *      java.lang.String, java.lang.String[], byte[], byte[])
	 */
	@WebMethod
	public boolean order(

	@WebParam(name = "clientRef")
	String clientRef,

	@WebParam(name = "orderRef")
	String orderRef,

	@WebParam(name = "positions")
	OrderPositionTO[] positions,

	@WebParam(name = "documentUrl")
	String documentUrl,

	@WebParam(name = "labelUrl")
	String labelUrl,

	@WebParam(name = "destination")
	String destination) throws FacadeException {
		OrderType t = resolveOrderType(destination);
		return order(clientRef, orderRef, positions, documentUrl, labelUrl,
					 destination, t, new Date(), true, "");
	}

	private OrderType resolveOrderType(String destination) {

		LOSStorageLocation sl;
		try {
			sl = slQueryRemote.queryByIdentity(destination);
		} catch (BusinessObjectNotFoundException e) {
			return OrderType.TO_CUSTOMER;
		}

		switch (sl.getArea().getAreaType()) {
		case GOODS_OUT:
			return OrderType.TO_CUSTOMER;
		case QUALITY_ASSURANCE:
		case QUARANTINE:
			return OrderType.INTERNAL;
		case PRODUCTION:
			return OrderType.TO_PRODUCTION;
		default:
			return OrderType.TO_CUSTOMER;
		}
	}

	@WebMethod
	public void startOrder(

	@WebParam(name = "clientRef")
	String clientRef,

	@WebParam(name = "orderRef")
	String orderRef) throws FacadeException, OrderCannotBeStarted {

		LOSOrderRequest r;

		try {
			r = orderService.getByRequestId(orderRef);
		} catch (EntityNotFoundException e) {
			throw new InventoryException(
					InventoryExceptionKey.ORDER_CANNOT_BE_STARTED, orderRef);
		}
		orderBusiness.process(r);

	}
	
	public void startOrder(
	BODTO<LOSOrderRequest> orderTo) throws FacadeException, OrderCannotBeStarted {

		LOSOrderRequest r;

		try {
			r = orderService.get(orderTo.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(
					InventoryExceptionKey.ORDER_CANNOT_BE_STARTED, orderTo.getName());
		}
		orderBusiness.process(r);

	}


	public void finishOrders(List<BODTO<LOSOrderRequest>> orders)
			throws FacadeException {

		if (orders == null) {
			return;
		}

		for (BODTO<LOSOrderRequest> req : orders) {
			LOSOrderRequest r = manager
					.find(LOSOrderRequest.class, req.getId());
			if (r == null) {
				continue;
			}
			
			orderBusiness.processOrderPicked(r, true);
			orderBusiness.finishOrder(r, true);
		}

	}

	public void removeOrder(BODTO<LOSOrderRequest> r) throws FacadeException {
		if (r == null) {
			return;
		}

		LOSOrderRequest req = manager.find(LOSOrderRequest.class, r.getId());
		if (req == null) {
			return;
		}
		orderBusiness.remove(req);
	}

	public List<BODTO<LOSStorageLocation>> getGoodsOutLocations()
			throws LOSLocationException {
		List<LOSStorageLocation> sls = getGoodsOutLocationsSrv();
		List<BODTO<LOSStorageLocation>> ret = new ArrayList<BODTO<LOSStorageLocation>>();
		for (LOSStorageLocation sl : sls) {
			ret.add(new BODTO<LOSStorageLocation>(sl.getId(), sl.getVersion(),
					sl.getName()));
		}
		return ret;
	}

	private List<LOSStorageLocation> getGoodsOutLocationsSrv()
			throws LOSLocationException {
		List<LOSStorageLocation> slList;
		slList = slService.getListByAreaType(clientService.getSystemClient(),
				LOSAreaType.GOODS_OUT);
		slList.addAll(slService.getListByAreaType(clientService
				.getSystemClient(), LOSAreaType.QUALITY_ASSURANCE));
		slList.addAll(slService.getListByAreaType(clientService
				.getSystemClient(), LOSAreaType.QUARANTINE));
		slList.addAll(slService.getListByAreaType(clientService
				.getSystemClient(), LOSAreaType.TOLL));
		slList.addAll(slService.getListByAreaType(clientService
				.getSystemClient(), LOSAreaType.TOLL));
		slList.addAll(slService.getListByAreaType(clientService
				.getSystemClient(), LOSAreaType.PRODUCTION));

		if (slList.size() == 0) {
			// LOSStorageLocation defLoc;
			// defLoc =
			// createGoodsReceiptLocation(LOSGoodsReceiptConstants.GOODSRECEIPT_DEFAULT_LOCATION_NAME);
			// slList.add(defLoc);
			throw new LOSLocationException(
					// LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new
					// Object[0]);
					LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION,
					new Object[0]);
		}
		return slList;
	}

	public boolean order(String clientRef, 
						 String orderRef,
						 OrderPositionTO[] positions, 
						 String documentUrl, 
						 String labelUrl,
						 String destination, 
						 OrderType type,
						 Date deliveryDate,
						 boolean processAutomaticly,
						 String comment) throws FacadeException 
	{
		LOSOrderRequest order;
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("CREATE OrderRequest FAILED (wrong client): " + orderRef
					+ " (" + clientRef + ")");
			throw new BusinessObjectNotFoundException();
		}
		try {
			
			if(deliveryDate == null){
				deliveryDate = new Date(System.currentTimeMillis() + (24 * 3600 * 1000));
			}
			
			order = orderBusiness.createOrder(c, orderRef, positions, documentUrl,
					labelUrl, destination, deliveryDate, type);
			
			order = manager.find(LOSOrderRequest.class, order.getId());
			
			if (order == null) {
				throw new RuntimeException("OrderRequest could not be created");
			}
			
			order.setAdditionalContent(comment);

		} catch (OrderCannotBeStarted t) {
			log.error("Change Exception message. " + t.getMessage());
			// Change the message text when creation and start is done in one transaction
			throw new OrderCannotBeStarted(orderRef);
			
		} catch (FacadeException t) {
			log.error(t.getMessage());
			throw t;
		}
		
		try {
			if(processAutomaticly){
				orderBusiness.process(order);
			}
			
		} catch (OrderCannotBeStarted t) {
			log.error("Change Exception message. " + t.getMessage());
			// Change the message text when creation and start is done in one transaction
			throw new OrderCannotBeStarted(orderRef);

		} catch (FacadeException t) {
			log.error(t.getMessage());
			throw t;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(
					InventoryExceptionKey.ORDER_CANNOT_BE_CREATED, t.getLocalizedMessage());

		}
		return true;
	}

	public LOSOrderRequest order(BODTO<Client> c, String orderRef, 
			OrderPositionTO position, String documentUrl, String labelUrl,
			BODTO<LOSStorageLocation> loc, OrderType type,
			List<BODTO<StockUnit>> sus) throws FacadeException {

		LOSOrderRequest r;
		Client client = manager.find(Client.class, c.getId());
		
		try {
			Date delivery = new Date(System.currentTimeMillis()
					+ (24 * 3600 * 1000));
			r = orderBusiness.createOrder(client, orderRef, new OrderPositionTO[]{position},
					documentUrl,
					labelUrl, 
					loc.getName(), delivery, type);
			r = manager.find(LOSOrderRequest.class, r.getId());
			
			List<StockUnit> stockUList = new ArrayList<StockUnit>();
			for (BODTO<StockUnit> suTo : sus){
				stockUList.add(manager.find(StockUnit.class, suTo.getId()));
			}
			
			pickOrderService.processOrderRequest(r, stockUList, null);
			
			if (r == null) {
				throw new RuntimeException("OrderRequest could not be created");
			}
		} catch (FacadeException t) {
			log.error(t.getMessage(), t);
			throw t;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(
					InventoryExceptionKey.ORDER_CANNOT_BE_CREATED, t.getLocalizedMessage());

		}
		return r;
		
	}
	
	@SuppressWarnings("unchecked")
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(BODTO<LOSOrderRequestPosition> orderPosTO,
			  																	 BODTO<Lot> lotTO,
			  																	 BODTO<LOSStorageLocation> locationTO) 
		throws InventoryException
	{
		
		if(orderPosTO == null){
			return new LOSResultList<LOSOrderStockUnitTO>();
		}
		
		LOSOrderRequestPosition orderPos = manager.find(LOSOrderRequestPosition.class, orderPosTO.getId());
		
		if(orderPos == null){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ORDERPOSITION, new Object[0]);
		}
		
		StringBuffer sb = new StringBuffer("SELECT new ");
		sb.append(LOSOrderStockUnitTO.class.getName());
		sb.append("(su.id, su.version, su.lot, ul.labelId, ");
		sb.append("ul.storageLocation.name, su.amount, su.reservedAmount) ");
		sb.append(" FROM "+StockUnit.class.getSimpleName()+" su ");
		
		sb.append(" LEFT OUTER JOIN su.lot AS l, ");
				
		sb.append(LOSUnitLoad.class.getSimpleName()+" ul ");
		sb.append("WHERE su.unitLoad=ul ");
		sb.append("AND su.lock = 0 ");
		sb.append("AND (su.amount - su.reservedAmount) > 0 ");
		sb.append("AND ul.lock = 0 ");
		sb.append("AND ul.storageLocation.lock = 0 ");
		sb.append("AND su.itemData=:it ");
				
		Lot lot = null;
		
		if(lotTO != null){
			lot = manager.find(Lot.class, lotTO.getId());
		}
		
		if(lot != null){
			sb.append("AND su.lot=:l ");
			sb.append("AND su.lot.lock = 0 ");
		}
		
		LOSStorageLocation sl = null;
		if(locationTO != null){
			sl = manager.find(LOSStorageLocation.class, locationTO.getId());
		}
		
		if(sl != null){
			sb.append("AND ul.storageLocation=:sl");
		}
		
		Query query = manager.createQuery(sb.toString());
		
		log.debug("--- Query stocks for orderposition > "+sb.toString());
		log.debug("--- Params > IT "+orderPos.getItemData().getNumber()+" | LOT "+lot+" | SL "+sl);
		
		query.setParameter("it", orderPos.getItemData());
		
		if(lot != null){
			query.setParameter("l", lot);
		}
		
		if(sl != null){
			query.setParameter("sl", sl);
		}
		
		return new LOSResultList<LOSOrderStockUnitTO>(query.getResultList());
	}

}
