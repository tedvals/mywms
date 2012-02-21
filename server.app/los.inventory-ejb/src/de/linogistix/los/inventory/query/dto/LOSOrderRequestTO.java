/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.query.BODTO;

public class LOSOrderRequestTO extends BODTO<LOSOrderRequest>{

	private static final long serialVersionUID = 1L;

	public String clientNumber;
	public String orderRef;
	public Date delivery;
	public String orderState;
	public String destinationName;
	
	public LOSOrderRequestTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public LOSOrderRequestTO(Long id, int version, String name, String clientNumber, String orderRef, Date deliveryDate, LOSOrderRequestState orderState, String destinationName){
		super(id, version, name);
		this.orderState = orderState.name();
		this.delivery = deliveryDate;
		this.clientNumber = clientNumber;
		this.orderRef = orderRef;
		this.destinationName = destinationName;
		setClassName(LOSOrderRequest.class.getName());
	}


	public String getClientNumber() {
		return clientNumber;
	}


	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}


	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}


	public Date getDelivery() {
		return delivery;
	}


	public void setDelivery(Date delivery) {
		this.delivery = delivery;
	}


	public String getOrderState() {
		return orderState;
	}


	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}


	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	
}
