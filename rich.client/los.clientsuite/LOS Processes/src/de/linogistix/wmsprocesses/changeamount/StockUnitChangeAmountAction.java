/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.changeamount;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.wmsprocesses.changeamount.component.BOStockUnitChangeAmountWizard;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Action which shows BOBrowser component.
 */
public class StockUnitChangeAmountAction extends AbstractAction {

    static final String ICON_PATH = "de/linogistix/common/res/icon/Action.png";

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }

    public StockUnitChangeAmountAction() {
        super(NbBundle.getMessage(WMSProcessesBundleResolver.class, "changeAmount"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(StockUnitChangeAmountAction.ICON_PATH, true)));
        
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        try {

            BOStockUnitChangeAmountWizard w = new BOStockUnitChangeAmountWizard(null);

            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();

                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                ManageInventoryFacade m = loc.getStateless(ManageInventoryFacade.class);
                m.changeAmount(w.getSu(), w.getAmount(), w.getReserveAmount(), w.getInfo());
                
            }
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}
