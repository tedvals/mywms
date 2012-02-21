package de.linogistix.los.inventory.model;

import de.linogistix.los.model.HostMsg;

public class HostMsgOrder extends HostMsg{

	private LOSOrderRequest order;
	private String orderNumber;
	
	public HostMsgOrder( LOSOrderRequest order ) {
		this.order = order;
		this.orderNumber = (order == null ? "?" : order.getNumber());
	}

	public LOSOrderRequest getOrder() {
		return order;
	}

	public void setOrder(LOSOrderRequest order) {
		this.order = order;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	
}
