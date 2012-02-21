/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.query.LOSStocktakingOrderQueryRemote;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mywms.globals.Role;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class StockTakingProcessController {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }
    
    J2EEServiceLocator loc;
    private StockTakingQueryTopComponent topComponent;
    LOSStocktakingOrderQueryRemote orderQuery;
    private boolean hasPermission = false;
    
    StockTakingProcessController(StockTakingQueryTopComponent pTopComponent) {
        this.topComponent = pTopComponent;
        loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        
        topComponent.getBOQueryPanel().getMasterDetailView().getDetailManager().
                addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node[] nodes = (Node[]) evt.getNewValue();
                    if (nodes != null && nodes.length > 0) {
                        onSelectionChanged((LOSStocktakingOrder) ((BOEntityNode) nodes[0]).getBo());
                    }
                    else {
                       topComponent.stockTakingProcessPanel.onSelectionCleared(); 
                    }
                }
            }
        });
        
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        hasPermission = login.checkRolesAllowed(getAllowedRoles());

    }
    
    void onSelectionChanged(final LOSStocktakingOrder order) {
        
        topComponent.stockTakingProcessPanel.onSelectionChanged(order);
        
        switch (order.getState()) {
            case COUNTED:
                topComponent.getFooterPanel().getRecountButton().setEnabled(true && hasPermission);
                topComponent.getFooterPanel().getFinishButton().setEnabled(true && hasPermission);
                break;
            case CANCELLED:
            case CREATED:
            case FINISHED:
            case FREE:
            case STARTED:
            default:
                topComponent.getFooterPanel().getRecountButton().setEnabled(false);
                topComponent.getFooterPanel().getFinishButton().setEnabled(false);
            
        }
    }
    

    public void processRecount() {
        
        if (getSelectedOrder() != null) {
            
            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

            try {
                LOSStocktakingFacade stFacade = loc.getStateless(LOSStocktakingFacade.class);
                stFacade.recountOrder( getSelectedOrder().getId() );
                topComponent.getBOQueryPanel().reload();
            } catch (Exception ex) {
                ExceptionAnnotator.annotate(ex);
            }
        } else {
            System.out.println("--- NO LOSStockTakingOrder SELECTED ---");
        }
        
    }
    
    public void processAccept() {
        
        if (getSelectedOrder() != null) {
            
            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

            try {
                LOSStocktakingFacade stFacade = loc.getStateless(LOSStocktakingFacade.class);
                stFacade.acceptOrder(getSelectedOrder().getId());
                topComponent.getBOQueryPanel().reload();
            } catch (Exception ex) {
                ExceptionAnnotator.annotate(ex);
            }
        } else {
            System.out.println("--- NO LOSStockTakingOrder SELECTED ---");
        }
        
    }

    private LOSStocktakingOrder getSelectedOrder() {
        Node[] nodes = topComponent.getBOQueryPanel().getMasterDetailView().getDetailManager().getSelectedNodes();
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("No Order selected");
        }
        BOEntityNode en = (BOEntityNode) nodes[0];
        LOSStocktakingOrder order = (LOSStocktakingOrder) en.getBo();
        return order;
        
    }
    
    public LOSStocktakingOrder getOrder(Long id) {
        LOSStocktakingOrder order = null;
        try {
            if (orderQuery == null) {
                orderQuery = loc.getStateless(LOSStocktakingOrderQueryRemote.class);
            }
            order = orderQuery.queryById(id);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (BusinessObjectNotFoundException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (BusinessObjectSecurityException ex) {
            ExceptionAnnotator.annotate(ex);
        }
        return order;
    }
    
    public StockTakingQueryTopComponent getTopComponent() {
        return topComponent;
    }
}
