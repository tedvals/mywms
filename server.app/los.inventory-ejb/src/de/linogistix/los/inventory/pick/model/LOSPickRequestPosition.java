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

package de.linogistix.los.inventory.pick.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicEntity;
import org.mywms.model.BusinessException;
import org.mywms.model.ItemData;
import org.mywms.model.ItemMeasure;
import org.mywms.model.ItemUnit;
import org.mywms.model.PickingDimensionType;
import org.mywms.model.PickingSupplyType;
import org.mywms.model.PickingWithdrawalType;
import org.mywms.model.StockUnit;
import org.mywms.model.SubstitutionType;
import org.mywms.model.UnitLoad;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_pickreqpos")
public class LOSPickRequestPosition extends BasicEntity{

	private static final long serialVersionUID = 1L;
  
	private LOSOrderRequestPosition parentRequest;
	
	private LOSPickRequest pickRequest;

	private LOSPickRequestPosition complementOn = null;
	
	private List<LOSPickRequestPosition> complements = new ArrayList<LOSPickRequestPosition>(); 
	
	private BigDecimal amount = new BigDecimal(0);

	private BigDecimal pickedAmount = new BigDecimal(0);

	private StockUnit stockUnit;

	private boolean canceled = false;

	private int index = 0;

	private PickingSupplyType pickingSupplyType = PickingSupplyType.STATIC_DECENTRALIZED;

	private PickingDimensionType pickingDimensionType = PickingDimensionType.ONE_DIMENSIONAL;

	private PickingWithdrawalType withdrawalType = PickingWithdrawalType.UNORDERED_FROM_STOCKUNIT;
	
	private Boolean solved = false;

	private SubstitutionType substitutionType = SubstitutionType.SUBSTITUTION_NOT_ALLOWED;

	/**
	 * Getter for property itemData.
	 * 
	 * @return Value of property itemData.
	 */
	@Transient
	public ItemData getItemData() {
		return stockUnit.getItemData();
	}

	/**
	 * Getter for property amount.
	 * 
	 * @return Value of property amount.
	 */
	@Column(nullable = false, precision = 17, scale = 4)
	public BigDecimal getAmount() {
		return amount.setScale(getItemData().getScale());
	}

	/**
	 * Setter for property amount.
	 * 
	 * @param amount
	 *            New value of property amount.
	 */
	public void setAmount(BigDecimal amount) {
		if (amount.compareTo(new BigDecimal(0)) <= 0) {
			throw new BusinessException("amount must be greater than 0 but is "
					+ amount);
		}
		if (amount.compareTo(pickedAmount) < 0) {
			throw new BusinessException(
					"currently more items are already picked than tried to: "
							+ pickedAmount + " of " + amount);
		}

		this.amount = amount;
	}

	/**
	 * Getter for property pickedAmount.
	 * 
	 * @return Value of property pickedAmount.
	 */
	@Column(nullable = false, precision = 17, scale = 4)
	public BigDecimal getPickedAmount() {
		return pickedAmount.setScale(getItemData().getScale());
	}

	/**
	 * Setter for property pickedAmount. This method is used to support
	 * hibernate. That is because it is private.
	 * 
	 * @param pickedAmount
	 *            New value of property pickedAmount.
	 */
	@SuppressWarnings("unused")
	private void setPickedAmount(BigDecimal pickedAmount) {
		this.pickedAmount = pickedAmount;
	}

	/**
	 * Setter for property pickedAmount.
	 * 
	 * @param pickedAmount
	 *            New value of property pickedAmount.
	 */
	public void setPicked(BigDecimal pickedAmount, StockUnit stockUnit) {
		if (pickedAmount.compareTo(new BigDecimal(0)) < 0) {
			throw new BusinessException(
					"picked amount must be equal to or greater than 0");
		}
		if (pickedAmount.compareTo(amount) > 0) {
			throw new BusinessException(
					"the amount must smaller than or equal to the amount");
		}

		if (stockUnit == null) {
			throw new BusinessException("a stock unit must be specified");
		}

		this.stockUnit = stockUnit;
		this.pickedAmount = pickedAmount;

	}

	/**
	 * Getter for property unitLoad.
	 * 
	 * @return Value of property unitLoad.
	 */
	@Transient
	public UnitLoad getUnitLoad() {
		return stockUnit.getUnitLoad();
	}

	/**
	 * Checks, if the position is solved. A position is solved, if the picked
	 * amount matches the amount to be picked. The method requires the amount to
	 * be greater than 0 to return true at all.
	 * 
	 * @return true, if the position is solved, false otherwise.
	 */
	// @Transient
	public boolean isSolved() {
		this.solved = amount.compareTo(new BigDecimal(0)) > 0
				&& pickedAmount.compareTo(amount) == 0;
		return solved;
	}

	void setSolved(Boolean solved) {
		if (solved != null) {
			this.solved = solved;
		} else {
			this.solved = false;
		}
	}

	/**
	 * Checks, if the position has been picked without regard of if the position
	 * is solved. A position is picked, if the setPicked() method has been
	 * called successfully.
	 * 
	 * @return true, if the position has been picked, false otherwise
	 */
	@Transient
	public boolean isPicked() {
		if (pickedAmount.compareTo(new BigDecimal(0)) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Getter for property stockUnit.
	 * 
	 * @return Value of property stockUnit.
	 */
	@ManyToOne(optional = false)
	public StockUnit getStockUnit() {
		return this.stockUnit;
	}

	/**
	 * Setter for property stockUnit.
	 * 
	 * @param stockUnit
	 *            New value of property stockUnit.
	 */
	/**
	 * @param stockUnit
	 */
	public void setStockUnit(StockUnit stockUnit) {
		// assertions
		// causes hibernate exception. Is not suitable for some processes
		// either.
		// " org.hibernate.PropertyAccessException: Exception occurred inside
		// setter of org.mywms.model.PickingRequestPosition.stockUnit"
		// when reinitializing entity
		/*
		 * if (this.stockUnit != null) { throw new BusinessException("the stock
		 * unit cannot be reset"); }
		 */
		// assignment
		this.stockUnit = stockUnit;
	}

	/**
	 * Getter for property canceled.
	 * 
	 * @return Value of property canceled.
	 */
	@Column(nullable = false)
	public boolean isCanceled() {

		return this.canceled;
	}

	/**
	 * Setter for property canceled.
	 * 
	 * @param canceled
	 *            New value of property canceled.
	 */
	public void setCanceled(boolean canceled) {
		// Don't set pickedAmount to null. That is business locgic and must be
		// handeled elsewhere
		// We want to be able to cancel the podition an keep the pickedAmount
		// (partial fulfillment)
		// if (canceled) {
		// pickedAmount = 0L;
		// }
		this.canceled = canceled;
	}

	/**
	 * @return the index
	 */
	@Column(name = "pos_index")
	public int getIndex() {
		return this.index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * The supply type of this picking request position. A combination of
	 * 
	 * <ul>
	 * <li> static or dynamic and
	 * <li> centralized or decentralized
	 * </ul>
	 * 
	 * @return
	 */
	@Enumerated(EnumType.STRING)
	public PickingSupplyType getPickingSupplyType() {
		return pickingSupplyType;
	}

	public void setPickingSupplyType(PickingSupplyType pickingSupplyType) {
		this.pickingSupplyType = pickingSupplyType;
	}

	/**
	 * Does the movement to fulfill this position require one-, two- or three
	 * dimensional movement?
	 * 
	 * @return
	 */
	@Enumerated(EnumType.STRING)
	public PickingDimensionType getPickingDimensionType() {
		return pickingDimensionType;
	}

	public void setPickingDimensionType(
			PickingDimensionType pickingDimensionType) {
		this.pickingDimensionType = pickingDimensionType;
	}

	/**
	 * How should goods be picked from the stockunit
	 * 
	 * @return
	 */
	@Enumerated(EnumType.STRING)
	public PickingWithdrawalType getWithdrawalType() {
		return withdrawalType;
	}

	public void setWithdrawalType(PickingWithdrawalType withdrawalType) {
		this.withdrawalType = withdrawalType;
	}

	// public void setParentRequest(Request parentRequest) {
	// this.parentRequest = parentRequest;
	// }
	//
	// @ManyToOne(optional=true)
	// public Request getParentRequest() {
	// return parentRequest;
	// }

	public void setSubstitutionType(SubstitutionType substitutionType) {
		this.substitutionType = substitutionType;
	}

	@Enumerated(EnumType.STRING)
	public SubstitutionType getSubstitutionType() {
		return substitutionType;
	}

	/**
	 * Checks, if following constraints are kept during the previous operations.
	 * 
	 * @throws ConstraintViolatedException
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws BusinessException,
			ConstraintViolatedException {

		if (amount.scale() > getItemData().getScale()) {
			amount = amount.setScale(getItemData().getScale());
		}

		if (pickedAmount.scale() > getItemData().getScale()) {
			pickedAmount = pickedAmount.setScale(getItemData().getScale());
		}

	}

	/**
	 * A transient getter for the {@link ItemMeasure} containing a value, a
	 * {@link ItemUnit} and a format String
	 * 
	 * @return
	 */
	@Transient
	public ItemMeasure getDisplayAmount() {
		BigDecimal i = getAmount();
		i = i.setScale(getItemData().getScale());
		ItemMeasure m = new ItemMeasure(i, getItemData().getHandlingUnit());

		return m;

	}

	/**
	 * A transient getter for the {@link ItemMeasure} containing a value, a
	 * {@link ItemUnit} and a format String
	 * 
	 * @return
	 */
	@Transient
	public ItemMeasure getDisplayPickedAmount() {
		BigDecimal i = getPickedAmount();
		i = i.setScale(getItemData().getScale());
		ItemMeasure m = new ItemMeasure(i, getItemData().getHandlingUnit());

		return m;

	}
	
	@ManyToOne(optional=false)
	public LOSPickRequest getPickRequest() {
		return pickRequest;
	}

	public void setPickRequest(LOSPickRequest pickRequest) {
		this.pickRequest = pickRequest;
	}
	
	@Transient
	public LOSStorageLocation getStorageLocation(){
		LOSUnitLoad ul = (LOSUnitLoad)getStockUnit().getUnitLoad();
		return ul.getStorageLocation();
	}

	@ManyToOne(optional=false)
	public LOSOrderRequestPosition getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(LOSOrderRequestPosition parentRequest) {
		this.parentRequest = parentRequest;
	}

	public void setComplementOn(LOSPickRequestPosition complementOn) {
		this.complementOn = complementOn;
	}

	/**
	 * The {@link LOSPickRequestPosition} this {@link LOSPickRequestPosition} complements, i.e. after unexpected null
	 * @return 
	 */
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	public LOSPickRequestPosition getComplementOn() {
		return complementOn;
	}

	public void setComplements(List<LOSPickRequestPosition> complements) {
		this.complements = complements;
	}
	
	/**
	 * Returns true if a serial no is missing although it is mandatory.
	 * @return
	 */
	@Transient
	public boolean missingMandatorySerialNo(){
		
		if (getStockUnit() != null){
			switch(getStockUnit().getItemData().getSerialNoRecordType()){
			 case ALWAYS_RECORD:
			 case GOODS_OUT_RECORD:
			 	return  getStockUnit().getSerialNumber() == null || getStockUnit().getSerialNumber().length() == 0;
			 default:
				 break;
			 }
		}
		
		return false;
		
	}

	@OneToMany(mappedBy="complementOn")
	public List<LOSPickRequestPosition> getComplements() {
		return complements;
	}
	
	@Override
	public String toDescriptiveString() {
		//load to prevent lazy init exception
		getComplementOn();
		getComplements();
		return super.toDescriptiveString();
	}

}
