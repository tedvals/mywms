package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.WorkType;
import org.mywms.service.BasicService;
import org.mywms.service.WorkTypeService;


@Stateless
public class WorkTypeCRUDBean extends BusinessObjectCRUDBean<WorkType> implements WorkTypeCRUDRemote {

    @EJB
    WorkTypeService service;

    @Override
    protected BasicService<WorkType> getBasicService() {
        return service;
    }

    @Override
    public WorkType create(WorkType entity)
    throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {

    //public String getRemarks() {
    //public String getworktype() {
    //public boolean isPeriodic() {
    //public BigDecimal getPeriodicCircle() {
    //public BigDecimal getCompletionTime() {

        if (entity.getworktype() == null || entity.getworktype().length() == 0) throw new BusinessObjectCreationException();
        //if (entity.getHandlingUnit() == null) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[] {"handlingUnit"}, BundleResolver.class);

        return super.create(entity);
    }

}
