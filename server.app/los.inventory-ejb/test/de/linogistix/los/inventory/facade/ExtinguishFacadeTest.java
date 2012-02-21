/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.query.ExtinguishOrderQueryRemote;
import de.linogistix.los.inventory.query.ExtinguishRequestQueryRemote;
import de.linogistix.los.inventory.query.dto.ExtinguishOrderTO;
import de.linogistix.los.inventory.service.ExtinguishOrderService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.test.TestUtilities;

public class ExtinguishFacadeTest extends TestCase {

	private static final Logger logger = Logger
			.getLogger(ExtinguishFacadeTest.class);

	ManageExtinguishFacade exControllCenterService;

	ExtinguishFacade extinguishFacade;

	ExtinguishOrderService extinguishOrderService;

	ManageInventoryFacade manageInventoryFacade;

	ExtinguishOrderQueryRemote extinguishOrderQueryRemote;

	ExtinguishRequestQueryRemote extinguishRequestQueryRemote;

	public static final String UNITLOAD_NAME_1 = "Test Unitload 1";

	public static final String UNITLOAD_NAME_2 = "Test Unitload 2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		exControllCenterService = TestUtilities.beanLocator
				.getStateless(ManageExtinguishFacade.class);

		extinguishOrderQueryRemote = TestUtilities.beanLocator
				.getStateless(ExtinguishOrderQueryRemote.class);

		extinguishFacade = TestUtilities.beanLocator
				.getStateless(ExtinguishFacade.class);

		manageInventoryFacade = TestUtilities.beanLocator
				.getStateless(ManageInventoryFacade.class);

		extinguishOrderService = TestUtilities.beanLocator
				.getStateless(ExtinguishOrderService.class);

		manageInventoryFacade.createStockUnitOnStorageLocation(
				CommonTestTopologyRemote.TESTCLIENT_NUMBER, LocationTestTopologyRemote.TEST_RACK_1_NAME
						+ "-1-1-1", ManageInventoryFacadeBeanTest.TEST_ITEM,
				ManageInventoryFacadeBeanTest.TEST_LOT, new BigDecimal(12), UNITLOAD_NAME_1);

		manageInventoryFacade.createStockUnitOnStorageLocation(
				CommonTestTopologyRemote.TESTCLIENT_NUMBER, LocationTestTopologyRemote.TEST_RACK_1_NAME
						+ "-1-1-2", ManageInventoryFacadeBeanTest.TEST_ITEM,
				ManageInventoryFacadeBeanTest.TEST_LOT, new BigDecimal(12), UNITLOAD_NAME_2);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExtinguishRequest() throws Exception {

		ExtinguishOrderTO orderTO = (ExtinguishOrderTO) exControllCenterService
				.createExtinguishOrder(ManageInventoryFacadeBeanTest.TEST_LOT, ManageInventoryFacadeBeanTest.TEST_ITEM,
						CommonTestTopologyRemote.TESTCLIENT_NUMBER, Calendar.getInstance()
								.getTime());
		LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator.getStateless(LOSStorageLocationQueryRemote.class);
		LOSStorageLocation nirwana = slQuery.getNirwana();
		assertTrue(orderTO != null);
		assertTrue(orderTO.getDestinationName().equals(nirwana.getName()));
		assertTrue(orderTO.getLotName()
				.equals(ManageInventoryFacadeBeanTest.TEST_LOT));
		assertTrue(orderTO.getOrderState().equals(LOSOrderRequestState.RAW));

		// Start the Extinguish order.
		exControllCenterService.startExtinguishOrder(orderTO);
		ExtinguishOrder eo = extinguishOrderQueryRemote.queryById(orderTO.getId());
		assertTrue(eo.getOrderState().equals(LOSOrderRequestState.PROCESSING));

		// check the created picking request
		List<ExtinguishRequest> requests = extinguishFacade
				.getRawExtinguishRequest();
		assertTrue(requests.size() == 1);
		for (ExtinguishRequest request : requests){
        	if (! (request.getClient().getNumber().equals(CommonTestTopologyRemote.TESTCLIENT_NUMBER)
            		|| request.getClient().getNumber().equals(CommonTestTopologyRemote.TESTMANDANT_NUMBER))){
            		logger.warn("!!!! Not assigned to a test client: " + request.toDescriptiveString());
            		continue;
            	}
			assertTrue(request.getDestination().getName().equals(
					nirwana.getName()));
	
			assertTrue(request.getLot() != null);
	
			request = extinguishFacade.loadExtinguishRequest(request);
			assertTrue(request.getPositions().size() > 0);
	
			List<LOSPickRequestPosition> positions = request.getPositions();
			for (LOSPickRequestPosition position : positions) {
				if (! (request.getClient().getNumber().equals(CommonTestTopologyRemote.TESTCLIENT_NUMBER)
	            		|| request.getClient().getNumber().equals(CommonTestTopologyRemote.TESTMANDANT_NUMBER))){
	            		logger.warn("!!!! Not assigned to a test client: " + request.toDescriptiveString());
	            		continue;
	            	}

				assertTrue(position.getStockUnit().getReservedAmount().compareTo(position.getStockUnit().getAmount()) == 0 );
				
			}
			
			PickOrderFacade pickOrderFacade = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
			pickOrderFacade.finishPickingRequest(request, request.getDestination().getName());
			
	//		Might Fail if VM and host have different system clock
			extinguishOrderService.getRipe();

		}
	}
}
