/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.clearing.gui.object;

import java.util.List;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOptionRetval;

/**
 *
 * @author artur
 */
public class ClearingObject {
    Object name;
    List<ClearingItemOptionRetval> list;
    ClearingItem item;
    
    public ClearingObject(){
        
    }
    
    public ClearingObject(ClearingItem item, List<ClearingItemOptionRetval> list) {
        this.list = list; 
        this.item = item;
    }
    
    public Object getName() {
        //return name;
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public List<ClearingItemOptionRetval> getList() {
        return list;
    }

    


}
