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

import org.mywms.globals.PickingRequestState;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.location.model.LOSStorageLocation;

@Local
public interface ExtinguishRequestService extends
		BasicService<ExtinguishRequest> {

	public ExtinguishRequest create(Client client, ExtinguishOrder order,
			Lot lot, LOSStorageLocation sl);

	public List<ExtinguishRequest> getListByState(PickingRequestState state);

	

}
