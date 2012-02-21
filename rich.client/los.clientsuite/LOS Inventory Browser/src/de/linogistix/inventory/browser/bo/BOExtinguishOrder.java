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
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.browser.action.BOOrderFinishAction;
import de.linogistix.inventory.browser.action.BOOrderRemoveAction;
import de.linogistix.inventory.browser.action.BOOrderStartAction;
import de.linogistix.inventory.browser.masternode.BOExtinguishOrderMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.ExtinguishOrderCRUDRemote;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.query.ExtinguishOrderQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
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
public class BOExtinguishOrder extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
    
  
  protected  String initName() {
    return "ExtinguishOrders";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ExtinguishOrder.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (ExtinguishOrderQueryRemote)loc.getStateless(ExtinguishOrderQueryRemote.class);
      
    } catch (Throwable t){
      //
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    ExtinguishOrder c;
    
    c = new ExtinguishOrder();    
    return c;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    ExtinguishOrderCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (ExtinguishOrderCRUDRemote)loc.getStateless(ExtinguishOrderCRUDRemote.class);
      
    } catch (Throwable t){
      //ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  protected Class initBundleResolver() {
    return InventoryBundleResolver.class;
  }
  
   protected String[] initIdentifiableProperties() {
    return new String[]{"number"};
  }



    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOExtinguishOrderMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOExtinguishOrderMasterNode.class;
    }

}
