/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.Lot;

import de.linogistix.los.crud.LotCRUDRemote;
import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.test.TestUtilities;

public class ManageExtinguishFacadeBeanTest extends TestCase {

	private static final Logger log = Logger.getLogger(ManageExtinguishFacadeBeanTest.class);
	
	ManageExtinguishFacade bean;
	
	ManageInventoryFacade manageInv;
	
	Client c;
	protected void setUp() throws Exception {
		super.setUp();
		
		this.bean = TestUtilities.beanLocator.getStateless(ManageExtinguishFacade.class);
		this.manageInv = TestUtilities.beanLocator.getStateless(ManageInventoryFacade.class);
		ClientQueryRemote clQuery = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class);
		c = clQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetTooOld(){
		
		
		String LOT_TOO_OLD = "tooOld";
		GregorianCalendar today = new GregorianCalendar();
		today.set(GregorianCalendar.HOUR, 9);
		today.set(GregorianCalendar.MINUTE, 0);
		
		GregorianCalendar yesterday = new GregorianCalendar();
		yesterday.set(GregorianCalendar.HOUR, 9);
		yesterday.set(GregorianCalendar.MINUTE, 0);
		yesterday.add(GregorianCalendar.DAY_OF_MONTH, -1);
		
		GregorianCalendar lastweek = new GregorianCalendar();
		lastweek.set(GregorianCalendar.HOUR, 9);
		lastweek.set(GregorianCalendar.MINUTE, 0);
		lastweek.add(GregorianCalendar.DAY_OF_MONTH, -7);
		
		manageInv.createAvis(
				CommonTestTopologyRemote.TESTCLIENT_NUMBER,
				InventoryTestTopologyRemote.ITEM_A1_NUMBER,
				LOT_TOO_OLD, 
				new BigDecimal(1000), today.getTime(), yesterday.getTime(), lastweek.getTime(), true);
		List<Lot> lots = bean.getTooOld(c);
		
		boolean found = false;
		for (Lot lot : lots){
			if (lot.getName().equals(LOT_TOO_OLD)){
				found = true;
				break;
			} else{
				found = false;
			}
		}
		assertTrue(found);
		
	}
	
	public void testGetNotToUse(){
		
		LotQueryRemote lotQuery = TestUtilities.beanLocator.getStateless(LotQueryRemote.class);
		try{
			String LOT_NOT_TO_USE = "too young";
			GregorianCalendar today = new GregorianCalendar();
			today.set(GregorianCalendar.HOUR, 9);
			today.set(GregorianCalendar.MINUTE, 0);
			
			GregorianCalendar yesterday = new GregorianCalendar();
			yesterday.set(GregorianCalendar.HOUR, 9);
			yesterday.set(GregorianCalendar.MINUTE, 0);
			yesterday.add(GregorianCalendar.DAY_OF_MONTH, -1);
			
			GregorianCalendar tomorrow = new GregorianCalendar();
			tomorrow.set(GregorianCalendar.HOUR, 9);
			tomorrow.set(GregorianCalendar.MINUTE, 0);
			tomorrow.add(GregorianCalendar.DAY_OF_MONTH, +1);
			
			GregorianCalendar nextweek = new GregorianCalendar();
			nextweek.set(GregorianCalendar.HOUR, 9);
			nextweek.set(GregorianCalendar.MINUTE, 0);
			nextweek.add(GregorianCalendar.DAY_OF_MONTH, +7);
			
			manageInv.createAvis(
					CommonTestTopologyRemote.TESTCLIENT_NUMBER,
					InventoryTestTopologyRemote.ITEM_A1_NUMBER,
					LOT_NOT_TO_USE, 
					new BigDecimal(1000), today.getTime(), nextweek.getTime(), tomorrow.getTime(), true);
			Client c = TopologyBeanTest.getTESTCLIENT();
			Lot lot = lotQuery.queryByIdentity(c,LOT_NOT_TO_USE).get(0);
			assertEquals(lot.getLock(), LotLockState.LOT_TOO_YOUNG.getLock());
			
			List<Lot> lots = bean.getNotToUse(c);
			
			boolean found = false;
			for (Lot l : lots){
				if (l.getName().equals(LOT_NOT_TO_USE)){
					found = true;
					break;
				} else{
					found = false;
				}
			}
			assertTrue(found);
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			fail(t.getMessage());
		}
	}
	
	public void testGetToUseFromNow(){
		
		LotCRUDRemote lotCrud = TestUtilities.beanLocator.getStateless(LotCRUDRemote.class);
		LotQueryRemote lotQuery = TestUtilities.beanLocator.getStateless(LotQueryRemote.class);
		
		try{
			String LOT_USE_NOW = "use now";
			GregorianCalendar today = new GregorianCalendar();
			today.set(GregorianCalendar.HOUR, 9);
			today.set(GregorianCalendar.MINUTE, 0);
			
			GregorianCalendar nextweek = new GregorianCalendar();
			nextweek.set(GregorianCalendar.HOUR, 9);
			nextweek.set(GregorianCalendar.MINUTE, 0);
			nextweek.add(GregorianCalendar.DAY_OF_MONTH, +7);
			
			manageInv.createAvis(
					CommonTestTopologyRemote.TESTCLIENT_NUMBER,
					InventoryTestTopologyRemote.ITEM_A1_NUMBER,
					LOT_USE_NOW, 
					new BigDecimal(1000), today.getTime(), nextweek.getTime(), today.getTime(), true);
			Client c = TopologyBeanTest.getTESTCLIENT();
			Lot lot = lotQuery.queryByIdentity(c,LOT_USE_NOW).get(0);
			lot.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
			lotCrud.update(lot);
			
			List<Lot> lots = bean.getToUseFromNow(c);
			boolean found = false;
			
			for (Lot l : lots){
				if (l.getName().equals(LOT_USE_NOW)){
					found = true;
					break;
				} else{
					found = false;
				}
			}
			assertTrue(found);
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			fail(t.getMessage());
		}
	}


}
