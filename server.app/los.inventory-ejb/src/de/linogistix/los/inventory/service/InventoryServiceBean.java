/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.ItemData;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSInventory;
import org.mywms.model.Lot;

@Stateless
public class InventoryServiceBean extends BasicServiceBean<LOSInventory>
implements InventoryService{

	Logger log = Logger.getLogger(InventoryServiceBean.class);
	
	public LOSInventory create(ItemData itemData) {
		
		LOSInventory i = new LOSInventory();
		i.setClient(itemData.getClient());
		i.setItemDataRef(itemData.getNumber());
		i.setLotRef(itemData.getNumber());
		manager.persist(i);
		manager.flush();
		
		return i;
		
	}
	
	public LOSInventory create(Lot batch) {
		
		LOSInventory i = new LOSInventory();
		i.setClient(batch.getClient());
		i.setItemDataRef(batch.getItemData().getNumber());
		i.setLotRef(batch.getName());
		manager.persist(i);
		manager.flush();
		
		return i;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSInventory> getByItemData(ItemData data) throws EntityNotFoundException{
		Query query =
            manager.createQuery("SELECT o FROM "
                + LOSInventory.class.getSimpleName()
                + " o "
                + "WHERE o.itemDataRef=:idata ");

        query.setParameter("idata", data.getNumber());

        try {
            List<LOSInventory> i = query.getResultList();
            return i;
        }
        catch (NoResultException ex) {
        	log.error(ex.getMessage(), ex);
            throw new EntityNotFoundException(		
            //###TODO: resourcekey
                ServiceExceptionKey.NO_AREA_WITH_NAME);
        }
	}

	public LOSInventory getByBatch(Lot batch)
			throws EntityNotFoundException {
		Query query =
            manager.createQuery("SELECT o FROM "
                + LOSInventory.class.getSimpleName()
                + " o "
                + "WHERE o.batchRef=:batch ");

        query.setParameter("batch", batch.getName());

        try {
            LOSInventory i = (LOSInventory)query.getSingleResult();
            return i;
        }
        catch (NoResultException ex) {
        	log.error(ex.getMessage(), ex);
            throw new EntityNotFoundException(		
            //###TODO: resourcekey
                ServiceExceptionKey.NO_AREA_WITH_NAME);
        }
	}
	
	public LOSInventory getByBatchName(String batchName)
			throws EntityNotFoundException {
		Query query =
            manager.createQuery("SELECT o FROM "
                + LOSInventory.class.getSimpleName()
                + " o "
                + "WHERE o.batchRef=:batch ");

        query.setParameter("batch", batchName);

        try {
            LOSInventory i = (LOSInventory)query.getSingleResult();
            return i;
        }
        catch (NoResultException ex) {
        	log.error(ex.getMessage(), ex);
            throw new EntityNotFoundException(		
            //###TODO: resourcekey
                ServiceExceptionKey.NO_AREA_WITH_NAME);
        }
	}

}
