/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.component;

import de.linogistix.wmsprocesses.processes.order.gui.gui_builder.AbstractCenterPanel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.BusinessExceptionResolver;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ClientItemDataLotFilteringComponent;
import de.linogistix.inventory.gui.component.controls.ItemDataComboBoxModel;
import de.linogistix.inventory.gui.component.controls.LotComboBoxModel;
import de.linogistix.los.inventory.businessservice.OrderCannotBeStarted;
import de.linogistix.los.inventory.facade.OrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.wmsprocesses.processes.order.gui.object.OrderItem;
import de.linogistix.wmsprocesses.processes.order.gui.object.OrderTypeItem;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class CenterPanel extends AbstractCenterPanel {

    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
    private final static Logger log = Logger.getLogger(CenterPanel.class.getName());
    TopComponentPanel topComponentPanel;
    ClientItemDataLotFilteringComponent cilComp;
     
    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        
        getDeliveryDateTextField().setEnabled(true);
        getDeliveryDateTextField().setMandatory(true);
        getDeliveryDateTextField().getTextFieldLabel().setTitleText(
                NbBundle.getMessage(WMSProcessesBundleResolver.class,"DATE_OF_ALLOCATION"));
                
        getAmountTextField().setEnabled(false);
        getAmountTextField().setMinimumValue(new BigDecimal(0), false);
        getAmountTextField().setMandatory(true);
        getAmountTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Amount"));
        
        getCommentLabel().setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Comment"));
        
    }

    private void initAutofiltering(){
        
        getClientComboBox().setEnabled(true);
        getClientComboBox().setMandatory(true);
        getClientComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"client"));
        
        getItemDataComboBox().setEnabled(true);
        getItemDataComboBox().setMandatory(true);
        getItemDataComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"itemData"));
        
        getItemDataComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
               
                ItemData selItemData = getItemDataComboBox().getSelectedAsEntity();

                if(selItemData != null){
                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getHandlingUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
                else {
                    getAmountTextField().setEnabled(false);
                }
                
            }
        });
        
        getLotComboBox().setMandatory(false); 
        getLotComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "lot"));
        getLotComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                
                if(getLotComboBox().getSelectedAsEntity() == null){
                    return;
                }
                
                ItemData selItemData = getLotComboBox().getSelectedAsEntity().getItemData();

                if(selItemData != null){
                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getHandlingUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
                else {
                    getAmountTextField().setEnabled(false);
                }
                
            }
        });
        
        try{
            cilComp = new ClientItemDataLotFilteringComponent(getClientComboBox(), getItemDataComboBox(), getLotComboBox());
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }    
        
    }
    
    @Override
    public void componentOpened() {
        super.componentOpened();
        
        initOrderTreeTable();
        
        initAutofiltering();      
        
        processTargetplace();
        
        this.requestFocus();
        
        invalidate();
        getParent().invalidate();
        
        clear();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        treeTable.repaint();
        getClientComboBox().requestFocus();
    }

    
    void clear() {

        
        orderNumberTextField.setText("");
        labelTextField.setText("");
        documentTextField.setText("");
        // ---------------
        
        getDeliveryDateTextField().setText("");
        
        getCommentArea().setText("");
        
        setOrderDetailsEnabled(true);
        
        clearPositionDetail();

        processTargetplace();
        treeTable.clear();
        treeTable.repaint();
        
        initDefaults();
        getClientComboBox().requestFocus();
    }
    
    private void clearPositionDetail() {
        
        getAmountTextField().setValue(new BigDecimal(0));
        getAmountTextField().setEnabled(false);
 
        BODTO<Client> clientTO = getClientComboBox().getSelectedItem();
        
        getLotComboBox().clear();
        ((LotComboBoxModel)getLotComboBox().getComboBoxModel()).setClientTO(clientTO);
        
        getItemDataComboBox().clear();
        ((ItemDataComboBoxModel)getItemDataComboBox().getComboBoxModel()).setClientTO(clientTO);
        
    }
    
    private void setOrderDetailsEnabled(boolean enabled){
        
        getClientComboBox().setEnabled(enabled);
        getDeliveryDateTextField().setEnabled(enabled);
        documentTextField.setEnabled(enabled);
        targetplaceComboBox.setEnabled(enabled);
        orderTypeCombo.setEnabled(enabled);
        orderNumberTextField.setEnabled(enabled);
        labelTextField.setEnabled(enabled);
    }

    private void processTargetplace() {
        targetplaceComboBox.removeAllItems();
        try {
            OrderFacade r = (OrderFacade) loc.getStateless(OrderFacade.class);
            List<BODTO<LOSStorageLocation>> items = r.getGoodsOutLocations();
            if (items != null) {
                for (BODTO pos : items) {
                    targetplaceComboBox.addItem(pos.getName());
                }
            }
        } catch (LOSLocationException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    protected void addButtonActionPerformedListener(java.awt.event.ActionEvent evt) {

        if(!getClientComboBox().checkSanity()
           || !getLotComboBox().checkSanity()
           || !getItemDataComboBox().checkSanity()
           || !getAmountTextField().checkSanity())
        {
            return;
        }
        
        String selectedItem = getItemDataComboBox().getSelectedItem().getName();
        if( selectedItem != null ) {
            ItemData item = getItemDataComboBox().getSelectedAsEntity();
            if( item.getLock() != 0 ) {
                selectedItem = "* "+selectedItem;
            }
        }
        String selectedLot = "";
        if(getLotComboBox().getSelectedItem() != null){
            selectedLot = getLotComboBox().getSelectedItem().getName();
            Lot lot = getLotComboBox().getSelectedAsEntity();
            if( lot != null && lot.getLock() != 0 ) {
                selectedLot = "* " + selectedLot;
            }
        }
        
        if(treeTable.contains(selectedItem, selectedLot)){
            
            FacadeException fex = new FacadeException("Duplicate position for item", 
                                                      "ORDER_DUPLICATE_POSITION", 
                                                      new Object[]{selectedItem, selectedLot});
            fex.setBundleResolver(WMSProcessesBundleResolver.class);
            
            ExceptionAnnotator.annotate(fex);
            
            return;
        }
        
        int pos;
        if (treeTable.getExplorerManager().getRootContext() != null){
        Node[] nodes = treeTable.getExplorerManager().getRootContext().getChildren().getNodes();
            if (nodes != null && nodes.length > 0){
                pos = nodes.length + 1;
            } else{
                pos = 1;
            }
        } else{
            pos = 1;
        }
        try {
            treeTable.addRow("" + pos, 
                             selectedLot, 
                             selectedItem,
                             getAmountTextField().getValue().toString());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        setOrderDetailsEnabled(false);
        
        clearPositionDetail();
    }

    @Override
    protected void delButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        treeTable.delSelectedRows();
    }
    
    public void process(boolean processAutomaticly) {
        CursorControl.showWaitCursor();
        try {
            OrderFacade orderFacade = (OrderFacade) loc.getStateless(OrderFacade.class);
            List<OrderItem> items = treeTable.getOrderItems();
            if (items.size() < 1) {
                
                FacadeException fex = new FacadeException("No order position", 
                                                         "ERROR_NO_ORDER_POSITION", 
                                                         new Object[0]);
                
                fex.setBundleResolver(WMSProcessesBundleResolver.class);
                
                ExceptionAnnotator.annotate(fex);
                
                return;
                
            }
            
            OrderPositionTO[] tos = new OrderPositionTO[items.size()];
            int i = 0;
            String clientStr = getClientComboBox().getSelectedItem().getName();
            for (OrderItem item : items) {
                OrderPositionTO to = new OrderPositionTO();
                to.amount = new BigDecimal(item.getAmount());
                String val = item.getArticel();
                if( val != null && val.startsWith("* ") )
                    val = val.substring(2);
                to.articleRef = val;
                val = item.getPrintnorm();
                if( val != null && val.startsWith("* ") )
                    val = val.substring(2);
                to.batchRef = val;
                to.clientRef = clientStr;
                tos[i] = to;
                i++;
            }

            OrderType orderType = ((OrderTypeItem) orderTypeCombo.getSelectedItem()).t;
            
            orderFacade.order(clientStr, 
                              orderNumberTextField.getText(), 
                              tos, 
                              documentTextField.getText(), 
                              labelTextField.getText(), 
                              targetplaceComboBox.getSelectedItem().toString(), 
                              orderType,
                              getDeliveryDateTextField().getDate(),
                              processAutomaticly,
                              getCommentArea().getText());
            clear();
            
        } catch (OrderCannotBeStarted ex) {
            ErrorManager em = ErrorManager.getDefault();
            String ann = BusinessExceptionResolver.resolve(ex, null);
            em.annotate(ex, ann);
            em.notify(ErrorManager.WARNING,ex);
            clear();
            
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally{
            CursorControl.showNormalCursor();
        }
    }

    @Override
    protected  OrderTypeItem[] initOrderTypeCombo() {
//        OrderTypeItem[] ret = new OrderTypeItem[OrderType.values().length-1];
//        int i = 0;
//        for (OrderType t : OrderType.values()) {
//            
//            switch(t){
//                case TO_EXTINGUISH : continue;
//                case TO_REPLENISH : continue;
//                default: ret[i++] = new OrderTypeItem(t);
//            }
//        }
        List<OrderTypeItem> otList = new ArrayList<OrderTypeItem>();
        for (OrderType t : OrderType.values()) {
            
            switch(t){
                case TO_EXTINGUISH : continue;
                case TO_REPLENISH : continue;
                case TO_OTHER_SITE : continue;
                case TO_PRODUCTION : continue;
                default: otList.add( new OrderTypeItem(t) );
            }
        }
        OrderTypeItem[] ret = new OrderTypeItem[otList.size()];
        int i = 0;
        for (OrderTypeItem t : otList) {
            ret[i++] = t;
        }
        return ret;
    }
    
    /** write default-values and default-selections to the fields */
    private void initDefaults() {
        
        if( getClientComboBox().getSelectedItem() == null ) {
            LoginService login = Lookup.getDefault().lookup(LoginService.class);
            String clName = login.getUsersClientNumber();
            getClientComboBox().getAutoFilteringComboBox().addItem(clName);
        }
        
        try {
            LOSSystemPropertyServiceRemote propertyFacade = loc.getStateless(LOSSystemPropertyServiceRemote.class);
            
            String locationName = propertyFacade.getString(loc.getWorkstationName(), LOSInventoryPropertyKey.DEFAULT_GOODS_OUT_LOCATION_NAME);
            if( locationName != null ) {
                int max = targetplaceComboBox.getItemCount();
                for( int i = 0; i<max; i++ ) {
                    String str = (String)targetplaceComboBox.getItemAt(i);
                    if( locationName.equals(str) ) {
                        targetplaceComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            String typeName = propertyFacade.getString(loc.getWorkstationName(), LOSInventoryPropertyKey.DEFAULT_GOODS_OUT_TYPE_NAME);
            if( typeName != null ) {
                int max = orderTypeCombo.getItemCount();
                for( int i = 0; i<max; i++ ) {
                    OrderTypeItem oti = (OrderTypeItem)orderTypeCombo.getItemAt(i);
                    if( typeName.equals(oti.toString()) ) {
                        orderTypeCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
        } catch (Throwable ex) { 
            System.out.println("Exception initDefaults: " + ex.toString());
            ex.printStackTrace();
        }
        
        getDeliveryDateTextField().setDate( new Date() );

    }
}
