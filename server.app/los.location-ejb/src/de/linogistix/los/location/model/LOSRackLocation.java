/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.location.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_racklocation")
public class LOSRackLocation extends LOSStorageLocation{
	
	private static final long serialVersionUID = 1L;

    private LOSRack rack;
    
    private int xPos;
    
    private int yPos;
    
    private int zPos;

    @ManyToOne(optional=false)
    public LOSRack getRack() {
        return rack;
    }

    public void setRack(LOSRack rack) {
        this.rack = rack;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

	public int getZPos() {
		return zPos;
	}

	public void setZPos(int pos) {
		zPos = pos;
	}
}
