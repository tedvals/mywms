/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;
import org.mywms.model.UnitLoadType;

/**
 * A LOSStorageLocationType may be restricted to certain UnitLoadTypes.
 * If a LOSStorageLocationType has no LOSTypeCapacityConstraint, 
 * it can store every kind of UnitLoad with an unbounded capacity.
 * If a LOSStorageLocationType has one LOSTypeCapacityConstraint with UnitLoadType set to null,
 * it can store every kind of UnitLoad up to the set capacity.
 * If a LOSStorageLocationType has several LOSTypeCapacityConstraints with UnitLoadType and capacity set,
 * it can store UnitLoadTypea as declared through the LOSTypeCapacityConstraints.
 *
 * @author Jordan
 */
@Entity
@Table(name="los_typecapacityconstraint",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"storagelocationtype_id","unitloadtype_id"})
       }
)
@Inheritance(strategy=InheritanceType.JOINED)
public class LOSTypeCapacityConstraint extends BasicEntity{
	
	private static final long serialVersionUID = 1L;

    private LOSStorageLocationType storageLocationType;
    
    private UnitLoadType unitLoadType = null;
    
    private int capacity = 1;

    private String name;
    
    @ManyToOne(optional=false)
    public LOSStorageLocationType getStorageLocationType() {
        return storageLocationType;
    }

    public void setStorageLocationType(LOSStorageLocationType storageLocationType) {
        this.storageLocationType = storageLocationType;
    }

    @ManyToOne
    public UnitLoadType getUnitLoadType() {
        return unitLoadType;
    }

    public void setUnitLoadType(UnitLoadType unitLoadType) {
        this.unitLoadType = unitLoadType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Column(nullable=false, unique=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toUniqueString() {
        return getName();
    }

    
    
    
    
}
