/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

/**
 * This interface declares the service for the entity LOSRack and LOSRackLocation.
 * 
 * @author Markus Jordan
 * @version $Revision: 320 $ provided by $Author: jordan $
 */
@Local
public interface LOSRackService
	extends BasicService<LOSRack>
{
	// ----------------------------------------------------------------
	// set of individual methods
	// ----------------------------------------------------------------
    
    public LOSRack createRack(Client client, String name)
            throws UniqueConstraintViolatedException;
    
    public LOSRack createRackWithLocations(Client client, String name, int rows, int columns, LOSStorageLocationType type)
            throws UniqueConstraintViolatedException;
    
    public LOSRackLocation createRackLocation(Client client, 
                                              String name,
                                              LOSStorageLocationType type,
                                              int xPos,
                                              int yPos,
                                              LOSRack rack);
    
    public LOSRack getByName(Client client, String name)
            throws EntityNotFoundException;
    
    public LOSRackLocation getByPosition(LOSRack r, int x, int y, int z);
    
    public List<LOSRackLocation> getFreePlacesForType(LOSRack r, UnitLoadType ulType);
}