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

//import javax.persistence.MappedSuperclass;


//@MappedSuperclass
@Entity
@Table(name="mywms_workvehicle"
,uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "labelId"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkVehicle
    extends BasicEntity {

    private VehicleData vehicleDataId;
    private String remarks;
    private WorkType workTypeId;
    private User workerId;
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;
    private String labelId;

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
