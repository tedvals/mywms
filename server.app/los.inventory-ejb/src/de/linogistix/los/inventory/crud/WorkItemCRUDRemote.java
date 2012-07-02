package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import javax.ejb.Remote;

import org.mywms.model.WorkItem;

@Remote
public interface WorkItemCRUDRemote extends BusinessObjectCRUDRemote<WorkItem>{

}
