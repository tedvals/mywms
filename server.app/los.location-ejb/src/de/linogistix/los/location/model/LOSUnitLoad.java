/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_unitload")
public class LOSUnitLoad extends UnitLoad{
	
	private static final long serialVersionUID = 1L;

    private LOSStorageLocation storageLocation;
    
    private List<StockUnit> stockUnitList = new ArrayList<StockUnit>();

    private LOSUnitLoadPackageType packageType = LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE;
    
    private Date stockTakingDate;
    
    private BigDecimal weightGross;
    private BigDecimal weightNet;

	@ManyToOne(optional=false)
    public LOSStorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(LOSStorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    @OneToMany(mappedBy="unitLoad")
    public List<StockUnit> getStockUnitList() {
		return stockUnitList;
	}

	public void setStockUnitList(List<StockUnit> stockUnitList) {
		this.stockUnitList = stockUnitList;
	}

	public void setPackageType(LOSUnitLoadPackageType packageType) {
		this.packageType = packageType;
	}

	@Enumerated(EnumType.STRING)
	public LOSUnitLoadPackageType getPackageType() {
		return packageType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}
	
	@Column(precision=16, scale=3)
	public BigDecimal getWeightGross() {
		return weightGross;
	}
	public void setWeightGross(BigDecimal weightGross) {
		this.weightGross = weightGross;
	}

	@Column(precision=16, scale=3)
	public BigDecimal getWeightNet() {
		return weightNet;
	}
	public void setWeightNet(BigDecimal weightNet) {
		this.weightNet = weightNet;
	}

	@Override
	public String toShortString() {
    	return super.toShortString() + "[labelId=" + getLabelId() + "][locationName=" + (storageLocation == null ? "" : storageLocation.getName()) + "]";
	}
}
