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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BusinessException;
import org.mywms.model.ItemData;
import org.mywms.model.ItemMeasure;
import org.mywms.model.ItemUnit;
import org.mywms.model.Lot;
import org.mywms.model.Request;
import org.mywms.service.ConstraintViolatedException;

/**
 * A Request for customer orders.
 * 
 * @author trautm
 */
@Entity
@Table(name = "los_orderreqpos")
public class LOSOrderRequestPosition extends Request{

	private static final long serialVersionUID = 1L;

	private LOSOrderRequest parentRequest;
	
	private ItemData itemData;
    
    private BigDecimal amount;
    
    private Lot lot;
    
    private boolean partitionAllowed;

    private int positionIndex;
    
    private LOSOrderRequestPositionState positionState = LOSOrderRequestPositionState.RAW;

    private BigDecimal pickedAmount = new BigDecimal(0);
    
    @ManyToOne(optional=false)
    public ItemData getItemData() {
        return itemData;
    }
      
    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    @Column(precision=17, scale=4)
    public BigDecimal getAmount() {
        return amount.setScale(getItemData().getScale());
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @ManyToOne(optional=true)
    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public boolean isPartitionAllowed() {
        return partitionAllowed;
    }

    public void setPartitionAllowed(boolean partitionAllowed) {
        this.partitionAllowed = partitionAllowed;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

	public void setPositionState(LOSOrderRequestPositionState positionState) {
		this.positionState = positionState;
	}

	@Enumerated(EnumType.STRING)
	public LOSOrderRequestPositionState getPositionState() {
		return positionState;
	}

	public void setPickedAmount(BigDecimal pickedAmount) {
		this.pickedAmount = pickedAmount;
	}

	@Column(precision=17, scale=4)
	public BigDecimal getPickedAmount() {
		return pickedAmount.setScale(getItemData().getScale());
	}

	@ManyToOne(optional=false)
	public LOSOrderRequest getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(LOSOrderRequest parentRequest) {
		this.parentRequest = parentRequest;
		if (this.parentRequest != null){
			setParentRequestNumber(parentRequest.getNumber());
        }
	}
	
	/**
	 * Checks, if following constraints are kept during the previous operations.

	 * @throws ConstraintViolatedException 
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws BusinessException, ConstraintViolatedException {
	
		if (amount.scale() > getItemData().getScale()){
			amount = amount.setScale(getItemData().getScale());
		}
		
		if (pickedAmount.scale() > getItemData().getScale()){
			pickedAmount = pickedAmount.setScale(getItemData().getScale());
		}
		
	}
	
	/**
	 * A transient getter for the {@link ItemMeasure}  containing a value, a {@link ItemUnit} and a format String
	 * @return
	 */
	@Transient
	public ItemMeasure getDisplayAmount() {
		BigDecimal i = getAmount();
		i = i.setScale(getItemData().getScale());
		ItemMeasure m  = new ItemMeasure(
				i, 
				getItemData().getHandlingUnit()
				);
		
		return m;
		
	}

	/**
	 * A transient getter for the {@link ItemMeasure}  containing a value, a {@link ItemUnit} and a format String
	 * @return
	 */
	@Transient
	public ItemMeasure getDisplayPickedAmount() {
		BigDecimal i = getPickedAmount();
		i = i.setScale(getItemData().getScale());
		ItemMeasure m  = new ItemMeasure(
				i, 
				getItemData().getHandlingUnit()
				);
		
		return m;
		
	}
	
    
}
