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
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.report.businessservice.ReportException;

/**
 *
 * @author trautm
 */
@Remote
public interface StockUnitLabelReport {

	/**
	 * Creates a StockUnitLabel, persists and print it.
	 * 
	 * This method will be called during Goods receipt process to create a label. 
	 * @param pos
	 * @return
	 * @throws ReportException
	 */
	public StockUnitLabel createStockUnitLabelGR(LOSGoodsReceiptPosition pos, String printer) throws ReportException ;
	    
		
    /**
     * Generates but doesn NOT persist a StockUnitLabel.
     * 
     * @param c
     * @param type
     * @param pos
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    StockUnitLabel generateStockUnitLabelGR(Client c, String type, LOSGoodsReceiptPosition pos) throws ReportException;

    StockUnitLabel generateStockUnitLabel(String type, StockUnit su) throws ReportException; 
    
    /**
     * Prints a persisted StockUnitLabel.
     * 
     * @param c
     * @param type
     * @param pos
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    StockUnitLabel printStockUnitLabel(StockUnit su, String printer) throws ReportException;
    
    /**
     * Prints a given StockUnitLabel.
     * 
     * @param c
     * @param type
     * @param pos
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    public StockUnitLabel printStockUnitLabel(StockUnitLabel label, String printer) throws ReportException;
        
    	
}
