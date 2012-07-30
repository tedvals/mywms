package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.inventory.service.LOSFuelOrderLogService;

@Stateless
public class LOSFuelOrderLogCRUDBean extends BusinessObjectCRUDBean<LOSFuelOrderLog> implements LOSFuelOrderLogCRUDRemote {

	@EJB 
	LOSFuelOrderLogService service;

    @Override
    protected BasicService<LOSFuelOrderLog> getBasicService() {
        return service;
    }
	
	
}
