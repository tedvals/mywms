/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.pick.query;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas
 *         Trautmann</a>
 */
@Remote
public interface LOSPickRequestQueryRemote extends
		BusinessObjectQueryRemote<LOSPickRequest> {

	/**
	 * Attention: elements of returned list may be lazily initialized
	 * 
	 * @param parentRequestNumber
	 * @return a list of {@link LOSPickRequest} having a parentRequest with given number
	 */
	public List<LOSPickRequest> queryByParentRequest(String parentRequestNumber);
	
	public List<BODTO<LOSPickRequest>> queryZombies(QueryDetail d) throws BusinessObjectQueryException;

	public List<LOSPickRequest> queryByOrderReference(String string);
	
	public LOSPickRequest queryByOrderReference(Client c, String string); 
	
}
