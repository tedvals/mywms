/*
 * BONodeClient.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.inventory.browser.action.BOReplenishIfNeededAction;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.browser.action.BOOrderRemoveAction;
import de.linogistix.inventory.browser.masternode.BOReplenishRequestMasterNode;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.OrderRequestCRUDRemote;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.query.ReplenishRequestQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOReplenishRequest extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
    
   protected  String initName() {
    return "ReplenishRequests";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ReplenishRequest.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (ReplenishRequestQueryRemote)loc.getStateless(ReplenishRequestQueryRemote.class);
      
    } catch (Throwable t){
      //
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    LOSReplenishRequest c;
    
    c = new LOSReplenishRequest();
    c.setRequestId("requestId");
    
    return c;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    OrderRequestCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (OrderRequestCRUDRemote)loc.getStateless(OrderRequestCRUDRemote.class);
      
    } catch (Throwable t){
      //ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  protected Class initBundleResolver() {
    return CommonBundleResolver.class;
  }
  
   protected String[] initIdentifiableProperties() {
    return new String[]{"requestId"};
  }
  
   @Override
    protected List<SystemAction> initActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>(); 
        actions = super.initActions();
        
        SystemAction action;
        action = SystemAction.get(BOReplenishIfNeededAction.class);
        action.setEnabled(true);
        actions.add(action);
        
        return actions;
    }
   

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOReplenishRequestMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOReplenishRequestMasterNode.class;
    }

  
}
