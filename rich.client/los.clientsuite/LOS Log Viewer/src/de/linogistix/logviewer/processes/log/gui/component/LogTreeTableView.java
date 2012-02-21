/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.log.gui.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.userlogin.LoginState;
import de.linogistix.logviewer.processes.clearing.gui.component.ClearingDialog;
import de.linogistix.logviewer.processes.clearing.gui.gui_builder.AbstractClearingDialog;
import de.linogistix.logviewer.processes.log.gui.object.ClearingItemNode;
import de.linogistix.logviewer.processes.log.gui.object.LogItemNode;
import de.linogistix.los.common.clearing.facade.LOSClearingFacade;
import de.linogistix.los.query.ClearingItemQueryRemote;
import de.linogistix.los.query.LogItemQueryRemote;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import org.mywms.globals.LogItemType;
import org.mywms.model.ClearingItem;
import org.mywms.model.LogItem;
import org.openide.explorer.ExplorerManager;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author artur
 */
public class LogTreeTableView extends JPanel implements ExplorerManager.Provider {

    private final ExplorerManager mgr = new ExplorerManager();
    Node rootNode;
    BrowserTreeTableView logTreeView;
    
    List<LogItemType> myLogItemTypeList;

    public LogTreeTableView() {
        setLayout(new java.awt.BorderLayout());
        logTreeView = new BrowserTreeTableView();
        add(logTreeView, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    public void setLogNodes(final List<LogItemType> logItemType, final boolean isClearingButtonSelected) {

        myLogItemTypeList = logItemType;
        
        Children.Keys<Object> keys = new Children.Keys<Object>() {

            @Override
            protected Node[] createNodes(Object arg0) {
                if (arg0 instanceof LogItem) {
                    return new Node[]{new LogItemNode((LogItem) arg0)};
                } else if (arg0 instanceof ClearingItem) {
                    ClearingItem ci = (ClearingItem) arg0;
                    ClearingItemNode cin = new ClearingItemNode(ci);
                    return new Node[]{cin};
                } else {
                    System.out.println("Unknown Object to display: " + arg0.getClass().getCanonicalName());
                    return new Node[]{};
                }
            }

            @Override
            protected void addNotify() {
                try {
                    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                    LogItemQueryRemote r = (LogItemQueryRemote) loc.getStateless(LogItemQueryRemote.class);                    
                     List l = r.queryRecent(20, logItemType);
                    //if clearing is selected
                    if (isClearingButtonSelected) {
                        List<ClearingItem> clearingList = getClearingItems();
                        Iterator<ClearingItem> iter = clearingList.iterator();
                        while (iter.hasNext()) {
                            l.add(iter.next());
                        }
                    }
                    setKeys(l);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }        
                    
/*                } catch (J2EEServiceLocatorException ex) {
  
                    ExceptionAnnotator.annotate(ex);
                }        */
            }
            };
        Node root = new AbstractNode(keys);
        logTreeView.setProperties(LogItemNode.templateProperties());
        logTreeView.setRootVisible(false);
        mgr.setRootContext(root);
//            mgr.setSelectedNodes(new Node[]{root.getChildren().getNodes()[0]});
    }

    @SuppressWarnings("unchecked")
    public List<ClearingItem> getClearingItems() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSClearingFacade r = (LOSClearingFacade) loc.getStateless(LOSClearingFacade.class);
            de.linogistix.common.userlogin.LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
            if (login.getState() == LoginState.AUTENTICATED) {
                return r.getUnresolvedClearingItemList();
            }
        } catch (J2EEServiceLocatorException ex) {
            Exceptions.printStackTrace(ex);
        }
        //return empty list
        return new ArrayList();
    }

    public boolean hasTableFocus() {
        return logTreeView.getTable().hasFocus();
    }

    public void addMouseListener() {
        logTreeView.getTable().addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
                    PropertySheetView d;
                    Node[] nodes = mgr.getSelectedNodes();
                    for (Node n : nodes) {
                        if (n instanceof LogItemNode) {

                        }
                        if (n instanceof ClearingItemNode) {
                            ClearingItemNode clearingNode = (ClearingItemNode) n;
                            ClearingItem item = clearingNode.getItem();
                            ClearingDialog dialog = new ClearingDialog(item);
                            dialog.showDialog();
                            if (dialog.dialogDescriptor.getValue() instanceof AbstractClearingDialog.CustomButton) {
                                ClearingDialog.CustomButton button = (ClearingDialog.CustomButton) dialog.dialogDescriptor.getValue();
                                if (button.getActionCommand() == ClearingDialog.OK_BUTTON) {
                                    setLogNodes(myLogItemTypeList, true);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private class BrowserTreeTableView extends TreeTableView {

        BrowserTreeTableView() {
        }

        JTree getTree() {
            return tree;
        }

        JTable getTable() {
            return treeTable;
        }
    }
}
