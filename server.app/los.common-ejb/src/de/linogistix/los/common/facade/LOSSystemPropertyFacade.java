/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.Remote;

/**
 * use {@link LOSSystemPropertyService}
 * @author krane
 *
 */
@Remote
@Deprecated
public interface LOSSystemPropertyFacade  {
	
	
	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with an empty value.
	 * 
	 * @param key
	 * @return
	 */
	public String getString( String key );
	
	
	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with the default-value.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getStringDefault( String key, String defaultValue );

	
	/**
	 * Writing a system-property.
	 * If the requested property does not exists, it will be generated'.
	 *  
	 * @param key
	 * @param value
	 */
	public void setString( String key, String value );

	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with the default-value.
	 * The default-value is FALSE.
	 * The value TRUE will be returned for property-values of '1', 'true' and 'yes'. Everything else is FALSE.
	 *  
	 * @param key
	 * @return
	 */
	public boolean getBoolean( String key );
	
	
	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with the default-value.
	 * The value TRUE will be returned for property-values of '1', 'true' and 'yes'. Everything else is FALSE.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanDefault( String key, boolean defaultValue );
	
	/**
	 * Writing a system-property.
	 * If the requested property does not exists, it will be generated'.
	 *  
	 * @param key
	 * @param value
	 */
	public void setBoolean( String key, boolean value );
	
	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with 0.
	 *  
	 * @param key
	 * @return
	 */
	public long getLong( String key );
	
	/**
	 * Reading a system-property.
	 * If the requested property does not exists, it will be generated with the default-value.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLongDefault( String key, long defaultValue );

	/**
	 * Writing a system-property.
	 * If the requested property does not exists, it will be generated.
	 * 
	 * @param key
	 * @param value
	 */
	public void setLong( String key, long value );
}
