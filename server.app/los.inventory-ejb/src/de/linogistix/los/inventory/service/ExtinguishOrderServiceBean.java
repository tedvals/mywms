/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Lot;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.LogItemService;

import de.linogistix.los.crud.BasicEntityMergeException;
import de.linogistix.los.crud.BasicEntityMerger;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class ExtinguishOrderServiceBean extends
		BasicServiceBean<ExtinguishOrder> implements ExtinguishOrderService {

	private static final Logger log = Logger
			.getLogger(ExtinguishOrderServiceBean.class);
	@EJB
	LOSStorageLocationService slService;

	@EJB
	ClientService clientService;

	@EJB
	ContextService contextService;

	@EJB
	InventoryGeneratorService genService;

	@EJB
	LogItemService logService;

	public ExtinguishOrder create(Lot lot, Date startDate) {
		ExtinguishOrder order;
		LOSStorageLocation sl;

		sl = slService.getNirwana();

//		lot = manager.find(Lot.class, lot.getId());
//		lot.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
		
		try {
			order = getByLot(lot);
			if (order != null){
				log.warn("ExtinguishOrder already exists: " + order.toDescriptiveString());
				return order;
			}
		} catch (EntityNotFoundException e) {
			log.info("going to create new ExtinguishOrder for lot " + lot.toDescriptiveString());
		}
		
		order = new ExtinguishOrder();
		order.setOrderType(OrderType.TO_EXTINGUISH);
		order.setDestination(sl);
		order.setDelivery(startDate);
		order.setAuthorizedBy(contextService.getCallersUser());
		order.setClient(lot.getClient());
		order.setLot(lot);
		order.setNumber(genService.generateExtinguishOrderNumber(lot
				.getClient()));
		order.setOrderState(LOSOrderRequestState.RAW);
		order.setRequestId(order.getNumber());

		manager.persist(order);
		manager.flush();

//		logService.create(order.getClient(), "SERVER",
//				ExtinguishOrderServiceBean.class.getSimpleName(),
//				contextService.getCallersUser().getName(), LogItemType.LOG,
//				"de.linogistix.los.imventory.res.Bundle",
//				"CREATED ExtinguishOrder: " + order.getRequestId(),
//				InventoryLogKeys.CREATED_EXTINGUISHORDER.name(),
//				new Object[] { order.getRequestId() });
		return order;

	}

	public ExtinguishOrder getByLot(Lot lot) throws EntityNotFoundException {
		if (lot == null) {
			log.error(ExtinguishOrderServiceBean.class.getSimpleName()
					+ "Lot is null");
			throw new NullPointerException("The Parameter: lot is null");
		}

		Query query = manager.createQuery("SELECT eo FROM "
				+ ExtinguishOrder.class.getSimpleName() + " eo "
				+ " WHERE eo.lot=:lot");

		query.setParameter("lot", lot);
		try {
			return (ExtinguishOrder) query.getSingleResult();

		} catch (NoResultException e) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_ENTITY_WITH_ID);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ExtinguishOrder> getRipe() {
		Date date = new Date();
		StringBuffer b = new StringBuffer();
		b.append(" SELECT o FROM ");
		b.append(ExtinguishOrder.class.getName());
		b.append(" o ");
		b.append(" WHERE o.delivery >=:today");

		Query q = manager.createQuery(new String(b));
		q = q.setParameter("today", date);
		try {
			return (List<ExtinguishOrder>)q.getResultList();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return new ArrayList<ExtinguishOrder>();
		}
	}

	public void delete(ExtinguishOrder entity)
			throws ConstraintViolatedException {

		entity = manager.find(ExtinguishOrder.class, entity.getId());
		manager.remove(entity);

	}

	public ExtinguishOrder merge(ExtinguishOrder entity) {
		ExtinguishOrder to = manager
				.find(ExtinguishOrder.class, entity.getId());
		BasicEntityMerger<ExtinguishOrder> merger = new BasicEntityMerger<ExtinguishOrder>();
		try {
			merger.mergeInto(entity, to);
		} catch (BasicEntityMergeException e) {
			log.error(e.getMessage(), e);
			return entity;
		}
		return to;

	}

}
