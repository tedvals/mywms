package de.linogistix.los.inventory.query;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.query.BusinessObjectQueryRemote;

@Remote
public interface LOSFuelOrderLogQueryRemote extends BusinessObjectQueryRemote<LOSFuelOrderLog>{ 
  
}
