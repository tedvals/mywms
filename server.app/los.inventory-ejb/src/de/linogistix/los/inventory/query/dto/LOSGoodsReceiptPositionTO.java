/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.query.BODTO;

public class LOSGoodsReceiptPositionTO extends BODTO<LOSGoodsReceiptPosition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String orderReference;
	public BigDecimal amount;
	public String itemData;
	public String lot;
	
	public LOSGoodsReceiptPositionTO(LOSGoodsReceiptPosition x)	{
		this(x.getId(), x.getVersion(), x.getPositionNumber(), x.getOrderReference(), x.getAmount(), x.getItemData(), x.getLot(), x.getScale());
	}
	
	public LOSGoodsReceiptPositionTO(Long id, int version, 
					String positionNumber,
					String orderReference,
					BigDecimal amount,
					String itemData, 
					String lot,
					int scale) 
	{
		super(id, version, positionNumber);
		this.orderReference = orderReference;
		this.amount = amount;
		this.itemData = itemData;
		this.lot = lot;
		try {
			this.amount = amount.setScale(scale);
		}
		catch( Throwable t ) {
			this.amount = amount;
		}
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
}
