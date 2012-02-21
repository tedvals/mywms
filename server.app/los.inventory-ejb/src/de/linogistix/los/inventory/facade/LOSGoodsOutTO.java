/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.io.Serializable;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;

public class LOSGoodsOutTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private LOSGoodsOutRequest order;
	private String nextLocationName;
	private String nextUnitLoadLabelId;
	private long numPosOpen = 0L;
	private long numPosDone = 0L;
	private boolean finished = false;

	public LOSGoodsOutTO( LOSGoodsOutRequest order ) {
		this.order = order;
	}

	public LOSGoodsOutRequest getOrder() {
		return order;
	}
	
	public String getOrderNumber() {
		return order == null ? "" : order.getNumber();
	}

	public String getNextLocationName() {
		return nextLocationName;
	}

	public void setNextLocationName(String nextLocationName) {
		this.nextLocationName = nextLocationName;
	}

	public String getNextUnitLoadLabelId() {
		return nextUnitLoadLabelId;
	}

	public void setNextUnitLoadLabelId(String nextUnitLoadLabelId) {
		this.nextUnitLoadLabelId = nextUnitLoadLabelId;
	}

	public long getNumPosOpen() {
		return numPosOpen;
	}

	public void setNumPosOpen(long numPosOpen) {
		this.numPosOpen = numPosOpen;
	}

	public long getNumPosDone() {
		return numPosDone;
	}

	public void setNumPosDone(long numPosDone) {
		this.numPosDone = numPosDone;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public String getComment() {
		return order == null ? "" : order.getAdditionalContent();
	}
	
}
