/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.pick.model.PickReceipt;

@Stateless
public class PickReceiptServiceBean extends BasicServiceBean<PickReceipt> implements PickReceiptService{

}
