package de.linogistix.los.inventory.query.dto;

import org.mywms.model.WorkVehicle;
//import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class WorkVehicleTO extends BODTO<WorkVehicle> {

    private static final long serialVersionUID = 1L;

    private Long vehicleDataId;
    private String vehiclePlate;
    private String remarks;
    private Long workTypeId;
    private String workType;
    private Long workerId;
    private String worker;
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;

    public WorkVehicleTO(WorkVehicle idat) {
        super(idat.getId(), idat.getVersion(), idat.getId());
        this.vehicleDataId	= idat.getVehicleDataId().getId();
        this.vehiclePlate	= idat.getVehicleDataId().getPlateNumber();
        this.remarks		= idat.getRemarks();
        this.workTypeId		= idat.getWorkTypeId().getId();
        this.workType		= idat.getWorkTypeId().getworktype();
        this.workerId		= idat.getWorkerId().getId();
        this.worker		= idat.getWorkerId().getName();
        this.urgent 		= idat.isUrgent();
        this.scheduleTime	= idat.getScheduleTime();
        this.executeDeadline	= idat.getExecuteDeadline();
    }

    public WorkVehicleTO(Long id, int version) {
        super(id, version, id);
    }

    public WorkVehicleTO(Long id, int version,
                         Long vehicleDataId, String vehiclePlate, String remarks, Long workTypeId, String workType, Long workerId,
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

//o.id, o.version, o.id, 
//o.vehicleDataId, 
//o.vehicleDataId.plateNumber, 
//o.remarks, 
//o.workTypeId, 
//o.workTypeId.worktype, 
//o.workerId, 
//o.workerId.name, 
//o.urgent, 
//o.scheduleTime, 
//o.executeDeadline) 

    //public WorkVehicleTO(String id, int version, String vehicleDataId, String vehiclePlate, String remarks, String workTypeId, String workType, String workerId,
                         //String worker, boolean urgent, Date scheduleTime, Date executeDeadline) {
        //super(id, version, id);
        //this.vehicleDataId	= vehicleDataId;
        //this.vehiclePlate	= vehiclePlate;
        //this.remarks		= remarks;
        //this.workTypeId		= workTypeId;
        //this.workType		= workType;
        //this.workerId		= workerId;
        //this.worker		= worker;
        //this.urgent 		= urgent;
        //this.scheduleTime	= scheduleTime;
        //this.executeDeadline	= executeDeadline;
    //}

    //public WorkVehicleTO(String id, int version, String vehicleDataId, String vehiclePlate, String remarks, String workTypeId, String workType, String workerId,
                         //String worker, boolean urgent, Date scheduleTime, Date executeDeadline) {
        //super(id, version, id);
        //this.vehicleDataId	= vehicleDataId;
        //this.vehiclePlate	= vehiclePlate;
        //this.remarks		= remarks;
        //this.workTypeId		= workTypeId;
        //this.workType		= workType;
        //this.workerId		= workerId;
        //this.worker		= worker;
        //this.urgent 		= urgent;
        //this.scheduleTime	= scheduleTime;
        //this.executeDeadline	= executeDeadline;
    //}

    public Long getVehicleDataId() {
        return vehicleDataId;
    }

    public void setVehicleDataId(Long vehicleDataId) {
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

    public Long getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(Long workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
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
