/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.facade;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemDataNumber;
import org.mywms.model.ItemUnit;
import org.mywms.model.ItemUnitType;
import org.mywms.model.Role;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.model.Zone;
import org.mywms.service.AreaService;
import org.mywms.service.ClientService;
import org.mywms.service.ItemDataService;
import org.mywms.service.ItemUnitService;
import org.mywms.service.RoleService;
import org.mywms.service.UserService;
import org.mywms.service.UserServiceException;
import org.mywms.service.ZoneService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.facade.OrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.location.entityservice.LOSLocationClusterService;
import de.linogistix.los.location.entityservice.LOSRackService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.reference.res.ProjectBundleResolver;
import de.linogistix.los.stocktaking.component.LOSStockTakingProcessComp;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class RefTopologyFacadeBean implements RefTopologyFacade {

	@EJB
	private ItemUnitService unitService;
	
	@EJB
	private ClientService clientService;

	@EJB
	private ItemUnitService itemUnitService;

	@EJB
	private UserService userService;
	
	@EJB
	private RoleService roleService;

	@EJB
	private ZoneService zoneService;
	
	@EJB
	private AreaService areaService;

	@EJB
	private LOSStorageLocationTypeService locationTypeService;

	@EJB
	private QueryTypeCapacityConstraintService capaService;
	
	@EJB
	private LOSStorageLocationService slService;
	
	@EJB
	private LOSRackService rackService;
	
	@EJB
	private ItemDataService itemDataService;
	
	@EJB
	private QueryFixedAssignmentService fixedService;
	
	@EJB
	private LOSLocationClusterService lcService;
	
	@EJB
	private QueryUnitLoadTypeService ultService;
	
	@EJB
	private LOSUnitLoadService ulService;
	
	@EJB
	private QueryUnitLoadService ulQueryService;
	
	@EJB
	private ContextService contextService;

	@EJB
	private QueryStorageLocationService locService;
	
	@EJB
	private ManageInventoryFacade inventoryFacade;

	@EJB
	private OrderFacade orderFacade;
	
	@EJB
	private ItemDataNumberService eanService;

	@EJB
	private LOSStockTakingProcessComp stService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	private static final Logger log = Logger.getLogger(RefTopologyFacadeBean.class);
	
	public void createBasicTopology() throws FacadeException {
		
		log.info("Create clients...");
		Client sys = clientService.getSystemClient();
		sys.setName( resolve("SystemClientName") );
		sys.setNumber( resolve("SystemClientNumber") );
		sys.setCode("01");

		log.info("Create Roles...");
		createRole(org.mywms.globals.Role.ADMIN_STR);

		createRole(org.mywms.globals.Role.OPERATOR_STR);
		
		createRole(org.mywms.globals.Role.FOREMAN_STR);

		createRole(org.mywms.globals.Role.INVENTORY_STR);
	
		log.info("Create ItemUnits...");
		ItemUnit pce = unitService.getDefault();
		pce.setUnitName( resolve("UnitPcsName") );
		
		
		log.info("Create User...");

		createUser(sys, "deutsch", "Deutsch", "de", org.mywms.globals.Role.ADMIN_STR);
		createUser(sys, "english", "English", "en", org.mywms.globals.Role.ADMIN_STR);
		createUser(sys, "français", "Français", "fr", org.mywms.globals.Role.ADMIN_STR);
		createUser(sys, "francais", "Français", "fr", org.mywms.globals.Role.ADMIN_STR);

		log.info("Create Zones...");
		
		createZone(sys, "A");
		createZone(sys, "B");
		createZone(sys, "C");

		log.info("Create Areas...");

		LOSArea giArea = createArea(sys, resolve("AreaNameGoodsIn"), LOSAreaType.GOODS_IN);
		LOSArea goArea = createArea(sys, resolve("AreaNameGoodsOut"), LOSAreaType.GOODS_OUT);
		createArea(sys, resolve("AreaNameStore"), LOSAreaType.STORE);
		createArea(sys, resolve("AreaNameClearing"), LOSAreaType.QUARANTINE);

		log.info("Create LocationTypes...");
		
		LOSStorageLocationType pType = locationTypeService.getDefaultStorageLocationType();
		
		LOSStorageLocationType sysType = locationTypeService.getNoRestrictionType();
		
		locationTypeService.getAttachedUnitLoadType();
		
		log.info("Create UnitLoadTypes...");

		UnitLoadType defUlType = ultService.getDefaultUnitLoadType();

		log.info("Create CapacityConstraints...");

		createCapa( resolve("KapaPallet"), pType, defUlType, 1, false);

		
		log.info("Create StorageLocations...");
		
		List<LOSStorageLocation> list = locService.getListByAreaType(LOSAreaType.GOODS_IN);
		if( list == null || list.size() == 0 ) {
			createStorageLocation(sys, resolve("LocationWe"), giArea, sysType, null);
		}
		list = locService.getListByAreaType(LOSAreaType.GOODS_OUT);
		if( list == null || list.size() == 0 ) {
			createStorageLocation(sys, resolve("LocationWa"), goArea, sysType, null);
		}
		slService.getClearing();
		slService.getNirwana();

	}
	
	
	
	public void createDemoTopology() throws FacadeException {
		createBasicTopology();
		
		log.info("Create clients...");
		
		Client sys = clientService.getSystemClient();

		
		log.info("Create ItemUnits...");
		ItemUnit pce = unitService.getDefault();
		
		ItemUnit gramm = createItemUnit(resolve("UnitGrammName"), ItemUnitType.WEIGHT, 1, null);
		
		createItemUnit( resolve("UnitKiloGrammName"), ItemUnitType.WEIGHT, 1000, gramm);
		
		ItemUnit pack = createItemUnit( resolve("UnitPackName"), ItemUnitType.PIECE, 1, null);
		

		
		log.info("Create Zones...");
		
		Zone zoneSysA = createZone(sys, "A");


		
		log.info("Create Areas...");

		LOSArea waArea = createArea(sys, resolve("AreaNameGoodsOut"), LOSAreaType.GOODS_OUT);

		LOSArea rackArea = createArea(sys, resolve("AreaNameStore"), LOSAreaType.STORE);
		
		LOSArea pickArea = createArea(sys, resolve("AreaNamePicking"), LOSAreaType.PICKING);
		
		
		
		
		log.info("Create LocationTypes...");
		
		LOSStorageLocationType pType = locationTypeService.getDefaultStorageLocationType();
		pType.setName( resolve("LocationTypePallet") );
		
		LOSStorageLocationType sysType = locationTypeService.getNoRestrictionType();
		sysType.setName( resolve("LocationTypeSystem") );

		LOSStorageLocationType fixType = locationTypeService.getAttachedUnitLoadType();
		fixType.setName( resolve("LocationTypePicking") );

		LOSStorageLocationType fType = createLocationType(sys, resolve("LocationTypeShelf") );
		

		log.info("Create UnitLoadTypes...");

		UnitLoadType euro140 = ultService.getDefaultUnitLoadType();
		euro140.setName( resolve("UnitLoadTypeEuro") );
		euro140.setDepth(BigDecimal.valueOf(1.20));
		euro140.setWidth(BigDecimal.valueOf(0.80));
		euro140.setHeight(BigDecimal.valueOf(1.40));
		euro140.setWeight(BigDecimal.valueOf(25));
		euro140.setLiftingCapacity(BigDecimal.valueOf(500));

		UnitLoadType kltType60 = createUnitLoadType(sys, resolve("UnitLoadTypeBox6040"), 0.40, 0.60, 0.25, 20, 0.7);
		
		UnitLoadType kltType30 = createUnitLoadType(sys, resolve("UnitLoadTypeBox3040"), 0.40, 0.30, 0.25, 20, 0.5);
		
		UnitLoadType picking = ultService.getPickLocationUnitLoadType();
		picking.setName( resolve("UnitLoadTypePicking") );
		
		
		log.info("Create CapacityConstraints...");

		createCapa( resolve("KapaPallet"), pType, euro140, 1, true);
		
		createCapa( resolve("KapaBox60Shelf"), fType, kltType60, 1, true);
		
		createCapa( resolve("KapaBox30Shelf"), fType, kltType30, 2, true);
		
		createCapa(resolve("KapaPicking"), fixType, picking, 1, true);
		
	

		log.info("Create StorageLocations...");
		
		LOSStorageLocation locationGo = createStorageLocation(sys, resolve("LocationWa"), waArea, sysType, null);
		

		log.info("Create RackLocations...");

		createRack(sys, resolve("RackNamePallet")+" A1", "A1", 5, 3, 2, rackArea, pType, null, zoneSysA);
		manager.flush();
		createRack(sys, resolve("RackNameShelf")+" A2", "A2", 3, 6, 2, rackArea, fType, null, zoneSysA);
		manager.flush();

		
		
		log.info("Create Picklocations...");
		
		createRack(sys, resolve("RackNamePickFixed"), "P1", 2, 3, 2, pickArea, fixType, null, zoneSysA);
		manager.flush();

		

		log.info("Create ItemData...");
		
		ItemData printer1 = createItemData(sys, resolve("CustomerItemData1Number"), resolve("CustomerItemData1Name"), resolve("CustomerItemData1Desc"), pce, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData printer2 = createItemData(sys, resolve("CustomerItemData2Number"), resolve("CustomerItemData2Name"), resolve("CustomerItemData2Desc"), pce, false, SerialNoRecordType.GOODS_OUT_RECORD, zoneSysA, euro140);
		ItemData paper1 = createItemData(sys, resolve("CustomerItemData3Number"), resolve("CustomerItemData3Name"), resolve("CustomerItemData3Desc"), pack, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		createEAN(paper1, "12345678");
		ItemData paper2 = createItemData(sys, resolve("CustomerItemData4Number"), resolve("CustomerItemData4Name"), resolve("CustomerItemData4Desc"), pack, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData toner1 = createItemData(sys, resolve("CustomerItemData5Number"), resolve("CustomerItemData5Name"), resolve("CustomerItemData5Desc"), pce, true, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		createEAN(toner1, "12312312");
		ItemData toner2 = createItemData(sys, resolve("CustomerItemData6Number"), resolve("CustomerItemData6Name"), resolve("CustomerItemData6Desc"), pce, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData screw1 = createItemData(sys, resolve("CustomerItemData7Number"), resolve("CustomerItemData7Name"), resolve("CustomerItemData7Desc"), gramm, false, SerialNoRecordType.NO_RECORD, zoneSysA, kltType60);
		ItemData screw2 = createItemData(sys, resolve("CustomerItemData8Number"), resolve("CustomerItemData8Name"), resolve("CustomerItemData8Desc"), gramm, false, SerialNoRecordType.NO_RECORD, zoneSysA, kltType60);
		
		
		
		log.info("Create FixedLocations...");
		
		createFixLocation( sys, "P1-011-1", paper1, fixType);
		createFixLocation( sys, "P1-012-1", paper2, fixType);
		

		
		
		log.info("Create Stock...");
		
		createStock(sys, "P1-011-1", paper1, new BigDecimal(200),"P1-011-1", null);
		
		createStock(sys, "A1-011-1", paper1, new BigDecimal(200),"000001", null);
		createStock(sys, "A1-012-1", paper1, new BigDecimal(200),"000002", null);
		createStock(sys, "A1-021-1", paper2, new BigDecimal(200),"000003", null);
		createStock(sys, "A1-022-1", paper2, new BigDecimal(200),"000004", null);

		createStock(sys, "A1-011-2", printer1, new BigDecimal(8), "000005", null);
		createStock(sys, "A1-012-2", printer1, new BigDecimal(8), "000006", null);
		createStock(sys, "A1-013-2", printer2, new BigDecimal(8), "000007", null);
		createStock(sys, "A1-021-2", toner1, new BigDecimal(144), "000008", "1582");
		createStock(sys, "A1-022-2", toner1, new BigDecimal(144),"000009", "1582");
		createStock(sys, "A1-023-2", toner1, new BigDecimal(144),"000010", "1582");
		createStock(sys, "A1-031-2", toner2, new BigDecimal(72),"000011", null);
		
		createStock(sys, "A2-011-1", screw1, new BigDecimal(15000),"000012", null);

		log.info("Create Goods In...");
		createAdvice( sys, printer1, new BigDecimal(16) );
		createAdvice( sys, paper1, new BigDecimal(2000) );
		createAdvice( sys, toner1, new BigDecimal(572) );
			
		log.info("Create Goods Out...");
		createOrder( sys, printer2, new BigDecimal(1), paper1, new BigDecimal(1), locationGo );
		createOrder( sys, printer1, new BigDecimal(1), null, null, locationGo );
		createOrder( sys, printer1, new BigDecimal(1), paper1, new BigDecimal(1), locationGo );
		createOrder( sys, paper1, new BigDecimal(10), toner1, new BigDecimal(1), locationGo );
		createOrder( sys, paper1, new BigDecimal(10), toner1, new BigDecimal(1), locationGo );
		
		log.info("Create Stocktaking...");
		createStocktaking( sys, "A1-01%1");
		
		log.info("Done.");

	}


	private Client createClient(String name, String number, String code) {
		Client client = null;
		try{
			client = clientService.getByName(name);
		} catch(Exception enf){
			// ignore
		}
		if( client == null ) {
			client = new Client();
			client.setName(name);
			client.setCode(code);
			client.setNumber(number);
			manager.persist(client);
		}
		
		client.setCode(number);
		client.setNumber(number);
			
		return client;
	}

	private ItemUnit createItemUnit(String name, ItemUnitType unitType, int baseFactor, ItemUnit baseUnit) {
		ItemUnit itemUnit = null;
		itemUnit = itemUnitService.getByName(name);
		if( itemUnit == null ) {
			itemUnit = new ItemUnit();
			itemUnit.setUnitName(name);
			manager.persist(itemUnit);
		}
		itemUnit.setUnitType(unitType);
		itemUnit.setBaseFactor(baseFactor);
		itemUnit.setBaseUnit(baseUnit);
			
		return itemUnit;
	}

	private User createUser(Client client, String name, String lastName, String locale, String roleName) {
		List<Role> roles = new ArrayList<Role>();
		try{
			roles.add(roleService.getByName(roleName));
		} catch (Throwable t){
			log.error("Cannot assign roles");
		}

		
		User user = null;
		try {
			user = userService.getByUsername(name);
		} catch (Exception e) {
			// ignore
		}
		if( user == null ) {
			user = new User();
			user.setClient(client);
			user.setName(name);
			user.setPassword(name);
			manager.persist(user);
		}
		user.setClient(client);
		user.setPassword(name);
		user.setLastname(lastName);
		user.setRoles(roles);
		user.setLocale(locale);
		
		try {
			userService.changePasswd(user, name, false);
		} catch (UserServiceException e) {}
		
		return user;
	}

	private Zone createZone(Client client, String name) {
		Zone zone = null;
		try {
			zone = zoneService.getByName(client, name);
		} catch (Exception e) {
			// ignore
		}
		if( zone == null ) {
			zone = new Zone();
			zone.setClient(client);
			zone.setName(name);
			manager.persist(zone);
		}

		
		zone.setClient(client);
		return zone;
	}
	
	private LOSArea createArea(Client client, String name, LOSAreaType type ) {
		LOSArea area = null; 
		try {
			area = (LOSArea)areaService.getByName(client, name);
		} catch (Exception e) {
			// ignore
		}
		if( area == null ) {
			area = new LOSArea();
			area.setClient(client);
			area.setName(name);
			manager.persist(area);
		}

		area.setClient(client);
		area.setAreaType(type);
		
		
		return area;
	}

	private LOSStorageLocationType createLocationType(Client client, String name) {
		LOSStorageLocationType type = null;
		try {
			type = locationTypeService.getByName(name);
		} catch (Exception e) {
			// ignore
		}
		if( type == null ) {
			type = new LOSStorageLocationType();
			type.setName(name);
			manager.persist(type);
		}

		
		return type;
	}

	private UnitLoadType createUnitLoadType(Client client, String name, double depth, double width, double height, double liftingCapacity, double weight) {
		UnitLoadType type = null;
		type = ultService.getByName(name);
		if( type == null ) {
			type = new UnitLoadType();
			type.setName(name);
			manager.persist(type);
		}

		type.setDepth(BigDecimal.valueOf(depth));
		type.setWidth(BigDecimal.valueOf(width));
		type.setHeight(BigDecimal.valueOf(height));
		type.setWeight(BigDecimal.valueOf(weight));
		type.setLiftingCapacity(BigDecimal.valueOf(liftingCapacity));

		return type;
	}

	private LOSTypeCapacityConstraint createCapa(String name, LOSStorageLocationType slType, UnitLoadType ulType, int capa, boolean overwrite) {
		LOSTypeCapacityConstraint constraint = null;
		constraint = capaService.getByTypes(slType, ulType);
		if( constraint == null ) {
			constraint = new LOSTypeCapacityConstraint();
			constraint.setStorageLocationType(slType);
			constraint.setUnitLoadType(ulType);
			constraint.setName(name);
			manager.persist(constraint);
		}

		if( overwrite ) {
			constraint.setName(name);
			constraint.setCapacity(capa);
		}
		
		return constraint;
	}

	private LOSStorageLocation createStorageLocation(Client client, String name, LOSArea area, LOSStorageLocationType slType, LOSLocationCluster cluster) {
		LOSStorageLocation loc = null;
		loc = slService.getByName(client, name);
		if( loc == null ) {
			loc = new LOSStorageLocation();
			loc.setClient(client);
			loc.setName(name);
			loc.setType(slType);
			manager.persist(loc);
		}

		loc.setType(slType);
		loc.setArea(area);
		loc.setCluster(cluster);
		
		return loc;
	}

	private LOSRack createRack(Client client, String name, String locationName, int noColumns, int noLoc, int noRows, LOSArea area, LOSStorageLocationType type, LOSLocationCluster cluster, Zone zone) {
		LOSRack rack = null;
		try {
			rack = rackService.getByName(client, name);
		} catch (Exception e) {
			// ignore
		}
		if( rack == null ) {
			rack = new LOSRack();
			rack.setClient(client);
			rack.setName(name);
			manager.persist(rack);
		}

		rack.setNumberOfColumns(noColumns);
		rack.setNumberOfRows(noRows);

		for(int x1=0; x1<noColumns; x1++){
			for(int x2=0; x2<noLoc; x2++){
				for(int y=0; y<noRows; y++){
	//				String slName = locationName + "-" + String.format("%1$02d",y+1) + "-" + String.format("%1$03d",x+1) ;
					String slName = locationName + "-" + String.format("%1$02d",x1+1) + String.format("%1$01d",x2+1) + "-" + String.format("%1$01d",y+1) ;
					LOSRackLocation rl = null;
					rl = (LOSRackLocation) slService.getByName(slName);
					if( rl == null ) {
						rl = new LOSRackLocation();
						rl.setClient(client);
						rl.setName(slName);
						rl.setType(type);
						rl.setRack(rack);
						manager.persist(rl);
					}				
					rl.setClient(client);
					rl.setArea(area);
					rl.setType(type);
					rl.setRack(rack);
					rl.setCluster(cluster);
					rl.setZone(zone);
				}
			}
		}
		return rack;
	}

	private ItemData createItemData(Client client, String number, String name, String descr, ItemUnit unit, boolean lotMandatory, SerialNoRecordType serialType, Zone zone, UnitLoadType type) {
		ItemData itemData = null;
		itemData = itemDataService.getByItemNumber(client, number);
		if( itemData == null ) {
			itemData = new ItemData();
			itemData.setClient(client);
			itemData.setNumber(number);
			itemData.setHandlingUnit(unit);
			manager.persist(itemData);
		}

		itemData.setName(name);
		itemData.setDescription(descr);
		itemData.setHandlingUnit(unit);
		itemData.setLotMandatory(lotMandatory);
		itemData.setSerialNoRecordType(serialType);
		itemData.setZone(zone);
		itemData.setTradeGroup( resolve("ItemDataTradeGroup"));
		itemData.setDefaultUnitLoadType(type);
		return itemData;
	}
	
	private ItemDataNumber createEAN(ItemData itemData, String code) {
		ItemDataNumber ean = null;
		List<ItemDataNumber> eanList = eanService.getListByNumber(null, code);
		if( eanList == null || eanList.size() == 0 ) {
			try {
				ean = eanService.create(itemData, code);
			} catch (FacadeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ean;
	}
	private LOSFixedLocationAssignment createFixLocation( Client client, String locationName, ItemData itemData, LOSStorageLocationType locType ) throws FacadeException {
		LOSStorageLocation sl = slService.getByName(client, locationName);
		if(sl == null) {
			return null;
		}
		
		sl.setType(locType);
		// Kapazitaetsbeschraenkung 1:1
		LOSFixedLocationAssignment fl = null;
		List<LOSFixedLocationAssignment> flList = fixedService.getByItemData(itemData);
		for( LOSFixedLocationAssignment x : flList ) {
			manager.remove(x);
		}
		LOSFixedLocationAssignment x = fixedService.getByLocation(sl);
		if( x != null ) {
			manager.remove(x);
		}
		manager.flush();
		
		fl = new LOSFixedLocationAssignment();
		fl.setItemData(itemData);
		fl.setAssignedLocation(sl);
		fl.setDesiredAmount(new BigDecimal(400));

		manager.persist(fl);

		
		sl.setType(locationTypeService.getNoRestrictionType());
		
		UnitLoadType pickUlType = ultService.getPickLocationUnitLoadType();
		LOSUnitLoad ul = null;
		try {
			ul = ulQueryService.getByLabelId(locationName);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( ul == null ) {
			ul = ulService.createLOSUnitLoad(client, locationName, pickUlType, sl);
		}
		else {
			ul.setClient(client);
			ul.setType(pickUlType);
			ul.setStorageLocation(sl);
		}
		
		ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
		
		return fl;
	}
	
	private LOSLocationCluster createCluster( String name ) {
		LOSLocationCluster cluster = null;
		try {
			cluster = lcService.getByName(name);
		} catch (Exception e) {
			// ignore
		}
		if( cluster == null ) {
			cluster = new LOSLocationCluster();
			cluster.setName(name);
			manager.persist(cluster);
		}
		return cluster;
	}
	
	private Role createRole( String name ) {
		Role role = null;
		try {
			role = roleService.getByName(name);
		} catch (Exception e) {
			// ignore
		}
		if( role == null ) {
			role = new Role();
			role.setName(name);
			manager.persist(role);
		}
		return role;
	}

	private void createStock( Client client, String locationName, ItemData idat, BigDecimal amount, String unitLoadNumber, String lotNumber) {
		try {
			LOSUnitLoad ul = ulQueryService.getByLabelId(unitLoadNumber);
			if( ul != null ) {
				LOSStorageLocation sl = slService.getByName(client, locationName);
				LOSFixedLocationAssignment x = fixedService.getByLocation(sl);
				if( x == null ) {
					log.error("Do not create UnitLoad " + unitLoadNumber+" twice");
					return;
				}
			}
			inventoryFacade.createStockUnitOnStorageLocation(client.getNumber(), locationName, idat.getNumber(), lotNumber, amount, unitLoadNumber);
		} catch (Exception e) {
			log.error("Error creating Stock: "+e.getMessage(), e);
		}
		
	}
	
	private void createAdvice( Client client, ItemData idat, BigDecimal amount ) {
		try {
			inventoryFacade.createAvis(client.getNumber(), idat.getNumber(), null, amount, new Date(), null, null, false);
		} catch (Exception e) {
			log.error("Error creating Advice: "+e.getMessage(), e);
		}
		
	}
	
	private void createOrder( Client client, ItemData idat1, BigDecimal amount1, ItemData idat2, BigDecimal amount2, LOSStorageLocation target ) {
		try {
			List<OrderPositionTO> posList = new ArrayList<OrderPositionTO>();
			if( idat1 != null ) {
				OrderPositionTO pos = new OrderPositionTO(client.getNumber(), null, idat1.getNumber(), amount1);
				posList.add(pos);
			}
			if( idat2 != null ) {
				OrderPositionTO pos = new OrderPositionTO(client.getNumber(), null, idat2.getNumber(), amount2);
				posList.add(pos);
			}
			orderFacade.order(client.getNumber(), null, posList.toArray(new OrderPositionTO[posList.size()]), null, null, target.getName() );

		} catch (Exception e) {
			log.error("Error creating Order: "+e.getMessage(), e);
		}
	}

	private void createStocktaking( Client client, String locationName ) {
		stService.generateOrders(true, client.getId(), null, null, null, null, locationName, null, null, null, true, true);
	}

	public static final String resolve( String key, Object[] parameters ) {
        // assertion
        if (key == null) {
            return "";
        }

        ResourceBundle bundle;
        String formatString;
        try {
            bundle = ResourceBundle.getBundle("TopologyBundle", Locale.getDefault(), ProjectBundleResolver.class.getClassLoader());
            // resolving key
            String s = bundle.getString(key);
            formatString = String.format(s, parameters);
            return formatString;
        }
        catch (MissingResourceException ex) {
            return key;
        }
        catch (IllegalFormatException ife){
        	return key;
        }
    }
	public final String resolve( String key ) {
        if (key == null) {
            return "";
        }
        
        Locale locale = new Locale(contextService.getCallersUser().getLocale());
        if( locale == null ) {
        	locale = Locale.getDefault();
        }
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("de.linogistix.los.reference.res.TopologyBundle", locale, ProjectBundleResolver.class.getClassLoader());
            // resolving key
            String s = bundle.getString(key);
            return s;
        }
        catch (MissingResourceException ex) {
        	log.error("Exception: "+ex.getMessage());
            return key;
        }
        catch (IllegalFormatException ife){
        	log.error("Exception: "+ife.getMessage());
        	return key;
        }
    }

	
}
