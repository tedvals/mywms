/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.unitload.transfer.component;

import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.unitload.transfer.gui_builder.AbstractChooseSourcePanel;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class ChooseSourcePanel extends AbstractChooseSourcePanel{

    public ChooseSourcePanel() {
         getUnitLoadAutofilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "unitLoadSource"));
        getUnitLoadAutofilteringComboBox().setEditorLabelText();
        getUnitLoadAutofilteringComboBox().setEnabled(true);
        
        clear();
    }

     void clear() {
        getUnitLoadAutofilteringComboBox().clear();
    }
     
      public boolean implIsValid() {      
        return true;
    }

    public void implReadSettings(Object settings) {
//

    }

    public void implStoreSettings(Object settings) {
        //this.w = (BOStockUnitTransferWizard) settings;
        
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "chooseSrcDest");
    }
    
}
