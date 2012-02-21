/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.model;

public class LOSPickPropertyKey {

	/**
	 * Check empty location only after a number of days.
	 * < 0: Empty locations are not checked
	 * = 0: Empty locations are checked every time
	 * > 0: Empty locations are checked after this number of days
	 */
	public static final String PICK_LOCATION_EMPTY_CHECK_DAYS = "PICK_LOCATION_EMPTY_CHECK_DAYS";


}
