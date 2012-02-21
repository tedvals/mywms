/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.query.dto;

import java.util.Date;

import org.mywms.globals.PickingRequestState;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.query.BODTO;

public class PickingRequestTO extends BODTO<LOSPickRequest> {

	private static final long serialVersionUID = 1L;
	public String client;
	public String state;
	public String parentRequest;
	public Date created;
//	public String user;
	public String orderRef;
	
	public PickingRequestTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public PickingRequestTO(Long id, int version, String name, Date created, String client, PickingRequestState state, String parentRequest, String orderRef){
		super(id, version, name);
		this.created = created;
		this.client = client;
		this.state = state.name();
		this.parentRequest = parentRequest;	
		this.orderRef = orderRef;
		setClassName(LOSPickRequest.class.getName());
	}
	
	public PickingRequestTO(Long id, int version, String name, Date created, String client, PickingRequestState state, String parentRequest){
		super(id, version, name);
		this.created = created;
		this.client = client;
		this.state = state.name();
		this.parentRequest = parentRequest;	
		this.orderRef = "";
		setClassName(LOSPickRequest.class.getName());

	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(String parentRequest) {
		this.parentRequest = parentRequest;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}
}
