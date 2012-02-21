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
import de.linogistix.inventory.browser.action.BOStorageRequestCancelAction;
import de.linogistix.inventory.browser.masternode.BOLOSStorageRequestMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSStorageRequestCRUDRemote;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
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
public class BOLOSStorageRequest extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }

  protected String initName() {
    return "LOSStorageRequests";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/LOSStorageRequest.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (LOSStorageRequestQueryRemote)loc.getStateless(LOSStorageRequestQueryRemote.class);
      
    } catch (Throwable t){
      //
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    LOSStorageRequest c;
    
    c = new LOSStorageRequest();
    c.setNumber("RequestId");
    
    return c;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    LOSStorageRequestCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (LOSStorageRequestCRUDRemote)loc.getStateless(LOSStorageRequestCRUDRemote.class);
      
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
        return BOLOSStorageRequestMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSStorageRequestMasterNode.class;
    }

  
}
