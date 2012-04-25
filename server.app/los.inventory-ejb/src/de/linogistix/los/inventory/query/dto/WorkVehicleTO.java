package de.linogistix.los.inventory.query.dto;

import org.mywms.model.WorkVehicle;
//import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class WorkVehicleTO extends BODTO<WorkVehicle> {

    private static final long serialVersionUID = 1L;

    private String vehicleDataId;
    private String vehiclePlate;
    private String remarks;
    private String workTypeId;
    private String workType;
    private String workerId;
    private String worker;
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;

    public WorkVehicleTO(WorkVehicle idat) {
        super(idat.getId(), idat.getVersion(), idat.getId());
this.vehicleDataId	= idat.getVehicleDataId().getLabelId();
this.vehiclePlate	= idat.getVehicleDataId().getPlateNumber();
this.remarks		= idat.getRemarks();
this.workTypeId		= idat.getWorkTypeId().getId().toString();
this.workType		= idat.getWorkTypeId().getworktype();
this.workerId		= idat.getWorkerId().getId().toString();
this.worker		= idat.getWorkerId().getName();
this.urgent 		= idat.isUrgent();
this.scheduleTime	= idat.getScheduleTime();
this.executeDeadline	= idat.getExecuteDeadline();
    }
//here
	public WorkVehicleTO(Long id, int version, String name){
		super(id, version, name);
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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

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
}
