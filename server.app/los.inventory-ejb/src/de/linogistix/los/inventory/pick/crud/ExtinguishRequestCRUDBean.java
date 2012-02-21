/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.crud;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.pick.service.ExtinguishRequestService;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;

/**
 * @author liu
 *
 */
@Stateless
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
	org.mywms.globals.Role.OPERATOR_STR,
	org.mywms.globals.Role.FOREMAN_STR,
	org.mywms.globals.Role.INVENTORY_STR,
	org.mywms.globals.Role.CLEARING_STR})
public class ExtinguishRequestCRUDBean extends
		BusinessObjectCRUDBean<ExtinguishRequest> implements
		ExtinguishRequestCRUDRemote {

	@EJB
	ExtinguishRequestService service;

	@Override
	protected BasicService<ExtinguishRequest> getBasicService() {
		return service;
	}

}
