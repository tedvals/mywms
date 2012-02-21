/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSUnitLoadService extends BasicService<LOSUnitLoad>{

    public LOSUnitLoad createLOSUnitLoad(Client client,
                                         String labelId,
                                         UnitLoadType type,
                                         LOSStorageLocation storageLocation) throws LOSLocationException;
    
    public List<LOSUnitLoad> getListByStorageLocation(LOSStorageLocation sl);
    
    public List<LOSUnitLoad> getListEmptyByStorageLocation(LOSStorageLocation sl);
    
    /**
	 * Searches for UnitLoads whose label starts with the given String 
	 * and which are belonging to the given Client. If the given Client is the 
	 * system client, the search is regardless of the client.
	 * 
	 * @param client the client the UnitLoads belong to or the system client.
	 * @param labelPart the characters the label should start with.
	 * @return true if there are one or more matching UnitLoads, false otherwise.
	 */
    public List<LOSUnitLoad> getListByLabelStartsWith(Client client, String labelPart);

    
    public LOSUnitLoad getByLabelId(Client client, String labelId) throws EntityNotFoundException;


    public LOSUnitLoad getNirwana();

}
