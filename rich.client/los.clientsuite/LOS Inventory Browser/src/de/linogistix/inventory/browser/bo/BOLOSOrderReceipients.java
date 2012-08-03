package de.linogistix.inventory.browser.bo;

import org.mywms.model.BasicEntity;
import org.mywms.globals.Role;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.inventory.crud.LOSOrderReceipientsCRUDRemote;
import de.linogistix.los.inventory.query.LOSOrderReceipientsQueryRemote;
import de.linogistix.los.inventory.model.LOSOrderReceipients;

import de.linogistix.inventory.browser.masternode.BOLOSOrderReceipientsMasterNode;

import org.openide.util.Lookup;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;

public class BOLOSOrderReceipients extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR};
    }

    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }

    protected String initName() {
        return "LOSOrderReceipients";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/User.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote)loc.getStateless(LOSOrderReceipientsQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
            return null;
        }

        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSOrderReceipients o;

        o = new LOSOrderReceipients();
        //o.setLabelId("");

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSOrderReceipientsCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected String[] initIdentifiableProperties() {
        return new String[] {"identityCard"};
    }

    @Override
    public Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSOrderReceipientsMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSOrderReceipientsMasterNode.class;
    }

}
