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

import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSGoodsOutRequestMasterNode extends BOMasterNode {

    LOSGoodsOutRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsOutRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsOutRequestTO) d;
    }

    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("client", String.class, to.getClient(), CommonBundleResolver.class);
            sheet.put(client);
            BOMasterNodeProperty<String> parentRequest = new BOMasterNodeProperty<String>("parentRequest", String.class, to.getParentRequest(), CommonBundleResolver.class);
            sheet.put(parentRequest);
            BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("outState", String.class, "LOSGoodsOutRequestState." + to.getOutState(), CommonBundleResolver.class, true);
            sheet.put(outState);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("client", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> parentRequest = new BOMasterNodeProperty<String>("parentRequest", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("outState", String.class, "", CommonBundleResolver.class);
        return new Property[]{client, parentRequest, outState};
        
    }
}
