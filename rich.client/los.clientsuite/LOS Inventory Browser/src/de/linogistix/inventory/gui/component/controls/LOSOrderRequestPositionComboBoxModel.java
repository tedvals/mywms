/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.gui.component.controls;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBoxModel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.inventory.model.LOSOrder;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.query.OrderRequestPositionQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LOSOrderRequestPositionComboBoxModel extends BOAutoFilteringComboBoxModel<LOSOrderRequestPosition>{

    private OrderRequestPositionQueryRemote positionQuery;
    
    private BODTO<LOSOrderRequest> orderTO;
    
    public LOSOrderRequestPositionComboBoxModel() throws Exception{
        super(LOSOrderRequestPosition.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        positionQuery = loc.getStateless(OrderRequestPositionQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<LOSOrderRequestPosition>> getResults(String searchString, QueryDetail detail) {
        LOSResultList<BODTO<LOSOrderRequestPosition>> resList;
        resList = positionQuery.autoCompletionByOrderRequest(searchString, orderTO, detail);
        return resList;
    }

    public BODTO<LOSOrderRequest> getOrderTO() {
        return orderTO;
    }
    
    public void setOrderTO(BODTO<LOSOrderRequest> orderTO) {
        this.orderTO = orderTO;
    }

    @Override
    public void clear() {
        super.clear();
        orderTO = null;
    }
    
}
