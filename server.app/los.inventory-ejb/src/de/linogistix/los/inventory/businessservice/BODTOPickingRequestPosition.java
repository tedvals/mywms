/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;

import org.mywms.facade.BasicTO;

import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;

/**
 * @author liu
 * 
 */
public class BODTOPickingRequestPosition extends BasicTO {
	private static final long serialVersionUID = 1L;

	public BigDecimal amount;

	public BigDecimal pickedAmount;

	public int index;

	public String itemName;

	public String itemNumber;

	protected BODTOPickingRequestPosition(LOSPickRequestPosition position) {
		super(position);

		amount = position.getAmount();
		pickedAmount = position.getPickedAmount();
		index = position.getIndex();
		if (position.getItemData() != null) {
			itemName = position.getItemData().getName();
			itemNumber = position.getItemData().getNumber();
		}
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPickedAmount() {
		return pickedAmount;
	}

	public void setPickedAmount(BigDecimal pickedAmount) {
		this.pickedAmount = pickedAmount;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

}
