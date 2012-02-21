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
import de.linogistix.inventory.browser.masternode.BOPickingRequestMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.pick.crud.LOSPickRequestCRUDRemote;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.ArrayList;
import java.util.List;
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
public class BOPickingRequest extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
     
    protected String initName() {
        return "PickingRequests";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/PickingRequest.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSPickRequestQueryRemote) loc.getStateless(LOSPickRequestQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSPickRequest c;

        c = new LOSPickRequest();
        c.setNumber("123");
        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSPickRequestCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSPickRequestCRUDRemote) loc.getStateless(LOSPickRequestCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"number"};
    }

    @Override
    protected List<SystemAction> initMasterActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>();
        SystemAction action;

       
// Does not work        
//        action = SystemAction.get(BOPickRequestFinishAction.class);
//        action.setEnabled(true);
//        actions.add(action);


        return actions;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOPickingRequestMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOPickingRequestMasterNode.class;
    }
}
