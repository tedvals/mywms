/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.picking.chooseOrder.gui.bean;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.picking.chooseOrder.NavigationEnum;

/**
 *
 * @author artur
 */
public class CenterBean extends BasicBackingBean {
    
    String mandant;
    String created;
    String order;
    String position;
    boolean forwardButtonEnabled = true;

    private String notifyNothingToDo() {
    	 List<String> buttonTextList = new ArrayList<String>();
         buttonTextList.add(resolve("Ok", new Object[]{}));
         NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("NOTHING_TODO_MESSAGE"), buttonTextList);
         return n.setCallbackListener(new ButtonListener() {
             public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                 return NavigationEnum.picking_chooseOrder_CenterPanel.toString();
             }
         });       
    }
    
    public String forwardActionPerformedListener() {

    	
    	ComboBoxBean comboBoxBean = JSFHelper.getInstance().getSessionBean(ComboBoxBean.class);
    	LOSPickRequest r = comboBoxBean.getPickingRequest();
    	if (r.getPositions() == null || r.getPositions().size() == 0){
    		return notifyNothingToDo();
    	} else{
    		accept(r);
    		return NavigationEnum.picking_scanOrder_CenterPanel.toString();
    	}
        
    }
    
    private void accept(LOSPickRequest r) {
        try {
            PickOrderFacade pof = getStateless(PickOrderFacade.class);
            pof.accept(r);
        } catch (PickingException ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");            
        } catch (BusinessObjectNotFoundException ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");            
        } catch (BusinessObjectSecurityException ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");            
        } catch (Throwable ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");            
        }
    }
                  
    public String cancelActionPerformedListener() {  
        return NavigationEnum.controller_CenterPanel.toString();   
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMandant() {
        return mandant;

    }

    public void setMandant(String mandant) {
        this.mandant = mandant;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;        
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isForwardButtonEnabled() {
        return forwardButtonEnabled;
    }

    public void setForwardButtonEnabled(boolean forwardButtonEnabled) {        
        this.forwardButtonEnabled = forwardButtonEnabled;
    }

    
    public void resetBean() {
//        JSFHelper.getInstance().resetBean(ComboBoxBean.class);
//        JSFHelper.getInstance().resetBean(de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.CenterBean.class);
//        JSFHelper.getInstance().resetBean(de.linogistix.mobile.processes.picking.scanOrder.gui.bean.CenterBean.class);        
//        JSFHelper.getInstance().resetBean(de.linogistix.mobile.processes.picking.outputPlace.gui.bean.CenterBean.class);                
/*        JSFHelper.getInstance().resetBean(BeanEnum.picking_chooseOrder_ComboBoxBean.toString());
        JSFHelper.getInstance().resetBean(BeanEnum.picking_chooseOrder_CenterBean.toString()); 
        JSFHelper.getInstance().resetBean(BeanEnum.picking_stockunit_CenterBean.toString());                 */
    }
}
