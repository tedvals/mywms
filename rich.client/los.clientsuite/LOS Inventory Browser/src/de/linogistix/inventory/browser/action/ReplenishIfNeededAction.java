/*
 * OpenBOQueryTopComponentAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.los.inventory.facade.ReplenishFacade;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class ReplenishIfNeededAction extends AbstractAction {
     
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }
    
    public ReplenishIfNeededAction() {
        super(NbBundle.getMessage(CommonBundleResolver.class, "createReplenishmentIfNeeded"));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }
    
    public void actionPerformed(ActionEvent evt) {

        CursorControl.showWaitCursor();
        try {

            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(CommonBundleResolver.class, "NotifyDescriptor.ReallyReplenishIfNeeded"),
                    NbBundle.getMessage(CommonBundleResolver.class, "createReplenishmentIfNeeded"),
                    NotifyDescriptor.OK_CANCEL_OPTION);

            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);

                ReplenishFacade facade = loc.getStateless(ReplenishFacade.class);
                facade.createReplenishmentIfNeeded();
                //facade.createCronJob();
            }

        } catch  (Throwable t) {
        ErrorManager em = ErrorManager.getDefault();
        //em.annotate(t, t.getMessage());
        em.notify(t);
        } finally {
            CursorControl.showNormalCursor();
        }

    }
}


