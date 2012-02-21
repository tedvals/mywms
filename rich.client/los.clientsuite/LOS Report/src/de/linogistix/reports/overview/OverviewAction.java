/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.overview;

import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Overview component.
 */
public class OverviewAction extends AbstractAction {

    public OverviewAction() {
        super(NbBundle.getMessage(ReportsBundleResolver.class, "CTL_OverviewAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OverviewTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = OverviewTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
