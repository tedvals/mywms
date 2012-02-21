/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Client;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;

import de.linogistix.los.inventory.facade.InventoryProcessFacade;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.ws.ManageInventory;
import de.linogistix.los.util.businessservice.LOSSequenceGeneratorService;

/**
 * 
 * @author Jordan
 */
@Stateless
public class InventoryGeneratorBean implements InventoryGeneratorService {


    @EJB
    private LOSSequenceGeneratorService seqService;
    // -----------------------------------------------------------------------
    
    public String generateGoodsReceiptNumber(Client c){
         String ret;
         String NUMBER_PREFIX = "WE";
         long n = seqService.getNextSequenceNumber(LOSGoodsReceipt.class);
         ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
         return ret;
     }
     
     public String generateAdviceNumber(Client c){
         String ret;
         String NUMBER_PREFIX = "AVIS";
         long n = seqService.getNextSequenceNumber(LOSAdvice.class);
         ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
         return ret;
     }

    public String generateUnitLoadLabelId(Client c, UnitLoadType ulType) {
        
        String ret;
        
        long n = seqService.getNextSequenceNumber(UnitLoadType.class);
        ret = String.format("%1$06d", n);
        return ret;
        
    }

    public String generatePickOrderNumber(Client c) {
        String ret;
        String NUMBER_PREFIX = "PICK";
        long n = seqService.getNextSequenceNumber(LOSPickRequest.class);
        ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
        return ret;
    }

    public String generateOrderNumber(Client c) {
        String ret;
        String NUMBER_PREFIX = "ORDER";
        long n = seqService.getNextSequenceNumber(LOSOrderRequest.class);
        ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
        return ret;
    }

    public String generateRecordNumber(Client c, String prefix, LOSStockUnitRecordType type) {
        String ret;
        String NUMBER_PREFIX = "R";
        long n = seqService.getNextSequenceNumber(LOSStockUnitRecord.class);
        ret = String.format(NUMBER_PREFIX  + "-%2$s-%3$s %1$06d", n, prefix, type.toString());
        return ret;
    }

    public String generateStorageRequestNumber(Client c) {
        String ret;
        String NUMBER_PREFIX = "STORE";
        long n = seqService.getNextSequenceNumber(LOSStorageRequest.class);
        ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
        return ret;
    }

	public String generateExtinguishOrderNumber(Client c) {
		String ret;
		String NUMBER_PREFIX = "EXT";
		long n = seqService.getNextSequenceNumber(ExtinguishRequest.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}
	
	public String generatePickReceiptReceiptNumber(Client c, UnitLoad ul) {
		String ret;
		String NUMBER_PREFIX = "PREC";
		long n = seqService.getNextSequenceNumber(PickReceipt.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}

	public String generateGoodsOutNumber(Client c) {
		String ret;
		String NUMBER_PREFIX = "GOUT";
		long n = seqService.getNextSequenceNumber(LOSGoodsOutRequest.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}

	public String generateReplenishNumber(Client c) {
		String ret;
		String NUMBER_PREFIX = "REPL";
		long n = seqService.getNextSequenceNumber(PickReceipt.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}

	public String generateManageInventoryNumber(User user) {
		String ret;
		String NUMBER_PREFIX = "IMAN";
		long n = seqService.getNextSequenceNumber(ManageInventory.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}
	
	public String generateInventoryProcessNumber(User user) {
		String ret;
		String NUMBER_PREFIX = "IINV";
		long n = seqService.getNextSequenceNumber(InventoryProcessFacade.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}
	

}
