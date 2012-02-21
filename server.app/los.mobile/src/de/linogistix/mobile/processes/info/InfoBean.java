/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.info;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;

import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;

/**
 * @author krane
 *
 */
public class InfoBean extends BasicDialogBean {
	Logger log = Logger.getLogger(InfoBean.class);

	
	private InfoFacade infoFacade;
	
	private List<InfoUnitLoadTO> currentUnitLoadList = null;
	private List<InfoStockUnitTO> currentStockUnitList = null;
	private List<InfoOrderTO> currentOrderList = null;
	
	private int currentUnitLoadIdx = -1;
	private int currentStockUnitIdx = -1;
	private int currentOrderIdx = -1;
	private String lastState = "";
	
	
	private InfoUnitLoadTO currentUnitLoadTO = null;
	private InfoLocationTO currentLocationTO = null;
	private InfoItemDataTO currentItemDataTO = null;
	private InfoStockUnitTO currentStockUnitTO = null;
	private InfoOrderTO currentOrderTO = null;
	
	private String inputCode = "";
	private boolean showClient = true;
	
	public InfoBean() {
		super();
		infoFacade = super.getStateless(InfoFacade.class);
		Client client = infoFacade.getDefaultClient();
		showClient = (client == null);
	}

	private void init() {
		currentUnitLoadList = null;
		currentStockUnitList = null;
		currentOrderList = null;
		
		currentOrderList = null;
		
		currentUnitLoadIdx = -1;
		currentStockUnitIdx = -1;
		currentOrderIdx = -1;
		lastState = "";

		
		currentUnitLoadTO = null;
		currentLocationTO = null;
		currentItemDataTO = null;
		currentStockUnitTO = null;
		currentOrderTO = null;
		

	}
	
	@Override
	public String getNavigationKey() {
		return InfoNavigation.INFO_ENTER_CODE.name();
	}

	@Override
	public String getTitle() {
		return resolve("TitleInfo");
	}
	
	public String processStart() {
		return InfoNavigation.INFO_ENTER_CODE.toString();
	}
	
	
	
	// ***********************************************************************
	// EnterCode.jsp
	// ***********************************************************************
	public String processEnterCode() {
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";

		init();
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterCode") );
			return InfoNavigation.INFO_ENTER_CODE.toString();
		}
		
		log.info("Search for code="+code);
		
		currentUnitLoadTO = infoFacade.readUnitLoad(code);
		currentLocationTO = infoFacade.readLocation(code);
		currentItemDataTO = infoFacade.readItemData(code);
		log.info("Found UL="+(currentUnitLoadTO!=null)+", Loc="+(currentLocationTO!=null)+", Item="+(currentItemDataTO!=null));
		
		

		int anzType = 0;
		if( currentUnitLoadTO != null )
			anzType ++;
		if( currentLocationTO != null )
			anzType ++;
		if( currentItemDataTO != null )
			anzType ++;
		
		if( currentUnitLoadTO != null && currentLocationTO != null ) {
			return getLocationPage();
		}
		else if( anzType > 1 ) {
			return InfoNavigation.INFO_SELECT_TYPE.toString();
		}
		else if( currentUnitLoadTO != null ) {
			return getUnitLoadPage();
		}
		else if( currentLocationTO != null ) {
			return getLocationPage();
		}
		else if( currentItemDataTO != null ) {
			return InfoNavigation.INFO_SHOW_ITEMDATA.toString();
		}
		else {
			JSFHelper.getInstance().message( resolve("MsgNothingFound") );
			return InfoNavigation.INFO_ENTER_CODE.toString();
		}
		
	}
	
	public String processEnterCodeCancel() {
		init();
		return InfoNavigation.INFO_BACK_TO_MENU.toString();
	}

	
	
	
	// ***********************************************************************
	// SelectType.jsp
	// ***********************************************************************
	public String processSelectUnitLoad() {
		return getUnitLoadPage();
	}

	public String processSelectItemData() {
		return InfoNavigation.INFO_SHOW_ITEMDATA.toString();
	}

	public String processSelectLocation() {
		return getLocationPage();
	}
	
	public String processSelectCancel() {
		init();
		return InfoNavigation.INFO_ENTER_CODE.toString();
	}

	public boolean isUnitLoadSelected() {
		return currentUnitLoadTO != null;
	}
	
	public boolean isLocationSelected() {
		return currentLocationTO != null;
	}
	
	public boolean isItemDataSelected() {
		return currentItemDataTO != null;
	}

	
	
	// ***********************************************************************
	// ShowUnitLoad.jsp
	// ***********************************************************************
	public String processUnitLoadUnitLoad() {
		return InfoNavigation.INFO_SHOW_UNITLOAD.name();
	}
	
	public String processUnitLoadStockUnit() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		currentStockUnitList = currentUnitLoadTO.getStockUnitList();
		if( currentStockUnitList == null || currentStockUnitList.size() <= 0 ) {
			return "";
		}
		currentStockUnitIdx = 0;
		currentStockUnitTO = currentStockUnitList.get(0);
		
		return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
	}
	
	public String processUnitLoadOrder() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		lastState = InfoNavigation.INFO_SHOW_UNITLOAD.name();
		
		if( currentUnitLoadTO.getPickList().size() > 0 ) {
			currentOrderList = currentUnitLoadTO.getPickList();
		}
		else if( currentUnitLoadTO.getOrderList().size() > 0 ) {
			currentOrderList = currentUnitLoadTO.getOrderList();
		}
		else {
			return "";
		}
		currentOrderIdx = 0;
		currentOrderTO = currentOrderList.get(0);
		
		return InfoNavigation.INFO_SHOW_ORDER.toString();
	}
	
	public String processUnitLoadStockOrder() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		lastState = InfoNavigation.INFO_SHOW_UNITLOAD_STOCK.name();
		
		if( currentUnitLoadTO.getPickList().size() > 0 ) {
			currentOrderList = currentUnitLoadTO.getPickList();
		}
		else if( currentUnitLoadTO.getOrderList().size() > 0 ) {
			currentOrderList = currentUnitLoadTO.getOrderList();
		}
		else {
			return "";
		}
		currentOrderIdx = 0;
		currentOrderTO = currentOrderList.get(0);
		
		return InfoNavigation.INFO_SHOW_ORDER.toString();
	}
	
	public String processUnitLoadLocation() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		String locationName = currentUnitLoadTO.getLocationName();
		selectLocation( locationName );
		
		return InfoNavigation.INFO_SHOW_LOCATION.toString();
	}
	
	public String processUnitLoadNext() {
		if( currentUnitLoadList == null || currentUnitLoadIdx >= currentUnitLoadList.size()-1 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return getUnitLoadPage();
		}
		
		currentUnitLoadIdx++;
		
		if( currentUnitLoadIdx >= currentUnitLoadList.size() || currentUnitLoadIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return getUnitLoadPage();
		}
		
		currentUnitLoadTO = currentUnitLoadList.get(currentUnitLoadIdx);
		
		return getUnitLoadPage();
	}
	
	public String processUnitLoadPrev() {
		if( currentUnitLoadIdx <= 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return getUnitLoadPage();
		}
		
		currentUnitLoadIdx --;
		
		if( currentStockUnitIdx >= currentUnitLoadList.size() || currentUnitLoadIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return getUnitLoadPage();
		}

		currentUnitLoadTO = currentUnitLoadList.get(currentUnitLoadIdx);
		
		return getUnitLoadPage();
	}

	private String getUnitLoadPage() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		if( currentUnitLoadTO.getStockUnitList().size() == 1 ) {
			currentStockUnitTO = currentUnitLoadTO.getStockUnitList().get(0);
			return InfoNavigation.INFO_SHOW_UNITLOAD_STOCK.toString();
		}
		else {
			return InfoNavigation.INFO_SHOW_UNITLOAD.toString();
		}
	}
	
	public boolean isUnitLoadHasPrev() {
		if( currentUnitLoadList == null || currentUnitLoadList.size() < 2 ) {
			return false;
		}
		return currentUnitLoadIdx > 0;
	}
	
	public boolean isUnitLoadHasNext() {
		if( currentUnitLoadList == null || currentUnitLoadList.size() < 2 ) {
			return false;
		}

		return currentUnitLoadIdx < (currentUnitLoadList.size()-1);
	}

	public boolean isUnitLoadHasOrderContent() {
		if( currentUnitLoadTO == null ) {
			return false;
		}

		return (currentUnitLoadTO.getOrderList().size() > 0 || currentUnitLoadTO.getPickList().size() > 0);
	}

	public boolean isUnitLoadHasContent() {
		return currentUnitLoadTO != null && currentUnitLoadTO.getStockUnitList().size() > 0;
	}

	
	
	// ***********************************************************************
	// ShowStockUnit.jsp
	// ***********************************************************************
	public String processStockUnitUnitLoad() {
		if( currentUnitLoadTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgUlNotSelected") );
			return "";
		}
		return InfoNavigation.INFO_SHOW_UNITLOAD.name();
	}

	public String processStockUnitItemData() {
		if( currentStockUnitTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgStockNotSelected") );
			return "";
		}
		currentItemDataTO = currentStockUnitTO.getItemData();
		
		return InfoNavigation.INFO_SHOW_ITEMDATA.toString();
	}

	public String processStockUnitOrder() {
		if( currentStockUnitTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgStockNotSelected") );
			return "";
		}
		lastState = InfoNavigation.INFO_SHOW_STOCKUNIT.name();

		if( currentStockUnitTO.getPickList().size() > 0 ) {
			currentOrderList = currentStockUnitTO.getPickList();
		}
		else if( currentStockUnitTO.getOrderList().size() > 0 ) {
			currentOrderList = currentStockUnitTO.getOrderList();
		}
		else {
			return "";
		}
		currentOrderIdx = 0;
		currentOrderTO = currentOrderList.get(0);
		
		return InfoNavigation.INFO_SHOW_ORDER.toString();
	}
	
	public String processStockUnitNext() {
		if( currentStockUnitList == null || currentStockUnitIdx >= currentStockUnitList.size()-1 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
		}
		
		currentStockUnitIdx++;
		
		if( currentStockUnitIdx >= currentStockUnitList.size() || currentStockUnitIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
		}

		currentStockUnitTO = currentStockUnitList.get(currentStockUnitIdx);
		
		return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
	}
	
	public String processStockUnitPrev() {
		if( currentStockUnitIdx <= 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
		}
		
		currentStockUnitIdx --;
		
		if( currentStockUnitIdx >= currentStockUnitList.size() || currentStockUnitIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
		}

		currentStockUnitTO = currentStockUnitList.get(currentStockUnitIdx);
		
		return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
	}

	public boolean isStockUnitHasPrev() {
		if( currentStockUnitList == null || currentStockUnitList.size() < 2 ) {
			return false;
		}
		return currentStockUnitIdx > 0;
	}
	
	public boolean isStockUnitHasNext() {
		if( currentStockUnitList == null || currentStockUnitList.size() < 2 ) {
			return false;
		}
		return currentStockUnitIdx < (currentStockUnitList.size()-1);
	}
	
	public boolean isStockUnitHasOrderContent() {
		if( currentStockUnitTO == null ) {
			return false;
		}

		return (currentStockUnitTO.getOrderList().size() > 0 || currentStockUnitTO.getPickList().size() > 0);
	}
	
	// ***********************************************************************
	// ShowLocation.jsp
	// ***********************************************************************
	public String processLocationUnitLoad() {
		if( currentLocationTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgLocNotSelected") );
			return "";
		}
		
		selectUnitLoadList( currentLocationTO.getName() );

		if( currentUnitLoadList != null && currentUnitLoadList.size() > 0 ) {
			currentUnitLoadTO = currentUnitLoadList.get(0);
			currentUnitLoadIdx = 0;
			return getUnitLoadPage();
		}
		
		return "";
	}
	
	public String processLocationLocation() {
		if( currentLocationTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgLocNotSelected") );
			return "";
		}
		return InfoNavigation.INFO_SHOW_LOCATION.name();
	}
		
	public String processLocationNext() {
		return InfoNavigation.INFO_SHOW_LOCATION.toString();
	}
	
	public String processLocationPrev() {
		return InfoNavigation.INFO_SHOW_LOCATION.toString();
	}

	private String getLocationPage() {
		if( currentLocationTO == null ) {
			JSFHelper.getInstance().message( resolve("MsgLocNotSelected") );
			return "";
		}
		if( currentLocationTO.getNumUnitLoads() == 1 ) {
			log.info("Found 1 UnitLoad");
			InfoUnitLoadTO ulto = currentLocationTO.getUnitLoad();
			if( ulto.getStockUnitList().size() == 1 ) {
				log.info("Found 1 StockUnit");
				currentUnitLoadTO = currentLocationTO.getUnitLoad();
				currentStockUnitTO = currentUnitLoadTO.getStockUnitList().get(0);
				return InfoNavigation.INFO_SHOW_STOCKUNIT.toString();
			}
		}
		return InfoNavigation.INFO_SHOW_LOCATION.toString();
	}
	
	public boolean isLocationHasContent() {
		return currentLocationTO != null && currentLocationTO.getNumUnitLoads() > 0;
	}

	
	
	// ***********************************************************************
	// ShowOrder.jsp
	// ***********************************************************************
	public String processOrderNext() {
		if( currentOrderList == null || currentOrderIdx >= currentOrderList.size()-1 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return InfoNavigation.INFO_SHOW_ORDER.toString();
		}
		
		currentOrderIdx++;
		
		if( currentOrderIdx >= currentOrderList.size() || currentOrderIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return InfoNavigation.INFO_SHOW_ORDER.toString();
		}
		
		currentOrderTO = currentOrderList.get(currentOrderIdx);
		
		return InfoNavigation.INFO_SHOW_ORDER.toString();
	}
	
	public String processOrderPrev() {
		if( currentOrderIdx <= 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEndOfList") );
			return InfoNavigation.INFO_SHOW_ORDER.toString();
		}
		
		currentOrderIdx --;
		
		if( currentOrderIdx >= currentOrderList.size() || currentOrderIdx < 0 ) {
			JSFHelper.getInstance().message( resolve("MsgPosNotSelected") );
			return InfoNavigation.INFO_SHOW_ORDER.toString();
		}

		currentOrderTO = currentOrderList.get(currentOrderIdx);
		
		return InfoNavigation.INFO_SHOW_ORDER.toString();
	}
	
	public String processOrderBack() {
		currentOrderIdx = -1;
		if( lastState != null && lastState.length()>0 ) {
			return lastState;
		}
		return InfoNavigation.INFO_ENTER_CODE.name();
	}

	public boolean isOrderHasPrev() {
		if( currentOrderList == null || currentOrderList.size() < 2 ) {
			return false;
		}
		return currentOrderIdx > 0;
	}
	
	public boolean isOrderHasNext() {
		if( currentOrderList == null || currentOrderList.size() < 2 ) {
			return false;
		}

		return currentOrderIdx < (currentOrderList.size()-1);
	}

	
	
	
	// ***********************************************************************
	// ShowItemData.jsp
	// ***********************************************************************
	public boolean isShowItemDataBack() {
		return ( currentStockUnitTO != null || currentLocationTO != null );
	}

	
	
	
	// ***********************************************************************
	private void selectLocation( String code ) {
		currentLocationTO = infoFacade.readLocation( code );
	}
	
	private void selectUnitLoadList( String code ) {
		currentUnitLoadList = infoFacade.readUnitLoadList( code );
	}


	
	// ***********************************************************************
	// Attributes
	// ***********************************************************************
	public InfoUnitLoadTO getInfoUnitLoad() {
		return currentUnitLoadTO == null ? new InfoUnitLoadTO() : currentUnitLoadTO;
	}

	public InfoLocationTO getInfoLocation() {
		return currentLocationTO == null ? new InfoLocationTO() : currentLocationTO;
	}
	
	public InfoItemDataTO getInfoItemData() {
		return currentItemDataTO == null ? new InfoItemDataTO() : currentItemDataTO;
	}
	
	public InfoOrderTO getInfoOrder() {
		return currentOrderTO == null ? new InfoOrderTO() : currentOrderTO;
	}
	
	public InfoStockUnitTO getInfoStock() {
		return currentStockUnitTO == null ? new InfoStockUnitTO() : currentStockUnitTO;
	}
	
	public String getItemDataLotTypeName() {
		if( currentItemDataTO == null ) {
			return "";
		}
		return currentItemDataTO.isLotMandatory ? resolve("LabelMandatory") : resolve("LabelNo");
	}
	
	public String getItemDataSerialTypeName() {
		if( currentItemDataTO == null ) {
			return "";
		}
		
		switch( currentItemDataTO.getSerialNoRecordType() ) {
		case NO_RECORD: 
			return resolve("LabelNo");
		case ALWAYS_RECORD: 
			return resolve("LabelAlways");
		case GOODS_OUT_RECORD:
			return resolve("LabelGoodsOut");
		}
		
		return "?";
	}
	
	public String getUnitLoadOrderLabel() {
		if( currentUnitLoadTO == null ) {
			return "";
		}
		
		if( currentUnitLoadTO.getPickList().size() > 0 ) {
			return resolve("LabelPick");
		}
		return resolve("LabelOrder");
	}
	
	public String getStockUnitOrderLabel() {
		if( currentStockUnitTO == null ) {
			return "";
		}
		
		if( currentStockUnitTO.getPickList().size() > 0 ) {
			return resolve("LabelPick");
		}
		return resolve("LabelOrder");
	}

	public String getUnitLoadContent() {
		if( currentUnitLoadTO == null ) {
			return "";
		}

		List<InfoStockUnitTO> stockList = currentUnitLoadTO.getStockUnitList();
		
		if( stockList == null || stockList.size() <= 0 ) {
			return "";
		}
		else if( stockList.size() == 1 ) {
			return "1 "+resolve("LabelStockUnit");
		}
		else { 
			return "" + stockList.size() +" "+resolve("LabelStockUnits");
		}
	}

	public String getLocationContent() {
		if( currentLocationTO == null ) {
			return "";
		}
		
		if( currentLocationTO.getNumUnitLoads() == 1 ) {
			return currentLocationTO.getUnitLoad().getLabel();
		}
		else if( currentLocationTO.getNumUnitLoads() > 1 ) {
			return "" + currentLocationTO.getNumUnitLoads() + " " + resolve("LabelUnitLoads");
		}
		return "";
	}

	public String getUnitLoadOrder() {
		if( currentUnitLoadTO == null ) {
			return "";
		}
		if( currentUnitLoadTO.getPickList().size()==1 ) {
			return currentUnitLoadTO.getPickList().get(0).getNumber();
		}
		else if( currentUnitLoadTO.getPickList().size()>1 ) {
			return ""+currentUnitLoadTO.getPickList().size()+" "+resolve("LabelOrders");
		}
		else if( currentUnitLoadTO.getOrderList().size()==1 ) {
			return currentUnitLoadTO.getOrderList().get(0).getNumber();
		}
		else if( currentUnitLoadTO.getOrderList().size()>1 ) {
			return ""+currentUnitLoadTO.getOrderList().size()+" "+resolve("LabelOrders");
		}
		return "";
	}
	
	public String getStockUnitOrder() {
		if( currentStockUnitTO == null ) {
			return "";
		}
		if( currentStockUnitTO.getPickList().size()==1 ) {
			return currentStockUnitTO.getPickList().get(0).getNumber();
		}
		else if( currentStockUnitTO.getPickList().size()>1 ) {
			return ""+currentStockUnitTO.getPickList().size()+" "+resolve("LabelOrders");
		}
		else if( currentStockUnitTO.getOrderList().size()==1 ) {
			return currentStockUnitTO.getOrderList().get(0).getNumber();
		}
		else if( currentStockUnitTO.getOrderList().size()>1 ) {
			return ""+currentStockUnitTO.getOrderList().size()+" "+resolve("LabelOrders");
		}
		return "";
	}
	
	public String getStockUnitAmount() {
		if( currentStockUnitTO == null ) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(currentStockUnitTO.getAmount());
		if( BigDecimal.ZERO.compareTo(currentStockUnitTO.getAmountRes()) != 0 ) {
			sb.append(" (");
			sb.append(currentStockUnitTO.getAmountRes());
			sb.append(")");
		}
		sb.append(" ");
		sb.append(currentStockUnitTO.getUnit());
		return sb.toString();
	}


	public String getorderState() {
		if( currentOrderTO == null ) {
			return "";
		}
		return resolve("LOSOrderRequestState."+currentOrderTO.getState());
		
	}
	public String getOrderType() {
		if( currentOrderTO == null ) {
			return "";
		}
		return resolve("OrderType."+currentOrderTO.getType());
	}

	
	
	
	// ***********************************************************************
	public String getInputCode() {
		return inputCode;
	}

	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}
	

	
	// ***********************************************************************
	public boolean isShowClient() {
		return showClient;
	}

	public boolean isShowSerialNo() {
		if( currentItemDataTO == null ) {
			return false;
		}
		return currentItemDataTO.getSerialNoRecordType() != SerialNoRecordType.NO_RECORD;
	}

	
	
	// ***********************************************************************
	@Override
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.info.InfoBundle", loc);
		return bundle;
	}


}
