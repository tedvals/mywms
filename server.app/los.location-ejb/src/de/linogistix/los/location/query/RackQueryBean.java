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

import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.query.dto.LOSRackTO;
import de.linogistix.los.query.BusinessObjectQueryBean;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class RackQueryBean 
        extends BusinessObjectQueryBean<LOSRack> 
        implements RackQueryRemote
{
	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"name",
		"lock"
	};
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<LOSRackTO> getBODTOClass() {
		return LOSRackTO.class;
	}
	
    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
}
