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

import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptPositionTO;
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
public class BOLOSGoodsReceiptPositionMasterNode extends BOMasterNode {

    LOSGoodsReceiptPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsReceiptPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsReceiptPositionTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, to.orderReference, CommonBundleResolver.class);
            sheet.put(orderReference);
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData",String.class, to.itemData, CommonBundleResolver.class);
            sheet.put(item);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, to.lot, CommonBundleResolver.class, true);
            sheet.put(lot);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.amount, CommonBundleResolver.class);
            sheet.put(amount);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData",String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", CommonBundleResolver.class, true);
            
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);
            
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            orderReference, item, lot, amount
        };

        return props;
    }
}
