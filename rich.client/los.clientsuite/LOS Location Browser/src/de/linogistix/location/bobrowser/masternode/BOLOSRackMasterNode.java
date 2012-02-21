/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.query.dto.LOSRackTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jordan
 */
public class BOLOSRackMasterNode extends BOMasterNode{

    private LOSRackTO to;
    
    public BOLOSRackMasterNode(BODTO d, BO bo)throws IntrospectionException
    {
        super(d, bo);
        to = (LOSRackTO) d;
    }
    
    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.lock != LOSStorageLocationLockState.NOT_LOCKED) {
            ret = "<font color=\"#CC0000\">" + ret + "</font>";
        }
        
        return ret;
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, to.lock.getMessage(), CommonBundleResolver.class);
            sheet.put(lock);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, " ", CommonBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            lock
        };

        return props;
    }
}
