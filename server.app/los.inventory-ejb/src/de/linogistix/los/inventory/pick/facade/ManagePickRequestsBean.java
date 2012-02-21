/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.query.BODTO;

@Stateless
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
	org.mywms.globals.Role.OPERATOR_STR,
	org.mywms.globals.Role.FOREMAN_STR,
	org.mywms.globals.Role.INVENTORY_STR,
	org.mywms.globals.Role.CLEARING_STR})
public class ManagePickRequestsBean implements ManagePickRequests {

	@EJB
	private PickOrderBusiness pickBusiness;
	
	@EJB
	private InventoryGeneratorService genService;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public void finish(List<BODTO<LOSPickRequest>> list) throws FacadeException {
		
		if (list == null){
			return;
		}
		
		for (BODTO<LOSPickRequest> req : list){
			LOSPickRequest r = manager.find(LOSPickRequest.class, req.getId());
			pickBusiness.finish(r, true);
		}
		
		
	}

	public String getNewPickRequestNumber() {
		
		return genService.generatePickOrderNumber(null);
	}
	


	
}
