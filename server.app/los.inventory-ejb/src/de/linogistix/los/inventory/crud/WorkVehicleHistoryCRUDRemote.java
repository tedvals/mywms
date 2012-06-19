package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import javax.ejb.Remote;

import org.mywms.model.WorkVehicleHistory;

@Remote
public interface WorkVehicleHistoryCRUDRemote extends BusinessObjectCRUDRemote<WorkVehicleHistory>{

}
