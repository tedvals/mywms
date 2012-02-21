/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.ItemDataService;
import org.mywms.service.LotService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.service.QueryAdviceService;

/**
 * Inventory relevant business operations
 * 
 * @author trautm
 */
@Stateless
public class QueryInventoryBusinessBean implements QueryInventoryBusiness {

	Logger log = Logger.getLogger(QueryInventoryBusinessBean.class);

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@EJB
	ItemDataService itemDataService;

	@EJB
	ClientService clientService;

	@EJB
	LotService lotService;

	@EJB
	QueryAdviceService adviseService;

	@EJB
	StockUnitService suService;

	
	public QueryInventoryTO[] getInventory(Client c, boolean consolidateLot) throws InventoryException{
		QueryInventoryTO[] ret;
		Map<String, QueryInventoryTO> m = getInvMap(c, null, null, consolidateLot);
		
		ret = m.values().toArray(new QueryInventoryTO[0]);
		return ret;
		
	}
	
	//-------------------------------------------------------------------------
	
	public Map<String, QueryInventoryTO> getInvMap(Client c, Lot lot, ItemData idat, boolean consolidateLot) throws InventoryException {

		Map<String, QueryInventoryTO> result;

		if (lot != null && idat != null && ! lot.getItemData().equals(idat)){
			throw new InventoryException(InventoryExceptionKey.ITEMDATA_LOT_MISMATCH, new String[]{idat.getNumber(), lot.getName()});
		}
		
		if (consolidateLot){
			result = getItemData(c, idat);
		} else{
			result = getLots(c, lot, idat);
		}
		
		getStockUnitAmount(c, lot, idat, result, consolidateLot);

		getAdvicedAmount(c, lot, idat, result, consolidateLot);
		
		// ---------------------------------------------------------------------
		// Return All
		// ---------------------------------------------------------------------

		return result;
	}
	
	public Map<String, QueryInventoryTO> getItemData(Client c,ItemData idat) {
		
		Map<String, QueryInventoryTO> result = new HashMap<String, QueryInventoryTO>();
		List<QueryInventoryTO> items = getItemDataInv(c, idat);
		
		for (QueryInventoryTO to : items){
			result.put(getKey(null, to.articleRef, true), to);
		}
		
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	public List<QueryInventoryTO> getItemDataInv(Client c, ItemData idat) {
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(QueryInventoryTO.class.getName());
		// client
		b.append("(");
		b.append("itemData.client.number, ");
		b.append("itemData.number, ");
		b.append("itemData.scale ");
		b.append(")");

		b.append(" FROM ");
		b.append(ItemData.class.getSimpleName());
		b.append(" itemData ");

		b.append(" WHERE itemData.client = :client ");
		if (idat != null) {
			b.append(" AND itemData = :idat ");
		}
		
		b.append(" AND itemData.lock <> :dellock ");
		
		
		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		query = query.setParameter("dellock", BusinessObjectLockState.GOING_TO_DELETE.getLock());
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}
		
		List<QueryInventoryTO> ret = query.getResultList();
		log.debug("returned elements " + ret.size());
		return ret;
	}
	
	public Map<String, QueryInventoryTO> getLots(Client c, Lot lot, ItemData idat) {
		
		Map<String, QueryInventoryTO> result = new HashMap<String, QueryInventoryTO>();
		List<QueryInventoryTO> items = getLotsInv(c, lot, idat);
		
		for (QueryInventoryTO to : items){
			result.put(getKey(to.lotRef, to.articleRef, false), to);
		}
		
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	public List<QueryInventoryTO> getLotsInv(Client c, Lot lot, ItemData idat) {
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(QueryInventoryTO.class.getName());
		// client
		b.append("(");
		b.append("lot.client.number, ");
		b.append("lot.itemData.number, ");
		b.append("lot.name, ");
		b.append("lot.itemData.scale ");
		b.append(")");

		b.append(" FROM ");
		b.append(Lot.class.getSimpleName());
		b.append(" lot ");

		b.append(" WHERE lot.client = :client ");
		
		if (lot != null) {
			b.append(" AND lot = :lot ");
		} else if (idat != null){
			b.append(" AND lot.itemData = :idat ");
		}
		
		b.append(" AND lot.itemData.lock <> :dellock ");

		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		query = query.setParameter("dellock", BusinessObjectLockState.GOING_TO_DELETE.getLock());
		
		if (lot != null) {
			query = query.setParameter("lot", lot);
		} else if (idat != null){
			query = query.setParameter("idat", idat);
		}
		
		List<QueryInventoryTO> ret = query.getResultList();
		log.debug("returned elements " + ret.size());

		return ret;
	}
	
	/**
	 * Current Amount of StockUnits
	 * 
	 * @return
	 */
	public Map<String, QueryInventoryTO> getStockUnitAmount(Client c, Lot lot,
			ItemData idat, Map<String, QueryInventoryTO> result, boolean consolidateLot) {

		String key;
		QueryInventoryTO inv;

		List<StockUnitResult> sus;
		sus = getStockUnitInv(c, lot, idat);

		for (StockUnitResult r : sus) {
			key = getKey(r.lotRef, r.articleRef, consolidateLot);
			if ((inv = (QueryInventoryTO) result.get(key)) == null) {
				if ( (! consolidateLot) && ( r.lotRef == null || r.lotRef.length() == 0)){
					inv = new QueryInventoryTO(r.clientRef, r.articleRef, r.lotRef, r.scale);
					result.put(key, inv);
					inv.available = inv.available!=null?inv.available.add(r.available):r.available;
					inv.inStock = inv.inStock!=null?inv.inStock.add(r.inStock):r.inStock;
					inv.reserved = inv.reserved!=null?inv.reserved.add(r.reserved):r.reserved;
					inv.locked =  inv.locked!=null?inv.locked.add(r.locked):r.locked;
					inv.addLock(r.lock, r.locked);
				} else{
					continue;
				}
			} else {
				inv.available = inv.available!=null?inv.available.add(r.available):r.available;
				inv.inStock = inv.inStock!=null?inv.inStock.add(r.inStock):r.inStock;
				inv.reserved = inv.reserved!=null?inv.reserved.add(r.reserved):r.reserved;
				inv.locked =  inv.locked!=null?inv.locked.add(r.locked):r.locked;
				inv.addLock(r.lock, r.locked);
			}
			try{
				inv.available = inv.available.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.inStock = inv.inStock.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.reserved = inv.reserved.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.locked = inv.locked.setScale(r.scale);
			}catch(ArithmeticException ae){}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<StockUnitResult> getStockUnitInv(Client c, Lot lot,
			ItemData idat) {
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(StockUnitResult.class.getName());
		// client
		b.append("(");
		b.append("su.client.number, ");
		b.append("su.itemData.number, ");
		b.append("su.itemData.scale, ");
		b.append("su.lot.name, ");
		b.append("su.amount, ");
		b.append("su.reservedAmount, ");
		b.append("su.lock");
		b.append(")");

		b.append(" FROM ");
		b.append(StockUnit.class.getSimpleName());
		b.append(" su ");

		b.append(" WHERE su.client = :client ");
		if (idat != null) {
			b.append(" AND su.itemData = :idat ");
		} else {
			b.append(" AND su.itemData IS NOT NULL");
		}
		if (lot != null) {
			b.append(" AND su.lot = :lot ");
		} 
//		else {
//			b.append(" AND su.lot IS NOT NULL");
//		}
		
		b.append(" AND su.amount > 0 ");
		
		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}
		if (lot != null) {
			query = query.setParameter("lot", lot);
		}

		List<StockUnitResult> suList = query.getResultList();
		
		log.debug("returned elements (with lot): " + suList.size());
		if( lot != null ) {
			return suList;
		}

		b = new StringBuffer();
		
		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(StockUnitResult.class.getName());
		// client
		b.append("(");
		b.append("su.client.number, ");
		b.append("su.itemData.number, ");
		b.append("su.itemData.scale, ");
		b.append("su.amount, ");
		b.append("su.reservedAmount, ");
		b.append("su.lock");
		b.append(")");

		b.append(" FROM ");
		b.append(StockUnit.class.getSimpleName());
		b.append(" su ");

		b.append(" WHERE su.client = :client ");
		if (idat != null) {
			b.append(" AND su.itemData = :idat ");
		} else {
			b.append(" AND su.itemData IS NOT NULL");
		}
		
		
		b.append(" AND su.lot IS NULL");
		
		
		b.append(" AND su.amount > 0 ");
		
		query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}
// There is no lot in the query
//		if (lot != null) {
//			query = query.setParameter("lot", lot);
//		}

		List<StockUnitResult> suListNoLot = query.getResultList();
		
		log.debug("returned elements without lot: " + suListNoLot.size());
		
		suList.addAll(suListNoLot);
		
		return suList;
	}
	
	/**
	 * Current Amount of adviced goods
	 * 
	 * @param c
	 * @param lot
	 * @param idat
	 * @return
	 */
	public Map<String, QueryInventoryTO> getAdvicedAmount(Client c, Lot lot,
			ItemData idat, Map<String, QueryInventoryTO> result, boolean consolidateLot) {

		String key;
		QueryInventoryTO inv;

		List<AdviseLotResult> ads;
		ads = getAdviseLotInv(c, lot, idat);

		for (AdviseLotResult r : ads) {
			key = getKey(r.lotRef, r.articleRef, consolidateLot);
			if ((inv = (QueryInventoryTO) result.get(key)) == null) {
				if ( (! consolidateLot) && ( r.lotRef == null || r.lotRef.length() == 0)){
					inv = new QueryInventoryTO(r.clientRef, r.articleRef, r.lotRef, r.scale);
					result.put(key, inv);
					inv.advised = inv.advised!=null?inv.advised.add(r.advised):new BigDecimal(0);
				} else{
					continue;
				}
			} else {
				inv.advised = inv.advised!=null?inv.advised.add(r.advised):new BigDecimal(0);
			}
			
			try{
				inv.advised = inv.advised.setScale(r.scale);
			}catch(ArithmeticException ae){}
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<AdviseLotResult> getAdviseLotInv(Client c, Lot lot,
			ItemData idat) {
		// QueryAdvise
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(AdviseLotResult.class.getName());
		// client
		b.append("(");
		b.append("ad");
		b.append(")");

		b.append(" FROM ");
		b.append(LOSAdvice.class.getSimpleName());
		b.append(" ad ");
		b.append(" WHERE ad.client = :client ");
		
		if (lot != null) {
			b.append(" AND ad.lot = :lot ");
		} else {
			//b.append(" AND ad.lot IS NOT NULL ");
		}
		if (idat != null) {
			b.append(" AND ad.itemData = :idat ");
		}

		b.append(" AND ad.adviceState IN ( :rawState, :goodsToComeState, :processingState ) ");

		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);

		if (lot != null) {
			query = query.setParameter("lot", lot);
		}
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}

		query = query.setParameter("rawState", LOSAdviceState.RAW);
		query = query.setParameter("goodsToComeState", LOSAdviceState.GOODS_TO_COME);
		query = query.setParameter("processingState", LOSAdviceState.PROCESSING);
		
		List<AdviseLotResult> adList = query.getResultList();

		return adList;
	}
		
	//-------------------------------------------------------------------------
	
	public QueryInventoryTO getInventory(Client c, Lot lot) throws InventoryException {

		Map<String, QueryInventoryTO> m = getInvMap(c, lot, lot.getItemData(), true);
		
		for (QueryInventoryTO to : m.values()){
			return to;
		}

		throw new InventoryException(InventoryExceptionKey.NO_INVENTORY_FOR_LOT, lot.getName());
		
	}

	public QueryInventoryTO[] getInventory(Client c, ItemData idat, boolean consolidateLot) throws InventoryException {
		QueryInventoryTO[] ret;
		
		if (idat == null){
			throw new NullPointerException();
		}
		
		Map<String, QueryInventoryTO> m = getInvMap(c, null, idat, consolidateLot);
		ret = m.values().toArray(new QueryInventoryTO[0]);
		return ret;
	}
	
	public static String getKey(String lot, String article, boolean consolidate){
		String key;
		
		if (consolidate){
			key = article;
		} else{
			if (lot == null || article == null) throw new NullPointerException();
			key = article + "-*-*-"  + lot;
		}
		
		return key;
	}
	
	//-------------------------------------------------------------------------
	
	final static class StockUnitResult {

		private static final Logger log = Logger.getLogger(StockUnitResult.class);
		
		public StockUnitResult(String clientRef, String articleRef, int scale,
				String lotRef, BigDecimal amount, BigDecimal reserved, int lock) {

			this.clientRef = clientRef;
			this.articleRef = articleRef;
			this.lotRef = lotRef != null ? lotRef : "";
			this.scale = scale;
			this.lock = lock;
			
			if (lock == BusinessObjectLockState.NOT_LOCKED.getLock()) {
				// nothing
			} else if (lock == BusinessObjectLockState.GOING_TO_DELETE.getLock()) {
				// do not regard
				return;
			} else if (lock > BusinessObjectLockState.NOT_LOCKED.getLock()){
				this.locked = amount != null ? amount : new BigDecimal(0);
			}
			
			try {
				this.locked = this.locked.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale
						+ " but was " + amount);
			}

			try {
				this.reserved = reserved.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale
						+ " but was " + reserved);
				this.reserved = reserved;
			}

			try {
				this.inStock = amount.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale
						+ " but was " + amount);
				this.inStock = amount;
			}

			try {
				this.available = amount.subtract(reserved).subtract(locked).setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale
						+ " but was " + amount.subtract(reserved));
				this.available = amount.subtract(reserved);
			}
			
		}

		/** No lot */
		public StockUnitResult(String clientRef, String articleRef, int scale,
				BigDecimal amount, BigDecimal reserved, int lock) {
			this(clientRef, articleRef, scale, null, amount, reserved, lock);
		}
		/**
		 * A unique reference to the ItemData/article
		 */
		public String articleRef;
		/**
		 * A unique reference to the Client
		 */
		public String clientRef;
		/**
		 * A unique reference to the Lot/Lot
		 */
		public String lotRef;
		/**
		 * Number of pieces that are reserved
		 */
		public BigDecimal reserved = new BigDecimal(0);
		/**
		 * Number of pieces that are available
		 */
		public BigDecimal available = new BigDecimal(0);
		/**
		 * Number of pieces that are locked
		 */
		public BigDecimal locked = new BigDecimal(0);

		public BigDecimal inStock = new BigDecimal(0);
		
		public int scale  = 0;
		
		public int lock = 0;
		
	}

	final static class AdviseLotResult {
		
		private static final Logger log = Logger.getLogger(AdviseLotResult.class);

		public AdviseLotResult(String clientRef, String articleRef, int scale,
				String lotRef, BigDecimal amount) {

			this.clientRef = clientRef;
			this.articleRef = articleRef;
			this.lotRef = lotRef != null ? lotRef : "";
			this.advised = amount!=null?amount:new BigDecimal(0);
			
			try{
				this.advised = this.advised.setScale(scale);
			}catch(ArithmeticException ae){
				log.warn("------- Expected scale = "+scale+" but was ");
				this.advised = amount!=null?amount:new BigDecimal(0);
			}
			
			this.scale = scale;
		}

		public AdviseLotResult(LOSAdvice ad) {

			this.clientRef = ad.getClient().getNumber();
			this.articleRef = ad.getItemData().getNumber();
			this.lotRef = ad.getLot() != null ? ad.getLot().getName() : "";
			if (ad.getNotifiedAmount().compareTo(ad.getReceiptAmount()) < 0) { // Ueberlieferung
				
				this.advised = new BigDecimal(0);
				
				try{
					advised = advised.setScale(ad.getItemData().getScale());
				}catch(ArithmeticException ae){
					log.warn("------- Expected scale = "+scale+" but was ");
					this.advised = new BigDecimal(0);
				}
				
			} else {
				this.advised = ad.getNotifiedAmount().subtract(ad.getReceiptAmount());
				try{
					advised = advised.setScale(ad.getItemData().getScale());
				}catch(ArithmeticException ae){
					log.warn("------- Expected scale = "+scale+" but was ");
					this.advised = ad.getNotifiedAmount().subtract(ad.getReceiptAmount());
				}
			}
			this.scale = ad.getItemData().getScale();

		}

		/**
		 * A unique reference to the ItemData/article
		 */
		public String articleRef;
		
		/**
		 * A unique reference to the Client
		 */
		public String clientRef;
		/**
		 * A unique reference to the Lot/Lot
		 */
		public String lotRef;
		/**
		 * Amount of adviced but not yet received items
		 */
		public BigDecimal advised = new BigDecimal(0);
		
		public int scale = 0;
	}

	final static class StockUnitRecordResult {
		
		private static final Logger log = Logger.getLogger(StockUnitRecordResult.class);

		public StockUnitRecordResult(String clientRef, String i, int scale, String lot,
				BigDecimal amount, Date created) throws DatatypeConfigurationException {

			this.clientRef = clientRef;
			this.articleRef = i;
			this.lotRef = lot;
			this.amount = amount!=null?amount:new BigDecimal(0);
			this.scale = scale;
			
			try{
				this.amount = this.amount.setScale(scale);
			}catch(ArithmeticException ae){
				log.warn("------- Expected scale = "+scale+" but was ");
				this.amount = amount!=null?amount:new BigDecimal(0);
			}
			
			GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
			cal.setTime(created);
			this.created = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(cal);
		}

		/**
		 * A unique reference to the ItemData/article
		 */
		public String articleRef;
		/**
		 * A unique reference to the Client
		 */
		public String clientRef;
		/**
		 * A unique reference to the Lot/Lot
		 */
		public String lotRef;

		public BigDecimal amount = new BigDecimal(0);;

		public XMLGregorianCalendar created;
		
		public int scale;
	}

	
}
