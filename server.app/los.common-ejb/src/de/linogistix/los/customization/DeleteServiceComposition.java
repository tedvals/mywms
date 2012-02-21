/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.customization;

import javax.ejb.Local;

import org.mywms.model.BasicEntity;

@Local
public interface DeleteServiceComposition {

	public void deleteEntity(BasicEntity e);
}
