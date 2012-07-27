package de.linogistix.los.inventory.query.dto;

import org.mywms.model.VehicleData;
import org.mywms.globals.FuelType;
import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class VehicleDataTO extends BODTO<VehicleData> {

    private static final long serialVersionUID = 1L;

    private String remarks;
    private String manufacturerName;
    private String modelName;
    //private String plateNumber;
    private String chassisNumber;
    private String engineNumber;
    private Date receiptDate;
    private Date storageDate;
    private BigDecimal mileage;
    private BigDecimal hoursMeter;
    private BigDecimal categoryId;
    private BigDecimal typeId;
    private String stnr;
    private String labelId;
    private FuelType fuelType;
    private String organizationUnit;
    private boolean workingCondition;

    public VehicleDataTO(VehicleData idat) {
        super(idat.getId(), idat.getVersion(), idat.getPlateNumber());
        this.remarks         = idat.getRemarks();
        this.manufacturerName= idat.getManufacturerName();
        this.modelName       = idat.getModelName();
	//this.plateNumber     = idat.getPlateNumber();
        this.chassisNumber   = idat.getChassisNumber();
        this.engineNumber    = idat.getEngineNumber();
        this.receiptDate     = idat.getReceiptDate();
        this.storageDate     = idat.getStorageDate();
        this.mileage         = idat.getMileage();
	this.hoursMeter		= idat.getHoursMeter();
	this.categoryId		= idat.getCategoryId();
	this.typeId		= idat.getTypeId();
	this.stnr		= idat.getStnr();
	this.labelId		= idat.getLabelId();
	this.fuelType 		= idat.getFuelType();         
	this.organizationUnit	= idat.getOrganizationUnit();
	this.workingCondition 	= idat.getWorkingCondition(); 
    }

	public VehicleDataTO(Long id, int version, String name){
		super(id, version, name);
	}

    public VehicleDataTO(Long id, int version, String name,
                         String remarks, String manufacturerName, String modelName, String chassisNumber, String engineNumber,
                         Date receiptDate, Date storageDate, BigDecimal mileage,
			BigDecimal hoursMeter, BigDecimal categoryId, BigDecimal typeId, String stnr, String labelId, 
			FuelType fuelType, String organizationUnit, boolean workingCondition) {
        super(id, version, name);
        this.remarks         = remarks;
        this.manufacturerName= manufacturerName;
        this.modelName       = modelName;
	//this.plateNumber     = plateNumber;
        this.chassisNumber   = chassisNumber;
        this.engineNumber    = engineNumber;
        this.receiptDate     = receiptDate;
        this.storageDate     = storageDate;
        this.mileage         = mileage;
	this.hoursMeter		=hoursMeter;
	this.categoryId		=categoryId;
	this.typeId		=typeId;
	this.stnr		=stnr;
	this.labelId		=labelId;
	this.fuelType 		=fuelType;
	this.organizationUnit	=organizationUnit;
	this.workingCondition 	=workingCondition;
    }

    //public VehicleDataTO(Long id, int version, String name,
    //String plateNumber, String chassisNumber, String engineNumber){
    //super(id, version, name);
    //this.plateNumber     = plateNumber;
    //this.chassisNumber   = chassisNumber;
    //this.engineNumber    = engineNumber;
    //}

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
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    //public String getPlateNumber() {
        //return plateNumber;
    //}

    //public void setPlateNumber(String plateNumber) {
        //this.plateNumber = plateNumber;
    //}

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public Date getStorageDate() {
        return storageDate;
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

    public BigDecimal getHoursMeter() {
        return hoursMeter;
    }

    public void setHoursMeter(BigDecimal hoursMeter) {
        this.hoursMeter = hoursMeter;
    }

    public BigDecimal getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

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
}
