/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import de.linogistix.los.location.model.LOSStorageLocation;

@Entity
@Table(name="los_replenishreq")
public class LOSReplenishRequest extends LOSOrderRequest {

	private static final long serialVersionUID = 1L;

	private List<LOSStorageLocation> allowedSources = new ArrayList<LOSStorageLocation>();

	@ManyToMany
	public List<LOSStorageLocation> getAllowedSources() {
		return allowedSources;
	}

	public void setAllowedSources(List<LOSStorageLocation> allowedSources) {
		this.allowedSources = allowedSources;
	}
	
	
}
