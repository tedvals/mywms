/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.location.query;


import javax.ejb.Stateless;

import org.mywms.model.UnitLoad;

import de.linogistix.los.query.BusinessObjectQueryBean;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class UnitLoadQueryBean extends BusinessObjectQueryBean<UnitLoad> implements UnitLoadQueryRemote{	
  
  public String getUniqueNameProp() {
    return "labelId";
  }


}
