/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.globals.PickingRequestState;
import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
@Local
public interface LOSPickRequestService extends BasicService<LOSPickRequest>{

	List<LOSPickRequest> getListByState(Client client, PickingRequestState state);

	List<LOSPickRequest> getListByOrder(LOSOrderRequest order) throws FacadeException;

	LOSPickRequest getByUnitLoadOnCart(String unitLoadId) throws EntityNotFoundException;
	
	LOSPickRequest createPickRequest(LOSOrderRequest order, String name) throws FacadeException;

}
