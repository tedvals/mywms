package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.mywms.model.BusinessException;
import org.mywms.service.ConstraintViolatedException;

import org.mywms.model.BasicEntity;

import de.linogistix.los.location.model.LOSStorageLocation;

import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.OrderReceiptPosition;

@Entity
@Table(name = "los_fuel_order_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "labelId"
    })
})
public class LOSFuelOrderLog extends BasicEntity {

    private static final long serialVersionUID = 1L;

    private String labelId;
    private LOSStorageLocation storLoc;
    private int stationPump;
    private LOSOrderReceipients receipientId;
    private OrderReceiptPosition rcptPosId;
    private String orderType;
    private BigDecimal tankRemaining;

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


    /**
     * Get storLoc.
     *
     * @return storLoc as LOSStorageLocation.
     */
    @ManyToOne(optional=false)
    public LOSStorageLocation getStorLoc() {
        return storLoc;
    }

    /**
     * Set storLoc.
     *
     * @param storLoc the value to set.
     */
    public void setStorLoc(LOSStorageLocation storLoc) {
        this.storLoc = storLoc;
    }

    /**
     * Get stationPump.
     *
     * @return stationPump as int.
     */
    public int getStationPump() {
        return stationPump;
    }

    /**
     * Set stationPump.
     *
     * @param stationPump the value to set.
     */
    public void setStationPump(int stationPump) {
        this.stationPump = stationPump;
    }

    /**
     * Get receipientId.
     *
     * @return receipientId as LOSOrderReceipients.
     */
    @ManyToOne(optional=false)
    public LOSOrderReceipients getReceipientId() {
        return receipientId;
    }

    /**
     * Set receipientId.
     *
     * @param receipientId the value to set.
     */
    public void setReceipientId(LOSOrderReceipients receipientId) {
        this.receipientId = receipientId;
    }

    /**
     * Get orderType.
     *
     * @return orderType as String.
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * Set orderType.
     *
     * @param orderType the value to set.
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * Get tankRemaining.
     *
     * @return tankRemaining as BigDecimal.
     */
    @Column(precision=18, scale=6)
    public BigDecimal getTankRemaining() {
        return tankRemaining;
    }

    /**
     * Set tankRemaining.
     *
     * @param tankRemaining the value to set.
     */
    public void setTankRemaining(BigDecimal tankRemaining) {
        this.tankRemaining = tankRemaining;
    }

    /**
     * Get rcptPosId.
     *
     * @return rcptPosId as OrderReceiptPosition.
     */
    @OneToOne(optional=false)
    public OrderReceiptPosition getRcptPosId() {
        return rcptPosId;
    }

    /**
     * Set rcptPosId.
     *
     * @param rcptPosId the value to set.
     */
    public void setRcptPosId(OrderReceiptPosition rcptPosId) {
        this.rcptPosId = rcptPosId;
    }
}
