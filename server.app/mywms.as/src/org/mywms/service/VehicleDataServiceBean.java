package org.mywms.service;

//import java.util.ArrayList;
//import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.ItemUnit;
//import org.mywms.model.StockUnit;
//import org.mywms.model.Zone;

@Stateless
public class VehicleDataServiceBean
    extends BasicServiceBean<VehicleData>
    implements VehicleDataService
{
	private static final Logger log  = Logger.getLogger(VehicleDataServiceBean.class);
    
    public VehicleData create()
    {
        VehicleData vehicleData = new VehicleData();
        manager.persist(vehicleData);

        log.info("CREATED VehicleData: " + vehicleData.toDescriptiveString());
        
		manager.flush();

        return vehicleData;
    }

	public VehicleData getByPlateNumber(String plateNumber) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                + VehicleData.class.getSimpleName()
                + " su "
                + " WHERE su.plateNumber= :adId");
        query = query.setParameter("adId", plateNumber);

        try{
        	VehicleData ret = (VehicleData) query.getSingleResult();
        	return ret;
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }

        
    }

}
