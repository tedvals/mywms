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
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.masternode.BOLOSServicePropertyMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.crud.LOSServicePropertyCRUDRemote;
import de.linogistix.los.model.LOSServiceProperty;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSServicePropertyQueryRemote;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSServiceProperty extends BO {

    private static String[] allowedRoles = new String[]{Role.ADMIN_STR,Role.CLEARING_STR};

    protected String initName() {
        return "LOSServicePropertys";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Edit.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSServicePropertyQueryRemote) loc.getStateless(LOSServicePropertyQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSServiceProperty c;

        c = new LOSServiceProperty();
        c.setService("Template");
        c.setKey("Template");
        
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        c.setClient( login.getUsersClient() );

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSServicePropertyCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSServicePropertyCRUDRemote) loc.getStateless(LOSServicePropertyCRUDRemote.class);

        } catch (Throwable t) {
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
        return new String[]{"id"};
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSServicePropertyMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSServicePropertyMasterNode.class;
    }

    
    @Override
    public String[] getAllowedRoles() {
        return allowedRoles;
    }

    
}
