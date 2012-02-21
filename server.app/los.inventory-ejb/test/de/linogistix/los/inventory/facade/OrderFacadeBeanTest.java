/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.test.TestUtilities;

/**
 *
 * @author trautm
 */
public class OrderFacadeBeanTest extends TestCase {
    
    private static final Logger logger = Logger.getLogger(OrderFacadeBeanTest.class);
    
    OrderFacade bean;
    
    public OrderFacadeBeanTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bean = TestUtilities.beanLocator.getStateless(OrderFacade.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testOrder(){
        
         String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         String orderRef = "TEST 1";
         String documentUrl ="";
         String labelUrl = "";
         String destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
         
         OrderPositionTO to = new OrderPositionTO();
         to.amount = new BigDecimal(10);
         to.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
         to.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
         to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         
         OrderPositionTO to2 = new OrderPositionTO();
         to2.amount = new BigDecimal(5);
         to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
         to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
         to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         
         OrderPositionTO[] positions = new OrderPositionTO[]{
        		 to,
        		 to2
         };
         
         try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
		
		clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        orderRef = "TEST 2";
        documentUrl ="";
        labelUrl = "";
        destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
        
        to = new OrderPositionTO();
        to.amount = new BigDecimal(99800);
        to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
        to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
        to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(5);
        to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
        to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        positions = new OrderPositionTO[]{
       		 to,
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
    }
    
    public void testOrderMultiClient(){
        
        String clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        String orderRef = "TEST 1";
        String documentUrl ="";
        String labelUrl = "";
        String destination = LocationTestTopologyRemote.SL_WA_TESTMANDANT_NAME;
        
        OrderPositionTO to = new OrderPositionTO();
        to.amount = new BigDecimal(10);
        to.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to.clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        
        OrderPositionTO to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(21);
        to2.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to2.clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        
        OrderPositionTO[] positions = new OrderPositionTO[]{
       		 to,
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
		
		clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
       orderRef = "TEST 2";
       documentUrl ="";
       labelUrl = "";
       destination = LocationTestTopologyRemote.SL_WA_TESTMANDANT_NAME;
       
       to = new OrderPositionTO();
       to.amount = new BigDecimal(99800);
       to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
       to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
       to.clientRef = clientRef;
       
       to2 = new OrderPositionTO();
       to2.amount = new BigDecimal(10);
       to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
       to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
       to2.clientRef = clientRef;
       
       positions = new OrderPositionTO[]{
      		 to,
      		 to2
       };
       
       try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
			
		} catch (FacadeException e) {
			logger.error(e,e);
			fail(e.getMessage());
		}
   }
    
    public void testCancellation(){
    	
    	LOSPickRequestQueryRemote pickQuery = TestUtilities.beanLocator.getStateless(LOSPickRequestQueryRemote.class);
    	ClientQueryRemote clQuery = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class);
    	String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        String orderRef = "TEST CANCELLATION";
        String documentUrl ="";
        String labelUrl = "";
        String destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
        
        Client c = null;
		try {
			c = clQuery.queryByIdentity(clientRef);
		} catch (BusinessObjectNotFoundException e1) {
			logger.error(e1.getMessage(), e1);
			fail(e1.getMessage());
		}
        
        OrderPositionTO to = new OrderPositionTO();
        to.amount = new BigDecimal(1);
        to.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        OrderPositionTO to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(1);
        to2.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        OrderPositionTO[] positions = new OrderPositionTO[]{
       		 to,
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
			
			OrderRequestQueryRemote orderQuery = TestUtilities.beanLocator.getStateless(OrderRequestQueryRemote.class);
			LOSOrderRequest r = orderQuery.queryByRequestId(c, orderRef);
			BODTO<LOSOrderRequest> dto = new BODTO<LOSOrderRequest>(r.getId(), r.getVersion(), r.getNumber());
			
			List<LOSPickRequest> picks = pickQuery.queryByParentRequest(dto.getName());
			assertEquals(1, picks.size());
			
			bean.removeOrder(dto);
			try{
				r = orderQuery.queryByIdentity(orderRef);
				fail("Has not been removed");
			} catch (BusinessObjectNotFoundException ex){
				//OK
			}
			
			try{
				pickQuery.queryById(picks.get(0).getId());
				fail("Has not been removed");
			} catch (BusinessObjectNotFoundException ex){
				//OK
			}
			
		} catch (FacadeException e) {
			logger.error(e,e);
			fail(e.getMessage());
		}
    	
    }
    
    /**
     * Don't use an specific ordertyep in {@link OrderFacade}: Is it calulated correct
     * depending on the destination?
     */
    public void testOrderToProduction(){
        
		OrderRequestQueryRemote orderQuery = TestUtilities.beanLocator.getStateless(OrderRequestQueryRemote.class);
		ClientQueryRemote clQuery = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class);
    	
        Client c = null;

        String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        String orderRef = "PROD 1";
        String documentUrl ="";
        String labelUrl = "";
        String destination = LocationTestTopologyRemote.SL_PRODUCTION_TESTCLIENT_NAME;
        
        OrderPositionTO to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(3);
        to2.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to2.clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        
        try {
			c = clQuery.queryByIdentity(clientRef);
		} catch (BusinessObjectNotFoundException e1) {
			logger.error(e1.getMessage(), e1);
			fail(e1.getMessage());
		}
		
        OrderPositionTO[] positions = new OrderPositionTO[]{
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination,OrderType.TO_PRODUCTION, new Date(),true, "A Test Order");
			LOSOrderRequest r = orderQuery.queryByRequestId(c,orderRef);
			assertEquals(OrderType.TO_PRODUCTION, r.getOrderType());
			
		} catch (FacadeException e) {
			logger.error(e,e);
			fail(e.getMessage());
		}
		
		
   }

    
   
}
