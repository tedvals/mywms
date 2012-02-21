/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.query.BODTO;

public class LOSFixedLocationAssignmentTO extends BODTO<LOSFixedLocationAssignment>{

	private static final long serialVersionUID = 1L;

	public String storageLocation;
	
	public String itemData;
	public String itemDataName;
	public BigDecimal amount;
	
	public LOSFixedLocationAssignmentTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public LOSFixedLocationAssignmentTO(Long id, int version, Long name, String storageLocation, String itemData, String itemDataName, int scale, BigDecimal amount){
		super(id, version, name);
		this.storageLocation = storageLocation;
		this.itemData = itemData;
		this.itemDataName = itemDataName;
		try {
			this.amount = amount.setScale(scale);
		}
		catch( Throwable t ) {
			this.amount = amount;
		}
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getItemDataName() {
		return itemDataName;
	}
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
