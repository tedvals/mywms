/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSRackLocation;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.model.LOSUnitLoadRecordType;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSStorageBean implements LOSStorage {

	private static final Logger log = Logger.getLogger(LOSStorageBean.class);
	
	@EJB
	private QueryUnitLoadTypeService ultService;
	
	@EJB
	private LOSStorageLocationTypeService slTypeService;

	@EJB
	private LOSStorageLocationService slService;

	@EJB
	LOSFixedLocationAssignmentComp fixLocationAssignmentComp;
	
	@EJB
	private ContextService ctxService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public LOSUnitLoad createUnitLoad(Client client, 
			  						  String label, 
			  						  UnitLoadType type, 
			  						  LOSStorageLocation location, 
			  						  String activityCode) throws LOSLocationException
	{
		LOSUnitLoad ul = new LOSUnitLoad();
		ul.setClient(client);
		ul.setLabelId(label);
		ul.setType(type);
		ul.setStorageLocation(location);
		ul.setStockTakingDate(Calendar.getInstance().getTime());
		
		manager.persist(ul);
		
		LOSUnitLoadRecord rec = new LOSUnitLoadRecord();
		rec.setClient(ul.getClient());
		rec.setActivityCode(activityCode);
		
		if(ctxService.getCallersUser() != null){
			rec.setOperator(ctxService.getCallersUser().getName());
		}
		
		rec.setRecordType(LOSUnitLoadRecordType.CREATED);
		rec.setLabel(ul.getLabelId());
		rec.setFromLocation("---");
		rec.setToLocation(location.getName());
		
		manager.persist(rec);
		
		log.info("[CREATED][UNITLOAD]-CL "+client.getNumber()+"-LA "+label+"-LOC "+location.getName());
		
		return ul;
	}
	
	// --------------------------------------------------------------------------------------
	public void reserveStorageLocation(LOSStorageLocation sl, LOSUnitLoad ul)
			throws LOSLocationException {

		if (sl.getLock() != 0) {
			throw new LOSLocationException(
					LOSLocationExceptionKey.STORAGELOCATION_LOCKED,
					new Object[] { sl.getName(), sl.getLock() + "" });
		}

		// check if unitLoad is suitable and get the constraint if there is one
		LOSTypeCapacityConstraint tcc = checkUnitLoadSuitable(sl, ul);

		// if the UnitLoadType is suitable but restricted, check if reservation
		// possible
		if (tcc != null) {

			int occupied = sl.getUnitLoads().size() + sl.getReservedCapacity();

			if (tcc.getCapacity() < (occupied + 1)) {
				throw new LOSLocationException(
						LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL,
						new Object[] { sl.getName() });
			}
		}

		// if all checks succeed
		sl.setReservedCapacity(sl.getReservedCapacity() + 1);

	}

	/**
	 * ------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#releaseStorageLocation(de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void releaseStorageLocation(LOSStorageLocation sl)
			throws LOSLocationException {
		sl.setReservedCapacity(0);
		log.info("RELEASED LOSStorageLocation: " + sl.toDescriptiveString());
	}

	/**
	 * ------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#releaseStorageLocation(de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public void releaseStorageLocation(LOSStorageLocation sl, LOSUnitLoad ul)
			throws LOSLocationException {

		// check if unitLoad is suitable and get the constraint if there is one
		// WHY ????
		// checkUnitLoadSuitable(sl, ul);

		// if all checks succeed
		if (sl.getReservedCapacity() > 0){
			sl.setReservedCapacity(sl.getReservedCapacity() - 1);
		}
		
		log.info("RELEASED LOSStorageLocation: " + sl.toDescriptiveString());

	}

	/**
	 * -------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#transferUnitLoad(java.lang.String, de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul) 
		throws LOSLocationException 
	{
		transferUnitLoad(userName, dest, ul, false);
	}


	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, boolean ignoreLock) 
			throws LOSLocationException 
		{
			transferUnitLoad(userName, dest, ul, -1, ignoreLock);
		}
	
	/**
	 * -------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#transferUnitLoad(java.lang.String, de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad, boolean)
	 */
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock) 
		throws LOSLocationException 
	{
		transferUnitLoad(userName, dest, ul, index, ignoreLock, "", "");
	}
	/**
	 * -------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#transferUnitLoad(java.lang.String, de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad, boolean)
	 */
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock, String info, String activityCode) 
		throws LOSLocationException 
	{
		// if we should check lock state before transfer
		if(!ignoreLock){
		
			// if lock state of destination is an other than 0 or 301
			if (dest.getLock() != LOSStorageLocationLockState.NOT_LOCKED.getLock() 
				&& dest.getLock() != LOSStorageLocationLockState.RETRIEVAL.getLock()) 
			{
				throw new LOSLocationException(
						LOSLocationExceptionKey.STORAGELOCATION_LOCKED,
						new Object[] { dest.getName(), dest.getLock() + "" });
			}
		}
		
		// check if unitLoad is suitable and get the constraint if there is one
		LOSTypeCapacityConstraint tcc = checkUnitLoadSuitable(dest, ul);

		// if the UnitLoadType is suitable but restricted, check if storing an
		// additional UnitLoad possible
		if (tcc != null) {

			int occupied = dest.getUnitLoads().size()
					+ dest.getReservedCapacity();

			if (tcc.getCapacity() < (occupied + 1)) {
				throw new LOSLocationException(
						LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL,
						new Object[] { dest.getName() });
			}
			
			if (index > -1){
				for (LOSUnitLoad listUl : dest.getUnitLoads()){
					if (listUl.getIndex() == index){
						throw new LOSLocationException(
								LOSLocationExceptionKey.STORAGELOCATION_INDEX_FULL,
								new Object[] { "" + index });
					}
				}
			}
		}

		// if the destination is permanent assigned to a special item data
		// check if stocks on the unit load only contain that item
		LOSFixedLocationAssignment fixAss;
		if ((fixAss = fixLocationAssignmentComp.getAssignment(dest)) != null){
			for (StockUnit su : ul.getStockUnitList()){
				if ( ! su.getItemData().equals(fixAss.getItemData())){
					throw new LOSLocationException(
							LOSLocationExceptionKey.WRONG_ITEMDATA_FIXASSIGNMENT, 
							new String[]{fixAss.getItemData().getNumber()});
				}
			}
		}

		// if all checks succeed

		// clear the source location
		LOSStorageLocation source = manager.find(LOSStorageLocation.class, ul.getStorageLocation().getId());
		source.getUnitLoads().remove(ul);
		if (source.getUnitLoads().size() == 0) {
			source.setCurrentTypeCapacityConstraint(null);
		}

		// move UnitLoad to destination
		ul.setStorageLocation(dest);
		if (index > -1) ul.setIndex(index);
		dest.getUnitLoads().add(ul);
		dest.setCurrentTypeCapacityConstraint(tcc);
		log.info("TRANSFERRED UnitLoad: " + ul.toDescriptiveString()
				+ " *** to Location: " + dest.getName());
		
		LOSUnitLoadRecord rec = new LOSUnitLoadRecord();
		rec.setClient(ul.getClient());
		rec.setActivityCode(activityCode);
		rec.setOperator(userName);
		rec.setRecordType(LOSUnitLoadRecordType.TRANSFERED);
		rec.setLabel(ul.getLabelId());
		rec.setFromLocation(source.getName());
		rec.setToLocation(dest.getName());
		rec.setAdditionalContent(info);
		
		manager.persist(rec);
	}

	/**
	 * --------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#transferUnitLoadOnReservedLocation(java.lang.String, de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public void transferUnitLoadOnReservedLocation(String userName,
			   									   LOSStorageLocation destination, 
			   									   LOSUnitLoad ul)
		throws LOSLocationException 
	{
		transferUnitLoadOnReservedLocation(userName, destination, ul, false);
	}
	
	public void transferUnitLoadOnReservedLocation(String userName,
			   LOSStorageLocation destination, 
			   LOSUnitLoad ul,
			   boolean ignoreLock)
				throws LOSLocationException {
		transferUnitLoadOnReservedLocation(userName, destination, ul, -1, ignoreLock);
	}
	/**
	 * --------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#transferUnitLoadOnReservedLocation(java.lang.String, de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad, boolean)
	 */
	public void transferUnitLoadOnReservedLocation(String userName,
												   LOSStorageLocation destination, 
												   LOSUnitLoad ul,
												   int index,
												   boolean ignoreLock)
		throws LOSLocationException 
	{
		
		if (destination.getReservedCapacity() > 0){
			destination.setReservedCapacity(destination.getReservedCapacity() - 1);
		}
		transferUnitLoad(userName, destination, ul, index, ignoreLock);
	}

	/**
	 * --------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#checkUnitLoadSuitable(de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public LOSTypeCapacityConstraint checkUnitLoadSuitable(
			LOSStorageLocation sl, LOSUnitLoad ul) throws LOSLocationException {
		// Check Client
		if ((!sl.getClient().isSystemClient())
				&& (!ul.getClient().isSystemClient())
				&& (!sl.getClient().equals(ul.getClient()))) {
			throw new LOSLocationException(
					LOSLocationExceptionKey.WRONG_CLIENT, new String[] {
							sl.getClient().getNumber(),
							ul.getClient().getNumber() });
		}
		
		// check if there is already a unitload on the location which determines
		// the type
		LOSTypeCapacityConstraint constr = sl
				.getCurrentTypeCapacityConstraint();

		// if the UnitLoadType is already determined by a UnitLoad stored on the
		// location
		// Do only check constraint, if there are really unit loads on the location
		// After manual manipulation sometimes this is not consistent
		if (constr != null && (sl.getUnitLoads().size()>0) ) {

			if (!constr.getUnitLoadType().equals(ul.getType())) {
				throw new LOSLocationException(
						LOSLocationExceptionKey.STORAGELOCATION_ALREADY_RESERVED_FOR_DIFFERENT_TYPE,
						new Object[] { sl.getName(),
								constr.getUnitLoadType().getName() });
			} else {

				return constr;

			}
		} else {

			List<LOSTypeCapacityConstraint> tccList = slTypeService
					.getByLocationType(sl.getType());

			// if there are some constraints, not every kind of UnitLoad could
			// be stored on that location
			if (tccList.size() > 0) {

				// check if the type of the unitLoad for which the location
				// should be reserved is in the list
				boolean suitable = false;
				for (LOSTypeCapacityConstraint tcc : tccList) {

					if (tcc.getUnitLoadType().equals(ul.getType())) {
						suitable = true;
						constr = tcc;
						break;
					}
				}
				// if the type of the UnitLoad is not in the list, the UnitLoad
				// is not suitable
				if (!suitable) {
					throw new LOSLocationException(
							LOSLocationExceptionKey.UNITLOAD_NOT_SUITABLE,
							new Object[] { ul.getLabelId(),
									sl.getType().getName() });
				}
			}
			return constr;
		}
	}
	
	/**
	 * --------------------------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#checkUnitLoadSuitable(de.linogistix.los.location.model.LOSStorageLocation, de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public LOSTypeCapacityConstraint checkUnitLoadSuitableNoRollback(
			LOSStorageLocation sl, LOSUnitLoad ul) throws LOSLocationException {
		// Check Client
		if ((!sl.getClient().isSystemClient())
				&& (!ul.getClient().isSystemClient())
				&& (!sl.getClient().equals(ul.getClient()))) {
			throw new LOSLocationWrongClientException(
					sl.getClient().getNumber(),
					ul.getClient().getNumber());
		}
		
		// check if there is already a unitload on the location which determines
		// the type
		LOSTypeCapacityConstraint constr = sl
				.getCurrentTypeCapacityConstraint();

		// if the UnitLoadType is already determined by a UnitLoad stored on the
		// location
		// Do only check constraint, if there are really unit loads on the location
		// After manual manipulation sometimes this is not consistent
		if (constr != null && (sl.getUnitLoads().size()>0) ) {

			if (!constr.getUnitLoadType().equals(ul.getType())) {
				throw new LOSLocationReservedException(sl.getName(),
								constr.getUnitLoadType().getName() );
			} else {
				if (! (constr.getUnitLoadType().equals(ul.getType()))) {
					throw new LOSLocationNotSuitableException(ul.getLabelId(),
							sl.getType().getName() );
				}
				
				int occupied = sl.getUnitLoads().size() + sl.getReservedCapacity();
				if (constr.getCapacity() < (occupied + 1)) {
					throw new LOSLocationAlreadyFullException(
							sl.getName());
				}
				
				return constr;

			}
		} else {

			List<LOSTypeCapacityConstraint> tccList = slTypeService
					.getByLocationType(sl.getType());

			// if there are some constraints, not every kind of UnitLoad could
			// be stored on that location
			if (tccList.size() > 0) {

				// check if the type of the unitLoad for which the location
				// should be reserved is in the list
				boolean suitable = false;
				for (LOSTypeCapacityConstraint tcc : tccList) {

					if (tcc.getUnitLoadType().equals(ul.getType())) {
						suitable = true;
						constr = tcc;
						break;
					}
				}
				// if the type of the UnitLoad is not in the list, the UnitLoad
				// is not suitable
				if (!suitable) {
					throw new LOSLocationNotSuitableException(ul.getLabelId(),
									sl.getType().getName() );
				} else{
					int occupied = sl.getUnitLoads().size()
					+ sl.getReservedCapacity();

					if (constr.getCapacity() < (occupied + 1)) {
						throw new LOSLocationAlreadyFullException(
								sl.getName());
					}
				}
			}
			return constr;
		}
	}

	public void sendToNirwana(String username, LOSUnitLoad u)
			throws FacadeException {

		LOSStorageLocation sl = slService.getNirwana();
		transferUnitLoad(username, sl, u);
		u.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
		u.setLabelId(u.getLabelId() + "-X-" + u.getId());
	}

	public void sendToClearing(String username, LOSUnitLoad existing)
			throws FacadeException {
		LOSStorageLocation sl = slService.getClearing();
		transferUnitLoad(username, sl, existing);
		existing.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());

	}

	/**
	 * -------------------------------------------------------------------------
	 * @see de.linogistix.los.location.businessservice.LOSStorage#isUnitLoadAccessibleForStorageRemoval(de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public boolean isUnitLoadAccessibleForStorageRemoval(LOSUnitLoad ul) {
				
		if(ul.getLock() != LOSUnitLoadLockState.NOT_LOCKED.getLock()
		   && ul.getLock() != LOSUnitLoadLockState.STORAGE.getLock())
		{
			return false;
		}
		
		LOSStorageLocation source = ul.getStorageLocation();
		
		if(source.getLock() != LOSStorageLocationLockState.NOT_LOCKED.getLock()
		   && source.getLock() != LOSStorageLocationLockState.STORAGE.getLock())
		{
			return false;
		}
		
		if(source instanceof LOSRackLocation){
			
			LOSRack rack = ((LOSRackLocation)source).getRack();
			
			if(rack.getLock() != LOSStorageLocationLockState.NOT_LOCKED.getLock()
			   && rack.getLock() != LOSStorageLocationLockState.STORAGE.getLock())
			{
				return false;
			}
		}
		
		return true;
	}

	public UnitLoadType getDefaultUnitLoadType() throws LOSLocationException {
		
		UnitLoadType defUlt = ultService.getDefaultUnitLoadType();
		
		if(defUlt == null){
			throw new LOSLocationException(
					LOSLocationExceptionKey.DEFAULT_UNITLOADTYPE_MISSING, new Object[]{});
		}
		
		return defUlt;
	}
}
