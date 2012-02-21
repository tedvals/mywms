/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.query.BODTO;

@Remote
public interface ManagePickRequests {
	
	String getNewPickRequestNumber();
	
	void finish(List<BODTO<LOSPickRequest>> list) throws FacadeException;
}
