/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.LogItemService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;

@Stateless
public class OrderRequestServiceBean extends BasicServiceBean<LOSOrderRequest>
		implements OrderRequestService {

	Logger log = Logger.getLogger(OrderRequestServiceBean.class);

	@EJB
	ClientService clientService;

	@EJB
	ItemDataService idatService;

	@EJB
	LOSStorageLocationService slService;

	@EJB
	LogItemService logService;

	@EJB
	InventoryGeneratorService genService;

	@EJB
	OrderRequestService orderService;

	public final String NUMBER_PREFIX = "ORDER";

	public LOSOrderRequest create(String clientRef, String requestId,
			OrderType orderType, Date delivery, String destination,
			String documentUrl, String labelUrl) throws InventoryException {

		LOSOrderRequest r;

		Client c = clientService.getByNumber(clientRef);

		if (c == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT,
					clientRef);
		}

		LOSStorageLocation sl = slService.getByName(c, destination);

		if (sl == null) {
			sl = slService.getByName(clientService.getSystemClient(),
					destination);
		}

		r = new LOSOrderRequest();
		r.setOrderType(orderType);
		r.setNumber(genService.generateOrderNumber(c));
		if (requestId != null && requestId.length() > 0) {
			r.setRequestId(requestId);
		} else {
			r.setRequestId(r.getNumber());
		}
		r.setParentRequestNumber(requestId);
		r.setClient(c);
		r.setDelivery(delivery);
		r.setDestination(sl);
		r.setDocumentUrl(documentUrl);
		r.setLabelUrl(labelUrl);
		r.setOrderState(LOSOrderRequestState.RAW);
		manager.persist(r);
		manager.flush();

		//logService.create(c, "", "", "", LogItemType.LOG, "", "");

		return r;
	}

	public int addPosition(LOSOrderRequest request, ItemData idat, Lot lot,
			BigDecimal amount, boolean partitionAllowed) {
		LOSOrderRequestPosition pos;

		pos = new LOSOrderRequestPosition();
		pos.setAmount(amount);
		pos.setPartitionAllowed(partitionAllowed);
		pos.setClient(request.getClient());
		pos.setLot(lot);
		pos.setItemData(idat);
		pos.setParentRequest(request);

		pos.setNumber(request.getNumber()
				+ "_"
				+ (request.getPositions() == null ? "0" : request
						.getPositions().size()));
		manager.persist(pos);
		manager.flush();

		return request.addPosition(pos);

	}

	public LOSOrderRequest getByRequestId(String requestId)
			throws EntityNotFoundException {

		Query query = manager.createQuery("SELECT o FROM "
				+ LOSOrderRequest.class.getSimpleName() + " o "
				+ "WHERE o.requestId=:requestId");

		query.setParameter("requestId", requestId);

		try {
			LOSOrderRequest o = (LOSOrderRequest) query.getSingleResult();
			return o;
		} catch (NoResultException ex) {
			log.error(ex.getMessage(), ex);
			throw new EntityNotFoundException(
			// ###TODO: resourcekey
					ServiceExceptionKey.NO_AREA_WITH_NAME);
		}

	}

	public void delete(LOSOrderRequest r) {
		r = manager.find(LOSOrderRequest.class, r.getId());
		List<LOSOrderRequestPosition> pos = r.getPositions();
		for (LOSOrderRequestPosition p : pos) {
			p = manager.find(LOSOrderRequestPosition.class, p.getId());
			if (p != null) {
				manager.remove(p);
			}
		}
		manager.remove(r);
		manager.flush();

	}

	@SuppressWarnings("unchecked")
	public List<LOSOrderRequest> getActiveByDestination(LOSStorageLocation dest) {
		Query query = manager.createQuery("SELECT o FROM "
				+ LOSOrderRequest.class.getSimpleName() + " o "
				+ " WHERE o.destination=:dest"
				+ " AND o.orderState IN (:raw, :processing, :pickedpartial) ");

		query.setParameter("dest", dest);
		query.setParameter("raw", LOSOrderRequestState.RAW);
		query.setParameter("processing", LOSOrderRequestState.PROCESSING);
		query
				.setParameter("pickedpartial",
						LOSOrderRequestState.PICKED_PARTIAL);

		return query.getResultList();

	}

}
