/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.model.LOSSystemProperty;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

@Stateless
@Deprecated
public class LOSSystemPropertyFacadeBean implements LOSSystemPropertyFacade  {
	Logger log = Logger.getLogger(LOSSystemPropertyFacadeBean.class);

	@EJB
	private LOSSystemPropertyService propertyService; 
	
	public String getString( String key ) {
		return propertyService.getString(key);
	}
	
	
	public String getStringDefault( String key, String defaultValue ) {
		return propertyService.getStringDefault(key, defaultValue);
	}


	public void setString( String key, String value ) {
		if( value == null ) {
			value = "";
		}
		
		LOSSystemProperty prop = propertyService.getByKey(key);
		if( prop == null ) {
			prop = propertyService.createSystemProperty(key, value);
		}
		else {
			prop.setValue(value);
		}
	}

	public boolean getBoolean( String key ) {
		return propertyService.getBoolean(key);
	}
	
	
	public boolean getBooleanDefault( String key, boolean defaultValue ) {
		return propertyService.getBooleanDefault(key, defaultValue);
	}
	

	public void setBoolean( String key, boolean value ) {
		setString(key, String.valueOf(value));
	}


	public long getLong(String key) {
		return propertyService.getLong(key);
	}


	public long getLongDefault(String key, long defaultValue) {
		return propertyService.getLongDefault(key, defaultValue);
	}


	public void setLong(String key, long value) {
		try {
			propertyService.setValue(key, value);
		}
		catch( UnAuthorizedException e ) {
			log.error("No Permission", e);
		}
	}

}
