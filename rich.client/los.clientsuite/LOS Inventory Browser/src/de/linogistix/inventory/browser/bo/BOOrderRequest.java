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
import de.linogistix.inventory.browser.masternode.BOOrderRequestMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.OrderRequestCRUDRemote;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
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
public class BOOrderRequest extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    protected String initName() {
        return "OrderRequests";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/OrderRequest.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (OrderRequestQueryRemote) loc.getStateless(OrderRequestQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSOrderRequest c;

        c = new LOSOrderRequest();
        c.setRequestId("requestId");

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        OrderRequestCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (OrderRequestCRUDRemote) loc.getStateless(OrderRequestCRUDRemote.class);

        } catch (Throwable t) {
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
    protected Property[] initBoMasterNodeProperties() {
        return BOOrderRequestMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOOrderRequestMasterNode.class;
    }
}
