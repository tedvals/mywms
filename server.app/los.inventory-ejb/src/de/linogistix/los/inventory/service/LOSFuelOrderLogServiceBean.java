package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
@Stateless
public class LOSFuelOrderLogServiceBean extends BasicServiceBean<LOSFuelOrderLog> implements LOSFuelOrderLogService{

}
