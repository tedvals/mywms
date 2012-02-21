/*
 * LoginBean.java
 *
 * Created on 29. April 2007, 21:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.linogistix.mobile.processes.picking.outputPlace.gui.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.ComboBoxBean;
import de.linogistix.mobile.processes.picking.scanOrder.NavigationEnum;


/**
 *
 * @author trautm
 */
public class CenterBean extends BasicBackingBean {

    String order;
    String articel;
    String storage;
    String storageTextField;
    String amount;
    String detail;
    String amountTextField;
    String position;
    String positionStatus;
    
    private de.linogistix.mobile.processes.picking.scanOrder.gui.bean.CenterBean scanOrderCenterBean;
    //----------------------------------------------------------------------------
    public void setArticel(String articel) {
        this.articel = articel;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setStorage(String storageplace) {
        this.storage = storageplace;
    }

    public String getOrder() {
        ComboBoxBean managedBean = JSFHelper.getInstance().getSessionBean(ComboBoxBean.class);
        if (managedBean.getPickingRequest().getParentRequest() != null) {
            return managedBean.getPickingRequest().getParentRequest().toUniqueString();
        } else {
            return "";
        }
    }

//    public String getArticel() {
//        return "23.434.43534";
//    }

    public String getStorage() {
        try {
        	PickOrderFacade pof = getStateless(PickOrderFacade.class);
            LOSStorageLocation loc = pof.getDestination(getPickingRequest());
            return loc.toUniqueString();
        } catch (PickingException ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");            
        } catch (Throwable ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            JSFHelper.getInstance().message("InternalError");                        
        }
        return "";
    }

    public String getAmount() {
        return "12";
    }

    public String getPosition() {
//        return position;
        return "1/3";
    }

    public String getPositionStatus() {
//        return positionStatus;
        return "Status : anfgefangen";
    }

    public void setPositionStatus(String postitionStatus) {
        this.positionStatus = postitionStatus;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAmountTextField() {
        return amountTextField;
    }

    public void setAmountTextField(String amountTextField) {
        this.amountTextField = amountTextField;
    }

    public String getStorageTextField() {
        return storageTextField;
    }

    public void setStorageTextField(String storageTextField) {
        this.storageTextField = storageTextField;
    }

    public String getDetail() {
        return resolve("Detail", new Object[]{});
    }

    public String forwardActionPerformedListener() {
        if (isUnitLoad()) {
            return NavigationEnum.picking_scanOrder_CenterPanel.toString();
        }
        return "";
    }

    public String backwardActionPerformedListener() {
        return "";
    }

    public String cancelActionPerformedListener() {
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_CANCEL_MESSAGE"), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    return NavigationEnum.controller_CenterPanel.toString();
            }
            });

    }

    private LOSPickRequest getPickingRequest() {
        ComboBoxBean managedBean = JSFHelper.getInstance().getSessionBean(ComboBoxBean.class);
        return managedBean.getPickingRequest();
    }

    private boolean isCompleteAll() {
        scanOrderCenterBean = JSFHelper.getInstance().getSessionBean(de.linogistix.mobile.processes.picking.scanOrder.gui.bean.CenterBean.class);
        return scanOrderCenterBean.isCompleteAll();
    }
    
    private boolean isFinished() {
        scanOrderCenterBean = JSFHelper.getInstance().getSessionBean(de.linogistix.mobile.processes.picking.scanOrder.gui.bean.CenterBean.class);
        return scanOrderCenterBean.isFinished();
    }

    private boolean isUnitLoad() {
        return false;
    }

    private void processFinish() throws FacadeException {

        PickOrderFacade pof = getStateless(PickOrderFacade.class);
        if (isCompleteAll()) {
            pof.finishPickingRequest(getPickingRequest(), storageTextField);
        } else {
            pof.finishCurrentUnitLoad(getPickingRequest(), storageTextField);
        }

    }

/*    public String finishActionPerformedListener() {
        try {
            processFinish();
        } catch (FacadeException ex) {
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE, null, ex);
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId) {
                    return NavigationEnum.picking_outputPlace_CenterPanel.toString();
                }
            });
            
        }
        if (isCompleteAll()) {
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_SUCCESS_MESSAGE"), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId) {
                    return NavigationEnum.controller_CenterPanel.toString();
                }
                });
        }
        return NavigationEnum.picking_scanOrder_CenterPanel.toString();
    }*/
    
   public String finishActionPerformedListener() {
        try {           
            processFinish();
            // reload of the Pickingrequest
            // TODO auslagern der Datenkapsel Pickingrequest in einen Bean, die ueber einen zustandsautomaten den Workflow haelt
            scanOrderCenterBean = JSFHelper.getInstance().getSessionBean(de.linogistix.mobile.processes.picking.scanOrder.gui.bean.CenterBean.class);
            scanOrderCenterBean.isReset();         
            storageTextField = "";
        } catch (FacadeException ex) {
            storageTextField = "";            
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE, null, ex);
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(getLocale()), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    return NavigationEnum.picking_outputPlace_CenterPanel.toString();
                }
            });
            
        } 
        if (isFinished()) {
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_SUCCESS_MESSAGE"), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    return NavigationEnum.controller_CenterPanel.toString();
                }
                });
        } else{
        	return NavigationEnum.picking_scanOrder_CenterPanel.toString();
        }
    }    

    public String validateLogin() {
        if (storageTextField == null || storageTextField.length() == 0) {
            message("Please fill all fields", new Object[]{}, null);
        } else if (amountTextField == null || amountTextField.length() == 0) {
            message("Please fill all fields", new Object[]{}, null);
//         StateEnum.FORWARD.toString();
        }
        return "";
    }
}
