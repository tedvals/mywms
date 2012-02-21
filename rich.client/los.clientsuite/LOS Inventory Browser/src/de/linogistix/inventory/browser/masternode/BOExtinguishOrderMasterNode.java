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
import de.linogistix.los.inventory.query.dto.ExtinguishOrderTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.util.GregorianCalendar;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOExtinguishOrderMasterNode extends BOMasterNode {

    ExtinguishOrderTO to;

    /** Creates a new instance of BODeviceNode */
    public BOExtinguishOrderMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (ExtinguishOrderTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        GregorianCalendar today = new GregorianCalendar();
        if (to.getOrderState().equals(LOSOrderRequestState.FAILED)) {
            ret = "<font color=\"#C0C0C0\"><s>" + ret + "</s></font>";
        } 
        return ret;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> itemDataNumber = new BOMasterNodeProperty<String>("itemDataNumber", String.class, to.getItemDataNumber(), InventoryBundleResolver.class);
            sheet.put(itemDataNumber);
            BOMasterNodeProperty<String> lotName = new BOMasterNodeProperty<String>("lotName", String.class, to.getLotName(), InventoryBundleResolver.class);
            sheet.put(lotName);
            BOMasterNodeProperty<String> orderState = new BOMasterNodeProperty<String>("orderState", String.class, "LOSOrderRequestState." + to.getOrderState(), CommonBundleResolver.class, true);
            sheet.put(orderState);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemDataNumber = new BOMasterNodeProperty<String>("itemDataNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> lotName = new BOMasterNodeProperty<String>("lotName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> orderState = new BOMasterNodeProperty<String>("orderState", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, itemDataNumber, lotName, orderState
        };

        return props;
    }
}
