/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.queryinventory;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction; 
import org.mywms.globals.Role;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows QueryInventory component.
 */
public class QueryInventoryAction extends AbstractAction {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }

    public QueryInventoryAction() {
        super(NbBundle.getMessage(ReportsBundleResolver.class, "CTL_QueryInventoryAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(QueryInventoryTopComponent.ICON_PATH, true)));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = QueryInventoryTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
