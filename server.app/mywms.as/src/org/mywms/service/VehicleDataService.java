/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

//import java.util.List;

import javax.ejb.Local;

//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.Zone;

@Local
public interface VehicleDataService
    extends BasicService<VehicleData>
{
    VehicleData create();


/*    /**
*     * Searches for ItemDatas that have a name containing the specified
*     * fragment. If specified client is the system client all matching
*     * ItemDatas will be returned, otherwise the list will be limited to
*     * the ItemDatas which belong to Client client.
*     * 
*     * @param client the Client ItemDatas should be assigned to or the
*     *            system client.
*     * @param fragment a substring that names of ItemDatas should
*     *            contain. Case insensitiv.
*     * @return list of ItemDatas, which may be empty. The list will be
*     *         ordered by names ascending.
*     *
*    List<ItemData> getListByNameFragment(Client client, String fragment);
*
*    /**
*     * Returns the entity with the specified item number
*     * 
*     * @param client the client owning the item data entity
*     * @param itemNumber the item number to find
*     * @return the found entity or NULL if there is no matching item
*     *
*    ItemData getByItemNumber(Client client, String itemNumber);
*
*    /**
*     * Returns a list of item data entities, of which the safety stock
*     * is underflown.
*     * 
*     * @param client the owning client
*     * @return list of item datas
*     *
*    List<ItemData> getListSafetyStockUnderflow(Client client);
*/
}
