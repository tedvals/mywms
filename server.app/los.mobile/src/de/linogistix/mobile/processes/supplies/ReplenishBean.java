/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.supplies;


import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.facade.ReplenishFacade;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;

/**
 *
 * @author krane
 */
public class ReplenishBean extends BasicDialogBean {

	private ReplenishFacade replFacade;
	
	private String inputLocationName = "";
	private String currentLocationName = "";
	private String inputAmount = "";
	
	private BigDecimal checkedAmount;
	private ItemDataTO itemDataTO;
	
	public ReplenishBean() {
		super();
		replFacade = getStateless(ReplenishFacade.class);
	}
	
	private void init() {
		inputLocationName = "";
		currentLocationName = "";
		inputAmount = "";
		itemDataTO = null;
		checkedAmount = null;
	}

	public String getNavigationKey() {
		return ReplenishNavigation.REPL_ENTER_LOCATION.name();
	}
	
	public String getTitle() {
		return resolve("Supplies");
	}

	public String processEnterLocation() {
		currentLocationName = "";

		if( inputLocationName == null ) 
			inputLocationName = "";
		inputLocationName = inputLocationName.trim();
		
		if( inputLocationName.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterLocation"));
			return "";
		}
		
		try {
			itemDataTO = replFacade.getItemDataByLocation(inputLocationName);
		}
		catch( FacadeException e ) {
			String msg = e.getLocalizedMessage(getLocale());
			if( msg == null || msg.length() == 0 ) {
				msg = resolve("MsgErrorReplenish");
			}
			JSFHelper.getInstance().message(msg);
			return "";
		}
		if( itemDataTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgEnterValidLocation"));
			return "";
		}

		currentLocationName = inputLocationName;
		inputLocationName = "";
		
		return ReplenishNavigation.REPL_ENTER_AMOUNT.name();
	}
	
	public String processEnterAmount() {
		checkedAmount = null;
		
		if( inputAmount == null ) 
			inputAmount = "";
		inputAmount = inputAmount.trim();
		
		if( inputAmount.length() > 0 ) {
			try {
				checkedAmount = new BigDecimal(inputAmount);
			}
			catch( Exception e ) {
				JSFHelper.getInstance().message(resolve("MsgEnterValidAmount"));
				return "";
			}
		}
		if( itemDataTO != null && checkedAmount != null ) {
			checkedAmount = checkedAmount.setScale(itemDataTO.getScale());
		}
		
		try {
			replFacade.replenish(currentLocationName, checkedAmount);
		} catch (FacadeException e) {
			String msg = e.getLocalizedMessage(getLocale());
			if( msg == null || msg.length() == 0 ) {
				msg = resolve("MsgErrorReplenish");
			}
			JSFHelper.getInstance().message(msg);
			return "";
		}

		inputAmount = "";
		
		JSFHelper.getInstance().message(resolve("MsgSuccessReplenish"));
		return ReplenishNavigation.REPL_INFO.name();
	}
	
	public String processStart() {
		init();
		return ReplenishNavigation.REPL_ENTER_LOCATION.name();
	}
	
	public String processEnd() {
		init();
		return ReplenishNavigation.REPL_MENU.name();
	}
	
	
	
	
	
    public String getInputLocationName() {
		return inputLocationName;
	}


	public void setInputLocationName(String inputLocationName) {
		this.inputLocationName = inputLocationName;
	}

    public String getLocationName() {
		return currentLocationName;
	}

	public String getAmountStr() {
		if( checkedAmount == null) {
			return "";
		}
		if( itemDataTO == null ) {
			return checkedAmount.toString();
		}
		return checkedAmount.toString() + " " + itemDataTO.getItemUnitName();
	}

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}


	public String getItemDataNumber() {
		return itemDataTO == null ? "" : itemDataTO.getNumber();
	}

	public String getItemDataName() {
		return itemDataTO == null ? "" : itemDataTO.getName();
	}

	public String getItemUnitName() {
		return itemDataTO == null ? "" : itemDataTO.getItemUnitName();
	}
	
	
	
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.supplies.ReplenishBundle", loc);
		return bundle;
	}
    
}
