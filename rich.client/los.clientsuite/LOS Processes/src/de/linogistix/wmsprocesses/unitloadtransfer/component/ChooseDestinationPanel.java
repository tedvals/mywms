/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.unitloadtransfer.component;

import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.stockunittransfer.BOStockUnitTransferWizard;
import de.linogistix.wmsprocesses.unitloadtransfer.gui_builder.AbstractChooseDestinationPanel;
import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class ChooseDestinationPanel extends AbstractChooseDestinationPanel {

    private static final Logger log = Logger.getLogger(ChooseDestinationPanel.class.getName());
    
    private BOStockUnitTransferWizard w;
            
    public ChooseDestinationPanel() {
        getUnitLoadAutoFilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "unitLoad"));
        getUnitLoadAutoFilteringComboBox().setEditorLabelText();
        getUnitLoadAutoFilteringComboBox().setEnabled(true);
       
        getDestinationAutofilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "AbstractChooseDestinationPanel.destinationSl"));
        getDestinationAutofilteringComboBox().setEditorLabelText();
        getDestinationAutofilteringComboBox().setEnabled(true);
        
        clear();
    }

    void clear() {

        getUnitLoadAutoFilteringComboBox().clear();
        getDestinationAutofilteringComboBox().clear();

    }

    public boolean implIsValid() {      
        return true;
    }

    public void implReadSettings(Object settings) {

    }

    public void implStoreSettings(Object settings) {
        
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "ChooseDestinationPanel.name");
    }
    
    
    
}
