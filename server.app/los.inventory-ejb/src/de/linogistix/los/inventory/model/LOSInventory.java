/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.ItemData;

@Entity
@Table(name="los_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "lotRef","client_id"
    })
})
public class LOSInventory extends BasicClientAssignedEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * References {@link ItemData#getNumber()}
	 */
	String itemDataRef;
	
	/**
	 * References {@link Batch#getName()}
	 */
	private String lotRef;
	
	/**
	 * Number of pieces that are reserved
	 */
	BigDecimal reserved;
	
	/**
	 * Number of pieces that are available
	 */
	BigDecimal available;
	
	/**
	 * Number of pieces that are locked
	 */
	BigDecimal locked;
	
	/**
	 * Number of pieces that are advised
	 */
	BigDecimal advised;
	
	/**
	 * Number of pieces in stock.
	 * 
	 * <code>inStock = available + locked + reserved</code>
	 */
	BigDecimal inStock;
	
	/*
	 * Date of the last incoming goods movement for this article.
	 */
	public Date lastIncoming;
	
	/**
	 * Amount of last incoming goods movement for this article.
	 */
	public BigDecimal lastAmount;
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("InventoryListItem: ");
		
		ret.append("[clientRef=");
		ret.append(getClient().getNumber());
		ret.append("] ");
		ret.append("[articleRef=");
		ret.append(itemDataRef);
		ret.append("] ");
		ret.append("[batchRef=");
		ret.append(getLotRef());
		ret.append("] ");
		
		ret.append("[inStock=");
		ret.append(inStock);
		ret.append("] ");
		ret.append("[available=");
		ret.append(available);
		ret.append("] ");
		ret.append("[reserved=");
		ret.append(reserved);
		ret.append("] ");
		ret.append("[advised=");
		ret.append(advised);
		ret.append("] ");
		ret.append("[locked=");
		ret.append(locked);
		ret.append("] ");
		
		return new String(ret);
		
	}

	
	public String getItemDataRef() {
		return itemDataRef;
	}

	public void setItemDataRef(String itemData) {
		this.itemDataRef = itemData;
	}

	@Column(precision=10, scale=4)
	public BigDecimal getReserved() {
		return reserved;
	}

	public void setReserved(BigDecimal reserved) {
		if (reserved == null){
			this.reserved = new BigDecimal(0);
		} else if (reserved.compareTo(new BigDecimal(0)) < 0 ){
			throw new IllegalArgumentException();
		} else{
			this.reserved = reserved;
		}
	}
	
	public BigDecimal addReserved(BigDecimal reserved){
		return (this.reserved = this.reserved.add(reserved));
	}

	@Column(precision=10, scale=4)
	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		if (available == null){
			this.available = new BigDecimal(0);
		} else if (available.compareTo(new BigDecimal(0)) < 0 ){
			throw new IllegalArgumentException();
		} else{
			this.available = available;
		}
	}
	
	public BigDecimal addAvailable(BigDecimal available){
		return (this.available = this.available.add(available));
	}
	

	@Column(precision=10, scale=4)
	public BigDecimal getLocked() {
		return locked;
	}

	public void setLocked(BigDecimal locked) {
		
		if (locked == null){
			this.locked = new BigDecimal(0);
		} else if (locked.compareTo(new BigDecimal(0)) < 0 ){
			throw new IllegalArgumentException();
		} else{
			this.locked = locked;
		}
	}
	
	public BigDecimal addLocked(BigDecimal locked){
		return (this.locked = this.locked.add(locked));
	}

	@Column(precision=10, scale=4)
	public BigDecimal getAdvised() {
		return advised;
	}

	public void setAdvised(BigDecimal advised) {
		if (advised == null){
			this.advised = new BigDecimal(0);
		} else if (advised.compareTo(new BigDecimal(0)) < 0 ){
			throw new IllegalArgumentException();
		} else{
			this.advised = advised;
		}
	}
	
	public BigDecimal addAdvised(BigDecimal advised){
		return (this.advised = this.advised.add(advised));
	}

	@Column(precision=10, scale=4)
	public BigDecimal getInStock() {
		return inStock;
	}

	public void setInStock(BigDecimal inStock) {
		if (inStock == null){
			this.inStock = new BigDecimal(0);
		} else if (inStock.compareTo(new BigDecimal(0)) < 0 ){
			throw new IllegalArgumentException();
		} else{
			this.inStock = inStock;
		}
	}
	
	public BigDecimal addInStock(BigDecimal inStock){
		return (this.inStock = this.inStock.add(inStock));
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastIncoming() {
		return lastIncoming;
	}

	public void setLastIncoming(Date lastIncoming) {
		this.lastIncoming = lastIncoming;
	}

	@Column(precision=10, scale=4)
	public BigDecimal getLastAmount() {
		return lastAmount;
	}

	public void setLastAmount(BigDecimal lastAmount) {
		this.lastAmount = lastAmount;
	}
	
    /**
     * References {@link Batch#getName()}
     */
	public String getLotRef() {
        return lotRef;
    }

    public void setLotRef(String lotRef) {
        this.lotRef = lotRef;
    }



}
