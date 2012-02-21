/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.customization;

import org.mywms.model.Client;
import org.mywms.model.ItemData;

import de.linogistix.los.stocktaking.exception.LOSStockTakingException;


public class LOSManageStocktakingServiceBean implements LOSManageStocktakingService {

	public String checkLotNo( Client client, ItemData itemData, String lotNo )  throws LOSStockTakingException {
		return lotNo;
	}
	
}
