/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.entityservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.BasicEntity;
import org.mywms.model.User;

import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class BusinessObjectLockBean implements BusinessObjectLockService {

	private static final Logger log = Logger.getLogger(BusinessObjectLockBean.class);

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
	@EJB
	ContextService context;
	
	public void lock(BasicEntity entity, int lock, String lockCause) throws BusinessObjectSecurityException {
		
		User user = context.getCallersUser();
		
		
		if (!context.checkClient(entity)) {
			throw new BusinessObjectSecurityException(user);
		}
		entity = manager.find(entity.getClass(), entity.getId());
		entity.setLock(lock);
		
		log.info(user.getName() + " lock state changed: " + entity.toDescriptiveString());
		
	}


}
