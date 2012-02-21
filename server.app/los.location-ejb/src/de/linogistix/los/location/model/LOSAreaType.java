/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

/**
 *
 * @author trautm
 */
public enum LOSAreaType {
    /**
     * Anything but...
     */
	GENERIC,
	/**
	 * An Area for goods intake
	 */
    GOODS_IN,
    /**
     * An area for goods out
     */
    GOODS_OUT,
    /**
     * Both, goods intake and goods out
     */
    GOODS_IN_OUT,
    /**
     * Area for storing goods (e.g. a rack)
     */
    STORE,
    /**
     * Area for bringing goods for toll procedure
     */
    TOLL,
    /**
     * Area for bringing goods for quality assurance procedure
     */
    QUALITY_ASSURANCE,
    /**
     * Area that is used for replenishment, e.g. for a pick area
     */
    REPLENISHMENT,
    /**
     * Area for storing goods that should not be used yet 
     */
    QUARANTINE,
    /**
     * Area where workers pick from fixed assigned storage locations
     */
    PICKING,
    /**
     * 
     */
    TRANSPORTATION,
    /**
     * Area from where to supply to production
     */
    PRODUCTION;
}
