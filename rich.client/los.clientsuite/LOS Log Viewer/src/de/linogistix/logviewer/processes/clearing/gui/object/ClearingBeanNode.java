/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.clearing.gui.object;

import de.linogistix.common.system.BundleHelper;
import de.linogistix.logviewer.res.BundleResolver;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mywms.model.ClearingItemOptionRetval;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class ClearingBeanNode extends BeanNode {

    @SuppressWarnings("unchecked")
    public ClearingBeanNode(ClearingObject object) throws IntrospectionException {
        super(object);
    }

    @Override
    protected void createProperties(Object object, BeanInfo info) {
        createProperties((ClearingObject) object);
    }

    @SuppressWarnings("unchecked")
    private Property createPropertyMessage(ClearingObject d, String key, String displayName, Object value, Class type) {
        Property property = null;
        try {
            ClearingObject dm = new ClearingObject(d.item, d.list);
            dm.setName(value);
            Method getter = dm.getClass().getMethod("getName", new Class[]{});
            Method setter = dm.getClass().getMethod("setName", new Class[]{Object.class});
            property = new PropertySupport.Reflection<Class>(dm, type, getter, setter);
            property.setName(key);
            property.setDisplayName(displayName);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }

        return property;
    }

    private Object getDefaultValue(Class type) {
        if (type == String.class) {
            return new String("");
        }
        if (type == Boolean.class) {
            return Boolean.FALSE;
        }
        if (type == Date.class) {
            return new Date(System.currentTimeMillis());
        }
        return 0;
    }

    private void createProperties(Object co) {
        if (co instanceof ClearingObject) {
            ClearingObject d = (ClearingObject) co;
            List<ClearingItemOptionRetval> list = d.getList();
            if (list != null) {
                Sheet sheet = getSheet();
                Sheet.Set basics = Sheet.createPropertiesSet();
                basics.setName("Klähurngsfall Einstellungen");
                basics.setDisplayName("Klähurngsfall Einstellungen");
                for (ClearingItemOptionRetval ret : list) {
      
                    if (ret.getRetval() == null) {                                             
                        basics.put(createPropertyMessage(d, ret.getNameResourceKey(),
                                ret.getName(d.item.getResourceBundleName(), d.item.getBundleResolver()),
                                getDefaultValue(ret.getType()), ret.getType()));
                    } else {
                        basics.put(createPropertyMessage(d, ret.getNameResourceKey(),
                                ret.getName(d.item.getResourceBundleName(), d.item.getBundleResolver()),
                                ret.getRetval(), ret.getType()));
                    }                    
                }
                sheet.put(basics);
            }
        }
    }
}
