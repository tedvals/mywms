/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import org.mywms.model.VehicleData;
import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class VehicleDataTO extends BODTO<VehicleData> {

    private static final long serialVersionUID = 1L;

    private String remarks;
    private String manufacturerName;
    private String modelName;
    private String plateNumber;
    private String chassisNumber;
    private String engineNumber;
    private Date receiptDate;
    private Date storageDate;
    private BigDecimal mileage;

    public VehicleDataTO(VehicleData idat) {
        super(idat.getId(), idat.getVersion(), idat.getLabelId());
        this.plateNumber = idat.getPlateNumber();
        this.chassisNumber = idat.getChassisNumber();
        this.engineNumber = idat.getEngineNumber();
    }

    public VehicleDataTO(Long id, int version, String name,
                         String remarks, String manufacturerName, String modelName, String plateNumber, String chassisNumber, String engineNumber,
                         Date receiptDate, Date storageDate, BigDecimal mileage) {
        super(id, version, name);
        this.remarks         = remarks;
        this.manufacturerName= manufacturerName;
        this.modelName       = modelName;
        this.plateNumber     = plateNumber;
        this.chassisNumber   = chassisNumber;
        this.engineNumber    = engineNumber;
        this.receiptDate     = receiptDate;
        this.storageDate     = storageDate;
        this.mileage         = mileage;
    }

    //public VehicleDataTO(Long id, int version, String number) {
    //super(id, version, number);
    //}

    //public VehicleDataTO(Long id, int version, String number,
    //String plateNumber, String chassisNumber, String engineNumber) {
    //super(id, version, number);
    //this.plateNumber = plateNumber;
    //this.chassisNumber = chassisNumber;
    //this.engineNumber = engineNumber;
    //}

    //public VehicleDataTO(String plateNumber, String chassisNumber,
    //String engineNumber) {
    //// super(id, version, id);
    //this.plateNumber = plateNumber;
    //this.chassisNumber = chassisNumber;
    //this.engineNumber = engineNumber;
    //}

    // public VehicleDataTO(){}
    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getManufacturerName() {
        return this.manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getChassisNumber() {
        return this.chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return this.engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Date getReceiptDate() {
        return this.receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public Date getStorageDate() {
        return this.storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }
}
