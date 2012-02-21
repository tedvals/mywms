/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.info;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

/**
 * @author krane
 *
 */
@Remote
public interface InfoFacade {
	
	public Client getDefaultClient();
	public InfoItemDataTO readItemData( String label );
	public InfoLocationTO readLocation( String name );
	public InfoUnitLoadTO readUnitLoad( String name );
	public List<InfoUnitLoadTO> readUnitLoadList( String locationName );

}
