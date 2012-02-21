/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.mywms.model.Area;

/**
 *
 * @author trautm
 */
@Entity
@Table(name="los_area")
public class LOSArea extends Area {
    
	private static final long serialVersionUID = 1L;
	
	private LOSAreaType areaType;
	
    @Enumerated(EnumType.STRING)
    public LOSAreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(LOSAreaType areaType) {
        this.areaType = areaType;
    }   
    
}
