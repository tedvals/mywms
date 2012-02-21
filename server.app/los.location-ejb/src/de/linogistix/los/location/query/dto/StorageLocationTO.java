/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;

public class StorageLocationTO  extends BODTO<LOSStorageLocation>{

	private static final long serialVersionUID = 1L;

	public int reserved;
	public int lock;
	
	public StorageLocationTO(LOSStorageLocation sl){
		this(sl.getId(), sl.getVersion(), sl.getName(), sl.getLock(), sl.getReservedCapacity());
	}
	
	public StorageLocationTO(Long id, int version, String name, int lock, int reserved){
		super(id, version, name);
		this.reserved = reserved;
		this.lock = lock;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
	
}
