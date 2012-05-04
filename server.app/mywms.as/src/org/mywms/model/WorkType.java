/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package org.mywms.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/*import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;

import org.mywms.facade.FacadeException;*/
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;
import org.mywms.service.ConstraintViolatedException;

@Entity
@Table(name = "los_work_type"
, uniqueConstraints = { 
		@UniqueConstraint(columnNames = {
				"worktype"}) })
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkType extends BasicEntity
{
    private String remarks;
    private String worktype;
    private boolean periodic = false;
    private BigDecimal periodicCircle;
    private BigDecimal completionTime;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    @Column(nullable=false)
    public String getworktype() {
        return worktype;
    }

    public void setworktype(String worktype) {
        this.worktype = worktype;
    }

    public boolean isPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    @Column(precision=15, scale=2)
    public BigDecimal getPeriodicCircle() {
        return periodicCircle;
    }

    public void setPeriodicCircle(BigDecimal periodicCircle) {
        this.periodicCircle = periodicCircle;
    }

    @Column(precision=15, scale=2)
    public BigDecimal getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(BigDecimal completionTime) {
        this.completionTime = completionTime;
    }
    
	@Override
	public String toUniqueString() {
		if (getworktype() != null) {
			return getworktype();
		} else {
			return getId().toString();
		}
	}

    @PreUpdate
    @PrePersist
    public void sanityCheck() throws BusinessException, ConstraintViolatedException {

        if (getId() != null) {
            if (( getworktype() == null || getworktype().length() == 0 )) {
                setworktype(getId().toString());
            } else {
                //ok
            }
        } else {
            throw new RuntimeException("Id cannot be retrieved yet - hence worktype cannot be set");
        }


    }
    
}
