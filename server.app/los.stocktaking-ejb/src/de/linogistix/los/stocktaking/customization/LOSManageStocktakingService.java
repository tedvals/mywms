/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.customization;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.ItemData;

import de.linogistix.los.stocktaking.exception.LOSStockTakingException;

@Local
public interface LOSManageStocktakingService {

	public String checkLotNo( Client client, ItemData itemData, String lotNo ) throws LOSStockTakingException;
	
}
