/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.report;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.report.businessservice.ReportException;

@Remote
public interface PickReport {
	
	public PickReceipt generatePickReceipt(Client c, String type, LOSUnitLoad ul, List<LOSPickRequestPosition> positions) throws ReportException;
}
