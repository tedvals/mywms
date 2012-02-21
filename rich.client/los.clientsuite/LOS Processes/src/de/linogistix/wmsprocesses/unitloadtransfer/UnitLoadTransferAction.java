/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.unitloadtransfer;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Action which shows BOBrowser component.
 */
public class UnitLoadTransferAction extends AbstractAction {

    static final String ICON_PATH = "de/linogistix/common/res/icon/Action.png";

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR};
    }
    
    public UnitLoadTransferAction() {
        super(NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferUnitLoad"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(UnitLoadTransferAction.ICON_PATH, true)));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());
        setEnabled(result);
    }

    public void actionPerformed(ActionEvent evt) {
        
        BOUnitLoadTransferWizard w;
        try {
            w = new BOUnitLoadTransferWizard(null);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }

        transferUnitLoad(w);
    }

    public boolean actionPerformed( BODTO unitLoadTo ) {
        BOUnitLoadTransferWizard w;
        try {
            w = new BOUnitLoadTransferWizard(unitLoadTo);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        return transferUnitLoad(w);
    }

    private boolean transferUnitLoad(BOUnitLoadTransferWizard w) {
        boolean redo = true;
        while(redo){
            try {

                Dialog d = DialogDisplayer.getDefault().createDialog(w);
                d.setVisible(true);

                if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                    CursorControl.showWaitCursor();
                    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                    ManageInventoryFacade m = loc.getStateless(ManageInventoryFacade.class);
                    m.transferUnitLoad(w.getStorageLocationTO(), w.getUnitLoadTO(), -1, w.isIgnoreLock(), w.getHint());
                    CursorControl.showNormalCursor();
                    return true;
                }
                
                redo = false;

            } catch (FacadeException ex){
                ExceptionAnnotator.annotate(ex);
                BOUnitLoadTransferWizard tmp;
                try {
                    tmp = new BOUnitLoadTransferWizard(null);
                    tmp.setHint(w.getHint());
                    tmp.setStorageLocationTO(w.getStorageLocationTO());
                    tmp.setUnitLoadTO(w.getUnitLoadTO());
                    tmp.setIgnoreLock(w.isIgnoreLock());
                    w = tmp;
                } catch (InstantiationException ex1) {
                    ExceptionAnnotator.annotate(ex);
                    redo = false;
                }
                redo = true;
            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
                redo = false;
            }
        }
        CursorControl.showNormalCursor();
        return false;
    }
}
