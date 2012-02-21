/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestPositionState;
import de.linogistix.los.query.BODTO;

public class LOSOrderRequestPositionTO extends BODTO<LOSOrderRequestPosition>{

	private static final long serialVersionUID = 1L;

	public String itemData;
	public String itemDataName;
	public String lot;
	public BigDecimal amount;
	public String positionState;
	public String parentRequest;
	
	public LOSOrderRequestPositionTO(LOSOrderRequestPosition x){
		this(x.getId(), x.getVersion(), x.getNumber(), x.getItemData().getNumber(), x.getItemData().getName(), x.getLot(), x.getAmount(), x.getItemData().getScale(), x.getPositionState(), x.getParentRequest().getNumber() );
	}

	public LOSOrderRequestPositionTO(Long id, int version, String name, String itemmData, String itemDataName,
			Lot lot, BigDecimal amount, int scale, LOSOrderRequestPositionState positionState, String parentRequest){
		super(id,version, name);
		this.itemData = itemmData;
		this.itemDataName = itemDataName;
		if(lot != null){
			this.lot = lot.getName();
		}else{
			this.lot = "";
		}
		this.amount = amount.setScale(scale);
		this.positionState = positionState.name();
		this.parentRequest = parentRequest;
		setClassName(LOSOrderRequestPosition.class.getName());
	}
	
	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPositionState() {
		return positionState;
	}

	public void setPositionState(String positionState) {
		this.positionState = positionState;
	}

	public String getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(String parentRequest) {
		this.parentRequest = parentRequest;
	}

	public String getItemDataName() {
		return itemDataName;
	}
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}
	
}
