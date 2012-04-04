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

package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
//import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOVehicleDataMasterNode;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.VehicleDataCRUDRemote;
import de.linogistix.los.inventory.query.VehicleDataQueryRemote;
import de.linogistix.inventory.res.InventoryBundleResolver;
//import de.linogistix.los.inventory.query.ItemUnitQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
//import de.linogistix.los.query.QueryDetail;
//import java.util.List;
import org.mywms.globals.Role;
//import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.BasicEntity;
import org.mywms.model.VehicleData;
//import org.mywms.model.ItemUnit;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

public class BOVehicleData extends BO {
	//private ItemUnit itemUnit = null;
    
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

  rotected String initName() {
    return "VehicleDatas";
  }
  
  protected String initIconBaseWithExtension() {
	  return "de/linogistix/bobrowser/res/icon/ItemData.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(VehicleDataQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
      return null;
    }
    
    //readDefaultItemUnit();
    return ret;
  }
  
  
  //private void readDefaultItemUnit() {
    //ItemUnitQueryRemote itemUnitQuery = null;
    //try{
        //J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
        //itemUnitQuery = (ItemUnitQueryRemote)loc.getStateless(ItemUnitQueryRemote.class);
      
        //itemUnit = itemUnitQuery.getDefault();
        //if( itemUnit == null ) {
            //List<ItemUnit> list = itemUnitQuery.queryAll( new QueryDetail(0,1) );
            //if( list.size()>0 ) {
                //itemUnit=list.get(0);
            //}
        //}
    //} catch (Throwable t){
      //ExceptionAnnotator.annotate(t);
    //}
  //}
  
  
  protected BasicEntity initEntityTemplate() {
    VehicleData o;

    o = new VehicleData();
    //o.setName("Template");
    //o.setNumber("Template");
    //o.setSerialNoRecordType(SerialNoRecordType.NO_RECORD);
    //o.setHandlingUnit(itemUnit);
    //LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
    //o.setClient( login.getUsersClient() );

    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(VehicleDataCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
   protected String[] initIdentifiableProperties() {
    return new String[]{"id"};
  }

    @Override
    public Class getBundleResolver() {
        return InventoryBundleResolver.class;
    }
   
    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOVehicleDataMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOVehicleDataMasterNode.class;
    }

}
