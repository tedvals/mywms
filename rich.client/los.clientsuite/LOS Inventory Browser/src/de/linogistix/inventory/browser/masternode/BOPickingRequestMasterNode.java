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

import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.mywms.globals.PickingRequestState;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOPickingRequestMasterNode extends BOMasterNode {

    PickingRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickingRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (PickingRequestTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.state.equals(PickingRequestState.FINISHED.name())) {
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
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("client", String.class, to.client, CommonBundleResolver.class);
            sheet.put(client);
            BOMasterNodeProperty<String> parentRequest = new BOMasterNodeProperty<String>("parentRequest", String.class, to.parentRequest, CommonBundleResolver.class);
            sheet.put(parentRequest);
            BOMasterNodeProperty<String> orderRef = new BOMasterNodeProperty<String>("orderRef", String.class, to.orderRef, CommonBundleResolver.class);
            sheet.put(orderRef);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "PickingRequestState." + to.getState(), CommonBundleResolver.class, true);
            sheet.put(state);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("client", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> parentRequest = new BOMasterNodeProperty<String>("parentRequest", String.class,"", CommonBundleResolver.class);
            BOMasterNodeProperty<String> orderRef = new BOMasterNodeProperty<String>("orderRef", String.class,"", CommonBundleResolver.class);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        return new Property[]{client, parentRequest, orderRef, state};
    }
}
