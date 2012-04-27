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

    public WorkVehicleTO(Long id, int version) {
        super(id, version, id);
    }

    public WorkVehicleTO(Long id, int version, Long name,
                         String vehicleDataId, String vehiclePlate, String remarks, String workTypeId, String workType, String workerId,
                         String worker, boolean urgent, Date scheduleTime, Date executeDeadline) {
        super(id, version, id);
        this.vehicleDataId	= vehicleDataId;
        this.vehiclePlate	= vehiclePlate;
        this.remarks		= remarks;
        this.workTypeId		= workTypeId;
        this.workType		= workType;
        this.workerId		= workerId;
        this.worker		= worker;
        this.urgent 		= urgent;
        this.scheduleTime	= scheduleTime;
        this.executeDeadline	= executeDeadline;
    }

    public String getVehicleDataId() {
        return vehicleDataId;
    }

    public void setVehicleDataId(String vehicleDataId) {
        this.vehicleDataId = vehicleDataId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(String workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Date getExecuteDeadline() {
        return executeDeadline;
    }

    public void setExecuteDeadline(Date executeDeadline) {
        this.executeDeadline = executeDeadline;
    }
}
