/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
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
//import javax.persistence.UniqueConstraint;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.globals.ReparabilityCodeType;
import org.mywms.service.ConstraintViolatedException;*/

@Entity
@Table(name="mywms_workitemhistory")
/*,uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "client_id", "item_nr"
    })
})*/
@Inheritance(strategy = InheritanceType.JOINED)
public class WorkItemHistory
    extends WorkItem{

    private boolean completionsuccess = false;
    private Date completiondate;
    private String completionremarks;


    public boolean getCompletionSuccess() {
        return completionsuccess;
    }

    public void setCompletionSuccess(boolean completionsuccess) {
        this.completionsuccess = completionsuccess;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCompletionDate() {
        return this.completiondate;
    }

    public void setCompletionDate(Date completiondate) {
        this.completiondate = completiondate;
    }

    public String getCompletionRemarks() {
        return completionremarks;
    }

    public void setCompletionRemarks(String completionremarks) {
        this.completionremarks = completionremarks;
    }

}
