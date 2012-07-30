package de.linogistix.los.inventory.service;

import javax.ejb.Remote;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
@Remote
public interface LOSFuelOrderLogService extends BasicService<LOSFuelOrderLog>{

}
