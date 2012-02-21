/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import org.jboss.ejb3.metamodel.ApplicationException;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LOSStorage {
	
	public LOSUnitLoad createUnitLoad(Client client, 
									  String label, 
									  UnitLoadType type, 
									  LOSStorageLocation location, 
									  String activityCode) throws LOSLocationException;
	
	public void reserveStorageLocation(LOSStorageLocation sl, LOSUnitLoad ul)
					throws LOSLocationException;
	
	public void transferUnitLoad(String userName, 
								 LOSStorageLocation destination, 
								 LOSUnitLoad ul)
					throws LOSLocationException;
	
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, boolean ignoreLock) 
					throws LOSLocationException;
	
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock) 
					throws LOSLocationException; 
	
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock, String info, String activityCode) 
					throws LOSLocationException; 
	
	public void transferUnitLoadOnReservedLocation(String userName,
												   LOSStorageLocation destination, 
												   LOSUnitLoad ul)
					throws LOSLocationException;
	
	public void transferUnitLoadOnReservedLocation(String userName,
			   									   LOSStorageLocation destination, 
			   									   LOSUnitLoad ul,
			   									   boolean ignoreLock) 
					throws LOSLocationException;
	
    public LOSTypeCapacityConstraint checkUnitLoadSuitable(LOSStorageLocation sl, LOSUnitLoad ul) 
					throws LOSLocationException;
    
    /**
     * Checks whether a transfer of {@link LOSUnitLoad} to the given {@link LOSStorageLocation} will be successful.
     * 
     * Exceptions thrown are of type {@link ApplicationException} with <code>rollback=false</code> for being able to handle within transaction. 
     * 
     * @param sl the destination
     * @param ul the {@link LOSUnitLoad} to be transferred
     * @return the current {@link LOSTypeCapacityConstraint} of this {@link LOSStorageLocation} 
     * @throws LOSLocationAlreadyFullException doesn't rollback transaction
     * @throws LOSLocationNotSuitableException doesn't rollback transaction
     * @throws LOSLocationWrongClientException doesn't rollback transaction
     * @throws LOSLocationReservedException doesn't rollback transaction
     * @throws LOSLocationException
     */
    public LOSTypeCapacityConstraint checkUnitLoadSuitableNoRollback(
			LOSStorageLocation sl, LOSUnitLoad ul) throws LOSLocationAlreadyFullException, LOSLocationNotSuitableException, LOSLocationWrongClientException, LOSLocationReservedException, LOSLocationException ;
    	
    
    public void releaseStorageLocation(LOSStorageLocation sl, LOSUnitLoad ul)
					throws LOSLocationException;
    
    public void releaseStorageLocation(LOSStorageLocation sl) throws LOSLocationException;

	public void sendToNirwana(String username, LOSUnitLoad u) throws FacadeException;

	public void sendToClearing(String username, LOSUnitLoad existing) throws FacadeException;
     
	public boolean isUnitLoadAccessibleForStorageRemoval(LOSUnitLoad ul);
	
	public UnitLoadType getDefaultUnitLoadType() throws LOSLocationException;
}
