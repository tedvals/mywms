/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.mywms.facade.FacadeException;

/**
 * A Webservice for managing ItemData/articles in the wms.
 * 
 * @see ManageInventory
 * 
 * @author trautm
 *
 */

@Stateless
@SecurityDomain("los-login")
@Remote(ManageInventory.class)
@WebService(endpointInterface="de.linogistix.los.inventory.ws.ManageInventory")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)

public class ManageInventoryBean implements ManageInventory {

	Logger log = Logger.getLogger(ManageInventoryBean.class);
	
    @EJB
    ManageInventoryFacade delegate;
    
	/* 
	 * @see ManageInventoryRemote#createItemData(java.lang.String, java.lang.String)
	 */
	public boolean createItemData (
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef)throws InventoryException {
		
        return delegate.createItemData(clientRef, articleRef);
        
	}

	/* 
	 * @see ManageInventoryRemote#deleteItemData(java.lang.String, java.lang.String)
	 */
	public boolean deleteItemData(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef) {


        return delegate.deleteItemData(clientRef, articleRef);
	}

	/* 
	 * @see ManageInventoryRemote#updateItemReference(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean updateItemReference(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="existingRef") String existingRef,
			@WebParam( name="newRef") String newRef) {
		
        return delegate.updateItemReference(clientRef,existingRef, newRef);
	}

	public boolean createAvis(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef,
			@WebParam( name="articleRef") String articleRef,
			@WebParam( name="batchRef") String batchRef,
			@WebParam(name="amount") BigDecimal amount,
			@WebParam( name="expectedDelivery") Date expectedDelivery,
			@WebParam( name="bestBeforeEnd") Date bestBeforeEnd,
			@WebParam( name="useNotBefore") Date useNotBefore,
			@WebParam(name="expireBatch") boolean expireBatch)
			{
		
        return delegate.createAvis(clientRef,articleRef, batchRef, amount, expectedDelivery, bestBeforeEnd, useNotBefore, expireBatch);
		
	}
	
	/**
	 * 
	 */
	public boolean createAvis(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef,
			@WebParam( name="articleRef") String articleRef,
			@WebParam( name="batchRef") String batchRef,
			@WebParam(name="amount") BigDecimal amount,
			@WebParam( name="expectedDelivery") Date expectedDelivery,
			@WebParam( name="bestBeforeEnd") Date bestBeforeEnd,
			@WebParam( name="useNotBefore") Date useNotBefore,
			@WebParam(name="expireBatch") boolean expireBatch,
			@WebParam(name="parentRequest") String parentRequest)
			{
		
        return delegate.createAvis(clientRef,articleRef, batchRef, amount, expectedDelivery, bestBeforeEnd, useNotBefore, expireBatch, parentRequest);
		
	}

    public void createStockUnitOnStorageLocation(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
            @WebParam(name = "clientRef") String clientRef, 
            @WebParam(name = "slName") String slName,        
            @WebParam(name = "articleRef") String articleRef,      
            @WebParam(name = "lotRef") String lotRef,          
            @WebParam(name = "amount") BigDecimal amount,            
            @WebParam(name = "unitLoadRef") String unitLoadRef) throws InventoryException, FacadeException, EntityNotFoundException 
    {    
        delegate.createStockUnitOnStorageLocation(clientRef, slName, articleRef, lotRef, amount, unitLoadRef);
    }
	


}
