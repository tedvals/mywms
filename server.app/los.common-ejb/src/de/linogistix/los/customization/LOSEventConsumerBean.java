/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.customization;

import org.apache.log4j.Logger;

import de.linogistix.los.util.event.LOSEventConsumer;

public class LOSEventConsumerBean implements LOSEventConsumer{
    private static final Logger log = Logger.getLogger(LOSEventConsumerBean.class);

	public void processEvent(String eventName, Object param) {
        log.error("Just a dummy implementation");
	}

}
