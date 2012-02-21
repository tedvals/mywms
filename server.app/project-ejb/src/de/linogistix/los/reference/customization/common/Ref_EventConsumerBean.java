/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.common;

import javax.ejb.Stateless;

import de.linogistix.los.util.event.LOSEventConsumer;

@Stateless
public class Ref_EventConsumerBean implements LOSEventConsumer {

	public void processEvent(String eventName, Object param) throws Exception {
	}

}
