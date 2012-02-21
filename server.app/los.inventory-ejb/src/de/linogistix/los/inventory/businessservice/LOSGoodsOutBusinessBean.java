/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.annotation.IgnoreDependency;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Request;
import org.mywms.model.UnitLoad;
import org.mywms.model.User;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.inventory.pick.service.LOSPickRequestPositionService;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * 
 * @author trautm
 * 
 */
@Stateless
public class LOSGoodsOutBusinessBean implements LOSGoodsOutBusiness {

	private static final Logger log = Logger
			.getLogger(LOSGoodsOutBusinessBean.class);

	@EJB
	@IgnoreDependency
	OrderBusiness orderBusiness;

	@EJB
	UnitLoadQueryRemote ulQuery;

	@EJB
	InventoryGeneratorService genService;

	@EJB
	LOSGoodsOutRequestService outService;

	@EJB
	LOSPickRequestPositionService pickPosService;

	@EJB
	LOSGoodsOutRequestPositionService posService;

	@EJB
	ContextService context;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.los.inventory.businessservice.LOSGoodsOutBusiness#goodsOut(de.linogistix.los.inventory.model.LOSOrderRequest)
	 */
	public LOSGoodsOutRequest create(LOSOrderRequest order) throws FacadeException {
		LOSGoodsOutRequest r;
		LOSStorageLocation dest = order.getDestination();
		dest = manager.find(LOSStorageLocation.class, dest.getId());
		order = manager.find(LOSOrderRequest.class, order.getId());

		try {
			r = outService.getByOrder(order);
		} catch (EntityNotFoundException ex) {
			r = new LOSGoodsOutRequest();
			r.setClient(order.getClient());
			r.setParentRequest(order);
			r.setNumber(genService.generateGoodsOutNumber(r.getClient()));
		}

		manager.persist(r);
		manager.flush();

		for (LOSOrderRequestPosition p : order.getPositions()) {

			List<LOSPickRequestPosition> picks;
			picks = pickPosService.getByOrderPosition(p);

			for (LOSPickRequestPosition pick : picks) {

				LOSUnitLoad ul = (LOSUnitLoad) pick.getStockUnit()
						.getUnitLoad();

				LOSGoodsOutRequestPosition pos;
				try {
					pos = posService.getByUnitLoad(ul);
					continue;
				} catch (EntityNotFoundException ex) {
					pos = new LOSGoodsOutRequestPosition();
					pos.setSource(ul);
					pos.setGoodsOutRequest(r);
					pos.setOutState(LOSGoodsOutRequestPositionState.RAW);
					manager.persist(pos);
				}

			}

		}

		manager.flush();

		return r;
	}

	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out) throws FacadeException {
		return finish(out, false);
	}

	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out, boolean force) throws FacadeException {

		for (LOSGoodsOutRequestPosition pos : out.getPositions()) {
			switch (pos.getOutState()) {
			case RAW:
				if (force) {
					try {
						finishPosition((LOSUnitLoad) pos.getSource());
					} catch (EntityNotFoundException e) {
						log.error(e.getMessage(), e);
						continue;
					}
				} else {
					throw new InventoryException(
							InventoryExceptionKey.ORDER_NOT_FINIHED, "");
				}
			case FINISHED:
				// OK
				break;
			default:
				throw new RuntimeException("Unknown state: "
						+ pos.getOutState().toString());
			}
		}

		out.setOutState(LOSGoodsOutRequestState.FINISHED);
		Request prox = out.getParentRequest();
		LOSOrderRequest r = manager.find(LOSOrderRequest.class, prox.getId());
		if (r != null) {
			orderBusiness.finishOrder(r, false);
		}
		return out;
	}

	public LOSGoodsOutRequestPosition finishPosition(LOSUnitLoad ul)
			throws FacadeException, EntityNotFoundException {

		LOSGoodsOutRequestPosition pos = posService.getByUnitLoad(ul);
		pos.setOutState(LOSGoodsOutRequestPositionState.FINISHED);

		return pos;

	}

	public LOSGoodsOutRequest getByUnitLoad(UnitLoad ul)
			throws EntityNotFoundException, FacadeException {
		return outService.getByUnitLoad(ul);
	}

	@SuppressWarnings("unchecked")
	public List<LOSGoodsOutRequestTO> getRaw() {
		User user = context.getCallersUser();
		Client c = user.getClient();

		StringBuffer b = new StringBuffer();
		Query query;

		b.append(" SELECT new "+LOSGoodsOutRequestTO.class.getName()+"(out.id, out.version, out.number, out.number, out.parentRequest.requestId, out.outState, out.client.number) FROM ");
		b.append(LOSGoodsOutRequest.class.getName());
		b.append(" out ");
		b.append(" WHERE ( out.outState=:raw ");
		b.append(" or (out.outState=:processing and operator=:user) )");
		if (!c.isSystemClient()) {
			b.append(" AND out.client=:client ");
		}
		b.append(" ORDER BY out.number ");
		
		query = manager.createQuery(b.toString());
		
		query.setParameter("raw", LOSGoodsOutRequestState.RAW);
		query.setParameter("processing", LOSGoodsOutRequestState.PROCESSING);
		query.setParameter("user", context.getCallersUser());
		if (!c.isSystemClient()) {
			query.setParameter("client", c);
		}

		return query.getResultList();
	}

	public LOSGoodsOutRequest accept(LOSGoodsOutRequest req) throws FacadeException {
		log.info("start LOSGoodsOutRequest: " + req.getNumber());

		if( req.getOutState() == LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is already finished! Cannot cancel");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, new Object[]{});
		}

		req.setOperator(context.getCallersUser());
		req.setOutState(LOSGoodsOutRequestState.PROCESSING);

		return req;
	}

	public LOSGoodsOutRequest cancel(LOSGoodsOutRequest req) throws FacadeException {
		log.info("cancel LOSGoodsOutRequest: " + req.getNumber());
		if( req.getOutState() == LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is already finished! Cannot cancel");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, new Object[]{});
		}
		
		boolean hasOpen = false;
		boolean hasClosed = false;
		for(LOSGoodsOutRequestPosition pos : req.getPositions()) {
			if( pos.getOutState().equals(LOSGoodsOutRequestPositionState.RAW)) {
				hasOpen = true;
			}
			else if( pos.getOutState().equals(LOSGoodsOutRequestPositionState.FINISHED)) {
				hasClosed = true;
			}
		}
		
		if( hasOpen == false ) {
			log.warn("No Open positions found for LOSGoodsOutRequest " + req.getNumber() + ". => It will be finished");
			req.setOutState(LOSGoodsOutRequestState.FINISHED);
			return req;
		}
		
		if( hasClosed == false ) {
			req.setOperator(null);
		}
		req.setOutState(LOSGoodsOutRequestState.RAW);
		
		return req;
	}

	public void remove(LOSGoodsOutRequest req) throws FacadeException {
		log.info("remove LOSGoodsOutRequest: " + req.getNumber());
		if( req.getOutState() != LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is not finished! Cannot remove");
			throw new InventoryException(InventoryExceptionKey.ORDER_NOT_FINISHED, req.getNumber());
		}
		
		for(LOSGoodsOutRequestPosition pos : req.getPositions()) {
			manager.remove(pos);
		}
		manager.remove(req);
		
	}

}
