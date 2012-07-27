package org.mywms.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;
import org.mywms.service.ConstraintViolatedException;

import org.mywms.globals.FuelType;

@Entity
@Table(name="mywms_vehicledata",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "plateNumber", "labelId"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class VehicleData
    extends BasicEntity {

    private String remarks = "";
    private String manufacturerName = "";
    private String modelName = "";
    private String plateNumber;
    private String chassisNumber = "";
    private String engineNumber = "";
    private Date receiptDate;
    private Date storageDate;
    private BigDecimal mileage;
    private BigDecimal hoursMeter;
    private BigDecimal categoryId;
    private BigDecimal typeId;
    private String stnr;
    private String labelId;
    private FuelType fuelType = FuelType.S;
    private String organizationUnit;
    private boolean workingCondition = false;

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

    @Column(nullable=false)
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

    @Temporal(TemporalType.TIMESTAMP)
    public Date getReceiptDate() {
        return this.receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getStorageDate() {
        return this.storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    @Column(precision=15, scale=2)
    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    @Column(precision=15, scale=2)
    public BigDecimal getHoursMeter() {
        return hoursMeter;
    }

    public void setHoursMeter(BigDecimal hoursMeter) {
        this.hoursMeter = hoursMeter;
    }

    @Column(precision=19, scale=2)
    public BigDecimal getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    @Column(precision=19, scale=2)
    public BigDecimal getTypeId() {
        return typeId;
    }

    public void setTypeId(BigDecimal typeId) {
        this.typeId = typeId;
    }

    public String getStnr() {
        return stnr;
    }

    public void setStnr(String stnr) {
        this.stnr = stnr;
    }

    public String getLabelId() {
        return this.labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="char", nullable=false)
    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public String getOrganizationUnit() {
        return this.organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public boolean getWorkingCondition() {
        return workingCondition;
    }

    public void setWorkingCondition(boolean workingCondition) {
        this.workingCondition = workingCondition;
    }

    @Override
    public String toUniqueString() {
        if (getPlateNumber() != null) {
            return getPlateNumber();
        } else {
            return getId().toString();
        }
    }

    @PreUpdate
    @PrePersist
    public void sanityCheck() throws BusinessException, ConstraintViolatedException {

        if (getId() != null) {
            if (( getPlateNumber() == null || getPlateNumber().length() == 0 )) {
                setPlateNumber(getId().toString());
            } else {
                //ok
            }
        } else {
            throw new RuntimeException("Id cannot be retrieved yet - hence plateNum cannot be set");
        }


    }
}
