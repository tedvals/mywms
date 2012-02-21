/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.event;

import javax.ejb.Local;
import javax.jms.JMSException;


@Local
public interface LOSEventSender {


	public void sendMsg( LOSEvent event ) throws JMSException;
}
