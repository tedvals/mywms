/*
 * TopologyBean.java
 *
 * Created on 12. September 2006, 09:57
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.example;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnit;
import org.mywms.model.ItemUnitType;
import org.mywms.model.LogItem;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.ItemUnitService;
import org.mywms.service.RoleService;
import org.mywms.service.UserService;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.crud.LogItemCRUDRemote;
import de.linogistix.los.inventory.crud.ItemDataCRUDRemote;
import de.linogistix.los.inventory.crud.ItemUnitCRUDRemote;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.query.PickReceiptPositionQueryRemote;
import de.linogistix.los.inventory.pick.query.PickReceiptQueryRemote;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.pick.service.LOSPickRequestService;
import de.linogistix.los.inventory.query.ExtinguishRequestQueryRemote;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.ItemUnitQueryRemote;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import de.linogistix.los.inventory.query.LOSStockUnitRecordQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSUnitLoadAdviceQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.OrderReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.OrderReceiptQueryRemote;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
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
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSFixedLocationAssignmentQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadRecordQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LogItemQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * Creates an example topology
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless()
public class InventoryTestTopologyBean implements InventoryTestTopologyRemote {

	private static final Logger log = Logger.getLogger(InventoryTestTopologyBean.class);
	// --------------------------------------------------------------------------
	protected Client SYSTEMCLIENT;
	protected Client TESTCLIENT;
	protected Client TESTMANDANT;
	
//	protected UnitLoadType PALETTE;
//	
//	protected UnitLoadType DUMMY_KOMM_ULTYPE;
//	
//	protected UnitLoadType KLT;
//	
//	protected LOSStorageLocationType PALETTENPLATZ_TYP;
//	
//	protected LOSStorageLocationType PALETTENPLATZ_TYP_2;
//	protected LOSStorageLocationType OHNE_BESCHRAENKUNG_TYP;
//	protected LOSStorageLocationType KOMMPLATZ_TYP;
//	
//	protected LOSTypeCapacityConstraint VIELE_PALETTEN;
//	
//	protected LOSTypeCapacityConstraint EINE_PALETTE;
//	
//	protected LOSTypeCapacityConstraint KOMM_FACH_DUMMY_LHM_CONSTR;
//	
//	protected LOSArea STORE_AREA;
//	
//	protected LOSArea KOMM_AREA;
//	
//	protected LOSArea WE_BEREICH;
//	
//	protected LOSArea WA_BEREICH;
//	
//	protected LOSArea CLEARING_BEREICH;
//	
//	protected LOSArea PRODUCTION_BEREICH;
//	protected LOSStorageLocation SL_WE;
//	
//	protected LOSStorageLocation SL_PRODUCTION;
//	
	protected LOSRack TEST_RACK_1;
	
	protected LOSRack TEST_RACK_2;
//	
//	protected LOSStorageLocation SL_WA;
//	
//	protected LOSStorageLocation SL_NIRWANA;
//	
//	protected LOSStorageLocation SL_CLEARING;
	
	protected ItemData ITEM_A1;
	protected ItemData ITEM_A2;
	protected Lot LOT_N1_A1;
	protected Lot LOT_N2_A2;
		
	protected ItemUnit ITEM_KG;
	
	protected ItemUnit ITEM_G;
	
	protected ItemUnit ITEM_STK;
	
	@EJB
    RoleService roleService;
    @EJB
    UserService userService;
    @EJB
    ClientService clientService;
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
	ItemUnitCRUDRemote itemUnitCrud;
	@EJB
	ItemUnitQueryRemote itemUnitQuery;
	@EJB
	LotQueryRemote lotCrud;
	@EJB
	LotQueryRemote lotQuery;
//	@EJB
//	LOSStockUnitRecordCRUDRemote suRecordCrud;
	@EJB
	LOSStockUnitRecordQueryRemote suRecordQuery;
	@EJB
	LOSUnitLoadRecordQueryRemote ulRecordQuery;
	@EJB
	ExtinguishRequestQueryRemote extQuery;
//	@EJB
//	ExtinguishRequestCRUDRemote extCrud;
	@EJB
	LOSPickRequestQueryRemote pickQuery;
	@EJB
	LOSPickRequestService pickService;
	@EJB
	ItemUnitService itemUnitService;
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
	LOSUnitLoadAdviceQueryRemote ulAdvQueryRemote;
	@EJB
	ManageUnitLoadAdviceService manageUlAdviceService;
	
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager em;

	/** Creates a new instance of TopologyBean */
	public InventoryTestTopologyBean() {
	}
	
	//---------------------------------------------------
	
	//-----------------------------------------------------------------

	public void create() throws InventoryTopologyException {
		try {
			
			initClient();
			
			createItemUnit();
			createItemData();
			createFixedLocationAssignments();
			createLots();
			
			em.flush();
		} catch (FacadeException ex) {
			log.error(ex, ex);
			throw new InventoryTopologyException();
		}

	}
	
	public void createItemUnit() throws InventoryTopologyException {
		
		// default unit will be created during setup
	
		try {
			ITEM_G = itemUnitQuery.queryByIdentity(ITEM_G_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_G = new ItemUnit();
			ITEM_G.setUnitName(ITEM_G_NAME);
			ITEM_G.setBaseFactor(1);
			ITEM_G.setUnitType(ItemUnitType.WEIGHT);
			ITEM_G.setBaseUnit(null);
			em.persist(ITEM_G);
		}
		
		try {
			ITEM_KG = itemUnitQuery.queryByIdentity(ITEM_KG_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_KG = new ItemUnit();
			ITEM_KG.setUnitName(ITEM_KG_NAME);
			ITEM_KG.setBaseFactor(1000);
			ITEM_KG.setUnitType(ItemUnitType.WEIGHT);			ITEM_KG.setBaseUnit(ITEM_G);
			em.persist(ITEM_KG);
		}
		
		em.flush();

	}
	public void createItemData() throws InventoryTopologyException {

		ITEM_STK = itemUnitService.getDefault();
		
		try {
			ITEM_A1 = itemQuery.queryByIdentity(ITEM_A1_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_A1 = new ItemData();
			ITEM_A1.setClient(TESTCLIENT);
			ITEM_A1.setName(ITEM_A1_NUMBER);
			ITEM_A1.setNumber(ITEM_A1_NUMBER);
			ITEM_A1.setHandlingUnit(ITEM_STK);
			em.persist(ITEM_A1);
		}

		try {
			ITEM_A2 = itemQuery.queryByIdentity(ITEM_A2_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_A2 = new ItemData();
			ITEM_A2.setClient(TESTCLIENT);
			ITEM_A2.setName(ITEM_A2_NUMBER);
			ITEM_A2.setNumber(ITEM_A2_NUMBER);
			ITEM_A2.setHandlingUnit(ITEM_STK);
			em.persist(ITEM_A2);
		}
	}

	public void createLots() throws InventoryTopologyException {

		GregorianCalendar nextMonth = (GregorianCalendar) GregorianCalendar
				.getInstance();
		nextMonth.add(GregorianCalendar.MONTH, 1);

		GregorianCalendar today = (GregorianCalendar) GregorianCalendar
				.getInstance();

		GregorianCalendar nextNextMonth = (GregorianCalendar) GregorianCalendar
				.getInstance();
		nextNextMonth.add(GregorianCalendar.MONTH, 2);

		try {
			LOT_N1_A1 = lotQuery.queryByIdentity(LOT_N1_A1_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			LOT_N1_A1 = new Lot();
			LOT_N1_A1.setClient(TESTCLIENT);
			LOT_N1_A1.setName(LOT_N1_A1_NAME);
			LOT_N1_A1.setBestBeforeEnd(nextMonth.getTime());
			LOT_N1_A1.setUseNotBefore(today.getTime());
			LOT_N1_A1.setDate(today.getTime());
			LOT_N1_A1.setItemData(ITEM_A1);
			em.persist(LOT_N1_A1);
		}

		try {
			LOT_N2_A2 = lotQuery.queryByIdentity(LOT_N2_A2_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			LOT_N2_A2 = new Lot();
			LOT_N2_A2.setClient(TESTCLIENT);
			LOT_N2_A2.setName(LOT_N2_A2_NAME);
			LOT_N2_A2.setBestBeforeEnd(nextMonth.getTime());
			LOT_N2_A2.setUseNotBefore(today.getTime());
			LOT_N2_A2.setDate(today.getTime());
			LOT_N2_A2.setItemData(ITEM_A2);
			em.persist(LOT_N2_A2);
		}
	}

	public void createFixedLocationAssignments() throws InventoryTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException, BusinessObjectNotFoundException,
			BusinessObjectModifiedException, BusinessObjectMergeException {
		
		TEST_RACK_1 = rackQuery.queryByIdentity(LocationTestTopologyBean.TEST_RACK_1_NAME);
		
		TEST_RACK_2 = rackQuery.queryByIdentity(LocationTestTopologyBean.TEST_RACK_2_NAME);
		
		for (int x = 1; x < 5; x++) {
			for (int y = 1; y < 4; y++) {
				LOSRackLocation rl;
				String locName = TEST_RACK_1.getName() + "-1-" + y + "-" + x;
				
				rl = (LOSRackLocation) slQuery.queryByIdentity(locName);
							
				LOSFixedLocationAssignment ass = assService.getByLocation(rl);
				if(ass == null){
					ass = new LOSFixedLocationAssignment();
					ass.setAssignedLocation(rl);
					ass.setItemData(ITEM_A1);
					em.persist(ass);
				}
			}
		}
		
		for (int x = 1; x < 3; x++) {
			for (int y = 1; y < 4; y++) {
				LOSRackLocation rl;
				String locName = TEST_RACK_2.getName() + "-1-" + y + "-" + x;
				
				rl = (LOSRackLocation) slQuery.queryByIdentity(locName);
				
				LOSFixedLocationAssignment ass = assService.getByLocation(rl);
				if(ass == null){
					ass = new LOSFixedLocationAssignment();
					ass.setAssignedLocation(rl);
					ass.setItemData(ITEM_A1);
					em.persist(ass);
				}
			}
		}	
	}

	// ------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void remove(Class clazz) throws InventoryTopologyException {

		try {
			List<BasicEntity> l;
			l = em.createQuery("SELECT o FROM " + clazz.getName() + " o")
					.getResultList();
			for (Iterator<BasicEntity> iter = l.iterator(); iter.hasNext();) {
				BasicEntity element = iter.next();
				element = (BasicEntity) em.find(clazz, element.getId());
				em.remove(element);
			}
			em.flush();

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	private void initClient() throws InventoryTopologyException{
		try {
			TESTCLIENT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			TESTCLIENT = em.find(Client.class, TESTCLIENT.getId());
			
			TESTMANDANT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			TESTMANDANT = em.find(Client.class, TESTMANDANT.getId());
			
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}		
	}

	public void clear() throws InventoryTopologyException {
		try {
			initClient();

			clearGoodsOutRequest();
			clearPickReceipts();
			clearExtinguishRequests();
			clearPickingRequests();
			clearOrderRequests();
			clearStorageRequests();
			clearUnitloadAdvices();
			clearGoodsReceipts();
			
			clearAdvices();
			
			clearStockUnitLabels();
			clearOrderReceipt();
			
			clearStockUnits();
			
			clearLogItems();
			clearStockUnitRecords();
			
			clearLots();
			clearFixedLocationAssignments();
			clearItemData();
			//clearItemUnit();
			
		} catch (InventoryTopologyException ex) {
			throw ex;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearAdvices() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSAdvice.class);

			List<LOSAdvice> l = adQuery.queryByTemplate(d, q);
			for (LOSAdvice a : l) {
				a = em.find(LOSAdvice.class, a.getId());
				em.remove(a);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	private void clearStockUnitLabels() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(StockUnitLabel.class);

			List<StockUnitLabel> l = suLabelQuery.queryByTemplate(d, q);
			for (StockUnitLabel u : l) {
				u = em.find(StockUnitLabel.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	public void clearGoodsReceipts() throws InventoryTopologyException {
		initClient();
		try {
			
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");
			t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSGoodsReceiptPosition.class);

			List<LOSGoodsReceiptPosition> l = goodsRecPosQuery.queryByTemplate(d, q);
			for (LOSGoodsReceiptPosition pp : l) {
				pp = em.find(LOSGoodsReceiptPosition.class, pp.getId());
					em.remove(pp);
			}
			
			//--------------
			
			d = new QueryDetail(0, Integer.MAX_VALUE);
			t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSGoodsReceipt.class);

			List<LOSGoodsReceipt> re = goodsRecQuery.queryByTemplate(d, q);
			for (LOSGoodsReceipt u : re) {
				u = em.find(LOSGoodsReceipt.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearStorageRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSStorageRequest.class);

			List<LOSStorageRequest> l = storeReqQuery.queryByTemplate(d, q);
			for (LOSStorageRequest u : l) {
				u = em.find(LOSStorageRequest.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	private void clearPickReceipts() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(PickReceipt.class);
			
			List<PickReceipt> rs;
			rs = pickReceiptQuery.queryByTemplate(d, q);
			for (PickReceipt u : rs) {
				u = em.find(PickReceipt.class, u.getId());
				List<Long> ids = new ArrayList<Long>();
				for (PickReceiptPosition pos : u.getPositions()){
					ids.add(pos.getId());
				}
				
				for (Long id : ids){
					PickReceiptPosition pos = em.find(PickReceiptPosition.class, id);
					em.remove(pos);
				}
				
				u = em.find(PickReceipt.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	public void clearOrderRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSOrderRequest.class);
			List<LOSOrderRequest> l;
			l = orderReqQuery.queryByTemplate(d, q);
			
			
			
			for (LOSOrderRequest u : l) {
				u = em.find(LOSOrderRequest.class, u.getId());
				Vector<Long> ids = new Vector<Long>();	
				for (LOSOrderRequestPosition pos : u.getPositions()){
					ids.add(pos.getId());
				}
				for (Long id : ids){
					LOSOrderRequestPosition pos = em.find(LOSOrderRequestPosition.class, id);
					em.remove(pos);
				}
				
				u.setPositions(new ArrayList<LOSOrderRequestPosition>());
				
				em.remove(u);
				
				
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}
	
	public void clearUnitloadAdvices() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSUnitLoadAdvice.class);
			List<LOSUnitLoadAdvice> l;
			l = ulAdvQueryRemote.queryByTemplate(d, q);
			
			for (LOSUnitLoadAdvice u : l) {
				u = em.find(LOSUnitLoadAdvice.class, u.getId());
				manageUlAdviceService.deleteUnitLoadAdvice(u);
			}
			
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearGoodsOutRequest() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
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
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearExtinguishRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(ExtinguishRequest.class);

			List<ExtinguishRequest> l = extQuery.queryByTemplate(d, q);
			for (ExtinguishRequest u : l) {
				u = em.find(ExtinguishRequest.class, u.getId());
				List<LOSPickRequestPosition> pos = pickposService.getByPickRequest(u);
				for (LOSPickRequestPosition p : pos){
					p = em.find(LOSPickRequestPosition.class, p.getId());
					em.remove(p);
				}
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	public void clearPickingRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSPickRequest.class);			
			
			List<LOSPickRequest> l = pickQuery.queryByTemplate(d, q);
			for (LOSPickRequest u : l) {
				pickService.delete(u);
			}
			
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	private void clearStockUnitRecords() throws InventoryTopologyException {
		// Delete LogItems
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSStockUnitRecord.class);

			List<LOSStockUnitRecord> l = suRecordQuery.queryByTemplate(d, q);
			for (LOSStockUnitRecord u : l) {
				u = em.find(LOSStockUnitRecord.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}

	}
	
	private void clearStockUnits() throws InventoryTopologyException {

		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(StockUnit.class);

			List<StockUnit> l = suQuery.queryByTemplate(d, q);
			for (StockUnit rl : l) {
				try {
					rl = suQuery.queryById(rl.getId());
					rl = em.find(StockUnit.class, rl.getId());
					em.remove(rl);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
				}
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}

	}

	private void clearOrderReceipt() throws InventoryTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(OrderReceiptPosition.class);
			
			List<OrderReceiptPosition> pos = orderReceiptPositionQueryRemote.queryByTemplate(d, q);
			for (OrderReceiptPosition p : pos){
				p = em.find(OrderReceiptPosition.class, p.getId());
				em.remove(p);
				em.flush();
			}
			
			d = new QueryDetail(0, Integer.MAX_VALUE);
			t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);

			t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(OrderReceipt.class);
			
			List<OrderReceipt> recs = orderReceiptQueryRemote.queryByTemplate(d, q);
			
			for (OrderReceipt r : recs){
				r = em.find(OrderReceipt.class, r.getId());
				em.remove(r);
				em.flush();
			}
			
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}

	}

	public void clearLogItems() throws InventoryTopologyException {
		// Delete LogItems
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LogItem.class);

			List<LogItem> l = logQuery.queryByTemplate(d, q);
			for (LogItem u : l) {
				u = em.find(LogItem.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}
	}
	
	public void clearFixedLocationAssignments() throws InventoryTopologyException,
														BusinessObjectExistsException, BusinessObjectCreationException,
														BusinessObjectSecurityException, BusinessObjectNotFoundException,
														BusinessObjectModifiedException, BusinessObjectMergeException {

		try{
			TEST_RACK_1 = rackQuery.queryByIdentity(LocationTestTopologyBean.TEST_RACK_1_NAME);
		
			for (int x = 1; x < 5; x++) {
				for (int y = 1; y < 4; y++) {
					LOSRackLocation rl;
					String locName = TEST_RACK_1.getName() + "-1-" + y + "-" + x;
					
					rl = (LOSRackLocation) slQuery.queryByIdentity(locName);
								
					LOSFixedLocationAssignment ass = assService.getByLocation(rl);
					
					if(ass != null){
						em.remove(ass);
					}
				}
			}
			
			em.flush();
			
		} catch (BusinessObjectNotFoundException ex){
			log.warn("Skip: " + ex.getMessage());
		}
		
		try{
		
			TEST_RACK_2 = rackQuery.queryByIdentity(LocationTestTopologyBean.TEST_RACK_2_NAME);
			
			
			
			for (int x = 1; x < 3; x++) {
				for (int y = 1; y < 4; y++) {
					LOSRackLocation rl;
					String locName = TEST_RACK_2.getName() + "-1-" + y + "-" + x;
					
					rl = (LOSRackLocation) slQuery.queryByIdentity(locName);
					
					LOSFixedLocationAssignment ass = assService.getByLocation(rl);
					
					if(ass != null){
						em.remove(ass);
					}
				}
			}	
			
			em.flush();
		} catch (BusinessObjectNotFoundException ex){
			log.warn("Skip: " + ex.getMessage());
		}
	}

	public void clearItemData() throws InventoryTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(ItemData.class);

			List<ItemData> l = itemQuery.queryByTemplate(d, q);
			for (ItemData u : l) {
				u = em.find(ItemData.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}
	}

	public void clearItemUnit() throws InventoryTopologyException {
		
		try {
			ITEM_KG = itemUnitQuery.queryByIdentity(ITEM_KG_NAME);
			ITEM_KG = em.find(ItemUnit.class, ITEM_KG.getId());
			em.remove(ITEM_KG);
			em.flush();
		} catch (BusinessObjectNotFoundException ex) {
			//
		}
		
		try {
			ITEM_G = itemUnitQuery.queryByIdentity(ITEM_G_NAME);
			ITEM_G = em.find(ItemUnit.class, ITEM_G.getId());
			em.remove(ITEM_G);
			em.flush();
		} catch (BusinessObjectNotFoundException ex) {
			//
		}

	}
	
	public void clearLots() throws InventoryTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(Lot.class);

			List<Lot> l = lotQuery.queryByTemplate(d, q);
			for (Lot u : l) {
				
				u = em.find(Lot.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}
	}
	


}
