/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;

public class LOSStorageRequestTO extends BODTO<LOSStorageRequest> {

	private static final long serialVersionUID = 1L;

    private String unitLoadLabel;
    private String requestState;
    private String destinationName;


	public LOSStorageRequestTO() {
	}

	public LOSStorageRequestTO(Long id, int version, String name) {
		super(id, version, name);
	}
	
	public LOSStorageRequestTO(Long id, int version, String name, 
			String unitLoadLabel, LOSStorageRequestState requestState, LOSStorageLocation destination) {
		super(id, version, name);
		this.unitLoadLabel = unitLoadLabel;
		this.requestState = requestState.name();
		this.destinationName = destination == null ? "" : destination.getName();
	}

	
	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getRequestState() {
		return requestState;
	}

	public void setRequestState(String requestState) {
		this.requestState = requestState;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

}
