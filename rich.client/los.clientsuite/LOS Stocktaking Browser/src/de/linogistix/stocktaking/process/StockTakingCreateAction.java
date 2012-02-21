/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.stocktaking.process.gui.StocktakingCreateTopComponent;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows StockTakingTopComponent component.
 */
public class StockTakingCreateAction extends AbstractAction {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }

    public StockTakingCreateAction() {
        super(NbBundle.getMessage(StocktakingBundleResolver.class, "CTL_StockTakingTopComponentAction"));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = StocktakingCreateTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
