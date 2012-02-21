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

import de.linogistix.los.inventory.pick.query.dto.PickingRequestPositionTO;
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
public class BOPickingRequestPositionMasterNode extends BOMasterNode {

    PickingRequestPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickingRequestPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (PickingRequestPositionTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.solved) {
             ret = "<font color=\"#C0C0C0\">" + ret + "</font>";
        } else if (to.canceled) {
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
            BOMasterNodeProperty<String> pickRequest = new BOMasterNodeProperty<String>("pickRequest", String.class, to.pickRequest, CommonBundleResolver.class);
            sheet.put(pickRequest);
            BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemData", String.class, to.itemData, CommonBundleResolver.class);
            sheet.put(itemNumber);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.itemDataName, InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.amount, CommonBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<Boolean> solved = new BOMasterNodeProperty<Boolean>("solved", Boolean.class, to.solved, CommonBundleResolver.class);
            sheet.put(solved);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

            BOMasterNodeProperty<String> pickRequest = new BOMasterNodeProperty<String>("pickRequest", String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemData", String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);

            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);
            
            BOMasterNodeProperty<Boolean> solved = new BOMasterNodeProperty<Boolean>("solved", Boolean.class, Boolean.FALSE, CommonBundleResolver.class);
            
        return new Property[]{pickRequest, itemNumber, itemDataName, amount, solved};
    }
}
