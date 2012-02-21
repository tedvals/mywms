/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.report.businessservice.ReportException;

@Remote
public interface OrderReport {
	
	public OrderReceipt generateOrderReceipt(Client c, String type, LOSOrderRequest r) throws ReportException;
}
