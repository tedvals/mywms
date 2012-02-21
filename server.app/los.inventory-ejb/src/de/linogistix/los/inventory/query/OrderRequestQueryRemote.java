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

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface OrderRequestQueryRemote extends BusinessObjectQueryRemote<LOSOrderRequest>{ 
  
	public List<BODTO<LOSOrderRequest>> queryZombies(QueryDetail d) throws BusinessObjectQueryException;
	
	/**
	 * returns null if not found!
	 * @return
	 */
	public LOSOrderRequest queryByRequestId(Client c, String requestId);
	
	public LOSResultList<BODTO<LOSOrderRequest>> autoCompletionByState(String typed,
																	   BODTO<Client> clientTO,
																	   LOSOrderRequestState orderState, 
																	   QueryDetail detail); 
}
