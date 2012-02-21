/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.clearing.gui.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.userlogin.LoginState;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.logviewer.processes.clearing.gui.gui_builder.AbstractClearingDialog;
import de.linogistix.logviewer.processes.clearing.gui.object.ClearingBeanNode;
import de.linogistix.logviewer.processes.clearing.gui.object.ClearingObject;
import de.linogistix.los.common.clearing.exception.LOSClearingException;
import de.linogistix.los.common.clearing.facade.LOSClearingFacade;
import java.beans.IntrospectionException;
import java.io.NotSerializableException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import org.mywms.model.ClearingItem;
import org.mywms.model.ClearingItemOption;
import org.mywms.model.ClearingItemOptionRetval;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author artur
 */
public class ClearingDialog extends AbstractClearingDialog {
    
    private ClearingItem clearingItem;
    private List<ClearingItemOptionRetval> retList;
    private ClearingItemOption clearingOption;
    
    public ClearingDialog(ClearingItem clearingItem) {
        
        this.clearingItem = clearingItem;
        
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                splitPane.setDividerLocation((centerPanel.getWidth() / 2));
            }
            });
       
        setTitle(clearingItem.getMessage());
        setOptions(clearingItem);
        setPropertySheet(clearingItem, null);
    }

    private void setOptions(ClearingItem item) {
        for (ClearingItemOption n : item.getOptions()) {           
            addRow(n.getMessage(item.getResourceBundleName(), item.getBundleResolver()));
        }
    }

    private void setPropertySheet(ClearingItem item, List<ClearingItemOptionRetval> list) {
        try {
            ClearingBeanNode node = new ClearingBeanNode(new ClearingObject(item, list));
            sheet.setNodes(new Node[]{node});
            sheet.invalidate();
            sheet.validate();
            getExplorerManager().setRootContext(node);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void selectionChangedListener(ListSelectionEvent e) {
        ArrayList<ClearingItemOption> option = clearingItem.getOptions();
        if (table.getSelectedRow() != -1) {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (table.getSelectedRow() == i) {
                    clearingOption = option.get(i);
                    retList = option.get(i).getRetvals();
                    setPropertySheet(clearingItem, retList);
                }
            }
        }
    }

    private void updateClearingItem() {
        ClearingBeanNode node = (ClearingBeanNode) getExplorerManager().getRootContext();
        PropertySet[] set = node.getPropertySets();
        for (PropertySet n : set) {
            try {
                //give the properties in the propertysheet
                Property[] prop = n.getProperties();
                for (int i = 0; i < prop.length; i++) {
                    //give in the same order the results back as the properties will be added.
                    //So retlist(i) is the original and prop(i) the new value
                    ClearingItemOptionRetval retItem = retList.get(i);
                    retItem.setRetval(prop[i].getValue());
                }

            } catch (NotSerializableException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void actionPerformed(final java.awt.event.ActionEvent ev) {
        String action = ev.getActionCommand();
        if (action == OK_BUTTON) {
            updateClearingItem();        
            sendRetValue();
            //printRetValue();
            closeDialog();
        }    
    }
    
    private void printRetValue() {
        List<ClearingItemOptionRetval> item = clearingOption.getRetvals();
        Iterator<ClearingItemOptionRetval> iter = item.iterator();
        while (iter.hasNext()) {            
            ClearingItemOptionRetval n = iter.next();
            System.out.println("name = "+n.getName(clearingItem.getResourceBundleName(), clearingItem.getBundleResolver()));
            System.out.println("retval = "+n.getRetval());
            System.out.println("tpye = "+n.getType());
        }
    }

    private void sendRetValue() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            
            LOSClearingFacade clearingFacade = loc.getStateless(LOSClearingFacade.class);            
            
            de.linogistix.common.userlogin.LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
            if (login.getState() == LoginState.AUTENTICATED) {
                try {
                    clearingFacade.setClearingSolution(clearingItem, clearingOption);
                } catch (LOSClearingException ex) {
                    ExceptionAnnotator.annotate(ex);
                } catch (Throwable ex){
                    ExceptionAnnotator.annotate(ex);
                }
            }
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        }

    }
}
