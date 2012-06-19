package org.mywms.service;

//import java.util.List;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
//import org.mywms.model.Client;
import org.mywms.model.WorkVehicleHistory;
import org.mywms.model.VehicleData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Local
public interface WorkVehicleHistoryService
    extends BasicService<WorkVehicleHistory> {

    public WorkVehicleHistory create(VehicleData vehicleDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException;

}
