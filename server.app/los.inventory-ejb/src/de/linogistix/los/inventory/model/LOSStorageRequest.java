/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.mywms.model.Request;

/**
 *
 * @author trautm
 */

@Entity
@Table(name="los_storagereq")
public class LOSStorageRequest extends Request{

	private static final long serialVersionUID = 1L;

	private LOSStorageLocation destination;
    
    private LOSStorageRequestState requestState;
    
    private LOSUnitLoad unitLoad;

    @ManyToOne
    public LOSStorageLocation getDestination() {
        return destination;
    }

    public void setDestination(LOSStorageLocation destination) {
        this.destination = destination;
    }

    @Enumerated(EnumType.STRING)
    public LOSStorageRequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(LOSStorageRequestState requestState) {
        this.requestState = requestState;
    }

    @ManyToOne
    public LOSUnitLoad getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(LOSUnitLoad unitLoad) {
        this.unitLoad = unitLoad;
    }

    @Override
    public String toUniqueString() {
        return super.toUniqueString();
    }
    
    
   
}
