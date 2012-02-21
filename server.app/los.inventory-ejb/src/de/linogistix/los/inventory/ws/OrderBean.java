/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.mywms.facade.BasicFacadeBean;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.facade.OrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;

/**
 * A Webservice for ordering items from stock
 * 
 * @see de.linogistix.los.inventory.connector.Order
 * 
 * @author trautm
 *
 */

@Stateless
@SecurityDomain("los-login")
@Remote(Order.class)
@WebService(endpointInterface = "de.linogistix.los.inventory.ws.Order")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice",  authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
public class OrderBean extends BasicFacadeBean implements Order {

    Logger log = Logger.getLogger(OrderBean.class);
   
    @EJB
    OrderFacade delegate;
    
    
    /* (non-Javadoc)
     * @see de.linogistix.los.inventory.connector.OrderRemote#order(java.lang.String, java.lang.String, java.lang.String[], byte[], byte[])
     */
    @WebMethod
    public boolean order (
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
            @WebParam(name = "clientRef") String clientRef,     
            @WebParam(name = "orderRef") String orderRef,       
            @WebParam(name = "positions") OrderPositionTO[] positions,  
            @WebParam(name = "documentUrl") String documentUrl,
            @WebParam(name = "labelUrl") String labelUrl,  
            @WebParam(name = "destination") String destination) 
    throws FacadeException {

        return delegate.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination);
    }
}
