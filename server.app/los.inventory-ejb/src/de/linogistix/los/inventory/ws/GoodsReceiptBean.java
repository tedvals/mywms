/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.mywms.facade.FacadeException;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.QueryAdviceService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
@SecurityDomain("los-login")
@Remote(ManageInventory.class)
@WebService(endpointInterface = "de.linogistix.los.inventory.ws.GoodsReceipt")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = false)
// @WebContext(contextRoot = "/webservice", transportGuarantee="NONE",
// secureWSDLAccess=false)
public class GoodsReceiptBean implements GoodsReceipt {

	private static final Logger log = Logger.getLogger(GoodsReceiptBean.class);

	@EJB
	LOSGoodsReceiptComponent goodsReceiptComponent;
	@EJB
	ClientService clientService;
	@EJB
	LOSLotService lotService;
	@EJB
	ItemDataService itemDataService;
	@EJB
	LOSStorageLocationService slService;
	@EJB
	QueryUnitLoadTypeService ulTypeService;
	@EJB
	ContextService context;
	@EJB
	QueryAdviceService advService;

	public void create(
			@WebParam(name = "client") String client, 
			@WebParam(name = "storageLocation") String storageLocation, 
			@WebParam(name = "licencePlate") String licencePlate, 
			@WebParam(name = "driver") 	String driver, 
			@WebParam(name = "deliveryNoteNumber") String deliveryNoteNumber,
			@WebParam(name = "forwarder") String forwarder,
			@WebParam(name = "positions") GoodsReceiptPositionTO[] positions) throws InventoryException, FacadeException {

		try {
			Client usersClient = context.getCallersUser().getClient();
			Client c = clientService.getByNumber(client);
			Client sys = clientService.getSystemClient();

			if ((!usersClient.isSystemClient()) && (!usersClient.equals(c))) {
				throw new InventoryException(
						InventoryExceptionKey.CLIENT_MISMATCH, usersClient
								.getNumber());
			}

			LOSGoodsReceipt goodsReceipt;
			goodsReceipt = goodsReceiptComponent.createGoodsReceipt(c,
					licencePlate, driver, forwarder, deliveryNoteNumber,
					new Date());

			LOSStorageLocation sl = slService.getByName(c, storageLocation);
			if (sl == null) {
				sl = slService.getByName(sys, storageLocation);
				if (sl == null){
					throw new BusinessObjectNotFoundException(storageLocation);
				}
			}

			for (GoodsReceiptPositionTO position : positions) {
				log.info("Retrieving Lot for " + c.getNumber() + "/"
						+ position.getLotName() + "/"
						+ position.getItemDataNumber());
				
				
				ItemData itemData = resolveItemData(c, position.getItemDataNumber());
				Lot lot = resolveLot(c, itemData, position.getLotName());
				UnitLoadType type = resolveUnitLoadType(c, position.getUnitLoadType());
				
				LOSUnitLoad ul = goodsReceiptComponent.getOrCreateUnitLoad(c,
						sl, type, position.getUnitLoadLabelId());
				
				LOSGoodsReceiptPosition pos = goodsReceiptComponent
						.createGoodsReceiptPosition(c, goodsReceipt,
								deliveryNoteNumber, position.getAmount());
				
				goodsReceiptComponent.receiveStock(pos, lot, itemData, position.getAmount(), ul, null);
				
				LOSAdvice adv = resolveAdvice(position.getAdvice());
				
				if (adv != null) {
					goodsReceiptComponent.assignAdvice(adv, pos);
				} else {
					if (itemData.isAdviceMandatory()) {
						throw new InventoryException(
								InventoryExceptionKey.ADVICE_MANDATORY,
								itemData.getNumber());
					}
				}
			}
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new InventoryException(
					InventoryExceptionKey.CREATE_GOODSRECEIPT, "");
		}
	}

	private Lot resolveLot(Client c, ItemData idat, String lotName) throws FacadeException{
		Lot lot = null;
		try {
			lot = lotService.getByNameAndItemData(c, lotName, idat.getNumber());
		} catch (EntityNotFoundException ex) {
			log.warn(ex.getMessage() + ": " + lotName);
			throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
		}
		
		return lot;
	}
	
	private ItemData resolveItemData(Client c, String number) throws InventoryException{
		
		ItemData itemData =  itemDataService.getByItemNumber(c, number);
		
		if(itemData == null){
			log.error("--- ITEM NUMBER NOT FOUND : "	+ number);
			throw new InventoryException(
					InventoryExceptionKey.ITEMDATA_NOT_FOUND, number);
		}
		
		return itemData;
	}
	
	private UnitLoadType resolveUnitLoadType(Client c, String name) throws BusinessObjectNotFoundException{
		UnitLoadType type = null;
		type = ulTypeService.getByName(name);
		if( type == null ) {
			log.error("Could not find Unitload type: " + name );
			type = ulTypeService.getDefaultUnitLoadType();
			if (type == null) {
				throw new BusinessObjectNotFoundException(name);
			}
		}
		
		return type;
	}
	
	private LOSAdvice resolveAdvice(String advice) throws EntityNotFoundException{
		try {
			return advService.getByAdviceNumber(advice);
		} catch (UnAuthorizedException e) {
			throw new EntityNotFoundException(ServiceExceptionKey.LOGIN_FAILED);
		}
	}
	
	
	public String[] getSuitableAdvice(String client, String itemData, String Lot) {
		return new String[0];
	}

}
