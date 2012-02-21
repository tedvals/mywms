/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.query.BODTO;

public class LOSGoodsOutRequestTO extends BODTO<LOSGoodsOutRequest> {

	String number;
	String parentRequest;
	String client;
	String outState;
	
	private static final long serialVersionUID = 1L;

	public LOSGoodsOutRequestTO( LOSGoodsOutRequest x ){
		this(x.getId(), x.getVersion(), x.getNumber(), x.getNumber(), x.getParentRequest().getNumber(), x.getOutState(), x.getClient().getNumber());
	}
	
	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String name,
			String number,
			String parentRequestNumber,
			LOSGoodsOutRequestState outState,
			String clientNumber){
		
		super(id, version, name);
		this.number = number;
		this.parentRequest = parentRequestNumber;
		this.outState = outState.name(); 
		this.client = clientNumber;
		setClassName(LOSGoodsOutRequest.class.getName());
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(String parentRequest) {
		this.parentRequest = parentRequest;
	}

	public String getOutState() {
		return outState;
	}

	public void setOutState(String outState) {
		this.outState = outState;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

}
