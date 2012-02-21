/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.query.BODTO;

public class LOSGoodsOutPositionTO extends BODTO<LOSGoodsOutRequestPosition> {

	private static final long serialVersionUID = 1L;
	
	private String unitLoadLabel;
	private String outState;
	private String goodsOutNumber;
	
	
	public LOSGoodsOutPositionTO(LOSGoodsOutRequestPosition x) {
		this(x.getId(), x.getVersion(), x.getId(), x.getOutState(), x.getSource().getLabelId(), x.getGoodsOutRequest().getNumber());
	}
	
	public LOSGoodsOutPositionTO(Long id, int version, Long name,
				LOSGoodsOutRequestPositionState outState,
				String unitLoadLabel,
				String goodsOutNumber) {
		super(id, version, name);
		this.unitLoadLabel = unitLoadLabel;
		this.outState = outState.name();
		this.goodsOutNumber = goodsOutNumber;
	}


	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}


	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getOutState() {
		return outState;
	}


	public void setOutState(String outState) {
		this.outState = outState;
	}


	public String getGoodsOutNumber() {
		return goodsOutNumber;
	}


	public void setGoodsOutNumber(String goodsOutNumber) {
		this.goodsOutNumber = goodsOutNumber;
	}

	
}
