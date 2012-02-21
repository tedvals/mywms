/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.OrderReceipt;
@Stateless
public class OrderReceiptServiceBean extends BasicServiceBean<OrderReceipt> implements OrderReceiptService{

}
