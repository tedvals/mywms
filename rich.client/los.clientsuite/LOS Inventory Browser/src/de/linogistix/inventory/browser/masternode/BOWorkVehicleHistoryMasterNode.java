package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.WorkVehicleHistoryTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOWorkVehicleHistoryMasterNode extends BOMasterNode {

    WorkVehicleHistoryTO to;

    /** Creates a new instance of BODeviceNode */
    public BOWorkVehicleHistoryMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (WorkVehicleHistoryTO) d;
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
            
            BOMasterNodeProperty<String> vehicleData = new BOMasterNodeProperty<String>("vehicleData", String.class, to.getVehicleData(), InventoryBundleResolver.class);
            sheet.put(vehicleData);
            BOMasterNodeProperty<String> workType= new BOMasterNodeProperty<String>("workType", String.class, to.getWorkType(), InventoryBundleResolver.class);
            sheet.put(workType);
            BOMasterNodeProperty<String> worker= new BOMasterNodeProperty<String>("worker", String.class, to.getWorker(), InventoryBundleResolver.class);
            sheet.put(worker);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> vehicleData = new BOMasterNodeProperty<String>("vehicleData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> workType = new BOMasterNodeProperty<String>("workType", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> worker = new BOMasterNodeProperty<String>("worker", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
            vehicleData, workType, worker
        };
//Long.valueOf(0)
        return props;
    }
}
