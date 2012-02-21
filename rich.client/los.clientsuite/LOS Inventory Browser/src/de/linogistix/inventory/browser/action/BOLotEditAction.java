/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.crud.BOEditAction;
import org.mywms.globals.Role;

/**
 *
 * @author trautm
 */
public class BOLotEditAction extends BOEditAction{

     private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,
        Role.CLIENT_ADMIN_STR,
        Role.INVENTORY_STR
    };
     
    @Override
    public String[] getAllowedRoles() {
        return allowedRoles;
    }
    
    
}
