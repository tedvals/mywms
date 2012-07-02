package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.WorkItem;
import org.mywms.service.BasicService;
import org.mywms.service.WorkItemService;

@Stateless
public class WorkItemCRUDBean extends BusinessObjectCRUDBean<WorkItem> implements WorkItemCRUDRemote {

    @EJB
    WorkItemService service;

    @Override
    protected BasicService<WorkItem> getBasicService() {

        return service;
    }

    @Override
    public WorkItem create(WorkItem entity)
    throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {

        if (entity.getItemDataId() == null) throw new BusinessObjectCreationException();
        if (entity.getWorkTypeId() == null) throw new BusinessObjectCreationException();
        if (entity.getWorkerId() == null) throw new BusinessObjectCreationException();
        if (entity.getScheduleTime() == null) throw new BusinessObjectCreationException();

        return super.create(entity);
    }

}
