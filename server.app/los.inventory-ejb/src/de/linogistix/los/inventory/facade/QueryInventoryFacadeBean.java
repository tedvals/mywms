/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.inventory.businessservice.QueryInventoryBusiness;
import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.QueryAdviceService;


/**
 * A Webservice for retrieving inventory information from the wms.
 *  
 * @see de.linogistix.los.inventory.connector.QueryInventoryFacade
 * @author trautm
 *
 */
@Stateless 
public class QueryInventoryFacadeBean implements QueryInventoryFacade{

	Logger log = Logger.getLogger(QueryInventoryFacadeBean.class);
	
	@EJB
	ItemDataService itemDataService;
	
	@EJB
	ClientService clientService;
	
	@EJB
	LOSLotService lotService;
	
	@EJB
	QueryAdviceService adviseService;
	
	@EJB 
	StockUnitService suService; 
	
	@EJB
	QueryInventoryBusiness invBusiness;
	
	@PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;
	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByArticle(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryByArticle(
			@WebParam(name = "clientRef") String clientRef,
			@WebParam(name = "articleRef") String articleRef,
			@WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException{
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientRef);
		}
		
		ItemData idat = itemDataService.getByItemNumber(c,  articleRef);
		
		if(idat == null){
			log.error("--- !!! NO ITEM WITH NUMBER > "+articleRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
		}else{
			return invBusiness.getInventory(c,idat, consolidateLot);
		}
	}

	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByBatch(java.lang.String, java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO getInventoryByLot(
			@WebParam(name = "clientRef")  String clientRef,
			@WebParam(name = "articleRef")  String articleRef,
			@WebParam(name = "lotRef")  String lotRef) throws InventoryException {
		
		Client c = clientService.getByNumber(clientRef);
		Lot lot;
		ItemData idat;
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientRef);
		}

		idat = itemDataService.getByItemNumber(c, articleRef);
		if (idat == null){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
		}
		
		try{
			lot = lotService.getByNameAndItemData(c, lotRef, articleRef);
		} catch (EntityNotFoundException ex){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_LOT, lotRef);
		}
		
		return invBusiness.getInventory(c, lot); 
		
	}

	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryList(java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryList(
			@WebParam(name = "clientRef")  String clientRef,
			@WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException{
		
		QueryInventoryTO[] ret;
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			return new QueryInventoryTO[0];
		}
			
		ret = invBusiness.getInventory(c, consolidateLot);

		return ret;

	}
	
}
