package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOWorkItemHistoryMasterNode;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.WorkItemHistoryCRUDRemote;
import de.linogistix.los.inventory.query.WorkItemHistoryQueryRemote;
import de.linogistix.inventory.res.InventoryBundleResolver;
//import de.linogistix.los.inventory.query.ItemUnitQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
//import de.linogistix.los.query.QueryDetail;
import java.util.List;
import java.util.ArrayList;
import org.mywms.globals.Role;
//import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.BasicEntity;
import org.mywms.model.WorkItemHistory;
//import org.mywms.model.ItemUnit;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import de.linogistix.common.bobrowser.action.RefreshBOBeanNodeAction;
import de.linogistix.common.bobrowser.action.BOLockAction;

public class BOWorkItemHistory extends BO {
    //private ItemUnit itemUnit = null;

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }

    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

    protected String initName() {
        return "WorkItemsHistory";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/ItemData.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote)loc.getStateless(WorkItemHistoryQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
            return null;
        }

        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        WorkItemHistory o;

        o = new WorkItemHistory();
        //o.setLabelId("");

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(WorkItemHistoryCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected String[] initIdentifiableProperties() {
        return new String[] {"labelId"};
    }

    @Override
    protected List<SystemAction> initMasterActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>();
        SystemAction action;

        action = SystemAction.get(RefreshBOBeanNodeAction.class);
        action.setEnabled(true);
        actions.add(action);

	action = SystemAction.get(BOLockAction.class);
	action.setEnabled(true);
	actions.add(action);

        return actions;
    }

    @Override
    public Class initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOWorkItemHistoryMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOWorkItemHistoryMasterNode.class;
    }

}
