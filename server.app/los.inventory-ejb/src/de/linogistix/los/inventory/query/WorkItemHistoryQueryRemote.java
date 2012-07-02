package de.linogistix.los.inventory.query;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.WorkItemHistory;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface WorkItemHistoryQueryRemote extends BusinessObjectQueryRemote<WorkItemHistory>{ 
  
	//public LOSResultList<BODTO<WorkItem>> queryByDefault(String itemData, String workType, String worker, QueryDetail d) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
	//public LOSResultList<WorkItem> queryByLabelId(QueryDetail d, String vdId) throws BusinessObjectNotFoundException, BusinessObjectQueryException;

}
