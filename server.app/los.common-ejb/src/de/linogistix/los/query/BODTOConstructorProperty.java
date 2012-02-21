/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.io.Serializable;

public class BODTOConstructorProperty implements Serializable{

	private static final long serialVersionUID = 1L;

	private String propertyName;
	
	private boolean entityReference;
	
	private boolean nullableReference;

	public BODTOConstructorProperty(String propertyName, 
									boolean nullableReference ) 
	{
		this.nullableReference = nullableReference;
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isEntityReference() {
		return entityReference;
	}

	public boolean isNullableReference() {
		return nullableReference;
	}
}
