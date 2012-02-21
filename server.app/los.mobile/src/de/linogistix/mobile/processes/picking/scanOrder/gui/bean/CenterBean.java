/*
 * LoginBean.java
 *
 * Created on 29. April 2007, 21:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.linogistix.mobile.processes.picking.scanOrder.gui.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.pick.exception.NullAmountNoOtherException;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.exception.PickingExceptionKey;
import de.linogistix.los.inventory.pick.exception.PickingExpectedNullException;
import de.linogistix.los.inventory.pick.exception.PickingSubstitutionException;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.ComboBoxBean;
import de.linogistix.mobile.processes.picking.scanOrder.NavigationEnum;
import de.linogistix.mobile.res.BundleResolver;

/**
 *
 * @author trautm
 */
public class CenterBean extends BasicBackingBean {
	private static final Logger log = Logger.getLogger(CenterBean.class);

//    String order;
    String storageLocationTextField = "";
    String amountTextField = "";
    String amount;
    String detail;
    String position;
    String positionStatus;
    boolean nullpointerCheckBox = false;
    boolean takeWholeUnitLoadCheckBox = false;
    boolean reset;
    int currentPosition = 1;
    int totalPosition = 1;
    List<String> serials;
    boolean stockEmptyConfirmed = false;
    
    ComboBoxBean comboBoxBean = (ComboBoxBean) JSFHelper.getInstance().getSessionBean(ComboBoxBean.class);
    boolean emptyMessage;
    ProcessMode processMode = ProcessMode.STANDARD;
    private BigDecimal discoveredAmount;
    
//    HashMap<Integer, CacheObject> posCacheHashtable = new HashMap<Integer, CacheObject>();
    
    PickOrderFacade pof = getStateless(PickOrderFacade.class);
    LOSPickRequest pickingRequest;

    public CenterBean() {
    }

    
    /**
     * will be called by each load or reload of the site
     * @return
     */
    public boolean isReset() {
        
    	try {
            pickingRequest = pof.loadPickingRequest(comboBoxBean.getPickingRequest());
            totalPosition = pickingRequest.getPositions().size();
        } catch (BusinessObjectNotFoundException ex) {
            log.info(ex.getMessage(), ex);
            JSFHelper.getInstance().message("InternalError");                       
        } catch (BusinessObjectSecurityException ex) {
        	log.info(ex.getMessage(), ex);
            JSFHelper.getInstance().message("InternalError");                                   
        } catch (Throwable ex) {
        	log.info(ex.getMessage(), ex);            
            JSFHelper.getInstance().message("InternalError");                       
        }
        
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }


    public String getUnitLoad(){
    	
    	if(getCurrentObject() == null){
    		return "";
    	}
    	else{
    		return getCurrentObject().getStockUnit().getUnitLoad().getLabelId();
    	}
    }

//    public void setStorageLocation(String storageplace) {
//        this.storageLocation = storageplace;
//    }

    public String getOrder() {
        if (getPickingRequest().getParentRequest() != null) {
            return getPickingRequest().getParentRequest().toUniqueString();
        } else {
            return "";
        }
    }

    public boolean isAmountTextFieldEnable() {
//        if (isUnitLoad()) {
//            amountTextField = "";
//            return false;
//        }
        return !isComplete();
    }

    
    public boolean isStorageLocationTextFieldEnable() {
        return !isComplete();
    }

    private LOSPickRequestPosition getPickingRequestPosition() {
//        System.out.println("current Position = " + currentPosition);
        List<LOSPickRequestPosition> positionList = getPickingRequest().getPositions();
    	if( currentPosition > positionList.size() || currentPosition < 1 ) {
    		log.error("Reset wrong position pointer");
    		currentPosition = 1;
    	}
    	return positionList.get(currentPosition - 1);
    }

    private LOSPickRequest getPickingRequest() {
        return pickingRequest;
    }

    public String getArticel() {
        return getPickingRequestPosition().getItemData().getName();
    }

    public String getStorageLocation() {
        try {
            LOSStorageLocation loc = getPickingRequestPosition().getStorageLocation();
            System.out.println("getName = ");
            loc.getName();
            return loc.toUniqueString();                                                          
        } catch (Throwable ex) {
        	log.info(ex.getMessage(), ex);
            JSFHelper.getInstance().message("InternalError");                                                                       
        }
        return "";
    }

    public String getAmount() {
        
    	if (isUnitLoad()) {
            return String.valueOf(getPickingRequestPosition().getDisplayAmount()) 
            		+ "("
            		+ resolve("Whole palet")
            		+ ")";
        } else{
        	return String.valueOf(getPickingRequestPosition().getDisplayAmount());	
        }
    }

    public String getPosition() {
        totalPosition = getPickingRequest().getPositions().size();
        return currentPosition + "/" + totalPosition;
    }

    public String getPositionStatus() {
    	//TODO resource bundle
        return "anfgefangen";
    }

    public void setPositionStatus(String postitionStatus) {
        this.positionStatus = postitionStatus;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAmountTextField() {
        restore();
        return amountTextField;
    }

    public void setAmountTextField(String amountTextField) {
        this.amountTextField = amountTextField;
    }

    public String getStorageLocationTextField() {
        restore();
        return storageLocationTextField;
    }

    public void setStorageLocationTextField(String storageTextField) {
        this.storageLocationTextField = storageTextField;
    }

    public String getDetail() {
        return resolve("Detail");
    }

    private String notifyWithSubstitution(PickingSubstitutionException ex) {
    	String s = ex.getLocalizedMessage(getUIViewRoot().getLocale());
    	String q =  resolve("QUESTION_PROCEED", new Object[0]);
    	
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Yes", new Object[]{}));
        buttonTextList.add(resolve("No", new Object[]{}));
        
    	NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.QUESTION, s+"\n"+q, buttonTextList);
        return n.setCallbackListener(new ButtonListener() {
            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
            	switch(buttonId){
            	case 1: // Yes            		
            		processMode = ProcessMode.WITH_SUBSTITUTION;
            		return forwardActionPerformedListener(); //go on
            	default:
            		return NavigationEnum.picking_scanOrder_CenterPanel.toString(); // retry
            	}
            }
        });
    }
    
    private String notifyExpectedNull(PickingExpectedNullException ex) {
    	log.info(getUIViewRoot().getLocale().getLanguage());
    	String s = ex.getLocalizedMessage(getUIViewRoot().getLocale());
//    	String q =  resolve("QUESTION_PROCEED", new Object[0]);
    	String inputLabel = resolve("ProvideDiscoveredAmount");
    	String htmlMessage = s + "<br><br>" + inputLabel;
    	
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Forward", new Object[]{}));
        buttonTextList.add(resolve("Back", new Object[]{}));
    	NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INPUT, htmlMessage, buttonTextList, "0");
    	return n.setCallbackListener(new ButtonListener() {
			public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
            	switch(buttonId){
            	case 1: // Forward            		
            		processMode = ProcessMode.EXPECTED_NULL;
            		discoveredAmount = new BigDecimal(notifyDescriptorBean.getParam());
            		stockEmptyConfirmed = true;
            		if( BigDecimal.ZERO.compareTo(discoveredAmount) < 0 ) {
            			takeWholeUnitLoadCheckBox = false;
            		}
            		return forwardActionPerformedListener(); //go on
            	default: // Back
            		return NavigationEnum.picking_scanOrder_CenterPanel.toString(); // retry
            	}
            }
        });
    }
    
    private String notifySerialNo() {
    	if (serials == null) {
    		serials = new ArrayList<String>();
    	}
    	final int total = getCurrentObject().getPickedAmount().intValue();
    	
    	String q =  resolve("PICKING_SERIAL", new Object[]{""+(serials.size()+1),""+total});
    	
    	String inputLabel = resolve("serialNoLabel");
    	
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Forward", new Object[]{}));
    	
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INPUT, q +"<br><br>"+inputLabel+":", buttonTextList, "");
    	
        return n.setCallbackListener(new ButtonListener() {
			public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
            	switch(buttonId){
            	case 1: // Forward         
            		
            		String serialInput = notifyDescriptorBean.getParam();
            		
            		if(serialInput.length() == 0 || serialInput.startsWith(" ")){
            			FacadeException ex = new FacadeException("INPUT REQUIRED", 
            													 "SERIAL_INPUT_REQUIRED", 
            													 new Object[0], 
            													 "de.linogistix.mobile.res.Bundle");
            			ex.setBundleResolver(BundleResolver.class);
            			return notifyError(ex);
            		}
            		
            		if(serials.contains(notifyDescriptorBean.getParam())){
            			FacadeException ex = new FacadeException("DUPLICATE SERIAL", 
            													 "DUPLICATE_SERIAL", 
            													 new Object[0], 
            													 "de.linogistix.mobile.res.Bundle");
            			ex.setBundleResolver(BundleResolver.class);
            			return notifyError(ex);
            		}
            		
            		if( ! pof.isSerialNumberUnique(getCurrentObject().getItemData(), serialInput) ) {
            			FacadeException ex = new FacadeException("DUPLICATE SERIAL", 
								 "DUPLICATE_SERIAL", new Object[0], "de.linogistix.mobile.res.Bundle");
						ex.setBundleResolver(BundleResolver.class);
						return notifyError(ex);
            		}
            		
            		serials.add(notifyDescriptorBean.getParam());
            		if (serials.size() < total ){
            			return notifySerialNo(); // retry
            		} else {
            			processMode = ProcessMode.SERIAL_NO;
            			return forwardActionPerformedListener(); //go on
            		}
            	default: // Back
            		throw new IllegalArgumentException("Unknown buttonId");
            	}
            }
        });
    }
    
    private void processForward() throws FacadeException {
        PickOrderFacade pof = getStateless(PickOrderFacade.class);     

        try{
        	pof.testCanProcess(getPickingRequestPosition(), nullpointerCheckBox, storageLocationTextField, new BigDecimal(amountTextField));
        } catch (PickingExpectedNullException ex){
        	if (processMode != ProcessMode.EXPECTED_NULL) throw ex; //TODO Bug 1093
        } catch (PickingSubstitutionException ex){
        	if (processMode != ProcessMode.WITH_SUBSTITUTION) throw ex;
        }
    	switch (processMode) {
		case STANDARD:
			pof.processPickRequestPosition(getPickingRequestPosition(), nullpointerCheckBox, storageLocationTextField, new BigDecimal(amountTextField), takeWholeUnitLoadCheckBox, stockEmptyConfirmed);
			break;
		case EXPECTED_NULL:
			pof.processPickRequestPositionExpectedNull(getPickingRequestPosition(), storageLocationTextField, new BigDecimal(amountTextField), discoveredAmount, takeWholeUnitLoadCheckBox, stockEmptyConfirmed);
			break;
		case WITH_SUBSTITUTION:
			pof.processPickRequestPositionSubstitution(getPickingRequestPosition(), nullpointerCheckBox, storageLocationTextField, new BigDecimal(amountTextField), takeWholeUnitLoadCheckBox, stockEmptyConfirmed);
			break;
		default:
			pof.processPickRequestPosition(getPickingRequestPosition(), nullpointerCheckBox, storageLocationTextField, new BigDecimal(amountTextField), takeWholeUnitLoadCheckBox, stockEmptyConfirmed);
		}
    	
        isReset();        
    }

    public String forwardActionPerformedListener() {
        try {
        	
        	LOSPickRequestPosition position = getPickingRequestPosition();
        	
        	if (processMode == ProcessMode.SERIAL_NO){
        		
        		ProcessSerial();
        		
        	} else{
        		
	            if ( ! isInputCorrect()) {
	                return "";
	            }
	            
	            if ((storageLocationTextField.trim().equals("")) ||
	                    (amountTextField.trim().equals(""))) {
	                return nextPosition();
	            }
	            
	            if (isComplete()){
	            	if (position.missingMandatorySerialNo()){
	            		 return notifySerialNo();
	            	} else{
	            		return nextPosition();
	            	}
	            }
	            
	            processForward();
	            
	            switch(getCurrentObject().getItemData().getSerialNoRecordType()){
	            case ALWAYS_RECORD:
	            case GOODS_OUT_RECORD:
	            	return notifySerialNo(); 
	            default:
	            	break;
	            }
        	}
            
            if (nullpointerCheckBox){
            	isReset();
            	totalPosition = getPickingRequest().getPositions().size();
            }
            
            if (takeWholeUnitLoadCheckBox && pof.isSomethingPicked(getPickingRequest())) {
            	
                //Close Position and call outputplace.
                isReset();
                if (this.currentPosition < totalPosition){
                	this.currentPosition++;
                }
                return NavigationEnum.picking_outputPlace_CenterPanel.toString();
            } else{
            	return nextPosition();
            }
            
        } catch (NullAmountNoOtherException ex){
        	return notifyNoOtherAmount();
        } catch (PickingSubstitutionException ex){
        	return notifyWithSubstitution(ex);
        } catch (PickingExpectedNullException ex){
        	return notifyExpectedNull(ex);
        } catch (PickingException ex) {
            if (ex.getPickingExceptionKey().equals(PickingExceptionKey.PICK_UNEXPECTED_NULL)) {
                return notifyAmountToLess(ex);
            }
            if (ex.getPickingExceptionKey().equals(PickingExceptionKey.STOCK_HAS_MORE_AMOUNT)) {
                return notifyAmountToLess(ex);
            }
            return notifyError(ex);
        } catch (FacadeException ex) {
        	log.info(ex.getMessage(), ex);
            return notifyError(ex);
        }
    }

    private String notifyError(FacadeException ex) {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(getUIViewRoot().getLocale()),
				buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.picking_scanOrder_CenterPanel.toString();
			}
		});

	}
	private void ProcessSerial() throws FacadeException{
		
		PickOrderFacade pof = getStateless(PickOrderFacade.class);     

		if ( getPickingRequestPosition().missingMandatorySerialNo()	){
	    	pof.assignSerialNumbers(getPickingRequestPosition(), serials);
	    	serials = null;
		}
        
        isReset();    
		
	}


	private String notifyNoOtherAmount() {
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, resolve("NO_OTHER_AMOUNT"), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
         
                return nextPosition();
            }
        });   
	}
    @SuppressWarnings("unused")
	private String notifyOtherAmountOK() {
    	//recalculate
    	totalPosition = getPickingRequest().getPositions().size();
    	
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("OTHER_AMOUNT_OK"), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                return nextPosition();
            }
        });   
	}
    

    private String getArticelInfo() {
        LOSPickRequestPosition pos = getPickingRequestPosition();        
        String itemNumber = pos.getStockUnit().getItemData().getNumber();
        String lot = pos.getStockUnit().getLot() == null ? "" : pos.getStockUnit().getLot().getName();
        String description = pos.getStockUnit().getItemData().getDescription();                                 
        String result = resolve("ARTICEL_NUMBER")+": "+itemNumber+"<BR>";
        result = result+resolve("Charge")+": "+lot+"<BR>";
        result = result+resolve("DESCRIPTION")+": "+description;
        return result;
    }        
    
    public String articelDescriptionActionPerformedListener() {
    	List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, getArticelInfo() , buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
            }
        });   
	

    }
    
    public void closePosition() {
    	isReset();
    }

    /**
     * 
     * @return if all position will be done return true
     */
    private boolean isAllComplete() {
        List<LOSPickRequestPosition> positionList = getPickingRequest().getPositions();
        for (int i = 0; i <= positionList.size() - 1; i++) {
            if (positionList.get(i).isSolved() || positionList.get(i).isCanceled()) {
                //ok
            } else{
            	System.out.println("zaehler = "+i+"  false");
                return false;
            }
        }
        return true;
    }

    /**
     * close the currentPosition and inc the currentPosition
     * @return
     */
    public String nextPosition() {
        isReset(); 
        
        if (isAllComplete()) {
        	return notifyFinish();
        }
        
        System.out.println("--- nextPosition() : currentPosition = "+currentPosition);
        
        List<LOSPickRequestPosition> positionList = getPickingRequest().getPositions();
        totalPosition = positionList.size();
        for (int i = currentPosition; i<positionList.size(); i++) {
        	
        	if (positionList.get(i).isPicked() || positionList.get(i).isCanceled()) {
                //ok
            } else{
            	currentPosition = i;
            	if (currentPosition < totalPosition) {
            		currentPosition++;
            		return NavigationEnum.picking_scanOrder_CenterPanel.toString();
            	}else {
            		return notifyPositionEndReach();
            	}
            		
            }	
        }
        
        return notifyPositionEndReach();
    }

    private String notifyPositionEndReach() {
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("NoFurtherPositionAvailable"), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
            }
        });
    }
    
    private String notifyAmountToLess(FacadeException ex) {
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Yes", new Object[]{}));
        buttonTextList.add(resolve("No", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, ex.getLocalizedMessage(getUIViewRoot().getLocale()), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                if (buttonId == 1) {
                	setNullpointerCheckBox(true);
                	stockEmptyConfirmed = true;
                    return forwardActionPerformedListener();
                }
                return NavigationEnum.picking_scanOrder_CenterPanel.toString();

            }
        });
    }

/*        private String notifyArticelDescription() {
//                    getPickingRequestPosition().getItemData().get
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, , buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId) {
                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
            }
        });
    }*/
    
    private boolean isUnitLoad() {
        LOSPickRequestPosition pos = getPickingRequestPosition();        
    	return pof.isCompleteUnitLoad(pos);
//        return pos.getWithdrawalType().equals(PickingWithdrawalType.TAKE_UNITLOAD);
    }

    private String notifyFinish() {
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Ok", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("AllPositionsPickedMessage"), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
            	try {
	    	        boolean somethingPicked = pof.isSomethingPicked( getPickingRequest() );
	
	    	        if( !somethingPicked ) {
	    	        	log.debug("Nothing picked. Try to close order without put away");
	    	        	
	    	            PickOrderFacade pof = getStateless(PickOrderFacade.class);
	    	            if (isCompleteAll()) {
	    	                pof.finishPickingRequest(getPickingRequest(), getPickingRequest().getDestination().getName() );
	    	            } else {
	    	                pof.finishCurrentUnitLoad(getPickingRequest(), getPickingRequest().getDestination().getName() );
	    	            }
	    	            
	    	            if( isFinished() ) {
	    		        	log.debug("Order finished.");
	    	                List<String> buttonTextList = new ArrayList<String>();
	    	                buttonTextList.add(resolve("Ok", new Object[]{}));
	    	                NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_SUCCESS_MESSAGE"), buttonTextList);

	    	                return n.setCallbackListener(new ButtonListener() {
	    	                    public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
	    	                        return NavigationEnum.controller_CenterPanel.toString();
	    	                    }
	    	                    });
	    	            }

	                    return NavigationEnum.picking_scanOrder_CenterPanel.toString();
	    	        }
        	    } catch (FacadeException ex) {
        	    	log.error("Exception: " + ex.getMessage());
        	        List<String> buttonTextList = new ArrayList<String>();
        	        buttonTextList.add(resolve("Ok", new Object[]{}));
        	        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(getLocale()), buttonTextList);
        	        return n.setCallbackListener(new ButtonListener() {
        	            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
        	                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
        	            }
        	        });
        	    } 
            	
                return NavigationEnum.picking_outputPlace_CenterPanel.toString();
            }
        });
    }

    public String backdActionPerformedListener() {
        currentPosition--;
        return "";
    }

    private String notifyCancelMessage() {
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_CANCEL_MESSAGE"), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    return NavigationEnum.controller_CenterPanel.toString();
                }
            });        
    }
    
    private String notifyMustGotoDestination() {
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("MUST_GOTO_DESTINAITON"), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                	
	    	        boolean somethingPicked = pof.isSomethingPicked( getPickingRequest() );
    	
	    	        if( !somethingPicked ) {
	    	        	log.debug("Nothing picked. nothing to bring away");
    	    	        	
            	        List<String> buttonTextList = new ArrayList<String>();
            	        buttonTextList.add(resolve("Ok", new Object[]{}));
            	        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, resolve("PICKING_NOTHING_PICKED"), buttonTextList);
            	        return n.setCallbackListener(new ButtonListener() {
            	            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
            	                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
            	            }
            	        });
            	    } 

                    return NavigationEnum.picking_outputPlace_CenterPanel.toString();
                }
            });        
        
    }
    
    public String cancelActionPerformedListener() {
        try {
            PickOrderFacade pof = getStateless(PickOrderFacade.class);
            pof.cancel(getPickingRequest());
            return notifyCancelMessage();

        } catch (PickingException ex) {
        	log.info("Exception: " + ex.getMessage());
            PickingExceptionKey pick = ex.getPickingExceptionKey();
            switch (pick) {
                case MUST_GOTO_DESTINATION : 
                	return notifyMustGotoDestination();
                case PICK_MISSING_SERIALNO: 
                	return notifySerialNo();
                default : 
                	log.info("Exception: " + ex.getMessage(), ex);
                	return notifyCancelMessage();
            }
        } catch (FacadeException ex) {
        	log.info("Exception: "+ex.getMessage(), ex);
        }
        return "";
    }
    
    public String finishUlActionPerformedListener() {
    	if( getPickingRequestPosition().missingMandatorySerialNo()){
   		 	return notifySerialNo();
    	}
    	return notifyMustGotoDestination();

    }

    /**
     * The user allow naviate by empty fieles forward and backward
     * @return
     */
    public boolean isEmptyMessage() {
        return false;
    //return !JSFHelper.getInstance().hasMessage();
    }

    private boolean isAllFieldsEmpty() {
        if ((storageLocationTextField.trim().equals("")) && (amountTextField.trim().equals(""))) {
            return true;
        }
        return false;
    }

    private boolean isInputCorrect() {
        //If all fields are empty return true
        if (isAllFieldsEmpty()) {
            return true;
        }
        //check for empty fields
        if ((storageLocationTextField.trim().equals("")) || (amountTextField.trim().equals(""))) {
            JSFHelper.getInstance().message("Please fill all fields");
            return false;
        }
        //check the number
        try {
            new BigDecimal(amountTextField);
        } catch (NumberFormatException e) {
        	//TODO: use resource bundle
            JSFHelper.getInstance().message("Please fill only numbers in field");
            return false;
        }
        return true;
    }

    /**
     * If nothing found, so it would not be edit, else already edit
     * @return True if already would edit else false
     */
    public boolean isComplete() {
        LOSPickRequestPosition pos = getPickingRequestPosition();
        if (pos.isSolved() || pos.isCanceled()) {
            return true;
        }

//        List<LOSPickRequestPosition> positionList = getPickingRequest().getPositions();
//        if (positionList.get(currentPosition - 1).isSolved()
//        		|| positionList.get(currentPosition - 1).isCanceled()) {
//            return true;
//        }
        return false;
    }


    /**
     * 
     * @return If all positions have been edited
     */
    public boolean isCompleteAll() {
    	isReset();
        log.debug("isCompleteAll = "+getPickingRequest().getState().toString());
        
    	switch(getPickingRequest().getState()){
    	case FINISHED:
    	case FINISHED_PARTIAL:
    	case FAILED:
    	case PICKED:
    		return true;
    	case PICKED_PARTIAL:
    		for (LOSPickRequestPosition pos : getPickingRequest().getPositions()){
    			if ((pos.getPickedAmount().compareTo(new BigDecimal(0)) == 0 && !pos.isCanceled())){
    				return false;
    			} 
    		}
    		return true;
    	case PICKING:
    	case ACCEPTED:
    	case RAW:
    	default:
    		return false;
    	}
    }
    
    /**
     * 
     * @return If all positions have been edited
     */
    public boolean isFinished() {
    	isReset();
    	log.debug("isFinished = "+getPickingRequest().getState().toString());
        
    	switch(getPickingRequest().getState()){
    	case FINISHED:
    	case FINISHED_PARTIAL:
    	case FAILED:
    		return true;
    	case PICKED:
    	case PICKED_PARTIAL:
    	case PICKING:
    	case ACCEPTED:
    	case RAW:
    	default:
    		return false;
    	}
    }

    /**
     * restore textfieldes from cacheObject (if found) (forward,backward button)
     */
    private void restore() {
/*        List<LOSPickRequestPosition> positionList = pickingRequest.getPositions();
        System.out.println("untiLoad = "+positionList.get(currentPosition - 1).getStockUnit().toUniqueString());         
        System.out.println("unitload = "+positionList.get(currentPosition - 1).getUnitLoad().toUniqueString());         
        System.out.println("Amount untiLoad = "+positionList.get(currentPosition - 1).getStockUnit().getAmount());         
        System.out.println("Amount = "+positionList.get(currentPosition - 1).getAmount()); */

    	stockEmptyConfirmed = false;
    	
        if (isComplete() && getCurrentObject() != null) {
            storageLocationTextField = getCurrentObject().getStorageLocation().getName();
            amountTextField = getCurrentObject().getAmount().toString();
            processMode = ProcessMode.STANDARD;
            
        } else {
            storageLocationTextField = "";
            amountTextField = "";
            nullpointerCheckBox = false;
            takeWholeUnitLoadCheckBox = false;
            processMode = ProcessMode.STANDARD;
        }
    }

    public void storageLocationValidator(FacesContext context, UIComponent toValidate, Object value) {
    }

    public void amountValidator(FacesContext context, UIComponent toValidate, Object value) {
    }

    public boolean isBackwardButtonEnable() {
        if (currentPosition == 1) {
            return false;
        }
        return true;
    }

    public String finishActionPerformedListener() {
        processFinish();
        
        try {
	        boolean somethingPicked = pof.isSomethingPicked( getPickingRequest() );
	
	        if( !somethingPicked ) {
	        	log.debug("Nothing picked. Try to close order without put away");
	        	
	            PickOrderFacade pof = getStateless(PickOrderFacade.class);
	            if (isCompleteAll()) {
	                pof.finishPickingRequest(getPickingRequest(), getPickingRequest().getDestination().getName() );
	            } else {
	                pof.finishCurrentUnitLoad(getPickingRequest(), getPickingRequest().getDestination().getName() );
	            }
	
	            isReset();
	            if( isFinished() ) {
		        	log.debug("Order finished.");
	                List<String> buttonTextList = new ArrayList<String>();
	                buttonTextList.add(resolve("Ok", new Object[]{}));
	                NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("PICKING_SUCCESS_MESSAGE"), buttonTextList);

	                return n.setCallbackListener(new ButtonListener() {
	                    public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
	                        return NavigationEnum.controller_CenterPanel.toString();
	                    }
	                    });
	            }
	            
	        	return NavigationEnum.picking_scanOrder_CenterPanel.toString();
	        }

	    } catch (FacadeException ex) {
	    	log.error("Exception: " + ex.getMessage());
	        List<String> buttonTextList = new ArrayList<String>();
	        buttonTextList.add(resolve("Ok", new Object[]{}));
	        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(getLocale()), buttonTextList);
	        return n.setCallbackListener(new ButtonListener() {
	            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
	                return NavigationEnum.picking_scanOrder_CenterPanel.toString();
	            }
	        });
	        
	    } 

        
    	return NavigationEnum.picking_outputPlace_CenterPanel.toString();
    }

    private void processFinish() {
		isReset();
	}


    public boolean isForwardButtonEnable() {
        return true;
    }


    public boolean isFinishUlButtonEnable() {
		return pof.hasPickedStock(getPickingRequest());
    }

    public boolean isNullpointerCheckBox() {
        return this.nullpointerCheckBox;
    }

    public void setNullpointerCheckBox(boolean nullpointerCheckBox) {
        this.nullpointerCheckBox = nullpointerCheckBox;
    }

    public boolean isTakeWholeUnitLoadCheckBox() {
    	// restore default-value on startup
    	takeWholeUnitLoadCheckBox = pof.isCompleteUnitLoad(getPickingRequestPosition());
    	return takeWholeUnitLoadCheckBox;
    }

    public boolean isTakeWholeUnitLoadCheckBoxDisabled() {
    	return !pof.isWholeUnitLoadAllowed(getPickingRequestPosition());
    }
    public void setTakeWholeUnitLoadCheckBox(boolean takeWholeUnitLoadCheckBox) {
        this.takeWholeUnitLoadCheckBox = takeWholeUnitLoadCheckBox;
    }

    public LOSPickRequestPosition getCurrentObject() {
    	return getPickingRequestPosition();
// ???        return getPickingRequest().getPositions().get(currentPosition-1);
    }


    static enum ProcessMode{
    	STANDARD,
    	WITH_SUBSTITUTION,
    	EXPECTED_NULL, 
    	SERIAL_NO
    }
}
