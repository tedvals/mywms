/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.query.BODTO;

/**
 * @author liu
 * 
 */
public class ExtinguishOrderTO extends BODTO<ExtinguishOrder> {

	private static final long serialVersionUID = 1L;

	private String lotName;
	
	private String itemDataNumber;
	
	private String clientNumber;
	
	private String requestNumber;

	private String destinationName;

	private String orderState;

	private String userName;

	private Date date;

	public ExtinguishOrderTO(ExtinguishOrder order) {
		super(order.getId(), order.getVersion(), order.getNumber());

		lotName = order.getLot() == null ? "" : order.getLot().getName();
		itemDataNumber = order.getLot() == null ? "" : order.getLot().getItemData().getNumber();
		requestNumber = order.getNumber();
		
		if (order.getDestination() != null)
			destinationName = order.getDestination().getName();

		userName = order.getAuthorizedBy().getName();
		orderState = order.getOrderState().name();
		date = order.getDelivery();
	}
	
	public ExtinguishOrderTO(Long id, int version, String number, 
			String lotName, String itemDataNumber, String clientNumber,
			String destinationName,
			LOSOrderRequestState orderState, String userName, Date date) {
		super(id, version, number);
		this.lotName = lotName;
		this.itemDataNumber = itemDataNumber;
		this.clientNumber = clientNumber;
		this.destinationName = destinationName;
		this.userName = userName;
		this.orderState = orderState.name();
		this.date = date;
	}

	public String getLotName() {
		return lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}
	
	public String getItemDataNumber() {
		return itemDataNumber;
	}

	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getRequestNumber() {
		return requestNumber;
	}

	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
