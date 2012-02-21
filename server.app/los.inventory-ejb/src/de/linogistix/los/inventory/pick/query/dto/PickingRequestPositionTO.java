/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.query.BODTO;

public class PickingRequestPositionTO extends BODTO<LOSPickRequestPosition>{

	private static final long serialVersionUID = 1L;

	public String itemData;
	public String itemDataName;
	public String lot;
	public BigDecimal amount;
	public String stockUnit;
	public boolean canceled;
	public boolean solved;
	public String pickRequest;
	public String parentRequest;
	
	public PickingRequestPositionTO( LOSPickRequestPosition x ){
		this(x.getId(), x.getVersion(), x.getId(), x.getItemData().getNumber(), x.getItemData().getName(), x.getItemData().getScale(), x.getAmount(), x.isSolved(), x.getPickRequest().getNumber() );
	}
	
	public PickingRequestPositionTO(
			Long id, 
			int version,
			Long name,
			String parentRequest,
			String piString,
			Long stockUnit,
			Boolean solved,
			Boolean canceled,
			String lot ){
		super(id, version, name);
		this.parentRequest = parentRequest;
		this.pickRequest = piString;
		this.stockUnit = stockUnit!=null?stockUnit.toString():null;
		this.solved = solved!=null?solved:false;
		this.canceled = canceled!=null?canceled:false;
		this.lot = lot;
		setClassName(LOSPickRequestPosition.class.getName());
		
	}
	
	public PickingRequestPositionTO(
			Long id, 
			int version,
			Long name,
			String parentRequest,
			Boolean solved,
			Boolean canceled
			){
		super(id, version, name);
		this.parentRequest = parentRequest;
		this.solved = solved!=null?solved:false;
		this.canceled = canceled!=null?canceled:false;
		setClassName(LOSPickRequestPosition.class.getName());

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

	public String getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(String stockUnit) {
		this.stockUnit = stockUnit;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public String getPickRequest() {
		return pickRequest;
	}

	public void setPickRequest(String pickRequest) {
		this.pickRequest = pickRequest;
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

	public PickingRequestPositionTO(Long id, 
									int version,
									Long name,
									String itemNumber,
									String itemName,
									int scale,
									BigDecimal amount,
									Boolean solved,
									String pickRequestNumber)
	{
		super(id, version, name);
		this.itemData = itemNumber;
		this.itemDataName = itemName;
		this.amount = amount.setScale(scale);
		this.solved = solved != null ? solved : false;
		this.pickRequest = pickRequestNumber;
	}
	
}
