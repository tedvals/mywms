/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.StockUnit;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSStockUnitRecordServiceBean 
				extends BasicServiceBean<LOSStockUnitRecord> 
				implements LOSStockUnitRecordService 
{
	
	private static final Logger log = Logger.getLogger(LOSStockUnitRecordServiceBean.class);

	@EJB
	ContextService contextService;
	
	@Deprecated
	public LOSStockUnitRecord record(BigDecimal amount, StockUnit from, StockUnit to, LOSStockUnitRecordType type, String text){
		
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		rec.setAmount(amount);
		rec.setClient(from.getClient());
		
		rec.setFromStockUnitIdentity(from.toUniqueString());
		rec.setFromUnitLoad(from.getUnitLoad().toUniqueString());
		rec.setFromStorageLocation(((LOSUnitLoad)from.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().toUniqueString());
		rec.setToStorageLocation(((LOSUnitLoad)to.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setItemData(from.getItemData().toUniqueString());
		rec.setScale(from.getItemData().getScale());
		if (from.getLot() != null) rec.setLot(from.getLot().toUniqueString());
		rec.setOperator(contextService.getCallersUser().toUniqueString());
		rec.setType(type);
		rec.setActivityCode(text);
		
		rec.setSerialNumber(from.getSerialNumber());
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
		
	}

	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String activityCode) {
		return recordCreation(amount, to, activityCode, null, null);
	}
	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String activityCode, String comment, String operator) {
		if( BigDecimal.ZERO.compareTo(amount) == 0 ) {
			log.debug("Do not record zero amount creation");
			return null;
		}
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		rec.setOperator(operator == null ? contextService.getCallersUser().toUniqueString() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(to.getAmount());
		rec.setClient(to.getClient());
		
		rec.setFromStockUnitIdentity(to.toUniqueString());
		rec.setFromUnitLoad(to.getUnitLoad().toUniqueString());
		rec.setFromStorageLocation(((LOSUnitLoad)to.getUnitLoad()).getStorageLocation().toUniqueString());

		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().toUniqueString());
		rec.setToStorageLocation(((LOSUnitLoad)to.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setItemData(to.getItemData().toUniqueString());
		rec.setScale(to.getItemData().getScale());
		if (to.getLot() != null) rec.setLot(to.getLot().toUniqueString());
		rec.setType(LOSStockUnitRecordType.STOCK_CREATED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(to.getSerialNumber());
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
	}

	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String activityCode) {
		return recordChange(amount, to, activityCode, null, null);
	}
	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		rec.setOperator(operator == null ? contextService.getCallersUser().toUniqueString() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(to.getAmount());
		rec.setClient(to.getClient());
		
		rec.setFromStockUnitIdentity(to.toUniqueString());
		rec.setFromUnitLoad(to.getUnitLoad().toUniqueString());
		rec.setFromStorageLocation(((LOSUnitLoad)to.getUnitLoad()).getStorageLocation().toUniqueString());

		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().toUniqueString());
		rec.setToStorageLocation(((LOSUnitLoad)to.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setItemData(to.getItemData().toUniqueString());
		rec.setScale(to.getItemData().getScale());
		if (to.getLot() != null) rec.setLot(to.getLot().toUniqueString());
		rec.setType(LOSStockUnitRecordType.STOCK_ALTERED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(to.getSerialNumber());
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
	}


	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String activityCode) {
		return this.recordRemoval(amount, su, activityCode, null, null);
	}
	
	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		rec.setOperator(operator == null ? contextService.getCallersUser().toUniqueString() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(su.getAmount());
		rec.setClient(su.getClient());
		
		rec.setFromStockUnitIdentity(su.toUniqueString());
		rec.setFromUnitLoad(su.getUnitLoad().toUniqueString());
		rec.setFromStorageLocation(((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setToStockUnitIdentity(su.toUniqueString());
		rec.setToUnitLoad(su.getUnitLoad().toUniqueString());
		rec.setToStorageLocation(((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().toUniqueString());
		
		rec.setItemData(su.getItemData().toUniqueString());
		rec.setScale(su.getItemData().getScale());
		if (su.getLot() != null) rec.setLot(su.getLot().toUniqueString());
		rec.setType(LOSStockUnitRecordType.STOCK_REMOVED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(su.getSerialNumber());
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
	}

	@SuppressWarnings("unchecked")
	public List<LOSStockUnitRecord> getByStockUnitAndType(StockUnit su,
			LOSStockUnitRecordType type) {
		
		StringBuffer b = new StringBuffer();
		b.append(" SELECT o ");
		b.append(" FROM ");
		b.append(LOSStockUnitRecord.class.getName());
		b.append(" o WHERE o.type=:type ");
		b.append(" AND ( o.toStockUnitIdentity=:su OR o.fromStockUnitIdentity=:su )");
		b.append(" ORDER BY o.id ");
		
		Query q = manager.createQuery(new String(b));
		q = q.setParameter("su", su.toUniqueString());
		q = q.setParameter("type", type);
		
		List<LOSStockUnitRecord> ret = q.getResultList();
		
		return ret;
		
	}

	public LOSStockUnitRecord recordTransfer(StockUnit su, LOSUnitLoad old, LOSUnitLoad dest, String activityCode) {
		return recordTransfer(su, old, dest, activityCode, null, null);
	}
	
	public LOSStockUnitRecord recordTransfer(StockUnit su, LOSUnitLoad old, LOSUnitLoad dest, String activityCode, String comment, String operator) {
		if( BigDecimal.ZERO.compareTo(su.getAmount()) == 0 ) {
			log.debug("Do not record zero amount transfer");
			return null;
		}
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		rec.setOperator(operator == null ? contextService.getCallersUser().toUniqueString() : operator);
		rec.setAmount(BigDecimal.ZERO);
		rec.setAmountStock(su.getAmount());
		rec.setClient(su.getClient());
		
		rec.setFromStockUnitIdentity(su.toUniqueString());
		rec.setFromUnitLoad(old.toUniqueString());
		rec.setFromStorageLocation((old).getStorageLocation().toUniqueString());
		
		rec.setToStockUnitIdentity(su.toUniqueString());
		rec.setToUnitLoad(dest.toUniqueString());
		rec.setToStorageLocation(dest.getStorageLocation().toUniqueString());
		
		rec.setItemData(su.getItemData().toUniqueString());
		rec.setScale(su.getItemData().getScale());
		if (su.getLot() != null) rec.setLot(su.getLot().toUniqueString());
		rec.setType(LOSStockUnitRecordType.STOCK_TRANSFERRED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(su.getSerialNumber());
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
		
	}
	
	public LOSStockUnitRecord recordCounting(StockUnit su, LOSUnitLoad ul, LOSStorageLocation loc, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = new LOSStockUnitRecord();
		
		rec.setClient(su != null ? su.getClient() : ul != null ? ul.getClient() : loc != null ? loc.getClient() : null);
		
		rec.setOperator(operator == null ? contextService.getCallersUser().toUniqueString() : operator);
		rec.setType(LOSStockUnitRecordType.STOCK_COUNTED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		rec.setScale( 0 );
		
		
		if( su != null ) {
			rec.setFromStockUnitIdentity(su.toUniqueString());
			rec.setFromUnitLoad(su.getUnitLoad().toUniqueString());
			rec.setFromStorageLocation(((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().toUniqueString());
			rec.setToStockUnitIdentity(su.toUniqueString());
			rec.setToUnitLoad(su.getUnitLoad().toUniqueString());
			rec.setToStorageLocation(((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().toUniqueString());
			rec.setAmountStock( su.getAmount() );
			
			rec.setItemData(su.getItemData().toUniqueString());
			rec.setScale(su.getItemData().getScale());
			if (su.getLot() != null) rec.setLot(su.getLot().toUniqueString());
			rec.setSerialNumber(su.getSerialNumber());
			
		}
		else if( ul != null ) {
			rec.setFromUnitLoad(ul.toUniqueString());
			rec.setFromStorageLocation(ul.getStorageLocation().toUniqueString());
			rec.setToUnitLoad(ul.toUniqueString());
			rec.setToStorageLocation(ul.getStorageLocation().toUniqueString());
			
		}
		else if( loc != null ) {
			rec.setFromStorageLocation(loc.toUniqueString());
			rec.setToStorageLocation(loc.toUniqueString());
			
		}
		else {
			rec.setFromStorageLocation("-");
			rec.setToStorageLocation("-");
			
		}
		
		
		
		
		manager.persist(rec);
		manager.flush();
		
		return rec;
		
	}

}
