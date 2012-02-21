/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.query.BODTO;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.PickingRequestState;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BOPickRequestRemoveAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOPickRequestRemoveAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.INVENTORY_STR
    };
    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "remove");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Action.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        if( !login.checkRolesAllowed(allowedRoles) ) {
            return false;
        }
        
        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        for (Node n : activatedNodes) {
            if (n == null) {
                continue;
            }
            if (!(n instanceof BOMasterNode)) {
                continue;
            }

            LOSPickRequestQueryRemote orderQuery;
            LOSPickRequest r;
            try {
                orderQuery = loc.getStateless(LOSPickRequestQueryRemote.class);
                r = orderQuery.queryById(((BOMasterNode)n).getEntity().getId());
            } catch (Exception e) {
                return false;
            }
            if( r.getState() != PickingRequestState.RAW && r.getState() != PickingRequestState.FINISHED && r.getState() != PickingRequestState.FAILED ) {
                return false;
            }
        }

        return true;
    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(CommonBundleResolver.class, "NotifyDescriptor.ReallyCancellation"),
                NbBundle.getMessage(CommonBundleResolver.class, "remove"),
                NotifyDescriptor.OK_CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        try {
            for (Node n : activatedNodes) {
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                }
                BODTO<LOSPickRequest> bo;
                bo = ((BOMasterNode)n).getEntity();
                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                PickOrderFacade m = loc.getStateless(PickOrderFacade.class);
                m.remove(bo);
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( activatedNodes.length>0 ) {
            Node n = activatedNodes[0];
            if (n instanceof BOMasterNode) {
                BOMasterNode ma = (BOMasterNode)n;
                BO bo = ma.getBo();
                bo.fireOutdatedEvent(ma);
            }
        }
    }
}

