package de.linogistix.los.inventory.query.dto;

import org.mywms.model.WorkVehicleHistory;
//import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;

public class WorkVehicleHistoryTO extends BODTO<WorkVehicleHistory> {

    private static final long serialVersionUID = 1L;

    private String vehicleData = "";
    private String remarks = "";
    private String workType = "";
    private String worker = "";
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;
    private boolean completionSuccess = false;
    private Date completionDate;
    private String completionRemarks;


    public WorkVehicleHistoryTO(WorkVehicleHistory idat) {
        super(idat.getId(), idat.getVersion(), idat.getLabelId());
        this.vehicleData	= idat.getVehicleDataId().getLabelId();
        this.remarks		= idat.getRemarks();
        this.workType		= idat.getWorkTypeId().getworktype();
        this.worker		= idat.getWorkerId().getName();
        this.urgent 		= idat.isUrgent();
        this.scheduleTime	= idat.getScheduleTime();
        this.executeDeadline	= idat.getExecuteDeadline();
        this.completionSuccess	= idat.getCompletionSuccess();
        this.completionDate   	= idat.getCompletionDate();
        this.completionRemarks	= idat.getCompletionRemarks();
    }

    public WorkVehicleHistoryTO(Long id, int version, String name) {
        super(id, version, name);
    }

    public WorkVehicleHistoryTO(Long id, int version, String name,
                                String vehicleData, String remarks, String workType, String worker,
                                boolean urgent, Date scheduleTime, Date executeDeadline,
                                boolean completionSuccess, Date completionDate, String completionRemarks) {
        super(id, version, name);
        this.vehicleData	= vehicleData;
        this.remarks		= remarks;
        this.workType		= workType;
        this.worker		= worker;
        this.urgent 		= urgent;
        this.scheduleTime	= scheduleTime;
        this.executeDeadline	= executeDeadline;
        this.completionSuccess	= completionSuccess;
        this.completionDate     = completionDate;
        this.completionRemarks  = completionRemarks;
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

    public boolean getCompletionSuccess() {
        return completionSuccess;
    }

    public void setCompletionSuccess(boolean completionSuccess) {
        this.completionSuccess = completionSuccess;
    }

    public Date getCompletionDate() {
        return this.completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getCompletionRemarks() {
        return completionRemarks;
    }

    public void setCompletionRemarks(String completionRemarks) {
        this.completionRemarks = completionRemarks;
    }

}
