/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.Date;
import java.util.List;

import de.linogistix.los.inventory.model.LOSOrderReceipients;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.mywms.model.Document;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_orderreceipt"
//	, uniqueConstraints = {
//	    @UniqueConstraint(columnNames = {
//	            "client_id","labelID"
//	        })}
) 
public class OrderReceipt extends Document{

	private static final long serialVersionUID = 1L;

	private String orderNumber;
	
	private String orderReference;
	
	private OrderType orderType;
	
	private String destination; 
	
	private String user;
	
	private Date date;
	
	private LOSOrderRequestState state;

	private List<OrderReceiptPosition> positions;

	private LOSOrderReceipients recipientId;
	private String mark;
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Column(nullable=false, updatable=false)
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(nullable=false, name="\"OR_DATE\"")
	public Date getDate() {
		return date;
	}

	public void setState(LOSOrderRequestState state) {
		this.state = state;
	}

	@Enumerated(EnumType.STRING)
	public LOSOrderRequestState getState() {
		return state;
	}

	public void setPositions(List<OrderReceiptPosition> positions) {
		this.positions = positions;
	}

	@OneToMany(mappedBy="receipt")
	@OrderBy("pos")
	public List<OrderReceiptPosition> getPositions() {
		return positions;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	@Column(nullable=false, updatable=false, name="\"OR_USER_\"")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestination() {
		return destination;
	}
    
	
	/**
	 * Get recipientId.
	 *
	 * @return recipientId as LOSOrderReceipients.
	 */
	@ManyToOne(optional = false)
	public LOSOrderReceipients getRecipientId()
	{
	    return recipientId;
	}
	
	/**
	 * Set recipientId.
	 *
	 * @param recipientId the value to set.
	 */
	public void setRecipientId(LOSOrderReceipients recipientId)
	{
	    this.recipientId = recipientId;
	}
	
	/**
	 * Get mark.
	 *
	 * @return mark as String.
	 */
	public String getMark()
	{
	    return mark;
	}
	
	/**
	 * Set mark.
	 *
	 * @param mark the value to set.
	 */
	public void setMark(String mark)
	{
	    this.mark = mark;
	}
}
