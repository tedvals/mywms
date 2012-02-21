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

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.customization.ManageOrderServiceBean;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgOrder;
import de.linogistix.los.inventory.model.LOSOrderRequest;

@Stateless
public class Ref_ManageOrderServiceBean extends ManageOrderServiceBean implements ManageOrderService {
	Logger log = Logger.getLogger(Ref_ManageOrderServiceBean.class);

	@EJB
	HostMsgService hostService;

	@Override
	public void processOrderFinishedEnd(LOSOrderRequest order) throws InventoryException {
		super.processOrderPickedEnd(order);
		try {
			hostService.sendMsg( new HostMsgOrder(order) );
		} catch (FacadeException e) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}

	}

}
