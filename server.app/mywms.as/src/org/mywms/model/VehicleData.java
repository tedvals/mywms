package org.mywms.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/*import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
*/
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;
import org.mywms.service.ConstraintViolatedException;

@Entity
@Table(name="mywms_vehicledata",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "labelId"
    })
})
/*,uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "client_id", "item_nr"
    })
})*/
@Inheritance(strategy = InheritanceType.JOINED)
public class VehicleData
    extends BasicEntity {

    private String remarks = "";

    private String manufacturerName = "";

    private String modelName = "";

    private String plateNumber = "";

    private String chassisNumber = "";

    private String engineNumber = "";

    private Date receiptDate;

    private Date storageDate;

    private BigDecimal mileage;

    private String labelId;


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

    @Column(nullable=false)
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
