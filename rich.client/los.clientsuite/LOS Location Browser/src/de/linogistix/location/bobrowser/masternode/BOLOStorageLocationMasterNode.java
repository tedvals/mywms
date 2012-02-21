/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOStorageLocationMasterNode extends BOMasterNode {

    StorageLocationTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOStorageLocationMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StorageLocationTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.lock == BusinessObjectLockState.GOING_TO_DELETE.getLock()) {
            ret = "<font color=\"#C0C0C0\"><s>" + ret + "</s></font>";
        }
        
        return ret;
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
            BOMasterNodeProperty<Integer> reserved = new BOMasterNodeProperty<Integer>("reserved", Integer.class, to.reserved, CommonBundleResolver.class);
            sheet.put(reserved);
            BOMasterNodeProperty<Integer> lock = new BOMasterNodeProperty<Integer>("lock", Integer.class, to.lock, CommonBundleResolver.class);
            sheet.put(lock);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<Integer> lock = new BOMasterNodeProperty<Integer>("lock", Integer.class, 0, CommonBundleResolver.class);

        BOMasterNodeProperty<Integer> reserved = new BOMasterNodeProperty<Integer>("reserved", Integer.class, 0, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            lock, reserved
        };

        return props;
    }
}
