package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.WorkVehicle;
import org.mywms.service.BasicService;
import org.mywms.service.WorkVehicleService;


@Stateless
public class WorkVehicleCRUDBean extends BusinessObjectCRUDBean<WorkVehicle> implements WorkVehicleCRUDRemote {

    @EJB
    WorkVehicleService service;

    @Override
    protected BasicService<WorkVehicle> getBasicService() {

        return service;
    }

    //---here
    @Override
    public WorkVehicle create(VehicleData entity)
    throws BusinessObjectExistsException,
        BusinessObjectCreationException, BusinessObjectSecurityException {

        //public String getRemarks() {
        //public String getManufacturerName() {
        //public String getModelName() {
        //public String getPlateNumber() {
        //public String getChassisNumber() {
        //public String getEngineNumber() {
        //public Date getReceiptDate() {
        //public Date getStorageDate() {
        //public BigDecimal getMileage() {

        if (entity.getPlateNumber() == null || entity.getPlateNumber().length() == 0) throw new BusinessObjectCreationException();
        //if (entity.getHandlingUnit() == null) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[] {"handlingUnit"}, BundleResolver.class);

        return super.create(entity);
    }

}
