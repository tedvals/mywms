/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.querystockunits;

import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows QueryStockUnit component.
 */
public class QueryStockUnitAction extends AbstractAction {

    public QueryStockUnitAction() {
        super(NbBundle.getMessage(ReportsBundleResolver.class, "CTL_QueryStockUnitAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(QueryStockUnitTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = QueryStockUnitTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
