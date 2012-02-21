/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.location.model.LOSRackLocation;

@Stateless
public class LOSRackLocationServiceBean extends
		BasicServiceBean<LOSRackLocation> implements LOSRackLocationService {

}
