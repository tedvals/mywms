/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import de.linogistix.los.location.model.LOSAreaType;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Area;
import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationService 
        extends BasicService<LOSStorageLocation>
{

    public LOSStorageLocation createStorageLocation(
                                    Client client,
                                    String name, 
                                    LOSStorageLocationType type);
    
    /**
     * Searches for a LOSStorageLocation which is assigned to Client client 
     * and has the specified name. 
     * 
     * @param client the Client the specified LOSStorageLocation is assigned to.
     * @param name specifies the LOSStorageLocation
     * @return a LOSStorageLocation that matches client and name or null if there is no such one.
     */
    public LOSStorageLocation getByName(Client client, String name);
    
    /**
     * Searches for a LOSStorageLocation which has the specified name. 
     * 
     * @param name specifies the LOSStorageLocation
     * @return a LOSStorageLocation, null if nothing was found
     */
    public LOSStorageLocation getByName(String name);

    public List<LOSStorageLocation> getListByArea(Client client, Area area);
    
    public List<LOSStorageLocation> getListByAreaType(Client client, LOSAreaType areatype);

	public LOSStorageLocation getNirwana();

	public LOSStorageLocation getClearing();
    
}
