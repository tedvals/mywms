package org.mywms.service;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.WorkVehicle;
import org.mywms.model.VehicleData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Stateless
public class WorkVehicleServiceBean
    extends BasicServiceBean<WorkVehicle>
    implements WorkVehicleService {

    private static final Logger log  = Logger.getLogger(WorkVehicleServiceBean.class);

    public WorkVehicle create(VehicleData vehicleDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException {

        if ( vehicleDataId == null ) {
            throw new NullPointerException("vehicleDataId  == null");
        }

        if ( workTypeId == null ) {
            throw new NullPointerException("workTypeId  == null");
        }

        if ( workerId == null ) {
            throw new NullPointerException("workerId  == null");
        }

        WorkVehicle workVehicle = new WorkVehicle();

        workVehicle.setVehicleDataId(vehicleDataId);
        workVehicle.setWorkTypeId(workTypeId);
        workVehicle.setWorkerId(workerId);

        manager.persist(workVehicle);

        log.info("CREATED workVehicle: " + workVehicle.toDescriptiveString());

        manager.flush();

        return workVehicle;
    }

}
