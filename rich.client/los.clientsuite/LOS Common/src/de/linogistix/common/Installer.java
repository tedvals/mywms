/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common;

import de.linogistix.common.gui.component.other.BigDecimalEditor;
import de.linogistix.common.gui.component.other.DatePropertyEditor;
import de.linogistix.common.log.RedirectStream;
import de.linogistix.common.system.ModuleInstallExt;
import de.linogistix.common.userlogin.*;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
//public class Installer extends ModuleInstall {
public class Installer extends ModuleInstallExt {

    @Override
    public void restored() {
        
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);           
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalEditor.class);           
        
         
        new RedirectStream().toConsole();       
        new LoginPanelImpl().doLogin();
        
        
        
    }

    @Override
    public void postRestored() {
          
//        MutableMultiFileSystem mf = (MutableMultiFileSystem) Lookup.getDefault().lookup(FileSystem.class);
        
//        URL url = getClass().getClassLoader().getResource("de/linogistix/common/layer_default.xml");
        
//        mf.addLayer(url);
        
        
    }

}



