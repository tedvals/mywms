package org.mywms.model;

import java.math.BigDecimal;
import java.util.Date;
//import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
//import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.OneToOne;
//import javax.persistence.OrderBy;
//import javax.persistence.PrePersist;
//import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.mywms.service.ConstraintViolatedException;
/*import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.globals.ReparabilityCodeType;
import org.mywms.service.ConstraintViolatedException;*/

import org.mywms.model.WorkVehicle;

@Entity
@Table(name="mywms_workvehiclehistory"
,uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "labelId"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkVehicleHistory
    extends BasicEntity {

    private VehicleData vehicleDataId;
    private String remarks;
    private WorkType workTypeId;
    private User workerId;
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;
    private String labelId;
    private boolean completionSuccess = false;
    private Date completionDate;
    private String completionRemarks;

    /*public WorkVehicleHistory(){
    }

    public WorkVehicleHistory(WorkVehicle wv){
	setVehicleDataId(wv.getVehicleDataId());
	setRemarks(wv.getRemarks());
	setWorkTypeId(wv.getWorkTypeId());
	setWorkerId(wv.getWorkerId());
	setUrgent(wv.isUrgent());
	setScheduleTime(wv.getScheduleTime());
	setExecuteDeadline(wv.getExecuteDeadline());
	    //public String getLabelId() {
	    //public void setLabelId(String labelId) {
	    //public boolean getCompletionSuccess() {
	    //public void setCompletionSuccess(boolean completionSuccess) {
	    //public Date getCompletionDate() {
	    //public void setCompletionDate(Date completionDate) {
	    //public String getCompletionRemarks() {
	    //public void setCompletionRemarks(String completionRemarks) {
    }*/

    @ManyToOne(optional = false)
    public VehicleData getVehicleDataId() {
        return this.vehicleDataId;
    }

    public void setVehicleDataId(VehicleData vehicleDataId) {
        this.vehicleDataId= vehicleDataId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @ManyToOne(optional = false)
    //@Column(nullable = false)
    public WorkType getWorkTypeId() {
        return this.workTypeId;
    }

    public void setWorkTypeId(WorkType workTypeId) {
        this.workTypeId = workTypeId;
    }

    @ManyToOne(optional = false)
    //@Column(nullable = false)
    public User getWorkerId() {
        return this.workerId;
    }

    public void setWorkerId(User workerId) {
        this.workerId = workerId;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getScheduleTime() {
        return this.scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getExecuteDeadline() {
        return this.executeDeadline;
    }

    public void setExecuteDeadline(Date executeDeadline) {
        this.executeDeadline = executeDeadline;
    }

    @Column(nullable = false)
    public String getLabelId() {
        return this.labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public boolean getCompletionSuccess() {
        return completionSuccess;
    }

    public void setCompletionSuccess(boolean completionSuccess) {
        this.completionSuccess = completionSuccess;
    }

    @Temporal(TemporalType.TIMESTAMP)
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

    @Override
    public String toUniqueString() {
        if (getLabelId() != null) {
            return getLabelId();
        } else {
            return getId().toString();
        }
    }

    @PreUpdate
    @PrePersist
    public void sanityCheck() throws BusinessException, ConstraintViolatedException {

        if (getId() != null) {
            if (( getLabelId() == null || getLabelId().length() == 0 )) {
                setLabelId(getId().toString());
            } else {
                //ok
            }
        } else {
            throw new RuntimeException("Id cannot be retrieved yet - hence labelId cannot be set");
        }


    }
}
