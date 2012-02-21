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
package de.linogistix.location.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.location.bobrowser.action.BOUnitLoadNirwanaAction;
import de.linogistix.location.bobrowser.masternode.BOLOSUnitLoadMasterNode;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.crud.LOSUnitLoadCRUDRemote;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
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
public class BOLOSUnitLoad extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    private static final Logger log = Logger.getLogger(BOLOSUnitLoad.class.getName());
    Vector<Action> actions;

    protected String initName() {
        return "LOSUnitLoads";
    }

    @Override
    protected String initIconBaseWithExtension() {

        return "de/linogistix/location/res/icon/UnitLoad.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(LOSUnitLoadQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSUnitLoad o;

        o = new LOSUnitLoad();
        o.setLabelId("Template");
        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSUnitLoadCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"number"};
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        List<BOQueryComponentProvider> ret = super.initQueryComponentProviders();
        try {
            Method m = LOSUnitLoadQueryRemote.class.getMethod("queryAllEmpty", new Class[]{QueryDetail.class});
            ret.add(new DefaultBOQueryComponentProvider(getQueryService(), m));
            return ret;
        } catch (NoSuchMethodException ex) {
            log.log(Level.INFO, ex.getMessage(), ex);
        } catch (SecurityException ex) {
            log.log(Level.INFO, ex.getMessage(), ex);
        }
        return ret;
    }

    public List<SystemAction> doNot_initMasterActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>();
        
        SystemAction action;

        action = SystemAction.get(BOUnitLoadNirwanaAction.class);
        action.setEnabled(true);
        actions.add(action);

        return actions;
        
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSUnitLoadMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSUnitLoadMasterNode.class;
    }
    
}
