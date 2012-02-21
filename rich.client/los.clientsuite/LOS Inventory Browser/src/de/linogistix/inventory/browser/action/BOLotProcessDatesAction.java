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

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.ManageExtinguishFacade;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLotProcessDatesAction extends NodeAction {
     
    private String[] roles = new String[]{
      Role.ADMIN.toString(),
      Role.INVENTORY.toString()};
  
    protected boolean enable(Node[] node) {
    
//    if ((node == null) || (node.length != 1)){
      //System.out.println("--> BONode " + node.length);
//      return false;
//    }
    
        return checkRoles();
    
    }
  
   /**
     * Checks whether the logged in user is allowed to see this node.
     */
    public boolean checkRoles(){
      LoginService login = (LoginService)Lookup.getDefault().lookup(LoginService.class);
      return login.checkRolesAllowed(getRoles());
    }
    
  protected void performAction(Node[] node) {
      
      
    NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyProcessLotDates"),
                NbBundle.getMessage(InventoryBundleResolver.class, "processLotDates"),
                NotifyDescriptor.OK_CANCEL_OPTION);

    if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
        return;
    }

    CursorControl.showWaitCursor();
    try{
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        
        ManageExtinguishFacade extFacade = loc.getStateless(ManageExtinguishFacade.class);
        extFacade.processLots();
//        extFacade.createCronJob();

    } catch (Throwable t){
      ErrorManager em = ErrorManager.getDefault();
      //em.annotate(t, t.getMessage());
      em.notify(t);
    } finally{
        CursorControl.showNormalCursor();
    }
    
  }

  public HelpCtx getHelpCtx() {
    return null;
  }

  public String getName() {
    return NbBundle.getMessage(InventoryBundleResolver.class, "processLotDates");
  }

  protected boolean asynchronous() {
    return false;
  }

  public String[] getRoles() {
    return roles;
  }
  


  
}


