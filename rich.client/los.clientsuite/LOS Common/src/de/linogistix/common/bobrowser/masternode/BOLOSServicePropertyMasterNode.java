/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.dto.LOSServicePropertyTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSServicePropertyMasterNode extends BOMasterNode {

    LOSServicePropertyTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSServicePropertyMasterNode(BODTO d) throws IntrospectionException {
        super(d);
        to = (LOSServicePropertyTO) d;
    }

    /** Creates a new instance of BODeviceNode */
    public BOLOSServicePropertyMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSServicePropertyTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = "";
        return ret;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {

            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> client;
            client = new BOMasterNodeProperty<String>("client", String.class, to.getClient(), CommonBundleResolver.class);
            sheet.put(client);

            BOMasterNodeProperty<String> service;
            service = new BOMasterNodeProperty<String>("service", String.class, to.getService(), CommonBundleResolver.class);
            sheet.put(service);

            BOMasterNodeProperty<String> key;
            key = new BOMasterNodeProperty<String>("key", String.class, to.getKey(), CommonBundleResolver.class);
            sheet.put(key);

            BOMasterNodeProperty<String> subkey;
            subkey = new BOMasterNodeProperty<String>("subkey", String.class, to.getSubkey(), CommonBundleResolver.class);
            sheet.put(subkey);

            BOMasterNodeProperty<String> value;
            value = new BOMasterNodeProperty<String>("value", String.class, to.getValue(), CommonBundleResolver.class);
            sheet.put(value);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> client;
        client = new BOMasterNodeProperty<String>("client", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty<String> service;
        service = new BOMasterNodeProperty<String>("service", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty<String> key;
        key = new BOMasterNodeProperty<String>("key", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty<String> subkey;
        subkey = new BOMasterNodeProperty<String>("subkey", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty<String> value;
        value = new BOMasterNodeProperty<String>("value", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            service, client, key, subkey, value
        };

        return props;
    }
}
