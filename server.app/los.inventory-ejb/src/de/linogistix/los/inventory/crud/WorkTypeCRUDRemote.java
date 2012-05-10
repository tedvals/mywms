package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import javax.ejb.Remote;

import org.mywms.model.WorkType;

@Remote
public interface WorkTypeCRUDRemote extends BusinessObjectCRUDRemote<WorkType>{

}
