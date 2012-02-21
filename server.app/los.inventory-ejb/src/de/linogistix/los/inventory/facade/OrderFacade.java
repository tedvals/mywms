/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.businessservice.OrderCannotBeStarted;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;

/**
 * A Facade for ordering items from stock
 * @author trautm
 *
 */
@Remote
public interface OrderFacade {
	
	/**
	 * creates a new Order for retrieving items from stock.
	 * 
	 * @param clientRef
	 *            a reference to the client
	 * @param orderRef
	 *            an external order reference rom ERP system or customer
	 * @param articleRefs
	 *            a list of articles
	 * @param document
	 *            an url to the document to be printed with the order
	 * @param label
	 *            an url to the label to be printed with the order
	 * @return true if order has been created
	 */
	@WebMethod
	boolean order(@WebParam(name = "clientRef")
	String clientRef, @WebParam(name = "orderRef")
	String orderRef, @WebParam(name = "positions")
	OrderPositionTO[] positions, @WebParam(name = "documentUrl")
	String documentUrl, @WebParam(name = "labelUrl")
	String labelUrl, @WebParam(name = "destination")
	String destination) throws FacadeException;

	/**
	 * creates a new Order for retrieving items from stock.
	 * 
	 * {@link OrderType} is responsible whether documents are printed or not.
	 * 
	 * 
	 * @param clientRef a reference to the client
	 * @param orderRef an external order reference rom ERP system or customer
	 * @param positions list of articles/items to be delivered
	 * @param documentUrl an url to the document to be printed with the order
	 * @param labelUrl an url to the label to be printed with the order
	 * @param destination the goods out location where the order has to be finished 
	 * @param type  controls e.g. printing of documents at end of order process
	 * @param deliveryDate when the order has to be finished at goods out
	 * @param processAutomaticly start order directly after creation.
	 * @param comment a description or handling note
	 * @return
	 * @throws FacadeException
	 */
	@WebMethod
	boolean order(@WebParam(name = "clientRef")
	String clientRef, @WebParam(name = "orderRef")
	String orderRef, @WebParam(name = "positions")
	OrderPositionTO[] positions, @WebParam(name = "documentUrl")
	String documentUrl, @WebParam(name = "labelUrl")
	String labelUrl, @WebParam(name = "destination")
	String destination, @WebParam(name = "orderType")
	OrderType type, @WebParam(name = "deliveryDate")
	Date deliveryDate, @WebParam(name = "processAutomaticly")
	boolean processAutomaticly, @WebParam(name = "comment")
	String comment) throws FacadeException;

	LOSOrderRequest order(BODTO<Client> c, String orderRef,
			OrderPositionTO position, String documentUrl, String labelUrl,
			BODTO<LOSStorageLocation> loc, OrderType type,
			List<BODTO<StockUnit>> sus) throws FacadeException;

	// /**
	// * creates a new OrderFacade for retrieving items from stock.
	// *
	// * @param clientRef a reference to the client
	// * @param orderRef a reference to the order
	// * @param articleRefs a list of article references
	// * @return true if order has been created
	// */
	// @WebMethod
	// boolean orderlocal(
	// @WebParam( name="clientRef") String clientRef,
	// @WebParam( name="orderRef") String orderRef,
	// @WebParam( name="positions") OrderPositionTO[] positions,
	// @WebParam( name="destination") String destination);

	/**
	 * Service for finishing {@link LOSOrderRequest}.
	 * Typical operations are printing some labels and setting the {@link LOSOrderRequestState.FINISHED}.
	 */
	void finishOrders(List<BODTO<LOSOrderRequest>> orders)
			throws FacadeException;

	/**
	 * Removes {@link LOSOrderRequest} from system
	 * 
	 * @param r
	 * @throws FacadeException
	 */
	void removeOrder(BODTO<LOSOrderRequest> r) throws FacadeException;

	/**
	 * Start an {@link LOSOrderRequest}. 
	 * {@link LOSOrderRequestState} is typically set to {@link LOSOrderRequestState}.PROCESSING.
	 * 
	 * 
	 * @param clientRef
	 * @param orderRef
	 * @throws FacadeException
	 * @throws OrderCannotBeStarted
	 */
	@WebMethod
	public void startOrder(
		@WebParam(name = "clientRef")
		String clientRef,
		@WebParam(name = "orderRef")
		String orderRef) throws FacadeException, OrderCannotBeStarted;

	/**
	 * Start an {@link LOSOrderRequest}. 
	 * {@link LOSOrderRequestState} is typically set to {@link LOSOrderRequestState}.PROCESSING.
	 * @param orderTo
	 * @throws FacadeException
	 * @throws OrderCannotBeStarted if no {@link LOSPickRequest} could be created
	 */
	public void startOrder(
			BODTO<LOSOrderRequest> orderTo) throws FacadeException, OrderCannotBeStarted ;
	/**
	 * Returns a List of all Goods out locations, i.e. all locations having an {@link LOSArea} with {@link LOSAreaType.GOODS_OUT}
	 * 
	 * @return
	 * @throws LOSLocationException
	 */
	List<BODTO<LOSStorageLocation>> getGoodsOutLocations()
			throws LOSLocationException;

	/**
	 * Returns List of {@link LOSOrderStockUnitTO} that are suitable to solve
	 * given {@link LOSOrderRequestPosition}.
	 * 
	 * @param orderPosTO
	 * @param lotTO
	 * @param locationTO
	 * @return
	 * @throws InventoryException
	 */
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(
			BODTO<LOSOrderRequestPosition> orderPosTO, BODTO<Lot> lotTO,
			BODTO<LOSStorageLocation> locationTO) throws InventoryException;
       
        
}
