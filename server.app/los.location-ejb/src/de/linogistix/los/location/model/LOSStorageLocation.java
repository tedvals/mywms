/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.Zone;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_storloc")
@Inheritance(strategy=InheritanceType.JOINED)
public class LOSStorageLocation extends BasicClientAssignedEntity{

	private static final long serialVersionUID = 1L;

	private String name;
    
    private LOSStorageLocationType type;

    private List<LOSUnitLoad> unitLoads = new ArrayList<LOSUnitLoad>();
    
    private LOSTypeCapacityConstraint currentTypeCapacityConstraint;
    
    private int reservedCapacity = 0;
    
    private LOSArea area;

    private LOSLocationCluster cluster;
    
    private Date stockTakingDate;
    
    private Zone zone = null;
    
    @Column(nullable=false, unique=true)
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional=false)
    public LOSStorageLocationType getType() {
        return type;
    }

    public void setType(LOSStorageLocationType type) {
        this.type = type;
    }
    
    @OneToMany(mappedBy="storageLocation")
    public List<LOSUnitLoad> getUnitLoads() {
        return unitLoads;
    }

    public void setUnitLoads(List<LOSUnitLoad> unitLoads) {
        this.unitLoads = unitLoads;
    }
    
    public int getReservedCapacity() {
		return reservedCapacity;
	}

	public void setReservedCapacity(int reservedCapacity) {
		this.reservedCapacity = reservedCapacity;
	}

	@ManyToOne
	@JoinColumn(name="currentTCC")
	public LOSTypeCapacityConstraint getCurrentTypeCapacityConstraint() {
		return currentTypeCapacityConstraint;
	}

	public void setCurrentTypeCapacityConstraint(
			LOSTypeCapacityConstraint currentTypeCapacityConstraint) {
		this.currentTypeCapacityConstraint = currentTypeCapacityConstraint;
	}

    @ManyToOne
	public LOSArea getArea() {
		return area;
	}

	public void setArea(LOSArea area) {
		this.area = area;
	}

    @Override
    public String toUniqueString() {
        return getName();
    }

    @ManyToOne(optional=true)
	public LOSLocationCluster getCluster() {
		return cluster;
	}

	public void setCluster(LOSLocationCluster cluster) {
		this.cluster = cluster;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}

    @ManyToOne
	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
    
	@Override
	public String toShortString() {
		return super.toShortString() + "[name=" + name + "]";
	}

    
}
