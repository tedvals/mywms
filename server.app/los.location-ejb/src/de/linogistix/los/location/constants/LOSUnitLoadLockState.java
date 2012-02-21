/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.constants;

/**
 * From 100..199
 * @author trautm
 *
 */
public enum LOSUnitLoadLockState {
	
	NOT_LOCKED(0),
	GENERAL(1),
	GOING_TO_DELETE(2),
	STORAGE(100),
	RETRIEVAL(101),
	CLEARING(102);
	
	int lock;
	  
	LOSUnitLoadLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static LOSUnitLoadLockState resolve(int lock){
		switch (lock){
		case 0: return NOT_LOCKED;
		case 1: return GENERAL;
		case 2: return GOING_TO_DELETE;
		case 100: return STORAGE;
		case 101: return RETRIEVAL;
		case 102: return CLEARING;
		default: throw new IllegalArgumentException();
		}
	}
}
