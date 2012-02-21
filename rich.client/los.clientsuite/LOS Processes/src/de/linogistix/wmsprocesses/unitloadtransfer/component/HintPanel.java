/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.unitloadtransfer.component;

import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.unitloadtransfer.gui_builder.AbstractHintPanel;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class HintPanel extends AbstractHintPanel {

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "HintPanel.name");
    }
    
}
