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

// --- since here 24/4/12
    private VehicleData vehicleDataId;
    private WorkType workTypeId;
    private User workerId;
    private boolean urgent = false;
    private Date scheduleTime;
    private Date executeDeadline;

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
            BOMasterNodeProperty<String> vehicleDataId = new BOMasterNodeProperty<String>("vehicleDataId", String.class, to.getPlateNumber(), InventoryBundleResolver.class);
            sheet.put(plateNumber);
            BOMasterNodeProperty<String> chassisNumber = new BOMasterNodeProperty<String>("chassisNumber", String.class, to.getChassisNumber(), InventoryBundleResolver.class);
            sheet.put(chassisNumber);
            BOMasterNodeProperty<String> engineNumber= new BOMasterNodeProperty<String>("engineNumber", String.class, to.getEngineNumber(), InventoryBundleResolver.class);
            sheet.put(engineNumber);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> plateNumber = new BOMasterNodeProperty<String>("plateNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> chassisNumber = new BOMasterNodeProperty<String>("chassisNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> engineNumber= new BOMasterNodeProperty<String>("engineNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
            plateNumber, chassisNumber, engineNumber
        };

        return props;
    }
}
