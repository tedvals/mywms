/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.businessservice.LOSGoodsOutBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.util.BusinessObjectHelper;
@Stateless
public class LOSGoodsOutFacadeBean implements LOSGoodsOutFacade {

	private static final Logger log = Logger.getLogger(LOSGoodsOutFacadeBean.class);

	@EJB
	UnitLoadQueryRemote ulQuery ;
	
	@EJB
	LOSGoodsOutBusiness outBusiness;
	
	@EJB
	LOSGoodsOutRequestQueryRemote outQuery;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public LOSGoodsOutRequest confirm(long goodsOutId) throws FacadeException {
		LOSGoodsOutRequest out = manager.find(LOSGoodsOutRequest.class, goodsOutId);
		if( out == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[]{});
		}
		out = outBusiness.finish(out,true);
		return out;
	}

	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out) throws FacadeException{	 	
		out  = manager.find(LOSGoodsOutRequest.class, out.getId());
		out = outBusiness.finish(out);
		return (LOSGoodsOutRequest)BusinessObjectHelper.eagerRead(out);
	}

	public LOSGoodsOutRequestPosition finishPosition(String labelId, LOSGoodsOutRequest reqActual)
			throws FacadeException {
		LOSGoodsOutRequestPosition ret;
		LOSUnitLoad ul = null;
		LOSGoodsOutRequest req = null;
		
		try {
			ul = (LOSUnitLoad)ulQuery.queryByIdentity(labelId);
		} catch (BusinessObjectNotFoundException e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}
		
		try {
			req = outBusiness.getByUnitLoad(ul);
		} catch (EntityNotFoundException e1) {
			throw new InventoryException(InventoryExceptionKey.SHIPPING_MSG_NO_GOODSOUT, new Object[]{});
		}
		
		if( ! req.equals(reqActual) ) {
			throw new InventoryException(InventoryExceptionKey.SHIPPING_MSG_WRONG_GOODSOUT, new Object[]{});
		}
		
		try {
			ret = outBusiness.finishPosition(ul);
			return ret;
		} catch (EntityNotFoundException e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}	
	}


	public List<LOSGoodsOutRequestTO> getRaw() {
		List<LOSGoodsOutRequestTO> out;
		out = outBusiness.getRaw();
		return out; 
	}

	public LOSGoodsOutRequest start(LOSGoodsOutRequest req) throws FacadeException {
		req = manager.find(LOSGoodsOutRequest.class, req.getId());
		
		req = outBusiness.accept(req);
		
		if (req.getPositions().size() < 1) { 
			log.warn("No positions found: "  +req.toDescriptiveString() );
		}
		
		if (!req.getOutState().equals(LOSGoodsOutRequestState.PROCESSING)){
			log.error("Unexpected state: " + req.toDescriptiveString());
		}
		
		return req;
	}


	public void cancel(LOSGoodsOutRequest req) throws FacadeException{
		
		req = manager.find(LOSGoodsOutRequest.class, req.getId());
		req = outBusiness.cancel(req);
		
		return;
	}
	
	public LOSGoodsOutRequestPosition getNextPosition(LOSGoodsOutRequest currentOrder) throws FacadeException {
		StringBuffer b = new StringBuffer();
		Query query;

		b.append(" SELECT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.goodsOutRequest=:order ");
		b.append(" and pos.outState=:state ");
		b.append(" ORDER BY pos.source.storageLocation.name, pos.source.labelId");

		query = manager.createQuery(b.toString());
		
		query.setParameter("order", currentOrder);
		query.setParameter("state", LOSGoodsOutRequestPositionState.RAW);
		query.setMaxResults(1);
		
		LOSGoodsOutRequestPosition pos = null;
		try {
			pos = (LOSGoodsOutRequestPosition)query.getSingleResult();
		}
		catch( NoResultException e ) {
			log.info("NOTHING FOUND for order " + currentOrder.getNumber());
		}
		
		if( pos != null ) {
			pos.getSource().getStorageLocation().getName();
		}
		return pos;
	}

	
	
	public LOSGoodsOutTO load(String number) throws FacadeException {
		LOSGoodsOutRequest req = null; 
		try {
			req = outQuery.queryByIdentity(number);
		}
		catch( BusinessObjectNotFoundException e) {}
		if( req != null ) {
			return getOrderInfo(req);
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public LOSGoodsOutTO getOrderInfo(LOSGoodsOutRequest order) throws FacadeException {

		LOSGoodsOutTO to = new LOSGoodsOutTO( order );
		
		StringBuffer b = new StringBuffer();
		Query query;

		b.append(" SELECT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.goodsOutRequest=:order ");
		b.append(" and pos.outState=:state ");
		b.append(" ORDER BY pos.source.storageLocation.name, pos.source.labelId");

		query = manager.createQuery(b.toString());
		
		query.setParameter("order", order);
		query.setParameter("state", LOSGoodsOutRequestPositionState.RAW);

		try {
			List<LOSGoodsOutRequestPosition> posList = null;
			posList = query.getResultList();
			if( posList.size()>0 ) {
				to.setNumPosOpen( posList.size() );
				LOSGoodsOutRequestPosition next = posList.get(0);
				to.setNextLocationName( next.getSource().getStorageLocation().getName() );
				to.setNextUnitLoadLabelId( next.getSource().getLabelId() );
			}
			else {
				to.setFinished(true);
			}
		}
		catch( NoResultException e ) {}

		
		b = new StringBuffer();
		b.append(" SELECT count(*) FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.goodsOutRequest=:order ");
		b.append(" and pos.outState=:state ");

		query = manager.createQuery(b.toString());
		
		query.setParameter("order", order);
		query.setParameter("state", LOSGoodsOutRequestPositionState.FINISHED);
		
		try {
			Long numPosDone = (Long)query.getSingleResult();
			to.setNumPosDone(numPosDone);
		}
		catch( NoResultException e ) {}

		return to;
	}

	public LOSGoodsOutRequest update(LOSGoodsOutRequest req, String comment) throws FacadeException {
		req = manager.find(LOSGoodsOutRequest.class, req.getId());
		req.setAdditionalContent(comment);
		return req;
	}

	public void remove(long goodsOutId) throws FacadeException {
		LOSGoodsOutRequest out = manager.find(LOSGoodsOutRequest.class, goodsOutId);
		if( out == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[]{});
		}
		outBusiness.remove(out);
	}

}
