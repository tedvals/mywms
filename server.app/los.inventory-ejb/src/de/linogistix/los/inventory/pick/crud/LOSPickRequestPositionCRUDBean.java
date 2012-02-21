/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.pick.crud;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;



/**
 * @author trautm
 *
 */
@Stateless
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
	org.mywms.globals.Role.OPERATOR_STR,
	org.mywms.globals.Role.FOREMAN_STR,
	org.mywms.globals.Role.INVENTORY_STR,
	org.mywms.globals.Role.CLEARING_STR})
public class LOSPickRequestPositionCRUDBean extends BusinessObjectCRUDBean<LOSPickRequestPosition> implements LOSPickRequestPositionCRUDRemote {

	@EJB 
	LOSPickRequestPositionService service;
	
	@Override
	protected BasicService<LOSPickRequestPosition> getBasicService() {	
		return service;
	}
	
}
