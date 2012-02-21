/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.pick.query;


import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.model.LOSPickRequestPosition;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSPickRequestPositionQueryRemote extends BusinessObjectQueryRemote<LOSPickRequestPosition>{ 
  
	List<LOSPickRequestPosition> queryByPickRequest(LOSPickRequest r);
	
	List<BODTO<LOSPickRequestPosition>> queryByNotSolved(QueryDetail qd) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
}
