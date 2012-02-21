/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;

public class UnitLoadTO extends BODTO<LOSUnitLoad> {

	private static final long serialVersionUID = 1L;
	
	public String storageLocation;
	
	public int lock;
	
	public UnitLoadTO(LOSUnitLoad ul){
		this(ul.getId(), ul.getVersion(), ul.getLabelId(), ul.getLock(), ul.getStorageLocation().getName());
	}
	
	public UnitLoadTO(Long id, int version, String name, int lock, String storageLocation){
		super(id, version, name);
		this.storageLocation = storageLocation;
		this.lock = lock;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
}
