package de.linogistix.los.inventory.query.dto;

import org.mywms.model.WorkType;
import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;

public class WorkTypeTO extends BODTO<WorkType> {

    private static final long serialVersionUID = 1L;

    private String remarks;
    //private String worktype;
    private boolean periodic = false;
    private BigDecimal periodicCircle;
    private BigDecimal completionTime;


    public WorkTypeTO(WorkType idat) {
        super(idat.getId(), idat.getVersion(), idat.getworktype());
        this.remarks		= idat.getRemarks();
	//this.worktype		= idat.getworktype();
        this.periodic 		= idat.isPeriodic();
        this.periodicCircle	= idat.getPeriodicCircle();
        this.completionTime	= idat.getCompletionTime();
    }

    public WorkTypeTO(Long id, int version, String name) {
        super(id, version, name);
    }

    public WorkTypeTO(Long id, int version, String name,
                      String remarks, 
		      //String worktype, 
		      boolean periodic, BigDecimal periodicCircle, BigDecimal completionTime) {
        super(id, version, name);
        this.remarks		= remarks;
	//this.worktype		= worktype;
        this.periodic 		= periodic;
        this.periodicCircle	= periodicCircle;
        this.completionTime	= completionTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    //public String getworktype() {
        //return worktype;
    //}

    //public void setworktype(String worktype) {
        //this.worktype = worktype;
    //}

    public boolean isPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public BigDecimal getPeriodicCircle() {
        return periodicCircle;
    }

    public void setPeriodicCircle(BigDecimal periodicCircle) {
        this.periodicCircle = periodicCircle;
    }

    public BigDecimal getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(BigDecimal completionTime) {
        this.completionTime = completionTime;
    }
}
