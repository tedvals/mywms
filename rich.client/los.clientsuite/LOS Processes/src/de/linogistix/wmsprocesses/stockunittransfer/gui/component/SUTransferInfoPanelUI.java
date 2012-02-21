/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.stockunittransfer.gui.component;

import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.stockunittransfer.gui.gui_builder.AbstractSUTransferInfoPanel;
import org.openide.util.NbBundle;


/**
 *
 * @author trautm
 */
public class SUTransferInfoPanelUI extends AbstractSUTransferInfoPanel {

    public SUTransferInfoPanelUI(){
   
    }

    String getInfo() {
        return additionalInfoTextArea.getText();
    }

    void implReadSettings(Object settings) {
       //
    }

    void implStoreSettings(Object settings) {
        //
    }

    void setInfo(String info) {
        additionalInfoTextArea.setText(info);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "SUTransferInfoPanel.contentData");
    }

    
}
