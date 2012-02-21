/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.bo.BOBeanNode;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOEntityQueryNode;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.editor.BOEditorButtons;
import de.linogistix.common.bobrowser.bo.editor.BOMasterDetailView;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class BOQueryMasterDetailView extends BOMasterDetailView implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(BOQueryMasterDetailView.class.getName());
    private ExplorerManager detailManager;
    private BOEditorButtons editButtons;
    

    /** Creates a new instance of BOQueryMasterDetailView */
    public BOQueryMasterDetailView(ExplorerManager masterManager, BONode boNode, Class bundleResolver) {
        super(masterManager, boNode, bundleResolver);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            refreshDetail((Node[]) evt.getNewValue());
        }
    }

    protected void refreshMaster(){
        
    }
    
    protected void refreshDetail(Node[] nodes) {
//    synchronized(semaphore){

        CursorControl.showWaitCursor();
        try {
            BasicEntity e;
            BOEntityNodeReadOnly entityNode = null;

            if (nodes != null && nodes.length > 0) {
                try {
                    // if more than one node is selected, take the last
                    Node n = nodes[nodes.length - 1];
                    BOQueryNode parent = null;

                    if (n.getParentNode() instanceof BOQueryNode) {
                        parent = (BOQueryNode) n.getParentNode();
                    }
                    if (n == null) {
                        log.warning("Null Node cannot be updated");
                        return;
                    } else if (n instanceof BOMasterNode) {
                        if (parent == null) {
                            e = null;
                        } else {
                            e = parent.update(((BOMasterNode) n).getId());
                        }
                        if (e == null) {
                            entityNode = null;
                            this.detailManager.setSelectedNodes(new Node[]{});
                        } else {
                            entityNode = new BOEntityQueryNode(e);
                            this.detailManager.setRootContext(entityNode);
                            this.detailManager.setSelectedNodes(new Node[]{entityNode});
                        }
                    } else if (n instanceof BOEntityQueryNode) {
                        entityNode = (BOEntityQueryNode) n;
                        this.detailManager.setRootContext(entityNode);
                        this.detailManager.setSelectedNodes(new Node[]{entityNode});
                    } else if (n instanceof BOBeanNode) {
                        BOEntityNodeReadOnly boBeanNode = (BOEntityNodeReadOnly) n;
                        entityNode = new BOEntityQueryNode(boBeanNode.getBo());
                        this.detailManager.setRootContext(entityNode);
                        this.detailManager.setSelectedNodes(new Node[]{entityNode});
                    }
                    
                    if(entityNode != null){
                        getDetailCommentArea().setText(entityNode.getBo().getAdditionalContent());
                    }
                    
                } catch(BusinessObjectRemovedException ore){
                    ExceptionAnnotator.annotate(ore);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, t.getMessage(), t);
                }
            } else {
                log.warning("no nodes retrieved");
                this.detailManager.setRootContext(new AbstractNode(Children.LEAF));
            }
        } finally {
            log.info("Manager # of nodes (refreshdetail) " + getExplorerManager().getRootContext().getChildren().getNodes().length);
            CursorControl.showNormalCursor();
        }
//    }
    }

    public void refresh() {
//    synchronized(semaphore){
        refreshMaster();
        Node[] nodes;
        nodes = getExplorerManager().getSelectedNodes();
        refreshDetail(nodes);
//        updateCounter();
//    }
    }

    @Override
    protected JPanel createDetailPanel() {
        ProviderPanel p = new ProviderPanel();
        this.detailManager = p.getExplorerManager();
//    this.detailManager.setRootContext(getExplorerManager().getRootContext());
        return p;
    }

    public BOEditorButtons getEditButtons() {
        return editButtons;
    }

    static class ProviderPanel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {

        ExplorerManager mgr;
        Lookup lookup;

        ProviderPanel() {
            ActionMap map = getActionMap();
            InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            lookup = ExplorerUtils.createLookup(getExplorerManager(), map);
        }

        public ExplorerManager getExplorerManager() {
            if (mgr == null) {
                mgr = new ExplorerManager();
            }
            return mgr;
        }

        public Lookup getLookup() {
            return lookup;
        }
    }

    public void addEditButtons() {

        editButtons = new BOEditorButtons(boNode, ((ProviderPanel) getDetailPanel()).getLookup());
        getDetailPanel().add(getEditButtons(), BorderLayout.SOUTH);
    }

    public void removeEditButtons() {
        if (getEditButtons() != null) {
            getDetailPanel().remove(getEditButtons());
        }
        editButtons = null;
    }

    public ExplorerManager getDetailManager(){
        return detailManager;
    }
    public ExplorerManager getMasterManager(){
        return getExplorerManager();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
//    log.info("property change:" + evt.getSource());
//    log.info("property change:" + evt.getNewValue());
    }
}
