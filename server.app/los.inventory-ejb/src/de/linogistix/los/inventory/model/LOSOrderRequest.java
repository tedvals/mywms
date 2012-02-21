/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Copyright (c) 2006-2008 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.Request;

import de.linogistix.los.inventory.businessservice.LOSGoodsOutBusiness;
import de.linogistix.los.inventory.businessservice.OrderBusiness;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * Represents an order of goods/items that should be retrieved from the warehouse.
 * 
 * What items should be retrieved (and in what quantity) is hold in a List of 
 * {@link LOSOrderRequestPosition} (see {@link #getPositions()}).
 * 
 * Furthermore the order holds information about where (at which gate, {@link #getDestination()}) and 
 * when ({@link #getDelivery()}) the items are expected for shipping.
 * 
 * The {@link LOSOrderRequest} Object can be created via dialogue or ERP interface.
 * 
 * @author trautm
 */
@Entity
@Table(name = "los_orderreq")
public class LOSOrderRequest extends Request{
    
	private static final long serialVersionUID = 1L;

	private List<LOSOrderRequestPosition> positions;

    private LOSOrderRequestState orderState;

    private Date delivery;
    
    private String requestId;
    
    private LOSStorageLocation destination;
    
    private String documentUrl;
    
    private String labelUrl;    
    
    private OrderType orderType = OrderType.TO_CUSTOMER;
    
    /**
     * @return A list of positions which describe what items should be retrieved (and in what quantity)
     * 
     */
    @OneToMany(mappedBy="parentRequest")
    @OrderBy("positionIndex ASC")
    public List<LOSOrderRequestPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<LOSOrderRequestPosition> positions) {
        this.positions = positions;
    }

    /**
     * @see LOSOrderRequestState
     * @return The state of this {@link LOSOrderRequest}
     */
    @Enumerated(EnumType.STRING)
    public LOSOrderRequestState getOrderState() {
        return orderState;
    }

    public void setOrderState(LOSOrderRequestState orderState) {
        this.orderState = orderState;
    }

    /**
     * At this date all {@link LOSOrderRequestPosition} of this {@link LOSOrderRequest} are expected to be at the gate
     *  and ready for shipping.
     */
    @Temporal(TemporalType.DATE)
    public Date getDelivery() {
        return delivery;
    }

    public void setDelivery(Date delivery) {
        this.delivery = delivery;
    }

    /**
     * An (external) identifier, often a handle to the ERP order.
     * 
     * @return
     */
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * @see #getPositions()
     * @return Number/size of {@link LOSPickRequestPosition}
     */
    public int positionCount(){
        return positions.size();
    }

    /**
     * @return  {@link LOSStorageLocation} where the items should be brought to (often a gate)
     */
    @ManyToOne(optional=false)
    public LOSStorageLocation getDestination() {
        return destination;
    }

    public void setDestination(LOSStorageLocation destination) {
        this.destination = destination;
    }

    /**
     * A String that is expected to hold a valid {@link URL} after this {@link LOSOrderRequest} has {@link LOSOrderRequestState#FINISHED}.
     * If a pdf document can be retrieved from this url it will be printed during {@link LOSGoodsOutBusiness} process.
     * 
     * @return
     */
    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    /**
     * A String that is expected to hold a valid {@link URL} after this {@link LOSOrderRequest} has {@link LOSOrderRequestState#FINISHED}.
     * If a pdf document can be retrieved from this url it will be printed during {@link LOSGoodsOutBusiness} process.
     * 
     * @return
     */
    public String getLabelUrl() {
        return labelUrl;
    }

    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }
    
    /**
     * Adds a {@link LOSOrderRequestPosition} to the list of positions.
     * 
     * @param pos
     * @return
     */
    public int addPosition(LOSOrderRequestPosition pos){
        if (positions == null){
            this.positions = new ArrayList<LOSOrderRequestPosition>();
        }
        pos.setPositionIndex(positionCount());
        pos.setNumber(getNumber() + "_" + pos.getPositionIndex());
        positions.add(pos);
        return pos.getPositionIndex();
    }

    /**
     * The {@link OrderType} defines what kind of {@link LOSOrderRequest} it is. Depending
     * of the {@link OrderType} the order handling within {@link OrderBusiness} process is different,
     * e.g. whether documents have to be printed or not. 
     */
    @Enumerated(EnumType.STRING)
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}   
    
    
}
