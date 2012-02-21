/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.PickingRequestState;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.businessservice.ContextService;
@Stateless
public class LOSPickRequestServiceBean extends BasicServiceBean<LOSPickRequest> implements LOSPickRequestService{

	
	@EJB
	ContextService contextService;
	
	@SuppressWarnings("unchecked")
	public List<LOSPickRequest> getListByState(Client client, PickingRequestState state){
			StringBuffer b = new StringBuffer();

	b.append("SELECT pr FROM ");

	b.append(LOSPickRequest.class.getSimpleName());
	b.append(" pr ");
	b.append(" WHERE pr.state=:state ");
	if (!client.isSystemClient()){
		b.append(" AND pr.client=:client ");
	}
	
	b.append("ORDER BY pr.number ASC");

	Query query = manager.createQuery(new String(b));
	if (!client.isSystemClient()){
		query.setParameter("client", client);
	}
	query.setParameter("state", state);

	return (List<LOSPickRequest>) query.getResultList();
	}

	@Override
	public void delete(LOSPickRequest r){
		r = manager.find(LOSPickRequest.class, r.getId());
		List<LOSPickRequestPosition> pos = r.getPositions();
		for (LOSPickRequestPosition p : pos){
			p = manager.find(LOSPickRequestPosition.class, p.getId());
			if (p != null){
				manager.remove(p);
			}
		}
		manager.remove(r);
		manager.flush();
		
	}

	
	@SuppressWarnings("unchecked")
	public List<LOSPickRequest> getListByOrder(LOSOrderRequest order){
		
		StringBuffer b = new StringBuffer();	
		b.append("SELECT pr FROM ");
		b.append(LOSPickRequest.class.getSimpleName());
		b.append(" pr ");
		b.append(" WHERE pr.parentRequest=:order ");
		b.append("ORDER BY pr.number ASC");
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("order", order);

		return (List<LOSPickRequest>) query.getResultList();
	
	}

	public LOSPickRequest getByUnitLoadOnCart(String unitLoadId) throws EntityNotFoundException{
		StringBuffer b = new StringBuffer();	
		b.append("SELECT pr FROM ");
		b.append(LOSPickRequest.class.getSimpleName());
		b.append(" pr, ");
		b.append(LOSUnitLoad.class.getSimpleName());
		b.append(" ul ");
		b.append(" WHERE pr.cart = ul.storageLocation " );
		b.append(" AND ul.labelId = :unitLoadId " );
		b.append("ORDER BY pr.number ASC");
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("unitLoadId", unitLoadId);

		try{
			return (LOSPickRequest) query.getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_ENTITY_WITH_ID);
		}
	}
	
	public LOSPickRequest createPickRequest(LOSOrderRequest order, String name) {

		LOSPickRequest request = new LOSPickRequest();
		request.setClient(order.getClient());
		request.setParentRequest(order);
		request.setState(PickingRequestState.RAW);

		request.setNumber(name);
		request.setCustomerNumber(order.getClient().getNumber());
		request.setDestination(order.getDestination());
		request.setState(PickingRequestState.RAW);
		request.setUser(contextService.getCallersUser());
		manager.persist(request);
		manager.flush();

		
		
		return request;
	}
	

}
