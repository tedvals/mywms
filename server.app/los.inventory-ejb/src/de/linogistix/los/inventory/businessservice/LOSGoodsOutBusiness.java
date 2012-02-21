/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.UnitLoad;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LOSGoodsOutBusiness {

	public abstract LOSGoodsOutRequest create(LOSOrderRequest order)
			throws FacadeException;
	
	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out) throws FacadeException;
	
	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out, boolean force) throws FacadeException;
	
	public LOSGoodsOutRequestPosition finishPosition(LOSUnitLoad ul) throws FacadeException, EntityNotFoundException;
	
	public LOSGoodsOutRequest getByUnitLoad(UnitLoad ul) throws EntityNotFoundException, FacadeException;
	
	public List<LOSGoodsOutRequestTO> getRaw();
	
	public LOSGoodsOutRequest accept(LOSGoodsOutRequest req) throws FacadeException;

	public LOSGoodsOutRequest cancel(LOSGoodsOutRequest req) throws FacadeException;
	
	public void remove(LOSGoodsOutRequest req) throws FacadeException;

}