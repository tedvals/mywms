/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.PickingRequestState;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class ExtinguishRequestServiceBean extends
		BasicServiceBean<ExtinguishRequest> implements ExtinguishRequestService {

	Logger log = Logger.getLogger(ExtinguishRequestServiceBean.class);

	@EJB
	InventoryGeneratorService generatorService;

	@EJB
	ContextService contextService;
	
	public ExtinguishRequest create(Client client, ExtinguishOrder order, Lot lot,
			LOSStorageLocation destination) {

		if (client == null || lot == null) {
			throw new NullPointerException(
					"createExtinguishRequest: parameter == null");
		}
		client = manager.merge(client);
		lot = manager.merge(lot);

		ExtinguishRequest request = new ExtinguishRequest();
		request.setClient(client);
		request.setParentRequest(order);
		request.setLot(lot);
		request.setState(PickingRequestState.RAW);

		String requestId = generatorService
				.generateExtinguishOrderNumber(client);
		request.setNumber(requestId);
		request.setCustomerNumber(client.getNumber());
		request.setDestination(destination);
		request.setState(PickingRequestState.RAW);
		request.setUser(contextService.getCallersUser());
		manager.persist(request);
		manager.flush();

		return request;
	}

	@SuppressWarnings("unchecked")
	public List<ExtinguishRequest> getListByState(PickingRequestState state) {

		Query query = manager.createQuery("SELECT pr FROM "
				+ ExtinguishRequest.class.getSimpleName() + " pr "
				+ "WHERE  pr.state=:state " + " ORDER BY pr.number ASC");

		query.setParameter("state", state);

		return (List<ExtinguishRequest>) query.getResultList();

	}

}
