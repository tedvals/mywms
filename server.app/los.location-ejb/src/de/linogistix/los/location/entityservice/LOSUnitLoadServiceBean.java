/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
/**
 *
 * @author Jordan
 */
@Stateless
public class LOSUnitLoadServiceBean 
        extends BasicServiceBean<LOSUnitLoad>
        implements LOSUnitLoadService
{

	private static final Logger log = Logger.getLogger(LOSUnitLoadServiceBean.class);
	
	@EJB
	ClientService clientService;

	@EJB
	LOSStorageLocationService slService;
	
	@EJB
	LOSStorageLocationTypeService slTypeService;
	
	@EJB
	UnitLoadTypeQueryRemote uLoadTypeQueryRemote;
	

    public LOSUnitLoad createLOSUnitLoad(Client client, 
                                         String labelId, 
                                         UnitLoadType type,
                                         LOSStorageLocation storageLocation) throws LOSLocationException 
    {
        if (client == null 
            || labelId == null
            || type == null
            || storageLocation == null) 
        {
            throw new NullPointerException(
                    "createLOSUnitLoad: parameter == null");
        }
        
        LOSUnitLoad ul = new LOSUnitLoad();
        ul.setClient(client);
        ul.setLabelId(labelId);
        ul.setType(type);
       
		checkUnitLoadSuitable(storageLocation, ul);
		
        ul.setStorageLocation(storageLocation);
        
        manager.persist(ul);
        
        log.info("CREATED LOSUnitLoad: " + ul.toShortString());
        
        return ul;
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListByStorageLocation(LOSStorageLocation sl) {
        
        Query query = manager.createQuery(
                            "SELECT ul FROM "+LOSUnitLoad.class.getSimpleName()+" ul "
                            +"WHERE ul.storageLocation=:sl ");
        query.setParameter("sl", sl);
                
        return (List<LOSUnitLoad>)query.getResultList();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListEmptyByStorageLocation(LOSStorageLocation sl) {
        
        Query query = manager.createQuery(
                            "SELECT ul FROM "+LOSUnitLoad.class.getSimpleName()+" ul "
                            +"WHERE ul.storageLocation=:sl "
        					+" AND ul.stockUnitList IS EMPTY ");
        query.setParameter("sl", sl);
                
        return (List<LOSUnitLoad>)query.getResultList();
    }

    //-----------------------------------------------------------------------
    /**
     * @see LOSUnitLoadService.getListByLabelStartsWith(Client, String)
     */
	@SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListByLabelStartsWith(Client client, String labelPart) 
	{
		String lowerPart = labelPart.toLowerCase();
        int partLength = lowerPart.length();

        StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ul FROM " + LOSUnitLoad.class.getSimpleName() + " ul ")
            .append("WHERE SUBSTRING(ul.labelId, 1, :length) = :part ");

        if (!client.isSystemClient()) {
            qstr.append("AND ul.client = :client ");
        }

        qstr.append("ORDER BY ul.labelId ASC");

        Query query = manager.createQuery(qstr.toString());
        
        query.setParameter("length", partLength);
        query.setParameter("part", lowerPart);

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }
        
        return (List<LOSUnitLoad>) query.getResultList();
	}

	public LOSUnitLoad getByLabelId(Client client, String labelId) throws EntityNotFoundException 
	{
		Query query	= manager.createQuery(
				"SELECT ul FROM "+LOSUnitLoad.class.getSimpleName()+" ul "
				+ "WHERE ul.labelId=:label ");
				// + "AND ul.client=:cl");
				
		query.setParameter("label",labelId);
		// query.setParameter("cl", client);
		
		try {
			LOSUnitLoad ul = (LOSUnitLoad)query.getSingleResult();
			return ul;
		}
		catch (NoResultException ex) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_UNITLOAD_WITH_LABEL);
		}
	}


	public LOSUnitLoad getNirwana() {
		LOSUnitLoad ul;
		LOSStorageLocation nirwana;
		nirwana = slService.getNirwana();
		
		try {
			ul = (LOSUnitLoad) getByLabelId(clientService.getSystemClient(), nirwana.getName());
		} catch (EntityNotFoundException e) {
			
			UnitLoadType t;
			t = uLoadTypeQueryRemote.getDefaultUnitLoadType();
			if (t == null){
				throw new RuntimeException("Nirwana does not exists. Neither does Default UnitLoadType");
			}
			
			ul = new LOSUnitLoad();
			ul.setClient(clientService.getSystemClient());
			ul.setLabelId(nirwana.getName());
			ul.setPackageType(LOSUnitLoadPackageType.MIXED);
			ul.setStorageLocation(nirwana);
			ul.setType(t);
			manager.persist(ul);
			manager.flush();
		}
		
		return ul;
	}

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
		// Check Lock status
		if (sl.getLock() != 0) {
			throw new LOSLocationException(
					LOSLocationExceptionKey.LOCATION_ISLOCKED,
					new String[] { sl.getName() });
		}
		// check if there is already a unitload on the location which determines
		// the type
		LOSTypeCapacityConstraint constr = sl
				.getCurrentTypeCapacityConstraint();

		// if the UnitLoadType is already determined by a UnitLoad stored on the
		// location
		if (constr != null) {

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


}
