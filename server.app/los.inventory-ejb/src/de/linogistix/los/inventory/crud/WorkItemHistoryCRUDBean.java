package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.WorkItemHistory;
import org.mywms.service.BasicService;
import org.mywms.service.WorkItemHistoryService;


@Stateless
public class WorkItemHistoryCRUDBean extends BusinessObjectCRUDBean<WorkItemHistory> implements WorkItemHistoryCRUDRemote {

    @EJB
    WorkItemHistoryService service;

    @Override
    protected BasicService<WorkItemHistory> getBasicService() {
        return service;
    }

    @Override
    public WorkItemHistory create(WorkItemHistory entity)
    throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {

        if (entity.getItemDataId() == null) throw new BusinessObjectCreationException();
        if (entity.getWorkTypeId() == null) throw new BusinessObjectCreationException();
        if (entity.getWorkerId() == null) throw new BusinessObjectCreationException();
	//if (entity.getScheduleTime() == null) throw new BusinessObjectCreationException();
	//if (entity.getExecuteDeadline() == null) throw new BusinessObjectCreationException();
	//if (entity.getPlateNumber() == null || entity.getPlateNumber().length() == 0) throw new BusinessObjectCreationException();
        //if (entity.getHandlingUnit() == null) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[] {"handlingUnit"}, BundleResolver.class);

        return super.create(entity);
    }

}
