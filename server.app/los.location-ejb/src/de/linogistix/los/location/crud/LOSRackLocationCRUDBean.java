/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.logging.Logger;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.location.entityservice.LOSRackLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * @author trautm
 *
 */
@Stateless
public class LOSRackLocationCRUDBean
        extends BusinessObjectCRUDBean<LOSRackLocation>
        implements LOSRackLocationCRUDRemote {

	private static final Logger log = Logger.getLogger(LOSRackLocationCRUDBean.class);
	
    @EJB
    LOSRackLocationService rlService;
    
    @EJB
    LOSStorageLocationTypeService slTypeService;
    
    @EJB
    QueryUnitLoadTypeService ulTypesService;
    
    @EJB
    LOSUnitLoadService ulService;
    
    @Override
    protected BasicService<LOSRackLocation> getBasicService() {
            
      return rlService;
    }
    
    /**
     * Creates an {@link LOSRackLocation}.
     * 
     * Creates attached Unit Load automatically if {@link LOSStorageLocationType} is an attachedUnitLoadType.
     */
    @Override
    public LOSRackLocation create(LOSRackLocation entity)
    		throws BusinessObjectExistsException,
    		BusinessObjectCreationException, BusinessObjectSecurityException {
    	
    	LOSRackLocation rl = super.create(entity);
    	LOSStorageLocationType attachedUlType = slTypeService.getAttachedUnitLoadType();
    	UnitLoadType pickUlType = ulTypesService.getPickLocationUnitLoadType();
    	
    	if (rl.getType().equals(attachedUlType)){
    		try {
				ulService.createLOSUnitLoad(rl.getClient(), rl.getName(), pickUlType, rl);
			} catch (LOSLocationException e) {
				log.error(e.getMessage(), e);
				BusinessObjectCreationException ex = new BusinessObjectCreationException(entity);
				throw ex;
			}
    	}
    	
    	return rl;
    	
    }
}
