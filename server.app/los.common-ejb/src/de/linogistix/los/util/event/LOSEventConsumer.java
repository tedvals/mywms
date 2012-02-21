/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.event;

import javax.ejb.Local;

@Local
public interface LOSEventConsumer {

	public void processEvent( String eventName, Object param ) throws Exception;
	
}
