/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.controller;

import java.util.ArrayList;
import java.util.List;

public class ManageMobileBean implements ManageMobile {
	
	
	public List<MobileFunction> getFunctions() {
		List<MobileFunction> functionList = new ArrayList<MobileFunction>();
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.info.InfoBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.goodsreceipt.GoodsReceiptBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.storage.StorageBackingBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.gr_direct.GRDirectBean", "MODE_GOODS_RECEIPT") );
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.gr_direct.GRDirectBean", "MODE_MATERIAL") );
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.ComboBoxBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.shipping.ShippingBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.supplies.ReplenishBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.stocktaking.StockTakingBean"));
		
		return functionList;
	}
	

	public int getMenuPageSize() {
		return 3;
	}
	
}
