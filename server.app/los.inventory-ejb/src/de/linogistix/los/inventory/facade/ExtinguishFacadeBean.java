/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;
import org.mywms.globals.PickingRequestState;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.service.ExtinguishRequestService;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * 
 * @author artur, liyu
 */
@Stateless
public class ExtinguishFacadeBean implements ExtinguishFacade {

	@EJB
	PickOrderFacade pickOrderFacade;

	@EJB
	ExtinguishRequestService extinguishRequestService;

	@EJB
	ContextService contextService;

	@EJB
	ClientService clientService;

	
	public ExtinguishRequest accept(ExtinguishRequest request)
			throws PickingException, InventoryException,
			BusinessObjectNotFoundException, BusinessObjectSecurityException {
		return (ExtinguishRequest) pickOrderFacade.accept(request);
	}

	public ExtinguishRequest finishCurrentUnitLoad(ExtinguishRequest req,
			String transfer) throws FacadeException {

		return (ExtinguishRequest) pickOrderFacade.finishCurrentUnitLoad(req,
				transfer);
	}

	public ExtinguishRequest finishExtinguishRequest(ExtinguishRequest req,
			String labelID) throws FacadeException {

		return (ExtinguishRequest) pickOrderFacade.finishPickingRequest(req,
				labelID);
	}

	public LOSStorageLocation getDestination(ExtinguishRequest request)
			throws PickingException {
		return pickOrderFacade.getDestination(request);
	}

	public List<ExtinguishRequest> getRawExtinguishRequest() {
		return extinguishRequestService.getListByState(PickingRequestState.RAW);
	}

	public LOSStorageLocation getStorageLocation(LOSPickRequestPosition position)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException {
		return pickOrderFacade.getStorageLocation(position);
	}

	public ExtinguishRequest loadExtinguishRequest(ExtinguishRequest req)
			throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException {
		return (ExtinguishRequest) pickOrderFacade.loadPickingRequest(req);
	}

//	public LOSPickRequestPosition processPickRequestPosition(
//			LOSPickRequestPosition position, boolean unexpectedNullAmount,
//			String label, BigDecimal amount) throws FacadeException {
//		return pickOrderFacade.processPickRequestPosition(position,
//				unexpectedNullAmount, label, amount);
//	}

}
