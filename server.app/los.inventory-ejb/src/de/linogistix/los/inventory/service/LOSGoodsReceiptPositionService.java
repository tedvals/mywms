/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.StockUnit;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;

@Local
public interface LOSGoodsReceiptPositionService extends BasicService<LOSGoodsReceiptPosition> {

	public List<LOSGoodsReceiptPosition> getByStockUnit(StockUnit su);
	
	public List<LOSGoodsReceiptPosition> getByStockUnit(String stockUnitStr);

}
