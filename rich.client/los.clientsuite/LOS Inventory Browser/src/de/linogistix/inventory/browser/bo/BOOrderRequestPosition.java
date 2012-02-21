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
import de.linogistix.inventory.browser.masternode.BOLOSOrderRequestPositionMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.OrderRequestPositionCRUDRemote;
import de.linogistix.los.inventory.query.OrderRequestPositionQueryRemote;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOOrderRequestPosition extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    protected String initName() {
    return "OrderRequestPositions";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/OrderRequestPosition.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (OrderRequestPositionQueryRemote)loc.getStateless(OrderRequestPositionQueryRemote.class);
      
    } catch (Throwable t){
      //
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    LOSOrderRequestPosition c;
    
    c = new LOSOrderRequestPosition();
    
    return c;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    OrderRequestPositionCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (OrderRequestPositionCRUDRemote)loc.getStateless(OrderRequestPositionCRUDRemote.class);
      
    } catch (Throwable t){
      //ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  @Override
  protected Class initBundleResolver() {
    return CommonBundleResolver.class;
  }
  
  @Override
  protected String[] initIdentifiableProperties() {
    return new String[]{"number"};
  }
   
  @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSOrderRequestPositionMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSOrderRequestPositionMasterNode.class;
    }
  
}
