/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.log;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.logviewer.res.BundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Log component.
 */
public class LogAction extends AbstractAction {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.CLEARING_STR};
    }

    public LogAction() {
        super(NbBundle.getMessage(BundleResolver.class, "CTL_LogAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(LogTopComponent.ICON_PATH, true)));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = LogTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
