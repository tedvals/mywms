/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Area;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;

import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.businessservice.ContextService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationServiceBean 
    //###TODO: Role Concept, Client check
        
        extends BasicServiceBean<LOSStorageLocation>
        implements LOSStorageLocationService{
    
	private static final Logger log = Logger.getLogger(LOSStorageLocationServiceBean.class);
	@EJB
	ClientService clService;
	@EJB
	LOSStorageLocationTypeService slTypeService;
	@EJB
	ContextService ctxService;
	
    public LOSStorageLocation createStorageLocation(
                                Client client,
                                String name, 
                                LOSStorageLocationType type)
    {
        
        if ((client == null || name == null) || type == null) {
            log.info("Cannot create location. Parameter == null");
            return null;
        }
        
        client = manager.merge(client);
        type = manager.merge(type);

        LOSStorageLocation sl = new LOSStorageLocation();
        sl.setClient(client);
        sl.setName(name);
        sl.setType(type);
        
        manager.persist(sl);
        log.info("CREATED LOSStorageLocation: " + sl.toShortString());
//        manager.flush();
        
        return sl;
        
    }

    //-----------------------------------------------------------------------
    /**
     * ###TODO: throw EntityNotFoundException to resemble mywms concepts
     * 
     * @param client
     * @param name
     * @return
     */
    public LOSStorageLocation getByName(Client client, String name) {
        
        try{
            Query query = manager.createQuery("SELECT sl FROM "
                            + LOSStorageLocation.class.getSimpleName()+" sl "
                            + "WHERE sl.name=:name "
                            + "AND sl.client=:cl");

            query.setParameter("name", name);
            query.setParameter("cl", client);

            LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
            return sl;
        }catch(NoResultException nre){
            return null;
        }
    }

    public LOSStorageLocation getByName(String name) {
        
        try{
            Query query = manager.createQuery("SELECT sl FROM "
                            + LOSStorageLocation.class.getSimpleName()+" sl "
                            + "WHERE sl.name=:name ");

            query.setParameter("name", name);

            LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
            return null;
        }
    }
    
  //-----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getListByArea(Client client, Area area) {
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ LOSStorageLocation.class.getSimpleName()+ " sl "
                	+ "WHERE sl.area=:area ");
		
        if (!client.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("area", area);
        
        if (!client.isSystemClient()) {
        	query.setParameter("cl", client);
        }

        return (List<LOSStorageLocation>) query.getResultList();
	}

	/* see here */
    @SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getListByAreaType(Client client, LOSAreaType areaType) {
        StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ LOSStorageLocation.class.getSimpleName()+ " sl "
                    + ", " + LOSArea.class.getSimpleName() + " a "
                	+ "WHERE a.areaType = :areaType AND sl.area.id = a.id ");
		
        if (!client.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("areaType", areaType);
        
        if (!client.isSystemClient()) {
        	query.setParameter("cl", client);
        }

        return (List<LOSStorageLocation>) query.getResultList();
    }

	public LOSStorageLocation getNirwana() {
		LOSStorageLocation nirwana;
		nirwana = manager.find(LOSStorageLocation.class, 0L);
		if (nirwana != null){
			return nirwana;
		}
		
		createSystemStorageLocation(0, "SYSTEM_DATA_NIRWANA_LOCATION");

		nirwana = manager.find(LOSStorageLocation.class, 0L);
		if (nirwana != null){
			return nirwana;
		} else{
			throw new RuntimeException("Nirwana not found");
		}
		
	}

	public LOSStorageLocation getClearing() {
		LOSStorageLocation clearing;		
			 
		clearing = manager.find(LOSStorageLocation.class, 1L);
		if (clearing != null){
			return clearing;
		} 
		
		createSystemStorageLocation(1, "SYSTEM_DATA_CLEARING_LOCATION");
		
		clearing = manager.find(LOSStorageLocation.class, 1L);
		if (clearing != null){
			return clearing;
		} 
		else{
			throw new RuntimeException("Clearing not found");
		}
	}
	
	private void createSystemStorageLocation(long id, String nameKey) {
		log.warn("Try to create system Location");
		
		User user = ctxService.getCallersUser();
		Locale locale = null;
		if( user != null ) {
			try {
				locale = new Locale(user.getLocale());
			}
			catch( Throwable x ) {
				// egal
			}
		}
		if( locale == null ) {
			locale = Locale.getDefault();
		}
		
		
		String name = BundleHelper.resolve(BundleResolver.class, nameKey, locale );
		
		LOSStorageLocation sl;
		sl = getByName(name);
		if(sl == null) {
			sl = createStorageLocation(clService.getSystemClient(), name, slTypeService.getNoRestrictionType());
			if( sl == null ) {
				log.error("Cannot create system Location");
				return;
			}
			String comment = BundleHelper.resolve(BundleResolver.class, "SYSTEM_DATA_COMMENT", locale );
			sl.setAdditionalContent(comment);
		}
		manager.flush();
		
		String queryStr = "UPDATE " + LOSStorageLocation.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", sl.getId());
		query.executeUpdate();
		manager.flush();
		sl = null;
	}
}
