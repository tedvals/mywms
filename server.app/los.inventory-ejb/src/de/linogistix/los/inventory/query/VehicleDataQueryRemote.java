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

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

@Remote
public interface VehicleDataQueryRemote extends BusinessObjectQueryRemote<VehicleData>{ 
  
    public LOSResultList<BODTO<ItemData>> autoCompletionClientAndLot(String exp, 
	BODTO<Client> client, 
	BODTO<Lot> lot, 
	QueryDetail detail);

}
