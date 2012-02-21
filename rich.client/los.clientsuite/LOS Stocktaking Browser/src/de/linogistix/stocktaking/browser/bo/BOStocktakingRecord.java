/*
 * BONodeUser.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.stocktaking.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.stocktaking.crud.LOSStocktakingRecordCRUDRemote;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.query.LOSStocktakingRecordQueryRemote;
import de.linogistix.stocktaking.browser.masternode.BOLOSStockTakingRecordMasterNode;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;



/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOStocktakingRecord extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
   
  protected String initName() {
    return "Records";
  }
  
    @Override
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemUnit.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LOSStocktakingRecordQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    LOSStocktakingRecord o;
    
    o = new LOSStocktakingRecord();
        
    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSStocktakingRecordCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
    @Override
   protected String[] initIdentifiableProperties() {
    return new String[]{"id"};
  }

    @Override
    protected Class initBundleResolver() {
        return StocktakingBundleResolver.class;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSStockTakingRecordMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSStockTakingRecordMasterNode.class;
    }
  
}
