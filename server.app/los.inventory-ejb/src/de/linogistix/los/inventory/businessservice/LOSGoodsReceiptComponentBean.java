/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.LotService;
import org.mywms.service.StockUnitService;
import org.mywms.service.UserService;

import de.linogistix.los.inventory.customization.ManageAdviceService;
import de.linogistix.los.inventory.customization.ManageReceiptService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.report.StockUnitLabelReport;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.QueryAdviceService;
import de.linogistix.los.inventory.service.StockUnitLabelService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSGoodsReceiptComponentBean extends BasicFacadeBean implements
		LOSGoodsReceiptComponent {

	private final Logger logger = Logger
			.getLogger(LOSGoodsReceiptComponentBean.class);
	@EJB
	private LOSStorageLocationService slService;
	@EJB
	private LOSInventoryComponent inventoryComp;
	@EJB
	private LOSGoodsReceiptService grService;
	@EJB
	private ClientService clientService;
	@EJB
	private ManageReceiptService manageGrService;
	
	@EJB
	LOSStockUnitRecordService suRecordService;
	@EJB
	UserService userService;
	@EJB
	QueryAdviceService adviceService;
	@EJB
	LOSAdviceQueryRemote adQuery;
	@EJB
	ExtinguishBusiness extinguishBusiness;
	@EJB
	LotService lotService;
	@EJB
	InventoryGeneratorService genService;
	@EJB
	LOSUnitLoadService ulService;
	@EJB
	ContextService contextService;
	@EJB
	StockUnitLabelService suLabelService;
	@EJB
	StockUnitLabelReport suLabelReport;
	@EJB
	StockUnitService suService;
	@EJB
	ItemDataService itemDataService;
	@EJB
	ManageAdviceService manageAdviceService;
	
	@EJB
	private LOSStorageRequestService storeReqService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	// --------------------------------------------------------------------------------------------

	public List<LOSStorageLocation> getGoodsReceiptLocations()
			throws LOSLocationException {

		List<LOSStorageLocation> slList;
		slList = slService.getListByAreaType(clientService.getSystemClient(),
				LOSAreaType.GOODS_IN);

		if (slList.size() == 0) {
			// LOSStorageLocation defLoc;
			// defLoc =
			// createGoodsReceiptLocation(LOSGoodsReceiptConstants.GOODSRECEIPT_DEFAULT_LOCATION_NAME);
			// slList.add(defLoc);
			throw new LOSLocationException(
					LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new Object[0]);
		}

		return slList;

	}

	public LOSGoodsReceipt getByGoodsReceiptNumber(String number) {

		return grService.getByGoodsReceiptNumber(number);
	}

	// --------------------------------------------------------------------------------------------
	public LOSGoodsReceipt createGoodsReceipt(Client client,
			String licencePlate, String driverName, String forwarder,
			String deliveryNoteNumber, Date receiptDate) {

		String sn = genService.generateGoodsReceiptNumber(client);

		LOSGoodsReceipt grr;
		grr = grService.createGoodsReceipt(client, sn);
		grr.setLicencePlate(licencePlate);
		grr.setDriverName(driverName);
		grr.setForwarder(forwarder);
		grr.setDeliveryNoteNumber(deliveryNoteNumber);
		grr.setReceiptDate(receiptDate);
		try {
			User user = userService.getByUsername(getCallersUsername());
			grr.setOperator(user);
		} catch (EntityNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		}

		return grr;

	}

	// --------------------------------------------------------------------------------------------
	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client,
			LOSGoodsReceipt gr, String orderReference, BigDecimal amount) throws InventoryException {
		return createGoodsReceiptPosition(client, gr, orderReference, amount,
				LOSGoodsReceiptType.INTAKE, "");
	}

	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client,
			LOSGoodsReceipt gr, String orderReference, BigDecimal amount,
			LOSGoodsReceiptType receiptType, String qaFault) throws InventoryException {
		
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
			throw new InventoryException(InventoryExceptionKey.AMOUNT_MUST_BE_GREATER_THAN_ZERO, "" + amount);
		}
		
		LOSGoodsReceiptPosition pos = new LOSGoodsReceiptPosition();
		pos.setClient(client);
		pos.setGoodsReceipt(gr);
		pos.setOrderReference(orderReference);
		pos.setReceiptType(receiptType);
		pos.setQaFault(qaFault);
		int positionNumber = gr.getPositionList() != null ? gr.getPositionList().size() : 0;
		
		String positionNumberStr = "";
		int numCheck = 0; // emergency brake
		do {
			positionNumber ++;
			numCheck ++;
			positionNumberStr = gr.getGoodsReceiptNumber() + "-" + positionNumber;
		}
		while( checkPosNumber(gr, positionNumberStr) == false && numCheck < 1000 );
		
		pos.setPositionNumber(positionNumberStr);
		
		pos.setAmount(amount);
		manager.persist(pos);

		return pos;
	}

	private boolean checkPosNumber( LOSGoodsReceipt gr, String posNumber ) {
		if( posNumber == null || posNumber.length() == 0 ) {
			return false;
		}
		for( LOSGoodsReceiptPosition posExist : gr.getPositionList() ) {
			if( posExist.getPositionNumber().equals(posNumber) ) {
				logger.info("Skipping existing position number <" + posNumber + ">");
				return false;
			}
		}
		return true;
	}
	
	// --------------------------------------------------------------------------------------------
	public StockUnit receiveStock(LOSGoodsReceiptPosition grPosition,
			Lot batch, ItemData item, BigDecimal amount, LOSUnitLoad unitLoad, String serialNumber)
			throws InventoryException {

		StockUnit su;

		if( batch == null ) {
			if( item.isLotMandatory() ) {
				throw new InventoryException(
						InventoryExceptionKey.LOT_MANDATORY, new String[] {
								item.getNumber() });
			}
		}
		else {
			if (!batch.getItemData().equals(item)) {
				throw new InventoryException(
						InventoryExceptionKey.ITEMDATA_LOT_MISMATCH, new String[] {
								batch.getName(), item.getNumber() });
			}
	
			if (batch.isLocked()) {
	
				if (batch.getLock() == LotLockState.LOT_TOO_YOUNG.getLock()) {
					logger.info("lot is too young - receive anyway");
	
				} else {
					throw new InventoryException(
							InventoryExceptionKey.LOT_ISLOCKED,
							new String[] { batch.getName() });
				}
			}
		}
		
		LOSArea a = (LOSArea) unitLoad.getStorageLocation().getArea();
		if (!a.getAreaType().equals(LOSAreaType.GOODS_IN)) {
			throw new InventoryException(
					InventoryExceptionKey.NOT_A_GOODSIN_LOCATION,
					new Object[] { unitLoad.getStorageLocation().getName() });
		}

		String activityCode = grPosition.getPositionNumber();

		su = inventoryComp.createStock(grPosition.getClient(), batch, item,
				amount, unitLoad, activityCode, serialNumber, null, false);

		manager.flush();

		grPosition.setStockUnit(su);

		if (grPosition.getGoodsReceipt().getForwarder() != null
				&& grPosition.getGoodsReceipt().getForwarder().length() > 0) {
			
			su.addAdditionalContent(InventoryBundleResolver.resolve(InventoryBundleResolver.class,"deliverer",Locale.getDefault() )
					+ ": " + grPosition.getGoodsReceipt().getForwarder() + "\n");

		}

		manager.flush();

		return su;
	}

	public void assignAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException{
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		adv = manager.find(LOSAdvice.class, adv.getId());
		
		if(!adv.getClient().equals(r.getClient())){
			throw new InventoryException(InventoryExceptionKey.CLIENT_MISMATCH, new Object[0]);
		}
		
		logger.debug("assignAdvice: " + adv.getAdviceNumber() + " to GR: " + r.getGoodsReceiptNumber());

		try {
			manageAdviceService.updateFromHost(adv);
		}
		catch( Exception e ) {	
			throw new InventoryException(InventoryExceptionKey.ADVICE_REFRESH_ERROR, adv.toUniqueString());
		}
		
		adv = manager.find(LOSAdvice.class, adv.getId());
		
		if (!r.containsAssignedAdvices(adv)){
			r.addAssignedAdvices(adv);
			switch (adv.getAdviceState()){
				case RAW:
					adv.setAdviceState(LOSAdviceState.PROCESSING);
					 break;
				case FINISHED:
					throw new InventoryException(InventoryExceptionKey.ADVICE_FINISHED, adv.toUniqueString());
				case GOODS_TO_COME:
				case OVERLOAD:
				case PROCESSING:
				default:
					break;
			}
		} else{
			throw new InventoryException(InventoryExceptionKey.ADVICE_ASSIGNED, adv.toUniqueString());
		}
	}
	
	public void removeAssignedAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException{
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		adv = manager.find(LOSAdvice.class, adv.getId());
		logger.debug("removeAssignedAdvice: " + adv.getAdviceNumber() + " from GR: " + r.getGoodsReceiptNumber());
		
		if (r.containsAssignedAdvices(adv)){
			
			try{
				r.removeAssignedAdvices(adv);
			} catch (IllegalArgumentException ex){
				throw new InventoryException(InventoryExceptionKey.ADVICE_POSITIONS_ASSIGNED, adv.getAdviceNumber());
			}
			if( adv.getAdviceState() == LOSAdviceState.PROCESSING ) {
				if( BigDecimal.ZERO.compareTo(adv.getReceiptAmount()) >= 0 ) { 
					adv.setAdviceState(LOSAdviceState.RAW);
				}
				else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())>0 ) {
					adv.setAdviceState(LOSAdviceState.OVERLOAD);
				}
				else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())<0 ) {
					adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				}
				else {
					adv.setAdviceState(LOSAdviceState.FINISHED);
				}
			}

		} else{
			//
		}
	}
	
	public void assignAdvice(LOSAdvice adv, LOSGoodsReceiptPosition pos)
			throws InventoryException {

		if (adv.getLot() != null
				&& (!adv.getLot().equals(pos.getStockUnit().getLot()))) {
			throw new InventoryException(InventoryExceptionKey.LOT_MISMATCH,
					new String[] { adv.getLot().getName() });
		}
		
		if(pos.getRelatedAdvice() != null){
		
			if (!pos.getRelatedAdvice().equals(adv)) {
				
				throw new InventoryException(
						InventoryExceptionKey.POSITION_ALREADY_ASSIGNED_ADVICE,
						new String[] { pos.getRelatedAdvice().toUniqueString(),
								pos.toUniqueString() });
			} else {
				logger.warn("Tried to assign Position twice to advice "
						+ pos.getRelatedAdvice().getAdviceNumber());
			}
		}
		else{
		
			adv.addGrPos(pos);
			if (adv.diffAmount().compareTo(BigDecimal.ZERO) == 0){
				adv.setAdviceState(LOSAdviceState.FINISHED);
			} else if (adv.diffAmount().compareTo(BigDecimal.ZERO) > 0){
				//Unterliefert
				adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				
			} else{
				//Ueberliefert
				adv.setAdviceState(LOSAdviceState.OVERLOAD);
			}
			
			if (adv.isExpireBatch() && pos.getStockUnit().getLot() != null) {
				expireOlderBatch(pos.getStockUnit().getLot());
			}
			
			logger.info("ASSIGNED POSITION: " + pos.getPositionNumber()
					+ " to advice " + adv.getAdviceNumber());
		}
	}

	public void expireOlderBatch(Lot newer) {
		List<Lot> lots = lotService.getListByItemData(newer.getItemData());
		for (Lot l : lots) {
			if (newer.equals(l)) {
				continue;
			}
			if (l.getCreated().after(newer.getCreated())) {
				continue;
			}
			try {
				extinguishBusiness.createExtinguishOrder(l, new Date());
			} catch (InventoryException e) {
				logger.error(e.getMessage(), e);
				continue;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<LOSAdvice> getSuitableLOSAdvice(String exp, Client client,
			Lot lot, ItemData idat) throws InventoryException {

		if (client == null) {
			throw new NullPointerException("Client must not be null");
		}
		if (lot == null) {
			// throw new NullPointerException("Lot must not be null");
		}
		if (idat == null) {
			// throw new NullPointerException("ItemData must not be null");
		}
		if (lot != null && idat != null && !lot.getItemData().equals(idat)) {
			throw new IllegalArgumentException("lot and itemdata don't match");
		}

		StringBuffer b = new StringBuffer();
		b.append(" SELECT DISTINCT a FROM ");
		b.append(LOSAdvice.class.getSimpleName());
		b.append(" a ");
		// b.append(" LEFT JOIN FETCH a.grPos ");
		b.append(" WHERE a.client = :client ");
		if (lot != null) {
			b.append(" AND a.lot = :lot ");
		}
		// b.append(" AND a.amount >= :amount ");
		b.append(" AND a.adviceState IN (:raw, :goodstocome, :overload, :processing) ");
		b.append(" AND LOWER(a.adviceNumber) LIKE :exp");
		b.append(" ORDER BY a.receiptAmount ASC, a.created ASC");

		Query q = manager.createQuery(new String(b)).setParameter("client",
				client);

		if (lot != null) {
			q = q.setParameter("lot", lot);
		}
		q = q
				.
				// setParameter("amount",
				// pos.getStockUnitRecord().getNewAmount()).
				setParameter("raw", LOSAdviceState.RAW).setParameter(
						"overload", LOSAdviceState.OVERLOAD).setParameter(
						"goodstocome", LOSAdviceState.GOODS_TO_COME)
				.setParameter("processing", LOSAdviceState.PROCESSING)
				.setParameter("exp", TemplateQueryWhereToken.transformLikeParam(exp));

		List<LOSAdvice> ret;
		ret = q.getResultList();

		return ret;

	}

	public void remove(LOSGoodsReceipt r, LOSGoodsReceiptPosition pos)
			throws InventoryException, FacadeException {

		if( (r.getReceiptState() == LOSGoodsReceiptState.FINISHED) ||
			(r.getReceiptState() == LOSGoodsReceiptState.TRANSFER) ) {
			logger.info("Won't remove position because of LOSGoodsReceipt.state = " + r.getReceiptState());
			throw new InventoryException(
					InventoryExceptionKey.WRONG_STATE, new Object[]{"LOSGoodsReceipt",r.getReceiptState()});
		}
		
		if( (pos.getState() == LOSGoodsReceiptState.FINISHED) ||
			(pos.getState() == LOSGoodsReceiptState.TRANSFER) ) {
			logger.info("Won't remove position because of LOSGoodsReceiptPosition.state = " + pos.getState());
			throw new InventoryException(
					InventoryExceptionKey.WRONG_STATE, new Object[]{"LOSGoodsReceiptPosition", pos.getState()});
		}

		logger.info("Remove GR-position " + pos.getPositionNumber());

		StockUnit su = pos.getStockUnit();
		if (su == null) {
			try {
				su = suService.getByLabelId(pos.getStockUnitStr());
			} catch (EntityNotFoundException ex) {
				logger.debug("EntityNotFound StockUnit AdditionalId=" + pos.getStockUnitStr());
				su = null;
			}
		}

		LOSUnitLoad ul = null;
		
		if (su != null) {
			BigDecimal amountSu = su.getAmount();
			BigDecimal amountGr = pos.getAmount();
			if( BigDecimal.ZERO.compareTo(amountSu) < 0 && !(amountSu.compareTo(amountGr)==0) ) {
				logger.info("The amount of StockUnit="+amountSu+" is not equal to amount of position="+amountGr+". Abort");
				throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_STOCK_ADDED, new Object[0]);
			}
		}
		if (su != null) {
			ul = (LOSUnitLoad)su.getUnitLoad();
			
			pos.setStockUnit(null);
			logger.info("Going to remove SU " + su.getLabelId());
			inventoryComp.removeStockUnit(su, pos.getPositionNumber(), false);
		} else {
			logger.warn("position holds no StockUnit: " + pos.getPositionNumber());
		}

		if (ul == null) {
						
			try {
				ul = ulService.getByLabelId(pos.getClient(), pos.getUnitLoad());
			} catch (EntityNotFoundException ex) {
				logger.debug("EntityNotFound UnitLoad Label=" + pos.getUnitLoad());
			}
		}
		LOSUnitLoad ulNirwana = ulService.getNirwana();
		if( ul != null && ul.equals(ulNirwana) ) {
			// The nirwana unit load is written to the position, when the stock is moved to another unit load
			logger.info("The UnitLoad is NIRWANA. So Stock has changed!. Abort");
			throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_STOCK_ADDED, new Object[0]);
		}
		if( ul != null ) {
			logger.info("Going to remove UNITLOAD " + ul.getLabelId());
			
			List<LOSStorageRequest> storageList = storeReqService.getListByLabelId(ul.getLabelId());
			for( LOSStorageRequest sr : storageList ) {
				if( sr.getRequestState() == LOSStorageRequestState.CANCELED
						|| sr.getRequestState() == LOSStorageRequestState.FAILED
						|| sr.getRequestState() == LOSStorageRequestState.TERMINATED ) {
					logger.info("Remove unit load from storage request " + sr.getNumber());
					sr.setUnitLoad(null);
					manager.remove(sr);
				}
				else {
					throw new InventoryException(InventoryExceptionKey.UNITLOAD_DELETE_ERROR_STORAGEREQUEST, new Object[0]);
				}
			}
			
			
			if( ul.getStockUnitList().size() <= 1 ) {
				logger.info("Remove UnitLoad " + ul.getLabelId());
				try {
					ulService.delete(ul);
				} catch (ConstraintViolatedException e) {
					logger.error(e.getMessage(), e);
					throw new InventoryException(InventoryExceptionKey.CONSTRAINT_VIOLATION, e.getMessage());
				}
			}
		}
		
		LOSAdvice advice = pos.getRelatedAdvice();
		if (advice != null) {
			advice.removeGrPos(pos);
			if (advice.diffAmount().compareTo(BigDecimal.ZERO) == 0){
				advice.setAdviceState(LOSAdviceState.FINISHED);
			} else if (advice.diffAmount().compareTo(BigDecimal.ZERO) > 0){
				//Unterliefert
				advice.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				
			} else{
				//too much delivered
				advice.setAdviceState(LOSAdviceState.OVERLOAD);
			}
		}

		r.getPositionList().remove(pos);
		manager.remove(pos);


	}

	public void cancel(LOSGoodsReceipt gr) throws FacadeException {
		gr = manager.find(LOSGoodsReceipt.class, gr.getId());

		List<Long> positionIds = new ArrayList<Long>();

		// remove postions
		for (LOSGoodsReceiptPosition pos : gr.getPositionList()) {
			positionIds.add(pos.getId());
		}
		for (Long id : positionIds) {
			LOSGoodsReceiptPosition pos = manager.find(
					LOSGoodsReceiptPosition.class, id);
			remove(gr, pos);
		}

		// delete LOSGoodsReceipt
		switch (gr.getReceiptState()) {
		case RAW:
			manager.remove(gr);
			break;
		default:
			logger.warn("Won't remove receipt: " + gr.toDescriptiveString()
					+ " *** Setting state to " + LOSGoodsReceiptState.CANCELED);
			gr.setReceiptState(LOSGoodsReceiptState.CANCELED);
			break;
		}
	}

	public LOSUnitLoad getOrCreateUnitLoad(Client c, LOSStorageLocation sl,
			UnitLoadType type, String ref) throws LOSLocationException {

		LOSUnitLoad ul;
		Client cl;
		if (c != null) {
			cl = manager.find(Client.class, c.getId());
		} else {
			cl = clientService.getSystemClient();

		}

		if (sl == null) {
			throw new NullPointerException("StorageLocation must not be null");
		}
		if (type == null) {
			throw new NullPointerException("UnitLoadType must not be null");
		}
		if (ref == null) {
			ref = genService.generateUnitLoadLabelId(cl, type);
		}

		if (ref != null && ref.length() != 0) {
			try {
				ul = (LOSUnitLoad) ulService.getByLabelId(cl, ref);
				boolean contains = false;
				for (UnitLoad comp : sl.getUnitLoads()) {
					if (comp.equals(ul)) {
						contains = true;
					}
				}
				if (!contains) {
					throw new LOSLocationException(
							LOSLocationExceptionKey.UNITLOAD_NOT_ON_LOCATION,
							new String[] { ul.getLabelId(), sl.getName() });
				}
			} catch (EntityNotFoundException ex) {
				logger.warn("CREATE UnitLoad: " + ex.getMessage());
				ul = ulService.createLOSUnitLoad(cl, ref, type, sl);
// use the default value
//				ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT);
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}
		return ul;
	}

	public void finishGoodsReceipt(LOSGoodsReceipt r) throws InventoryException, InventoryTransferException {

		LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());

		if (foundGr == null) {
			throw new InventoryException(
					InventoryExceptionKey.CREATE_GOODSRECEIPT, r
							.getGoodsReceiptNumber());
		}
		
		manageGrService.finishGoodsReceiptStart(foundGr);
		
		foundGr = manager.find(r.getClass(), r.getId());

		for (LOSGoodsReceiptPosition pos : foundGr.getPositionList()) {
			pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
			pos.setStockUnit(null);
		}
		
		for (LOSAdvice adv : foundGr.getAssignedAdvices()){
			if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())>0 ) {
				adv.setAdviceState(LOSAdviceState.OVERLOAD);
			}
			else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())<0 ) {
				adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
			}
			else {
				adv.setAdviceState(LOSAdviceState.FINISHED);
			}
		}
		
		foundGr.setReceiptState(LOSGoodsReceiptState.FINISHED);
		manageGrService.finishGoodsReceiptEnd(foundGr);
	}
	
	public void acceptGoodsReceipt(LOSGoodsReceipt r) throws InventoryException {
//		LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());
		switch (r.getReceiptState()) {
		case RAW:
		case ACCEPTED:
			r.setReceiptState(LOSGoodsReceiptState.ACCEPTED);
			break;
		default:
			throw new InventoryException(InventoryExceptionKey.ADVICE_CAANNOT_BE_ACCEPTED, r.getGoodsReceiptNumber());
		}
		
	}

	public StockUnitLabel createStockUnitLabel(LOSGoodsReceiptPosition pos, String printer)
			throws ReportException {

		pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
		StockUnitLabel label = suLabelReport.createStockUnitLabelGR(pos, printer);
		return label;
	}



	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------
	// private Area getGoodsReceiptArea() {
	//
	// Area grArea = null;
	// Client client = clientService.getSystemClient();
	//
	// try {
	// grArea = areaService.getByName(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_AREA_NAME);
	// } catch (EntityNotFoundException e) {
	// try {
	// grArea = areaService.create(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_AREA_NAME);
	// } catch (UniqueConstraintViolatedException e1) {
	// logger.error("Could not create GoodsReceipt Area !!");
	//
	// }
	// }
	//
	// return grArea;
	// }

	// //--------------------------------------------------------------------------------------------
	// private LOSStorageLocationType getGoodsReceiptLocationType() {
	//
	// LOSStorageLocationType giType = null;
	// Client client = clientService.getSystemClient();
	//
	// try {
	// giType = slTypeService.getByName(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_LOCATION_TYPE_NAME);
	// } catch (EntityNotFoundException e1) {
	// try {
	// giType = slTypeService.create(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_LOCATION_TYPE_NAME);
	// } catch (Exception e) {
	// logger.error("Could not create GoodsReceipt StorageLocationType!!");
	//
	// }
	// }
	//
	// return giType;
	// }
}
