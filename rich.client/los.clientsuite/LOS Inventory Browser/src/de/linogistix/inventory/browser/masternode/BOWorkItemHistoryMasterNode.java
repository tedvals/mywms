package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.WorkItemHistoryTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOWorkItemHistoryMasterNode extends BOMasterNode {

    WorkItemHistoryTO to;

    /** Creates a new instance of BODeviceNode */
    public BOWorkItemHistoryMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (WorkItemHistoryTO) d;
    }

    //private ItemData itemDataId;
    //private WorkType workTypeId;
    //private User workerId;
    //private boolean urgent = false;
    //private Date scheduleTime;
    //private Date executeDeadline;
    //public String getItemDataId() {
    //public String getItemPlate() {
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
            
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, to.getItemData(), InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> workType= new BOMasterNodeProperty<String>("workType", String.class, to.getWorkType(), InventoryBundleResolver.class);
            sheet.put(workType);
            BOMasterNodeProperty<String> worker= new BOMasterNodeProperty<String>("worker", String.class, to.getWorker(), InventoryBundleResolver.class);
            sheet.put(worker);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> workType = new BOMasterNodeProperty<String>("workType", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> worker = new BOMasterNodeProperty<String>("worker", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
            itemData, workType, worker
        };
//Long.valueOf(0)
        return props;
    }
}
