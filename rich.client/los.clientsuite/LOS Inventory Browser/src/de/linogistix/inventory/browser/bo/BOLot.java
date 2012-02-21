/*
 * BOClient.java
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
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.action.BOLotExtinguishAction;
import de.linogistix.inventory.browser.masternode.BOLotMasterNode;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.crud.LotCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.mywms.model.Lot;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLot extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }
   
    
    private static final Logger log = Logger.getLogger(BOLot.class.getName());
    
  protected String initName() {
    return "Lots";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/Document.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LotQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    Lot c;
    
    c = new Lot();
//    c.setName("Template");

    LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
    c.setClient( login.getUsersClient() );
    c.setDate(new Date());
    return c;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    LotCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (LotCRUDRemote)loc.getStateless(LotCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }

  protected Class initBundleResolver() {
    return CommonBundleResolver.class;
  }
  
  protected String[] initIdentifiableProperties() {
    return new String[]{"name"};
  }
  

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLotMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLotMasterNode.class;
    }

}
