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
import de.linogistix.inventory.browser.masternode.BOPickingRequestPositionMasterNode;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.pick.crud.LOSPickRequestPositionCRUDRemote;
import de.linogistix.los.inventory.pick.query.LOSPickRequestPositionQueryRemote;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOPickingRequestPosition extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
    
    protected String initName() {
        return "PickingRequestPositions";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/OrderRequest.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSPickRequestPositionQueryRemote) loc.getStateless(LOSPickRequestPositionQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSPickRequestPosition c;

        c = new LOSPickRequestPosition();
        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSPickRequestPositionCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSPickRequestPositionCRUDRemote) loc.getStateless(LOSPickRequestPositionCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"id"};
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
     
        try {
            
            Method m = getQueryService().getClass().getMethod("queryByNotSolved", new Class[]{QueryDetail.class});
            List<BOQueryComponentProvider> l = new ArrayList<BOQueryComponentProvider>();
            
            l.addAll(super.initQueryComponentProviders());
            l.add(new DefaultBOQueryComponentProvider(getQueryService(),m));

            return l;
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new ArrayList<BOQueryComponentProvider>();
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOPickingRequestPositionMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOPickingRequestPositionMasterNode.class;
    }
}
