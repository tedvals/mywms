/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.event;

import java.io.Serializable;

public class LOSEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String eventName;
	private Object parameter;

	public LOSEvent( String eventName, Object param ) {
		this.eventName = eventName;
		this.parameter = param;
	}
	
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

}
