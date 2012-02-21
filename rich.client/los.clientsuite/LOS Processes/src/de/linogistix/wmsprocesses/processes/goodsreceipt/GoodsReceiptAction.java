/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows GoodsReceipt component.
 */
public class GoodsReceiptAction extends AbstractAction {
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }

    public GoodsReceiptAction() {
        super(NbBundle.getMessage(WMSProcessesBundleResolver.class, "CTL_GoodsReceiptAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(GoodsReceiptTopComponent.ICON_PATH, true)));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = GoodsReceiptTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
