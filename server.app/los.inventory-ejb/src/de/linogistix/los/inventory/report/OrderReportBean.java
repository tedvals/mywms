/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.OrderRequestService;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class OrderReportBean implements OrderReport {

	private static final Logger log = Logger.getLogger(OrderReportBean.class);
	
	@EJB
	OrderRequestService orderService;
	@EJB
	ReportService repService;
	@EJB
	ContextService contextService;
	@EJB
	LOSPickRequestPositionService pickPosService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public OrderReceipt generateOrderReceipt(Client c, String type,
			LOSOrderRequest order)
			throws ReportException {

		OrderReceipt receipt;
		OrderReceiptPosition outPos;
		List<OrderReceiptPosition> export = new ArrayList<OrderReceiptPosition>();

//		if (order.getPositions() == null || order.getPositions().size() == 0) {
//			throw new IllegalArgumentException();
//		}
		if (order.getPositions() == null) {
			throw new IllegalArgumentException();
		}
		
		OrderReceiptSheet sheet = new OrderReceiptSheet(order);
		
		receipt = new OrderReceipt();
		receipt.setClient(c);
		receipt.setUser(contextService.getCallersUser().getName());
		receipt.setDate(new Date());
		receipt.setName(order.getNumber());
		receipt.setOrderNumber(order.getParentRequestNumber()==null?"":order.getParentRequestNumber());
		receipt.setOrderReference(order.getRequestId());
		receipt.setOrderType(order.getOrderType());
		receipt.setState(order.getOrderState());
		receipt.setType(type);
		receipt.setDestination(order.getDestination().getName());
		
		manager.persist(receipt);

		for (OrderReceiptSheetPosition p : sheet.getPositions()){
			if( p == null ) {
				log.error("This is not expected. One position of the order has not been picked");
				continue;
			}
			
			if (p.getWithoutLot().compareTo(new BigDecimal(0)) > 0){
				outPos = new OrderReceiptPosition();
				outPos.setClient(receipt.getClient());
				outPos.setAmount(p.getAmountPicked(null));
				outPos.setAmountordered(p.getAmountOrdered());
				outPos.setArticleRef(p.getOrderRequestPosition().getItemData().getNumber());
				outPos.setArticleDescr(p.getOrderRequestPosition().getItemData().getAdditionalContent());
				outPos.setArticleScale(p.getOrderRequestPosition().getItemData().getScale());
				outPos.setLotRef("");
				outPos.setPos(p.getOrderRequestPosition().getPositionIndex());
				outPos.setReceipt(receipt);
				manager.persist(outPos);

				export.add(outPos);
			}
			for (Lot lot : p.getLotPositions().keySet()){
				outPos = new OrderReceiptPosition();
				outPos.setClient(receipt.getClient());
				outPos.setAmount(p.getAmountPicked(lot));
				outPos.setAmountordered(p.getAmountOrdered());
				outPos.setArticleRef(p.getOrderRequestPosition().getItemData().getNumber());
				outPos.setArticleDescr(p.getOrderRequestPosition().getItemData().getAdditionalContent());
				outPos.setArticleScale(p.getOrderRequestPosition().getItemData().getScale());
				outPos.setLotRef(lot.getName());
				outPos.setPos(p.getOrderRequestPosition().getPositionIndex());
				outPos.setReceipt(receipt);
				manager.persist(outPos);

				export.add(outPos);
			}
		}

		manager.flush();

		if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
			try {
				JasperDesign d = repService.getJrxmlResource(c,
						InventoryBundleResolver.class, "OrderReceipt.jrxml");

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("LOS_ORDERNUMBER", order.getRequestId());
				parameters.put("LOS_DATE", new Date());
				parameters.put("LOS_STATE",order.getOrderState().toString());

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

	class OrderReceiptSheet{
		
		Map<LOSOrderRequestPosition, OrderReceiptSheetPosition> positions;
		LOSOrderRequest order;
		
		public OrderReceiptSheet(LOSOrderRequest order){
			this.order = order;
			positions = new HashMap<LOSOrderRequestPosition, OrderReceiptSheetPosition>();
			for (LOSOrderRequestPosition p : order.getPositions()) {
				p = manager.find(p.getClass(), p.getId());
				List<LOSPickRequestPosition> pickPositions = pickPosService.getByOrderPosition(p);
				for (LOSPickRequestPosition pickPos : pickPositions){
					add(p, pickPos);
				}
			}
		}
		
		public List<OrderReceiptSheetPosition> getPositions() {
			
			List<OrderReceiptSheetPosition> ret = new ArrayList<OrderReceiptSheetPosition>(positions.size());
			for (LOSOrderRequestPosition oPos : getOrderRequestPositions()){
				ret.add(positions.get(oPos));
			}
			return ret;
		}

		public Collection<LOSOrderRequestPosition> getOrderRequestPositions() {
			return this.order.getPositions();
		}

		void add(LOSOrderRequestPosition pos, LOSPickRequestPosition pickPos){
			if (positions.containsKey(pos)){
				positions.get(pos).addPickPosition(pickPos);
			} else{
				OrderReceiptSheetPosition sheetPos = new OrderReceiptSheetPosition(pos);
				sheetPos.addPickPosition(pickPos);
				positions.put(pos, sheetPos);
			}
			
		}
		
		
	}
	
	class OrderReceiptSheetPosition{
		LOSOrderRequestPosition pos;
		List<LOSPickRequestPosition> pickPositions;
		Map<Lot, BigDecimal> map;
		BigDecimal withoutLot;
		
		public OrderReceiptSheetPosition(LOSOrderRequestPosition pos){
			this.pos = pos;
			this.pickPositions = new ArrayList<LOSPickRequestPosition>();
			this.map = new HashMap<Lot, BigDecimal>();
			this.withoutLot = new BigDecimal(0);
		}
		
		public BigDecimal getWithoutLot() {
			return this.withoutLot;
		}

		public void addPickPosition(LOSPickRequestPosition pickPos){
			if (pickPos == null) throw new NullPointerException();
			this.pickPositions.add(pickPos);
			Lot lot = pickPos.getStockUnit().getLot();
			BigDecimal dec = new BigDecimal(0);
			if (lot != null){
				if (map.containsKey(lot)){
					dec = map.get(lot);
				}
				dec = dec.add(pickPos.getPickedAmount());
				map.put(lot, dec);
			} else{
				withoutLot = withoutLot.add(pickPos.getPickedAmount());
			}
		}
		
		public BigDecimal getAmountPicked(Lot lot){
			BigDecimal dec = new BigDecimal(0);
			if (lot == null){
				return this.withoutLot;
			} else{
				if (this.map.get(lot) != null){
					dec = this.map.get(lot);
				} 
				return dec;
			}
			
		}
		
		public BigDecimal getAmountOrdered(){
			return getOrderRequestPosition().getAmount();
		}
		
		public LOSOrderRequestPosition getOrderRequestPosition(){
			return this.pos;
		}
		
		public Map<Lot, BigDecimal>  getLotPositions(){	
			return this.map;
		}
		
		
	}

}
