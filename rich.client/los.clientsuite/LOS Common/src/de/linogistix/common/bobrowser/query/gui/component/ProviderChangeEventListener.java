/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

/**
 *
 * @author trautm
 */
public interface ProviderChangeEventListener {
    
    void providerSelected(BOQueryComponentProvider prov);
    
    void searchStringChanged(BOQueryComponentProvider prov, String searchStr);
    
    void reloadRequest();
    
}
