/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.VehicleData;
import org.mywms.service.BasicService;
import org.mywms.service.VehicleDataService;


@Stateless
public class VehicleDataCRUDBean extends BusinessObjectCRUDBean<VehicleData> implements VehicleDataCRUDRemote {

    @EJB
    VehicleDataService service;

    @Override
    protected BasicService<VehicleData> getBasicService() {

        return service;
    }

    @Override
    public VehicleData create(VehicleData entity)
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
