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
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;

@Stateless
public class ReplenishRequestServiceBean extends
		BasicServiceBean<LOSReplenishRequest> implements
		ReplenishRequestService {

	Logger log = Logger.getLogger(ReplenishRequestServiceBean.class);

	@EJB
	ClientService clientService;

	@EJB
	ItemDataService idatService;

	@EJB
	LOSStorageLocationService slService;

	@EJB
	LogItemService logService;

	@EJB
	OrderRequestService orderService;

	@EJB
	InventoryGeneratorService genService;

	public final String NUMBER_PREFIX = "ORDER";

	public LOSReplenishRequest create(Client client, String requestId,
			Date delivery, LOSStorageLocation destination, String documentUrl,
			String labelUrl) throws InventoryException {

		LOSReplenishRequest r;

		r = new LOSReplenishRequest();
		r.setOrderType(OrderType.TO_REPLENISH);
		r.setRequestId(requestId);
		r.setNumber(requestId);
		r.setClient(client);
		r.setDelivery(delivery);
		r.setDestination(destination);
		r.setDocumentUrl(documentUrl);
		r.setLabelUrl(labelUrl);
		r.setOrderState(LOSOrderRequestState.RAW);

		manager.persist(r);
		manager.flush();

		return r;
	}

	public int addPosition(LOSReplenishRequest request, ItemData idat, Lot lot,
			BigDecimal amount, boolean partitionAllowed) {
		return orderService.addPosition(request, idat, lot, amount,
				partitionAllowed);
	}

	public LOSReplenishRequest getByRequestId(String requestId)
			throws EntityNotFoundException {

		Query query = manager.createQuery("SELECT o FROM "
				+ LOSReplenishRequest.class.getSimpleName() + " o "
				+ "WHERE o.requestId=:requestId");

		query.setParameter("requestId", requestId);

		try {
			LOSReplenishRequest o = (LOSReplenishRequest) query
					.getSingleResult();
			return o;
		} catch (NoResultException ex) {
			log.error(ex.getMessage(), ex);
			throw new EntityNotFoundException(
			// ###TODO: resourcekey
					ServiceExceptionKey.NO_AREA_WITH_NAME);
		}

	}
}
