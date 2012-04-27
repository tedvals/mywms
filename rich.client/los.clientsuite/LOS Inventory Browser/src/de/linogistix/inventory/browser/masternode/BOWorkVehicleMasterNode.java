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
            
            BOMasterNodeProperty<String> vehicleDataId = new BOMasterNodeProperty<String>("vehicleDataId", String.class, to.getVehicleDataId(), InventoryBundleResolver.class);
            sheet.put(vehicleDataId);
            BOMasterNodeProperty<String> workTypeId= new BOMasterNodeProperty<String>("workTypeId", String.class, to.getWorkTypeId(), InventoryBundleResolver.class);
            sheet.put(workTypeId);
            BOMasterNodeProperty<String> workerId= new BOMasterNodeProperty<String>("workerId", String.class, to.getWorkerId(), InventoryBundleResolver.class);
            sheet.put(workerId);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> vehicleDataId = new BOMasterNodeProperty<String>("vehicleDataId", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> workTypeId = new BOMasterNodeProperty<String>("workTypeId", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> workerId= new BOMasterNodeProperty<String>("workerId", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
            vehicleDataId, workTypeId, workerId
        };

        return props;
    }
}
