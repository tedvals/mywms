package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.WorkVehicleTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOWorkVehicleMasterNode extends BOMasterNode {

    WorkVehicleTO to;

    /** Creates a new instance of BODeviceNode */
    public BOWorkVehicleMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (WorkVehicleTO) d;
    }

    //private VehicleData vehicleDataId;
    //private WorkType workTypeId;
    //private User workerId;
    //private boolean urgent = false;
    //private Date scheduleTime;
    //private Date executeDeadline;
    //public String getVehicleDataId() {
    //public String getVehiclePlate() {
    //public String getRemarks() {
    //public String getWorkTypeId() {
    //public String getWorkType() {
    //public String getWorkerId() {
    //public String getWorker() {
    //public Date getScheduleTime() {
    //public Date getExecuteDeadline() {

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
            BOMasterNodeProperty<Long> vehicleDataId = new BOMasterNodeProperty<String>("vehicleDataId", Long.class, to.getVehicleDataId(), InventoryBundleResolver.class);
            sheet.put(vehicleDataId);
            BOMasterNodeProperty<Long> workTypeId= new BOMasterNodeProperty<String>("workTypeId", Long.class, to.getWorkTypeId(), InventoryBundleResolver.class);
            sheet.put(workTypeId);
            BOMasterNodeProperty<Long> workerId= new BOMasterNodeProperty<String>("workerId", Long.class, to.getWorkerId(), InventoryBundleResolver.class);
            sheet.put(workerId);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<Long> vehicleDataId = new BOMasterNodeProperty<Long>("vehicleDataId", Long.class, 0, InventoryBundleResolver.class);
        BOMasterNodeProperty<Long> workTypeId = new BOMasterNodeProperty<Long>("workTypeId", Long.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<Long> workerId= new BOMasterNodeProperty<Long>("workerId", Long.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
            vehicleDataId, workTypeId, workerId
        };

        return props;
    }
}
