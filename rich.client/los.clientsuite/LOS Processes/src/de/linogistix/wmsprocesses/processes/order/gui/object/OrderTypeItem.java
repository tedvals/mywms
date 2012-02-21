/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.object;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.los.inventory.model.OrderType;

/**
 *
 * @author trautm
 */
public class OrderTypeItem {

    public OrderType t;

    public OrderTypeItem(OrderType t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return BundleResolve.resolve(new Class[]{CommonBundleResolver.class}, "OrderType." + t.toString(), new Object[0]);
    }
}
