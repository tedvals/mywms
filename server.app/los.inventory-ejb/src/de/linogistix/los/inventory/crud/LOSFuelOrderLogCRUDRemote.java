package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.model.LOSFuelOrderLog;


@Remote
public interface LOSFuelOrderLogCRUDRemote extends BusinessObjectCRUDRemote<LOSFuelOrderLog>{

}
