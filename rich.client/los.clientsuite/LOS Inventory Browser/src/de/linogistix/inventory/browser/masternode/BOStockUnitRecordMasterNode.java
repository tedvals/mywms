/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSStockUnitRecordTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOStockUnitRecordMasterNode extends BOMasterNode {

    LOSStockUnitRecordTO recordTo;

    /** Creates a new instance of BODeviceNode */
    public BOStockUnitRecordMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        recordTo = (LOSStockUnitRecordTO) d;
  
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        return ret;
    }

    @Override
    public String getDisplayName() {
        return recordTo.getName();
    }

    
    //    @Override
//    public Image getIcon(int arg0) {
//        if (to.lock > 0){
//            ImageIcon img = new ImageIcon(super.getIcon(arg0));
//            LockedIcon decorated = new LockedIcon(img);
//            return decorated.getImage();
//        } else {
//            return super.getIcon(arg0);
//        }
//    }
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "LOSStockUnitRecordType."+recordTo.type, InventoryBundleResolver.class, true);
            sheet.put(type);
            BOMasterNodeProperty<String> activityCode = new BOMasterNodeProperty<String>("activityCode", String.class, recordTo.activityCode, InventoryBundleResolver.class);
            sheet.put(activityCode);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, recordTo.amount, InventoryBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<BigDecimal> amountStock = new BOMasterNodeProperty<BigDecimal>("amountStock", BigDecimal.class, recordTo.amountStock, InventoryBundleResolver.class);
            sheet.put(amountStock);
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, recordTo.itemData, InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, recordTo.lot, InventoryBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<String> toSl = new BOMasterNodeProperty<String>("toSl", String.class, recordTo.toSl, InventoryBundleResolver.class);
            sheet.put(toSl);
            BOMasterNodeProperty<String> toUl = new BOMasterNodeProperty<String>("toUl", String.class, recordTo.toUl, InventoryBundleResolver.class);
            sheet.put(toUl);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "", InventoryBundleResolver.class, true);
        BOMasterNodeProperty<String> activityCode = new BOMasterNodeProperty<String>("activityCode", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amountStock = new BOMasterNodeProperty<BigDecimal>("amountStock", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
         
        BOMasterNodeProperty<String> toSl = new BOMasterNodeProperty<String>("toSl", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> toUl = new BOMasterNodeProperty<String>("toUl", String.class, "", InventoryBundleResolver.class);

        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            type,
            activityCode, 
            itemData,
            lot, 
            amount, amountStock,
            toSl, toUl
        };

        return props;
    }
}
