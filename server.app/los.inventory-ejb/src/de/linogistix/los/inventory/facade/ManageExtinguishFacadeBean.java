/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.businessservice.ExtinguishBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.query.dto.ExtinguishOrderTO;
import de.linogistix.los.inventory.service.ExtinguishOrderService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author liu
 * 
 */
@Stateless
public class ManageExtinguishFacadeBean implements
		ManageExtinguishFacade {

	Logger log = Logger.getLogger(ManageExtinguishFacadeBean.class);

	@EJB
	ClientService clientService;

	@EJB
	ExtinguishBusiness extinguishBusiness;

	@EJB
	ExtinguishOrderService extinguishOrderService;

	@EJB
	ContextService context;
	
	@EJB
	LOSLotService lotService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	

	
	public BODTO<ExtinguishOrder> createExtinguishOrder(String lotName, String itemDataNumber, String clientNumber, Date data) throws InventoryException {
		Client client;

		client = clientService.getByNumber(clientNumber);
		if( client == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT,
					new String[] { clientNumber });
		}

		
		List<Lot> l = lotService.getListByName(client, lotName, itemDataNumber);
		if (l.size() > 1){
			throw new RuntimeException("More than one lot with identical name: " + lotName);
		}
		else if (l.size() < 1){
			log.warn("No lot found for lotName="+lotName+", itemDataNumber="+itemDataNumber+", clientNumber="+clientNumber);
			throw new InventoryException(InventoryExceptionKey.NO_LOT_WITH_NAME, lotName);
		}
		ExtinguishOrder order = extinguishBusiness.createExtinguishOrder(l.get(0), data);

		return new ExtinguishOrderTO(order);
		
	}

	public void startExtinguishOrder(BODTO<ExtinguishOrder> orderTO)
			throws InventoryException {
		ExtinguishOrder order;
		try {
			order = extinguishOrderService.get(orderTO.getId());
			extinguishBusiness.startExtinguishOrder(order);
		} catch (EntityNotFoundException e) {
			throw new InventoryException(
					InventoryExceptionKey.NO_EXTINGUISHORDER_WITH_NUMBER,
					new Long[] { orderTO.getId()});
		}
		
	}
	
	public void createCronJob() {
		extinguishBusiness.createCronJob();
	}
	
	public List<Lot> getTooOld(Client c){
		return extinguishBusiness.getTooOld(c);
	}
	
	public List<Lot> getNotToUse(Client c){
		return extinguishBusiness.getNotToUse(c);
	}
	
	public List<Lot> getToUseFromNow(Client c){
		return extinguishBusiness.getToUseFromNow(c);
	}
	
	public void createExtinguishOrders(List<BODTO<Lot>> list) throws InventoryException{
		for (BODTO<Lot> l : list){
			Lot lot = manager.find(Lot.class, l.getId());
			this.createExtinguishOrder(lot.getName(), lot.getItemData().getNumber(), lot.getClient().getNumber(), new Date());
		}
	}

	public void processLots() {
		extinguishBusiness.processLots();
		
	}
}
