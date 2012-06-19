package de.linogistix.los.inventory.query;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.WorkVehicleHistory;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface WorkVehicleHistoryQueryRemote extends BusinessObjectQueryRemote<WorkVehicleHistory>{ 
  
	//public LOSResultList<BODTO<WorkVehicle>> queryByDefault(String vehicleData, String workType, String worker, QueryDetail d) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
	//public LOSResultList<WorkVehicle> queryByLabelId(QueryDetail d, String vdId) throws BusinessObjectNotFoundException, BusinessObjectQueryException;

}
