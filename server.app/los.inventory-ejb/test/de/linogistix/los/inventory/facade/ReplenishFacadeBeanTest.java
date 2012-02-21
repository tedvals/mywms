/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */


package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.LOSReplenishRequest;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.query.dto.PickingRequestTO;
import de.linogistix.los.inventory.query.ReplenishRequestQueryRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.test.TestUtilities;

public class ReplenishFacadeBeanTest extends TestCase {

	private static final Logger log = Logger.getLogger(ReplenishFacadeBeanTest.class);
	
	ReplenishFacade bean;
	
	protected void setUp() throws Exception {
		super.setUp();
		bean = TestUtilities.beanLocator.getStateless(ReplenishFacade.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testReplenishOnPickPlace(){
		try {
			bean.replenish("T1-1-1-1", new BigDecimal(1));
			ReplenishRequestQueryRemote r = TestUtilities.beanLocator.getStateless(ReplenishRequestQueryRemote.class);
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "destination.name", "T1-1-1-1");
			TemplateQuery q = new TemplateQuery();
			q.setBoClass(LOSReplenishRequest.class);
			q.addWhereToken(t);
			
			List<LOSReplenishRequest> rr = r.queryByTemplate(d, q);
			if (rr == null || rr.size() != 1){
				fail();
			}
			LOSReplenishRequest req = rr.get(0);
			PickOrderFacade pick = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
			
			StockUnitQueryRemote suQuery =TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class) ;
			
			List<PickingRequestTO> l = pick.getRawPickingRequest();
			for (PickingRequestTO to : l){
				if (to.parentRequest.equals(req.getNumber())){
					LOSPickRequest pr = pick.accept(pick.loadPickingRequest(to));
					LOSPickRequestPosition p = pr.getPositions().get(0);
					pick.processPickRequestPosition(p, false, p.getStorageLocation().getName(), p.getAmount(), false, false);
					StockUnit su = p.getStockUnit();
					su = suQuery.queryById(su.getId());
					assertTrue(su.getLock() == 0);
					
					pick.finishPickingRequest(pr, pr.getDestination().getName());
						
					su = suQuery.queryById(su.getId());
					assertTrue(su.getLock() == 0);
					
//					assertTrue(su.getAmount() == 0);
//					assertEquals(TopologyBean.SL_NIRWANA_NAME, ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().getName());
//					assertTrue(su.getReservedAmount() == 0);
//					
					LOSUnitLoadQueryRemote ulQueryRemote = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
					LOSUnitLoad ul = ulQueryRemote.queryByIdentity("T1-1-1-1");
					assertTrue(ul.getStockUnitList().size() == 1);
					assertFalse(ul.getStockUnitList().get(0).isLocked());
					StockUnit onDest = ul.getStockUnitList().get(0);
					assertFalse(onDest.isLocked());
					assertTrue(su.getAmount().compareTo(new BigDecimal(1)) >= 0);
				}
			}
			
		} catch (FacadeException e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
//	public void testReplenishOnFixed(){
//		try {
//			bean.replenish("T1-1-1-1", 1);
//			ReplenishRequestQueryRemote r = TestUtilities.beanLocator.getStateless(ReplenishRequestQueryRemote.class);
//			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
//			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "destination.name", "T1-1-1-1");
//			TemplateQuery q = new TemplateQuery();
//			q.setBoClass(LOSReplenishRequest.class);
//			q.addWhereToken(t);
//			
//			List<LOSReplenishRequest> rr = r.queryByTemplate(d, q);
//			if (rr == null || rr.size() != 1){
//				fail();
//			}
//			LOSReplenishRequest req = rr.get(0);
//			PickOrderFacade pick = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
//			List<PickingRequestTO> l = pick.getRawPickingRequest();
//			for (PickingRequestTO to : l){
//				if (to.parentRequest.equals(req.getNumber())){
//					LOSPickRequest pr = pick.accept(pick.loadPickingRequest(to));
//					LOSPickRequestPosition p = pr.getPositions().get(0);
//					pick.processPickRequestPosition(p, false, p.getStorageLocation().getName(), (int)p.getAmount());
//					pick.finishPickingRequest(pr, pr.getDestination().getName());
//					StockUnit su = p.getStockUnit();
//					
//					StockUnitQueryRemote suQuery =TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class) ;
//					su = suQuery.queryById(su.getId());
//					assertTrue(su.getLock() == 0);
//					assertTrue(su.getAmount() >= 1);
//					assertTrue(su.getReservedAmount() == 0);
//					LOSUnitLoadQueryRemote ulQueryRemote = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
//					LOSUnitLoad ul = ulQueryRemote.queryById(su.getUnitLoad().getId());
//					assertTrue(ul.getStockUnitList().size() == 1);
//				}
//			}
//			
//		} catch (FacadeException e) {
//			log.error(e.getMessage(), e);
//			fail(e.getMessage());
//		}
//	}
//	
	
	public void testReplenishOnPickPlaceAmountChanged(){
		try {
			
			ManageInventoryFacade manageInv = TestUtilities.beanLocator.getStateless(ManageInventoryFacade.class);
			
			LOSUnitLoadQueryRemote ulQueryRemote = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
			LOSUnitLoad ul = ulQueryRemote.queryByIdentity("T1-1-1-1");
			BODTO<LOSUnitLoad> ulTo = new BODTO<LOSUnitLoad>(ul.getId(), ul.getVersion(), ul.getId());
			List<BODTO<LOSUnitLoad>> ulList = new ArrayList<BODTO<LOSUnitLoad>>();
			ulList.add(ulTo);
			StockUnit su = ul.getStockUnitList().get(0);
			ul = ulQueryRemote.queryById(ul.getId());
			BODTO<StockUnit> suTO = new BODTO<StockUnit>(su.getId(), su.getVersion(), su.getId());
			
			manageInv.changeAmount(suTO, new BigDecimal(0), new BigDecimal(0), null);
			manageInv.sendStockUnitsToNirwanaFromUl(ulList);

			bean.replenish("T1-1-1-1", new BigDecimal(1));
			
			ReplenishRequestQueryRemote r = TestUtilities.beanLocator.getStateless(ReplenishRequestQueryRemote.class);
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "destination.name", "T1-1-1-1");
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderState", LOSOrderRequestState.PROCESSING);
			
			TemplateQuery q = new TemplateQuery();
			q.setBoClass(LOSReplenishRequest.class);
			q.addWhereToken(t);
			q.addWhereToken(t2);
			List<LOSReplenishRequest> rr = r.queryByTemplate(d, q);
			if (rr == null || rr.size() != 1){
				fail();
			}
			
			LOSReplenishRequest req = rr.get(0);
			PickOrderFacade pick = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
			List<PickingRequestTO> l = pick.getRawPickingRequest();
						
			for (PickingRequestTO to : l){
				if (to.parentRequest.equals(req.getNumber())){
					LOSPickRequest pr = pick.loadPickingRequest(to);
					LOSPickRequestPosition pos = pr.getPositions().get(0);
					BODTO<StockUnit> bodto = new BODTO<StockUnit>(
							pos.getStockUnit().getId(), pos.getStockUnit().getVersion(),
							pos.getStockUnit().getId());
					manageInv.changeAmount(bodto, 
							pos.getStockUnit().getAmount().subtract(new BigDecimal(1)), new BigDecimal(0), 
							null);
					
					pr = pick.accept(pick.loadPickingRequest(to));
						
					LOSPickRequestPosition p = pr.getPositions().get(0);
					pick.processPickRequestPosition(p, false, p.getStorageLocation().getName(), p.getAmount(), false, false);
					pick.finishPickingRequest(pr, pr.getDestination().getName());
					su = p.getStockUnit();
					
					StockUnitQueryRemote suQuery =TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class) ;
					su = suQuery.queryById(su.getId());
//					assertTrue(su.getLock() == 100);
//					assertTrue(su.getAmount() == 0);
//					assertEquals(TopologyBean.SL_NIRWANA_NAME, ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().getName());
//					assertTrue(su.getReservedAmount() == 0);
//					
					ul = ulQueryRemote.queryByIdentity("T1-1-1-1");
					assertTrue(ul.getStockUnitList().size() == 1);
					StockUnit onDest = ul.getStockUnitList().get(0);
					assertFalse(onDest.isLocked());
					assertTrue(su.getAmount().compareTo(new BigDecimal(1)) >= 0);
				}
			}
			
		} catch (FacadeException e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testReplenishOnPickPlaceTakeUnitload(){
		try {
			
			ManageInventoryFacade manageInv = TestUtilities.beanLocator.getStateless(ManageInventoryFacade.class);
			
			LOSUnitLoadQueryRemote ulQueryRemote = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
			LOSUnitLoad ul = ulQueryRemote.queryByIdentity("T1-1-1-1");
			BODTO<LOSUnitLoad> ulTo = new BODTO<LOSUnitLoad>(ul.getId(), ul.getVersion(), ul.getId());
			List<BODTO<LOSUnitLoad>> ulList = new ArrayList<BODTO<LOSUnitLoad>>();
			ulList.add(ulTo);
			StockUnit su = ul.getStockUnitList().get(0);
			ul = ulQueryRemote.queryById(ul.getId());
			BODTO<StockUnit> suTO = new BODTO<StockUnit>(su.getId(), su.getVersion(), su.getId());
			
			manageInv.changeAmount(suTO, new BigDecimal(0), new BigDecimal(0), null);
			manageInv.sendStockUnitsToNirwanaFromUl(ulList);

			bean.replenish("T1-1-1-1", new BigDecimal(1));
			
			ReplenishRequestQueryRemote r = TestUtilities.beanLocator.getStateless(ReplenishRequestQueryRemote.class);
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "destination.name", "T1-1-1-1");
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderState", LOSOrderRequestState.PROCESSING);
			
			TemplateQuery q = new TemplateQuery();
			q.setBoClass(LOSReplenishRequest.class);
			q.addWhereToken(t);
			q.addWhereToken(t2);
			List<LOSReplenishRequest> rr = r.queryByTemplate(d, q);
			if (rr == null || rr.size() != 1){
				fail();
			}
			
			LOSReplenishRequest req = rr.get(0);
			PickOrderFacade pick = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
			List<PickingRequestTO> l = pick.getRawPickingRequest();
						
			for (PickingRequestTO to : l){
				if (to.parentRequest.equals(req.getNumber())){
					LOSPickRequest pr = pick.loadPickingRequest(to);
					LOSPickRequestPosition pos = pr.getPositions().get(0);
					BODTO<StockUnit> bodto = new BODTO<StockUnit>(
							pos.getStockUnit().getId(), pos.getStockUnit().getVersion(),
							pos.getStockUnit().getId());
					manageInv.changeAmount(bodto, 
							pos.getStockUnit().getAmount().subtract(new BigDecimal(1)), new BigDecimal(0), 
							null);
					
					pr = pick.accept(pick.loadPickingRequest(to));
						
					LOSPickRequestPosition p = pr.getPositions().get(0);
					pick.processPickRequestPosition(p, false, p.getStorageLocation().getName(), p.getAmount(), false, false);
					pick.finishPickingRequest(pr, pr.getDestination().getName());
					su = p.getStockUnit();
					
					StockUnitQueryRemote suQuery =TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class) ;
					su = suQuery.queryById(su.getId());
//					assertTrue(su.getLock() == 100);
//					assertTrue(su.getAmount() == 0);
//					assertEquals(TopologyBean.SL_NIRWANA_NAME, ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().getName());
//					assertTrue(su.getReservedAmount() == 0);
//					
					ul = ulQueryRemote.queryByIdentity("T1-1-1-1");
					assertTrue(ul.getStockUnitList().size() == 1);
					StockUnit onDest = ul.getStockUnitList().get(0);
					assertFalse(onDest.isLocked());
					assertTrue(su.getAmount().compareTo(new BigDecimal(1)) >= 0);
				}
			}
			
		} catch (FacadeException e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
//	public void testCreateCronJob(){
//		bean.createCronJob();
//	}

}
