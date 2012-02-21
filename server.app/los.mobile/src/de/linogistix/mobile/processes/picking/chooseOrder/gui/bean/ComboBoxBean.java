/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.picking.chooseOrder.gui.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.picking.chooseOrder.NavigationEnum;



/**
 * 
 * @author artur
 */
public class ComboBoxBean extends BasicDialogBean {

    private String selectedNumber;
    private List<SelectItem> numbers = new ArrayList<SelectItem>();
    LOSPickRequest pickingRequest;
    Hashtable<String, PickingRequestTO> h = new Hashtable<String, PickingRequestTO>();
    boolean submit;

    public ComboBoxBean() {
        // dummyComboBoxFill();
    }

	public String getNavigationKey() {
		return NavigationEnum.picking_chooseOrder_CenterPanel.name();
	}
	
	public String getTitle() {
		return resolve("Picking");
	}

    public boolean isSubmit() {
        setComboBoxSrv();
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }
    
    private void setComboBoxSrv() {
        PickOrderFacade pof = getStateless(PickOrderFacade.class);
        CenterBean centerBean = JSFHelper.getInstance().getSessionBeanForce(
                CenterBean.class);
        numbers.clear();
        h.clear();
        try {
            List<PickingRequestTO> pickRequestList = pof.getRawPickingRequest();
            // throw new ValidatorException(new FacesMessage("sdfas"));
            if (pickRequestList != null && (pickRequestList.isEmpty() == false)) {
                numbers = new ArrayList<SelectItem>(pickRequestList.size());
                int zaehler = 0;
                for (PickingRequestTO to : pickRequestList) {
                    zaehler++;
                    h.put(String.valueOf(zaehler), to);
                    if (to.orderRef != null && to.orderRef.length()>0) {
	                    String displaname = to.getName() + " (" + to.orderRef +","+ to.state + ")";
                    	displaname = displaname.replaceAll("\\s0+", ""); 
                    	numbers.add(new SelectItem(String.valueOf(zaehler),
                                displaname));
                    } else {
                    	String displaname = to.getName() + " (" + to.state + ")";
                    	displaname = displaname.replaceAll("\\s0+", " "); 
                        numbers.add(new SelectItem(String.valueOf(zaehler), 
                        		displaname));
                    }
                }
                if (centerBean.getMandant() == null) {
                    processPickingRequest(pickRequestList.get(0));
                }
                centerBean.setForwardButtonEnabled(true);
            } else {
                centerBean.setForwardButtonEnabled(false);
            }
        } catch (Throwable ex) {
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE,
                    null, ex);
            JSFHelper.getInstance().message("InternalError");
        }
    }

    public List<SelectItem> getNumbers() {
        return numbers;
    }

    public LOSPickRequest getPickingRequest() {
        return pickingRequest;
    }

    public void setPickingRequest(LOSPickRequest pickingRequest) {
        this.pickingRequest = pickingRequest;
    }

    public void setNumbers(List<SelectItem> numbers) {
        this.numbers = numbers;
    }

    public String getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    private LOSPickRequest getPickingRequestSvr(PickingRequestTO pickingRequest) {
        try {
            PickOrderFacade pof = getStateless(PickOrderFacade.class);
            LOSPickRequest request = pof.loadPickingRequest(pickingRequest);
            return request;
        } catch (BusinessObjectNotFoundException ex) {
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE,
                    null, ex);
            JSFHelper.getInstance().message("InternalError");
        } catch (BusinessObjectSecurityException ex) {
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE,
                    null, ex);
            JSFHelper.getInstance().message("InternalError");
        } catch (Throwable ex) {
            Logger.getLogger(ComboBoxBean.class.getName()).log(Level.SEVERE,
                    null, ex);
            JSFHelper.getInstance().message("InternalError");
        }
        return new LOSPickRequest();
    }

    private void processPickingRequest(PickingRequestTO to) {
        // CenterBean managedBean = (CenterBean)
        // JSFHelper.getInstance().getSessionBeanForce(BeanEnum.picking_chooseOrder_CenterBean.toString(),new
        // CenterBean());
        CenterBean managedBean = (CenterBean) JSFHelper.getInstance().getSessionBeanForce(
                de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.CenterBean.class);
        LOSPickRequest pickingRequest = getPickingRequestSvr(to);
        if (pickingRequest != null) {
            setPickingRequest(pickingRequest);
            if (pickingRequest.getClient() != null && pickingRequest.getClient().getNumber() != null) {
                managedBean.setMandant(pickingRequest.getClient().getNumber());
            }
            if (pickingRequest.getCreated() != null) {
                DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                managedBean.setCreated(formatter.format(pickingRequest.getCreated()));
            }
            if (pickingRequest.getParentRequest() != null) {
                managedBean.setOrder(pickingRequest.getParentRequest().toUniqueString());
            }
            if (pickingRequest.getPositions() != null) {
            	managedBean.setPosition(String.valueOf(pickingRequest.getPositions().size()));
            }
        }
    }

    public void valueChanged(ValueChangeEvent vce) {
        
        PickingRequestTO pickingRequest = h.get(vce.getNewValue());
        processPickingRequest(pickingRequest);
    }
}
