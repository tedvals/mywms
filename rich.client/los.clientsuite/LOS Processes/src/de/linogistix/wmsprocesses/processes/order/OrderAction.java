/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order;



import de.linogistix.common.userlogin.LoginService;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Order component.
 */
public class OrderAction extends AbstractAction {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }

    public OrderAction() {
        super(NbBundle.getMessage(WMSProcessesBundleResolver.class, "CTL_OrderAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OrderTopComponent.ICON_PATH, true)));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = OrderTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
