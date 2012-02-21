/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.inventory;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.inventory.customization.ManagePickService;
import de.linogistix.los.inventory.customization.ManagePickServiceBean;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgPick;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;

@Stateless
public class Ref_ManagePickServiceBean extends ManagePickServiceBean implements ManagePickService {

	@EJB
	HostMsgService hostService;

	@Override
	public void finishEnd(LOSPickRequest req) throws FacadeException {
		super.finishEnd(req);
		try {
			hostService.sendMsg( new HostMsgPick(req) );
		} catch (FacadeException e) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}

	}
}
