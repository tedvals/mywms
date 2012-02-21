/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSInventory;

/**
 * TO for communication Inventory information.
 * 
 * @author trautm
 *
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)             
@XmlType(
		name = "InventoryTO",
		namespace="http://com.linogistix/inventory" )
public class InventoryTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InventoryTO(){
		this.clientRef = "<<no client reference>>";
		this.articleRef = "<<no article reference>>";
	}
	
	/**
	 * @param clientRef
	 * @param articleRef
	 */
	public InventoryTO(String clientRef, String articleRef, int scale){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.scale = scale;
	}
	
	/**
	 * @param clientRef
	 * @param articleRef
	 */
	public InventoryTO(String clientRef, String articleRef, String lotRef, int scale){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.lotRef = lotRef;
		this.scale = scale;
	} 
	
	/**
	 * @param clientRef
	 * @param articleRef
	 * @param reserved
	 * @param available
	 * @param locked
	 * @param advised
	 * @param inStock
	 * @param lastIncoming
	 */
	public InventoryTO(
			String clientRef, 
			String articleRef,
			String lotRef,
			BigDecimal reserved,
			BigDecimal amount,
			boolean locked){
		
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.lotRef = lotRef;
		this.reserved = reserved;
		this.inStock = amount;
	} 
	
	/**
	 * @param i
	 */
	public InventoryTO (LOSInventory i){
		this.clientRef = i.getClient().getNumber();
		this.articleRef = i.getItemDataRef();
		this.advised = i.getAdvised();
		this.available = i.getAvailable();
		this.inStock = i.getInStock();
		this.locked = i.getLocked();
		this.reserved = i.getReserved();
//		this.lastIncoming = i.getLastIncoming();
//		this.lastAmount = i.getLastAmount();
	}
	
	/**
	 * @param i
	 * @param lot
	 */
	public InventoryTO (LOSInventory i, Lot lot){
		this.clientRef = i.getClient().getNumber();
		this.articleRef = i.getItemDataRef();
		this.lotRef = lot.getName();
		this.advised = i.getAdvised();
		this.available = i.getAvailable();
		this.inStock = i.getInStock();
		this.locked = i.getLocked();
		this.reserved = i.getReserved();
//		this.lastIncoming = i.getLastIncoming();
//		this.lastAmount = i.getLastAmount();
	}
	
	
	
	/**
	 * A unique reference to the ItemData/article
	 */
	public String articleRef;
	
	/**
	 * A unique reference to the Client
	 */
	public String clientRef;
	
	/**
	 * A unique reference to the Batch/Lot
	 */
	public String lotRef;
	
	/**
	 * Number of pieces that are reserved
	 */
	public BigDecimal reserved = new BigDecimal(0);
	
	/**
	 * Number of pieces that are available
	 */
	public BigDecimal available = new BigDecimal(0);;
	
	/**
	 * Number of pieces that are locked
	 */
	public BigDecimal locked = new BigDecimal(0);;
	
	/**
	 * Number of pieces that are advised
	 */
	public BigDecimal advised = new BigDecimal(0);;
	
	/**
	 * Number of pieces in stock.
	 * 
	 * <code>inStock = available + locked + reserved</code>
	 */
	public BigDecimal inStock = new BigDecimal(0);;

	/**
	 * The scale of all amounts
	 */
	public int scale;
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("InventoryTO: ");
		
		ret.append("[clientRef=");
		ret.append(clientRef);
		ret.append("] ");
		ret.append("[articleRef=");
		ret.append(articleRef);
		ret.append("] ");
		
		ret.append("[inStock=");
		ret.append(inStock);
		ret.append("] ");
		ret.append("[available=");
		ret.append(available);
		ret.append("] ");
		ret.append("[reserved=");
		ret.append(reserved);
		ret.append("] ");
		ret.append("[advised=");
		ret.append(advised);
		ret.append("] ");
		ret.append("[locked=");
		ret.append(locked);
		ret.append("] ");
		
		return new String(ret);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj  == null) return false;
		
		if (obj == this) return true;
		
		if (obj instanceof InventoryTO){
			InventoryTO to = (InventoryTO)obj;
			return this.clientRef.equals(to.clientRef) 
                    && this.articleRef.equals(to.articleRef)
                    && this.lotRef.equals(to.lotRef);
		} else{
			return false;
		}
		
	}

}
