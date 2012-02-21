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

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSTypeCapacityConstraintService;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.mywms.service.BasicService;

/**
 * @author trautm
 *
 */
@Stateless
public class LOSTypeCapacityConstraintCRUDBean extends BusinessObjectCRUDBean<LOSTypeCapacityConstraint> implements LOSTypeCapacityConstraintCRUDRemote {

	@EJB 
	LOSTypeCapacityConstraintService service;
	
	@Override
	protected BasicService<LOSTypeCapacityConstraint> getBasicService() {		
		return service;
	}
}
