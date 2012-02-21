/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.jasperreports.engine.design.JasperDesign;

import org.apache.log4j.Logger;
import org.mywms.globals.DocumentTypes;
import org.mywms.model.Client;
import org.mywms.model.Request;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.inventory.pick.query.PickReceiptQueryRemote;
import de.linogistix.los.inventory.pick.res.BundleResolver;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.report.businessservice.ReportServiceBean;

@Stateless
public class PickReportBean implements PickReport {

	private static final Logger log = Logger.getLogger(PickReportBean.class);
	@EJB
	InventoryGeneratorService genService;
	@EJB
	LOSPickRequestService pickingService;
	@EJB
	ReportService repService;
	@EJB
	PickReceiptQueryRemote pickReceiptQueryRemote;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public PickReceipt generatePickReceipt(Client c, String type,
			LOSUnitLoad ul, List<LOSPickRequestPosition> positions)
			throws ReportException {

		PickReceipt receipt;
		LOSOrderRequest order;
		LOSOrderRequestPosition pos;
		LOSPickRequest pick;
		PickReceiptPosition outPos;
		List<PickListParam> export = new ArrayList<PickListParam>();
		if (positions == null || positions.size() == 0) {
			//throw new IllegalArgumentException();
			log.error("Won't print receipt - no positions");
			return null;
		}

		
		Request prox =  positions.get(0).getParentRequest();
		pos = manager.find(LOSOrderRequestPosition.class, prox.getId());
		pick = (LOSPickRequest) positions.get(0).getPickRequest();
		order = (LOSOrderRequest) pick.getParentRequest();

//		try {
//			receipt = pickReceiptQueryRemote.queryByIdentity(name);
//			log.warn("Receipt already exists: " + receipt.toDescriptiveString());
//		} catch (BusinessObjectNotFoundException e) {
			//OK
			receipt = new PickReceipt();
			receipt.setClient(c);
			receipt.setDate(new Date());
			receipt.setLabelID(ul.getLabelId());
			receipt.setName(genService.generatePickReceiptReceiptNumber(c, ul));
			receipt.setOrderNumber(order.getRequestId());
			receipt.setPickNumber(pick.getNumber());
			receipt.setState(pick.getState().toString());
			receipt.setType(type);
			
			manager.persist(receipt);

//		}
		
	    LOSPickRequestPosition[] posArray = positions.toArray(new LOSPickRequestPosition[0]);    
		Arrays.sort(posArray, new PickPosCompOut());
		
		for (LOSPickRequestPosition p : posArray) {
			pos = (LOSOrderRequestPosition) p.getParentRequest();
			outPos = new PickReceiptPosition();
			outPos.setAmount(p.getPickedAmount());
			outPos.setAmountordered(p.getAmount());
			outPos.setArticleRef(p.getStockUnit().getItemData().getNumber());
			outPos.setArticleDescr(p.getStockUnit().getItemData().getAdditionalContent());
			outPos.setLotRef(p.getStockUnit().getLot() == null ? "" : p.getStockUnit().getLot().getName());
			outPos.setReceipt(receipt);
			
			manager.persist(outPos);
			
			PickListParam param = new PickListParam();
			param.setAmount(p.getDisplayPickedAmount().toString());
			param.setItemNo(p.getStockUnit().getItemData().getNumber());
			param.setItemDesc(p.getStockUnit().getItemData().getDescription());
			param.setLotNo(p.getStockUnit().getLot() == null ? "" : p.getStockUnit().getLot().getName());
			param.setSerialNo(p.getStockUnit().getSerialNumber());
			
			export.add(param);
		}

		manager.flush();

		if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
			try {
				JasperDesign d = repService.getJrxmlResource(c,
						BundleResolver.class, "PickReceipt.jrxml");
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("LOS_ORDERNUMBER", order.getNumber());
				parameters.put("LOS_PICKNUMBER", pick.getNumber());
				parameters.put("LOS_DATE", new Date());
				parameters.put("LOS_LABELID", ul.getLabelId());
				parameters.put("LOS_STATE", pick.getState().toString());
				
				byte[] bytes = repService.typeExportPdf(receipt.getName(),
						receipt.getType(), d, export, parameters);
				receipt.setDocument(bytes);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
				throw new ReportException();
			}
		} else {
			throw new IllegalArgumentException("only pdf supported");
		}

		return receipt;

	}

	PickReceipt generateGoodsOutReceipt(String client, String type,
			String labelId, String name, String orderNo, String pickNo,
			String state,
			List<PickReceiptPosition> positions)
			throws ReportException {

		List<PickReceiptPosition> export = new ArrayList<PickReceiptPosition>();

		Client c = new Client();
		c.setNumber(client);
		
		if (positions == null || positions.size() == 0) {
			log.error("Won't print a goods out receipt - no positions: " );
			return null;
		}

		PickReceipt receipt = new PickReceipt();
		receipt.setClient(c);
		receipt.setDate(new Date());
		receipt.setLabelID(labelId);
		receipt.setName(name);
		receipt.setOrderNumber(orderNo);
		receipt.setPickNumber(pickNo);
		receipt.setState(state);
		receipt.setType(type);
		
		export = positions;

		if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
			try {
				if (repService == null){
					repService = new ReportServiceBean();
				}
				
				JasperDesign d = repService.getJrxmlResource(c,
						BundleResolver.class, "PickReceipt.jrxml");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("LOS_ORDERNUMBER", orderNo);
				parameters.put("LOS_PICKNUMBER", pickNo);
				parameters.put("LOS_DATE", new Date());
				parameters.put("LOS_LABELID", labelId);
				parameters.put("LOS_STATE", state);
				
				byte[] bytes;
				
				bytes = repService.typeExportPdf(receipt.getName(),
						type, d, export, parameters);
				receipt.setDocument(bytes);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
				throw new ReportException();
			}
		} else {
			throw new IllegalArgumentException("only pdf supported");
		}

		return receipt;

	}

	
	class PickPosCompOut implements Comparator<LOSPickRequestPosition>{

		public int compare(LOSPickRequestPosition o1,
				LOSPickRequestPosition o2) {
			if (o1 == null || o2 == null) {
				throw new NullPointerException();
			}
			if (o1 == o2) {
				return 0;
			}
			if (o1.equals(o2)) {
				return 0;
			}
			String lot1 = o1.getStockUnit().getLot() == null ? "" : o1.getStockUnit().getLot().getName();
			String lot2 = o2.getStockUnit().getLot() == null ? "" : o2.getStockUnit().getLot().getName();
			
			if (o1.getStockUnit().getItemData().equals(o2.getStockUnit().getItemData())) {
				if (lot1.equals(lot2)) {
					if (o1.getIndex() !=  o2.getIndex()) 
						return o1.getIndex() > o2.getIndex() ? 1 : -1;
					else 
						return o1.getId().compareTo(o2.getId());
				} else {
					return lot1.compareTo(lot2);
				}
			} else{
				return o1.getStockUnit().getItemData().getName().compareTo(o2.getStockUnit().getItemData().getName());
			}

		}
	}
	
	
	public class PickListParam {
		private String amount;
		private String itemNo;
		private String itemDesc;
		private String lotNo;
		private String SerialNo;
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount == null ? "" : amount;
		}
		public String getItemNo() {
			return itemNo;
		}
		public void setItemNo(String itemNo) {
			this.itemNo = itemNo == null ? "" : itemNo;
		}
		public String getItemDesc() {
			return itemDesc;
		}
		public void setItemDesc(String itemDesc) {
			this.itemDesc = itemDesc == null ? "" : itemDesc;
		}
		public String getLotNo() {
			return lotNo;
		}
		public void setLotNo(String lotNo) {
			this.lotNo = lotNo == null ? "" : lotNo;
		}
		public String getSerialNo() {
			return SerialNo;
		}
		public void setSerialNo(String serialNo) {
			SerialNo = serialNo == null ? "" : serialNo;
		}
	}
}
