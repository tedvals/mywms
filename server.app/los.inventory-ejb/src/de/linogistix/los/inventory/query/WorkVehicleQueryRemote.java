package de.linogistix.los.inventory.query;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.WorkVehicle;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface WorkVehicleQueryRemote extends BusinessObjectQueryRemote<WorkVehicle>{ 
  
	public LOSResultList<BODTO<WorkVehicle>> queryByDefault(String vehicleDataId, String workTypeId, String workerId, QueryDetail d) throws BusinessObjectNotFoundException, BusinessObjectQueryException;

}