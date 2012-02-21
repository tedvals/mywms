/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;

/**
 *
 * @author Jordan
 */
@Local
public interface InventoryGeneratorService {
     
    
    /**
     * Generates a unique number for a {@link LOSGoodsReceipt}
     * @param c the Client 
     */
    public String generateGoodsReceiptNumber(Client c);
    
    /**
     * Generates a unique number for a {@link LOSAdvice}
     * @param c
     * @return
     */
    public String generateAdviceNumber(Client c);
    
    public String generateUnitLoadLabelId(Client c, UnitLoadType ulType);
    
    public String generatePickOrderNumber(Client c);
    
    public String generateOrderNumber(Client c);
    
    public String generateStorageRequestNumber(Client c);
    
    public String generateRecordNumber(Client c, String prefix, LOSStockUnitRecordType type);
    
    public String generateExtinguishOrderNumber(Client c);
    
    public String generatePickReceiptReceiptNumber(Client c, UnitLoad ul) ;
    
    public String generateGoodsOutNumber(Client c) ;

	public String generateReplenishNumber(Client c);
    
	public String generateManageInventoryNumber(User user);
	
	public String generateInventoryProcessNumber(User user);
	
}
