/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.PickingRequestState;

import de.linogistix.los.crud.BusinessObjectDeleteException;
import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.crud.LogItemCRUDRemote;
import de.linogistix.los.example.CommonTopologyException;
import de.linogistix.los.inventory.crud.ItemDataCRUDRemote;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.query.PickReceiptPositionQueryRemote;
import de.linogistix.los.inventory.pick.query.PickReceiptQueryRemote;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.inventory.query.ExtinguishRequestQueryRemote;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import de.linogistix.los.inventory.query.LOSStockUnitRecordQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.OrderReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.OrderReceiptQueryRemote;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
import de.linogistix.los.inventory.query.ReplenishRequestQueryRemote;
import de.linogistix.los.inventory.query.StockUnitLabelQueryRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.crud.LOSAreaCRUDRemote;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSFixedLocationAssignmentQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LogItemQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
@Stateless
public class InventoryCleanupFacadeBean implements InventoryCleanupFacade {

	private static final Logger log = Logger.getLogger(InventoryCleanupFacadeBean.class);
	
	@EJB
	ClientQueryRemote clientQuery;
	@EJB
	ClientCRUDRemote clientCrud;
	
	@EJB
	UnitLoadTypeQueryRemote ulTypeQuery;
	@EJB
	UnitLoadTypeCRUDRemote ulTypeCrud;
	@EJB
	LOSStorageLocationTypeQueryRemote slTypeQuery;
	@EJB
	LOSStorageLocationTypeCRUDRemote slTypeCrud;
	@EJB
	LOSTypeCapacityConstraintQueryRemote typeCapacityConstaintQuery;
	@EJB
	LOSTypeCapacityConstraintCRUDRemote typeCapacityConstaintCrud;
	@EJB
	LOSAreaQueryRemote areaQuery;
	@EJB
	LOSAreaCRUDRemote areaCrud;
	@EJB
	LOSStorageLocationQueryRemote slQuery;
	@EJB
	LOSStorageLocationCRUDRemote slCrud;
	@EJB
	RackQueryRemote rackQuery;
	@EJB
	LOSRackCRUDRemote rackCrud;
	@EJB
	UnitLoadQueryRemote ulQuery;
	@EJB
	UnitLoadCRUDRemote ulCrud;
	@EJB
	StockUnitQueryRemote suQuery;
	@EJB
	LogItemQueryRemote logQuery;
	@EJB
	LogItemCRUDRemote logCrud;
	@EJB
	StockUnitCRUDRemote suCrud;
	@EJB
	LOSFixedLocationAssignmentService assService;
	@EJB
	LOSFixedLocationAssignmentQueryRemote assQuery;
	@EJB
	ItemDataCRUDRemote itemCrud;
	@EJB
	ItemDataQueryRemote itemQuery;
	@EJB
	LotQueryRemote lotCrud;
	@EJB
	LotQueryRemote lotQuery;
//	@EJB
//	LOSStockUnitRecordCRUDRemote suRecordCrud;
	@EJB
	LOSStockUnitRecordQueryRemote suRecordQuery;
	@EJB
	ExtinguishRequestQueryRemote extQuery;
	@EJB
	ReplenishRequestQueryRemote replQuery;
//	@EJB
//	ExtinguishRequestCRUDRemote extCrud;
	@EJB
	LOSPickRequestQueryRemote pickQuery;
	@EJB
	LOSPickRequestService pickService;
//	@EJB
//	PickingRequestCRUDRemote pickCrud;
	@EJB
	LOSGoodsOutRequestQueryRemote outQuery;
	@EJB
	LOSGoodsOutRequestPositionQueryRemote outPosQuery;
//	@EJB
//	LOSGoodsOutRequestCRUDRemote outCrud;
	@EJB
	OrderRequestQueryRemote orderReqQuery;
//	@EJB
//	OrderRequestCRUDRemote orderReqCrud;
	@EJB
	PickReceiptQueryRemote pickReceiptQuery;
	@EJB
	PickReceiptPositionQueryRemote pickReceiptPosQuery;
//	@EJB
//	PickReceiptCRUDRemote pickReceiptCrud;
	@EJB
	LOSStorageRequestQueryRemote storeReqQuery;
//	@EJB
//	LOSStorageRequestCRUDRemote storeReqCrud;
	@EJB
	LOSGoodsReceiptQueryRemote goodsRecQuery;
//	@EJB
//	LOSGoodsReceiptCRUDRemote goodsRecCrud;
//	@EJB
//	LOSInventoryCRUDRemote invCrud;
	@EJB
	StockUnitLabelQueryRemote suLabelQuery;
//	@EJB
//	StockUnitLabelCRUDRemote suLabelCrud;
//	@EJB
//	LOSAdviceCRUDRemote adCrud;
	@EJB
	LOSAdviceQueryRemote adQuery;
	@EJB
	LOSGoodsReceiptPositionQueryRemote goodsRecPosQuery;
//	@EJB
//	LOSGoodsReceiptPositionCRUDRemote goodsRecPosCrud;
	@EJB
	LOSPickRequestPositionService pickposService;
	@EJB
	OrderReceiptPositionQueryRemote orderReceiptPositionQueryRemote;
	@EJB
	OrderReceiptQueryRemote orderReceiptQueryRemote;
	@EJB
	LOSPickRequestService pickRequestService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager em;

	
	
	public void cleanup() throws FacadeException{
		clearGoodsOutRequest();
//		clearPickReceipts();
//		clearExtinguishRequests();
//		clearReplenishOrders();
		clearPickingRequests();
		clearOrderRequests();
		clearStorageRequests();
		
////		clearInventories();
//		clearAdvices();
//		clearGoodsReceipts();
////		clearStockUnitLabels();
////		clearOrderReceipt();
//		
//		clearStockUnits();
//		clearUnitLoads();
//		clearStorageLocations();

	}

	public void clearAdvices() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "adviceState",
					LOSAdviceState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSAdvice.class);

			List<LOSAdvice> l = adQuery.queryByTemplate(d, q);
			for (LOSAdvice a : l) {
				a = em.find(LOSAdvice.class, a.getId());
				em.remove(a);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CommonTopologyException();
		}

	}

		
//	public void clearGoodsReceipts() throws TopologyException {
//		initClient();
//		try {
//			
//			QueryDetail d = new QueryDetail();
//			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
//					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
//					TESTCLIENT);
//			TemplateQuery q = new TemplateQuery();
//			q.addWhereToken(t);
//			q.setBoClass(LOSGoodsReceiptPosition.class);
//
//			List<LOSGoodsReceiptPosition> l = goodsRecPosQuery.queryByTemplate(d, q);
//			for (LOSGoodsReceiptPosition pp : l) {
//				pp = em.find(LOSGoodsReceiptPosition.class, pp.getId());
//				em.remove(pp);
//			}
//			
//			//--------------
//			
//			d = new QueryDetail();
//			t = new TemplateQueryWhereToken(
//					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
//					TESTCLIENT);
//			q = new TemplateQuery();
//			q.addWhereToken(t);
//			q.setBoClass(LOSGoodsReceipt.class);
//
//			List<LOSGoodsReceipt> re = goodsRecQuery.queryByTemplate(d, q);
//			for (LOSGoodsReceipt u : re) {
//				u = em.find(LOSGoodsReceipt.class, u.getId());
//				em.remove(u);
//			}
//			em.flush();
//		} catch (Throwable e) {
//			log.error(e.getMessage(), e);
//			throw new TopologyException();
//		}
//	}

	public void clearStorageRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "requestState",
					LOSStorageRequestState.TERMINATED);

			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSStorageRequest.class);

			List<LOSStorageRequest> l = storeReqQuery.queryByTemplate(d, q);
			for (LOSStorageRequest u : l) {
				u = em.find(LOSStorageRequest.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}


	public void clearOrderRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "orderState",
					LOSOrderRequestState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSOrderRequest.class);
			List<LOSOrderRequest> l;
			l = orderReqQuery.queryByTemplate(d, q);
			for (LOSOrderRequest u : l) {
				u = em.find(LOSOrderRequest.class, u.getId());
				
				List<LOSPickRequest> children  = pickRequestService.getListByOrder(u);
				if (children.size() > 0){
					log.error("Won't delete: " + u.toDescriptiveString());
					continue;
				}
				em.remove(u);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}

	public void clearGoodsOutRequest() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "outState",
					LOSGoodsOutRequestState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSGoodsOutRequest.class);
			List<LOSGoodsOutRequest> outs = outQuery.queryByTemplate(d, q);
			for (LOSGoodsOutRequest o : outs) {
				o = em.find(LOSGoodsOutRequest.class, o.getId());
				for (LOSGoodsOutRequestPosition pos  :o.getPositions()){
					pos = em.find(LOSGoodsOutRequestPosition.class, pos.getId());
					em.remove(pos);
				}
				o = em.find(LOSGoodsOutRequest.class, o.getId());
				em.remove(o);
			}
			em.flush();
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}
	}
	
	public void clearPickingRequests() throws FacadeException {
		try {
			QueryDetail d = new QueryDetail(0, 25);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "state",
					PickingRequestState.FINISHED);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.setBoClass(LOSPickRequest.class);			
			
			List<BODTO<LOSPickRequest>> l = pickQuery.queryByTemplateHandles(d, q);
			for (BODTO<LOSPickRequest> to : l) {
				LOSPickRequest u;
				u = pickQuery.queryById(to.getId());
				if (u.getParentRequest() != null){
					LOSOrderRequest order = em.find(LOSOrderRequest.class, u.getParentRequest().getId());
					if (! order.getOrderState().equals(LOSOrderRequestState.FINISHED)){
						log.error("Parent request not FINISHED yet: " + order.toDescriptiveString());
						continue;
					}
					
				}
				log.error("GOING TO DELETE: " + u.toDescriptiveString());
				pickService.delete(u);
			}
			
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new BusinessObjectDeleteException();
		}

	}

	
}
