/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.inventory.res.InventoryBundleResolver;

import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.dto.LOSOrderRequestPositionTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSOrderRequestPositionMasterNode extends BOMasterNode {

    LOSOrderRequestPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSOrderRequestPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSOrderRequestPositionTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.getPositionState().equals(LOSOrderRequestState.FINISHED)) {
             ret = "<font color=\"#C0C0C0\">" + ret + "</font>";
        }
        return ret;
    }
    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData", String.class, to.itemData, CommonBundleResolver.class);
            sheet.put(item);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.itemDataName, InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, to.lot, CommonBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.amount, CommonBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<String> positionState = new BOMasterNodeProperty<String>("positionState", String.class, "LOSOrderRequestPositionState." + to.getPositionState(), CommonBundleResolver.class, true);
            sheet.put(positionState);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);
            BOMasterNodeProperty<String> positionState = new BOMasterNodeProperty<String>("positionState", String.class, "", CommonBundleResolver.class);
            
        return new Property[]{item, itemDataName, lot, amount, positionState};
    }
}
