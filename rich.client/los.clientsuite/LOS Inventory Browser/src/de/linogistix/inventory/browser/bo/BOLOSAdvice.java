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
import de.linogistix.inventory.browser.masternode.BOLOSAdviceMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSAdviceCRUDRemote;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
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
public class BOLOSAdvice extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }


    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSAdviceMasterNode.class;
    }

    public String initName() {
        return "LOSAdvices";
    }

    @Override
    public String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/LOSAdvice.png";
    }

    public BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSAdviceQueryRemote) loc.getStateless(LOSAdviceQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    public BasicEntity initEntityTemplate() {
        LOSAdvice c;

        c = new LOSAdvice();

        return c;

    }

    public BusinessObjectCRUDRemote initCRUDService() {
        LOSAdviceCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSAdviceCRUDRemote) loc.getStateless(LOSAdviceCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    public Class<InventoryBundleResolver> initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSAdviceMasterNode.boMasterNodeProperties();
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"adviceNumber"};
    }

}
