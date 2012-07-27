/*
 * StorageLocationType.java
 *
 * Created on November 19, 2007, 10:52 AM
 *
 * Copyright (c) 2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 */

package de.linogistix.los.location.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

/**
 * @author Jordan
 */
@Entity
@Table(name="los_storagelocationtype",
       uniqueConstraints={@UniqueConstraint(columnNames={"sltname"})})
@Inheritance(strategy=InheritanceType.JOINED)
public class LOSStorageLocationType extends BasicEntity{
    
	private static final long serialVersionUID = 1L;
	
    private String name;
    private String type;
    
    private List<LOSTypeCapacityConstraint> typeCapacityConstraints;

    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private BigDecimal volume;
    private BigDecimal lowVolume;
    private BigDecimal minimumVolume;
    private BigDecimal liftingCapacity;

    public LOSStorageLocationType(){
        typeCapacityConstraints = new ArrayList<LOSTypeCapacityConstraint>();
    }
    
    @Column(name="sltname", nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @OneToMany(mappedBy="storageLocationType")
    public List<LOSTypeCapacityConstraint> getTypeCapacityConstraints() {
        return typeCapacityConstraints;
    }

    public void setTypeCapacityConstraints(List<LOSTypeCapacityConstraint> typeCapacityConstraints) {
        this.typeCapacityConstraints = typeCapacityConstraints;
    }
    
    public void addTypeCapacityConstraint(LOSTypeCapacityConstraint constr){
    	typeCapacityConstraints.add(constr);
    }
    
    public void removeTypeCapacityConstraint(LOSTypeCapacityConstraint constr){
    	typeCapacityConstraints.remove(constr.getUnitLoadType());
    }
    
    
    @Column(nullable = true, precision=15, scale=2)
    public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}

    @Column(nullable = true, precision=15, scale=2)
	public BigDecimal getWidth() {
		return width;
	}
	public void setWidth(BigDecimal width) {
		this.width = width;
	}

    @Column(nullable = true, precision=15, scale=2)
	public BigDecimal getDepth() {
		return depth;
	}
	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

    @Column(nullable = true, precision=18, scale=6)
    public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

    @Column(nullable = true, precision=18, scale=6)
    public BigDecimal getLowVolume() {
		return lowVolume;
	}
	public void setLowVolume(BigDecimal lowVolume) {
		this.lowVolume = lowVolume;
	}

    @Column(nullable = true, precision=18, scale=6)
    public BigDecimal getMinimumVolume() {
		return minimumVolume;
	}
	public void setMinimumVolume(BigDecimal minimumVolume) {
		this.minimumVolume = minimumVolume;
	}

	@Column(nullable = true, precision=16, scale=3)
	public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}
	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}

	@Override
    public String toUniqueString() {
    	return getName();
    }
    
}
