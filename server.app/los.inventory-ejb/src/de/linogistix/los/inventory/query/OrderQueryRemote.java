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

package de.linogistix.los.inventory.query;


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSOrder;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface OrderQueryRemote extends BusinessObjectQueryRemote<LOSOrder>{ 
  
	public List<BODTO<LOSOrder>> queryZombies(QueryDetail d) throws BusinessObjectQueryException;
	
	/**
	 * returns null if not found!
	 * @return
	 */
	public LOSOrder queryByRequestId(Client c, String requestId);
}
