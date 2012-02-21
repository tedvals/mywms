/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.editor.PropertiesComboBoxModel;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.bobrowser.query.BOQueryPanel;
import de.linogistix.common.bobrowser.query.gui.gui_builder.AbstractBOQueryHeaderPanel;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.gui.component.other.NumericDocument;
import de.linogistix.common.gui.layout.WrapFlowLayout;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class BOQueryHeaderPanel extends AbstractBOQueryHeaderPanel implements ProviderChangeEventListener {

    private static final Logger log = Logger.getLogger(BOQueryHeaderPanel.class.getName());
    /** path to the icon used by the component and its open action */
    private boolean sortAscending = true;
    private int startResultIndex = 0;
    private long resultTotalSize;
    private int resultsPerPage;
    private int selectedPage = 1;
    private BOQueryNode bOQueryNode;
    private BOQueryPanel panel;
    private boolean initialized = false;
    boolean toggleDetail = true;
    
    public BOQueryHeaderPanel(BOQueryPanel panel, BOQueryNode bOQueryNode) {
        super();

        this.bOQueryNode = bOQueryNode;
        this.panel = panel;
        initOrderByComboBox();
        initQueryServices();

        buttonPanel.setLayout(new WrapFlowLayout(java.awt.FlowLayout.LEFT));
        limitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"25", "50", "100", "200", "500", NbBundle.getMessage(CommonBundleResolver.class, "all")}));
        limitComboBox.setSelectedIndex(0);
        resultsPerPage = Integer.parseInt((String) limitComboBox.getSelectedItem());
        pageTextField.setDocument(new NumericDocument());
        pageTextField.setText("1");

        initialized = true;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public int getStartResultIndex() {
        return startResultIndex;
    }

    @Override
    public void providerChangeEvent(EventObject evt) {
        
        BOQueryComponentProvider provider = null;

        try {
            queryPanel.removeAll();
            queryPanel.invalidate();
            
            if (!(queries.getSelectedItem() instanceof BOQueryComponentProvider)) {
                log.severe("No BOQueryComponentProvider");
                return;
            }

            provider = (BOQueryComponentProvider) queries.getSelectedItem();

            if (provider == null) {
                log.warning("No Provider");
                return;
            }

            provider.setBOQueryNode(bOQueryNode);
            provider.setProviderChangeEventListener(this);

            switch (provider.getDockingMode()) {
                case PLAIN:
                    break;
                case INLPLACE:
                    throw new OperationNotSupportedException("Not yet implemented");
                case QUERYPANEL:
                    queryPanel.add(provider.createComponent(), BorderLayout.CENTER);
                    bOQueryNode.getModel().setProvider(provider);
                    break;
                case WIZARD_GENERIC:
                    throw new OperationNotSupportedException("Not yet implemented");
                case WIZARD_OWN:
                    final WizardDescriptor w = provider.createWizard();
                  
                    SwingUtilities.invokeLater(new Runnable() {
                        
                        public void run() {
                            showWIZARD_OWN(w);
                        }
                    });
                    
                default:
                    // nothing to do
                    return;
            }
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            ExceptionAnnotator.annotate(t);
        } finally {
            if (provider != null && initialized) {
                bOQueryNode.getModel().setProvider(provider);
                if(provider.getDockingMode() != DockingMode.WIZARD_OWN){
                    reload();
                }
            } else {
                log.warning("No Provider or not initialized yet");
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                        
                public void run() {

                    queryPanel.invalidate();
                    panel.newLayout();
                }
            });
            
        }
    }
    
    private void showWIZARD_OWN(WizardDescriptor wizard){
        
        Dialog d = DialogDisplayer.getDefault().createDialog(wizard);
        d.setVisible(true);
        
        if (wizard.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                        //OK
        } else if (wizard.getValue().equals(NotifyDescriptor.CANCEL_OPTION)) {
            queries.setSelectedItem(bOQueryNode.getModel().getDefaultBOQueryProvider());
            providerChangeEvent(null);
        }
        
        reload();
    }
    
    @Override
    public void reload() {
        panel.reload();
        updatePageInfo();

    }

    @Override
    public void orderByItemChangeEvent() {
        panel.reload();
    }

    @Override
    public void toggleSortMode() {
        if (sortAscending) {
            jButtonOrderIcon.setToolTipText(
                    "<HTML><u>" + NbBundle.getMessage(CommonBundleResolver.class, "descending") + "</u></HTML>");
            jButtonOrderIcon.setIcon(sortDescIcon);
            sortAscending = false;

        } else {
            jButtonOrderIcon.setToolTipText(
                    "<HTML><u>" + NbBundle.getMessage(CommonBundleResolver.class, "ascending") + "</u></HTML>");
            jButtonOrderIcon.setIcon(sortAscIcon);
            sortAscending = true;
        }
        reload();
    }

    @Override
    public void setFirstPage() {
        if (isPageCorrect()) {
            startResultIndex = 0;
            pageTextField.setText("1");
            reload();
        }
    }

    @Override
    public void setBackPage() {
        if (isPageCorrect()) {
            if (getSelectedPage() > 1) {
                int page = getSelectedPage() - 1;
                startResultIndex = (page - 1) * resultsPerPage;
                pageTextField.setText(String.valueOf(page));
                reload();
            }
        }
    }

    @Override
    public void setNextPage() {
        if (isPageCorrect()) {
            if (getSelectedPage() < getPageCount()) {
                int page = getSelectedPage() + 1;
                startResultIndex = (page - 1) * resultsPerPage;
                pageTextField.setText(String.valueOf(page));
                reload();
            }
        }
    }

    @Override
    public void setPage(int i) {
        if (isPageCorrect()) {
            startResultIndex = (i - 1) * resultsPerPage;
            reload();
        }
    }

    @Override
    public void setLastPage() {
        if (isPageCorrect()) {
            int pageCount = getPageCount();
            startResultIndex = (pageCount - 1) * resultsPerPage;
            pageTextField.setText(String.valueOf(pageCount));
            reload();
        }
    }

    @Override
    public void setLimitPerPage(int i) {
        selectedPage = 1;
        startResultIndex = 0;
        pageTextField.setText("1");
        resultsPerPage = i;
        reload();
    }

    @Override
    public int getPageCount() {
        int pageCount = (int) (resultTotalSize / resultsPerPage);
        int lastPageSize = (int) (resultTotalSize % resultsPerPage);
        if (lastPageSize > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public void initOrderByComboBox() {
        BOEntityNodeReadOnly boBeanNode;
        ComboBoxModel comboBoxModel = null;

        try {
            boBeanNode = (BOEntityNodeReadOnly) bOQueryNode.getModel().getBoNode().getBoBeanNodeTemplate();
            comboBoxModel = new PropertiesComboBoxModel(boBeanNode);
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            ExceptionAnnotator.annotate(t);
        }

        getJComboBoxOrderBy().setModel(comboBoxModel);
        getJComboBoxOrderBy().revalidate();
    }

    //----------------------------------------------------------------------
    private boolean isPageCorrect() {
        String message = null;

        if (pageTextField.getText().trim().equals("")) {
            message = NbBundle.getMessage(CommonBundleResolver.class, "PAGE_EMPTY");
        }
        if ((getSelectedPage() < 1) || (getSelectedPage() > getPageCount())) {
            message = NbBundle.getMessage(CommonBundleResolver.class, "PAGE_NOT_EXIST", pageTextField.getText());
        }
        if (message != null) {
            NotifyDescriptor d = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            pageTextField.setText(String.valueOf(selectedPage));
            pageTextField.requestFocus();
            pageTextField.selectAll();
            return false;
        }
        return true;
    }

    public void pageNavigatorEnable() {
        firstPageButton.setEnabled(true);
        backPageButton.setEnabled(true);
        nextPageButton.setEnabled(true);
        lastPageButton.setEnabled(true);
        if (getSelectedPage() == getPageCount()) {
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
        }
        if (getSelectedPage() == 1) {
            firstPageButton.setEnabled(false);
            backPageButton.setEnabled(false);
        }
    }

    public void enableQueries(boolean enable) {
        this.queries.setEnabled(enable);
        this.reload.setEnabled(enable);
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void initQueryServices() {

        List<BOQueryComponentProvider> providerList;
        providerList = bOQueryNode.getModel().getQueryComponentProviders();
        
        try {
            for (BOQueryComponentProvider m : providerList) {
                queries.addItem(m);
            }
            if (queries.getItemCount() > 0) {
                queries.invalidate();
                queries.validate();
                queries.setSelectedIndex(0);

            } else {
                initialized = false;
                return;
            }
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
        }
        providerChangeEvent(null);
    }

    //------------------------------------------------------------------------
    public PropertiesComboBoxModel.BOBeanPropertyEntry getBOBeanPropertyEntry() {
        return (PropertiesComboBoxModel.BOBeanPropertyEntry) getJComboBoxOrderBy().getSelectedItem();
    }

    public void updatePageInfo() {
        if (bOQueryNode.getModel() != null) {
            resultTotalSize = bOQueryNode.getModel().getResultSetSize();
            pageLabel.setText("/ " + getPageCount());

        } else {
            resultTotalSize = 0;
            pageLabel.setText("/ " + 1);
        }
       
        int page = Integer.parseInt(pageTextField.getText());
        if (page > 1 && page > getPageCount() ) {
            log.warning("reset page navigation");
            startResultIndex = 0;
            pageTextField.setText("1");
            reload();
//            String message = NbBundle.getMessage(BundleResolver.class, "PAGE_NOT_EXIST", pageTextField.getText());
//            if (message != null) {
//                NotifyDescriptor d = new NotifyDescriptor.Message(message, NotifyDescriptor.INFORMATION_MESSAGE);
//                DialogDisplayer.getDefault().notify(d);
//            }
        }
        pageNavigatorEnable();
    }

    public void providerSelected(BOQueryComponentProvider prov) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void searchStringChanged(BOQueryComponentProvider prov, String searchStr) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reloadRequest() {
        reload();
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    public void toggleDetail() {
        panel.toggleDetail();  
    }
    
    //-------------------------------------------------------------------------


}
