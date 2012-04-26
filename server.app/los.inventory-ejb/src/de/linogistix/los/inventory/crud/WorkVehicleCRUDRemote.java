package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import javax.ejb.Remote;

import org.mywms.model.WorkVehicle;

@Remote
public interface WorkVehicleCRUDRemote extends BusinessObjectCRUDRemote<WorkVehicle>{

}
