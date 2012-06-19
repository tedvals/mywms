package org.mywms.service;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.WorkVehicleHistory;
import org.mywms.model.VehicleData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Stateless
public class WorkVehicleHistoryServiceBean
    extends BasicServiceBean<WorkVehicleHistory>
    implements WorkVehicleHistoryService {

    private static final Logger log  = Logger.getLogger(WorkVehicleHistoryServiceBean.class);

    public WorkVehicleHistory create(VehicleData vehicleDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException {

        if ( vehicleDataId == null ) {
            throw new NullPointerException("vehicleDataId  == null");
        }

        if ( workTypeId == null ) {
            throw new NullPointerException("workTypeId  == null");
        }

        if ( workerId == null ) {
            throw new NullPointerException("workerId  == null");
        }

        WorkVehicleHistory workVehicle = new WorkVehicleHistory();

        workVehicle.setVehicleDataId(vehicleDataId);
        workVehicle.setWorkTypeId(workTypeId);
        workVehicle.setWorkerId(workerId);

        manager.persist(workVehicle);

        log.info("CREATED workVehicleHistory: " + workVehicle.toDescriptiveString());

        manager.flush();

        return workVehicle;
    }

}
