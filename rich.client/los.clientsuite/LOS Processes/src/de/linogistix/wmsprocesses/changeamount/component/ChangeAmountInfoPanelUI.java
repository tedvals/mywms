/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.changeamount.component;

import de.linogistix.wmsprocesses.changeamount.gui_builder.AbstractChangeaAmountInfoPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import org.openide.util.NbBundle;



/**
 *
 * @author trautm
 */
public class ChangeAmountInfoPanelUI extends AbstractChangeaAmountInfoPanel {

    public ChangeAmountInfoPanelUI(){
   
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
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "ChangeAmountInfoPanel.contentData");
    }

}
