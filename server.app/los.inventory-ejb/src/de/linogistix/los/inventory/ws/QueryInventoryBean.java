/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.QueryInventoryFacade;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;


/**
 * A Webservice for retrieving inventory information from the wms.
 *  
 * @see de.linogistix.los.inventory.connector.QueryInventory
 * @author trautm
 *
 */


@Stateless 
@SecurityDomain("los-login")
@Remote(QueryInventory.class)
@WebService(endpointInterface="de.linogistix.los.inventory.ws.QueryInventory")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
public class QueryInventoryBean implements QueryInventory{

	Logger log = Logger.getLogger(QueryInventoryBean.class);
	
    @EJB
    QueryInventoryFacade delegate;
	 
	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByArticle(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryByArticle(
            @WebParam(name = "clientRef") String clientRef, 
            @WebParam(name = "articleRef") String articleRef, 
			@WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException{
		
        return delegate.getInventoryByArticle(clientRef, articleRef, consolidateLot);
	}
	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByBatch(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO getInventoryByLot(
            @WebParam(name="clientRef") String clientRef, 
            @WebParam(name="articleRef") String articleRef, 
			@WebParam(name="lotRef") String lotRef) throws InventoryException{
		
        return delegate.getInventoryByLot(clientRef, articleRef, lotRef);
	}

	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryList(java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryList(
            @WebParam(name = "clientRef")  String clientRef, 
            @WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException {
		
        return delegate.getInventoryList(clientRef, consolidateLot);

	}
	
}
