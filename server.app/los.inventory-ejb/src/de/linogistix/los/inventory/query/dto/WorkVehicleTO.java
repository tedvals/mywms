package de.linogistix.los.inventory.query.dto;

import org.mywms.model.WorkVehicle;
//import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class WorkVehicleTO extends BODTO<WorkVehicle> {

    private static final long serialVersionUID = 1L;

    private String vehicleData = "";
    private String remarks = "";
    private String workType = "";
    private String worker = "";
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;

    public WorkVehicleTO(WorkVehicle idat) {
        super(idat.getId(), idat.getVersion(), idat.getLabelId());
        this.vehicleData	= idat.getVehicleDataId().getLabelId();
        this.remarks		= idat.getRemarks();
        this.workType		= idat.getWorkTypeId().getworktype();
        this.worker			= idat.getWorkerId().getName();
        this.urgent 		= idat.isUrgent();
        this.scheduleTime	= idat.getScheduleTime();
        this.executeDeadline= idat.getExecuteDeadline();
    }

    public WorkVehicleTO(Long id, int version, String name) {
        super(id, version, name);
    }

    public WorkVehicleTO(Long id, int version, String name,
    		String vehicleData, String remarks, String workType, String worker,
    		boolean urgent, Date scheduleTime, Date executeDeadline) {
        super(id, version, name);
        this.vehicleData	= vehicleData;
        this.remarks		= remarks;
        this.workType		= workType;
        this.worker			= worker;
        this.urgent 		= urgent;
        this.scheduleTime	= scheduleTime;
        this.executeDeadline= executeDeadline;
    }
  

    public String getVehicleData() {
        return vehicleData;
    }

    public void setVehicleData(String vehicleData) {
        this.vehicleData = vehicleData;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
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
