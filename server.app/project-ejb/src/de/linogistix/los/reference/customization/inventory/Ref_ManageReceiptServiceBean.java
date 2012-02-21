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
import de.linogistix.los.inventory.customization.ManageReceiptService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgGR;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;

@Stateless
public class Ref_ManageReceiptServiceBean implements ManageReceiptService {

	@EJB
	HostMsgService hostService;
	
	public void finishGoodsReceiptEnd(LOSGoodsReceipt gr) throws InventoryException {
		try {
			hostService.sendMsg( new HostMsgGR(gr) );
		} catch (FacadeException e) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}
	}

	public void finishGoodsReceiptStart(LOSGoodsReceipt gr)
			throws InventoryException {
	}

}
