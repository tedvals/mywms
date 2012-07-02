package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import javax.ejb.Remote;

import org.mywms.model.WorkItemHistory;

@Remote
public interface WorkItemHistoryCRUDRemote extends BusinessObjectCRUDRemote<WorkItemHistory>{

}
