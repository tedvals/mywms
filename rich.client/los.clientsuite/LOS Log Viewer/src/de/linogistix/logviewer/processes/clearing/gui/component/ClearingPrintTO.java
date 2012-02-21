/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.clearing.gui.component;

import de.linogistix.common.system.BundleHelper;
import java.util.Date;
import org.mywms.model.ClearingItem;

/**
 *
 * @author trautm
 */
class ClearingPrintTO {
    
    private Date created;
    
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    
    static final String resolveMessage(ClearingItem clearingItem){
        BundleHelper bundle = new BundleHelper(clearingItem.getResourceBundleName(), clearingItem.getBundleResolver());
        
        return bundle.resolve2(clearingItem.getMessageResourceKey(), clearingItem.getShortMessageParameters());
    }
    
}
