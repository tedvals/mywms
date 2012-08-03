package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSOrderReceipientsTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOLOSOrderReceipientsMasterNode extends BOMasterNode {

    LOSOrderReceipientsTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSOrderReceipientsMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSOrderReceipientsTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
	    //BOMasterNodeProperty<String> identityCard = new BOMasterNodeProperty<String>("identityCard", String.class, to.getIdentityCard(), CommonBundleResolver.class);
	    //sheet.put(identityCard);
            BOMasterNodeProperty<String> lastName= new BOMasterNodeProperty<String>("lastName", String.class, to.getLastName(), CommonBundleResolver.class);
            sheet.put(lastName);
            BOMasterNodeProperty<String> tokenId= new BOMasterNodeProperty<String>("tokenId", String.class, to.getTokenId(), CommonBundleResolver.class);
            sheet.put(tokenId);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
	    //BOMasterNodeProperty<String> identityCard = new BOMasterNodeProperty<String>("identityCard", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> lastName = new BOMasterNodeProperty<String>("lastName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> tokenId = new BOMasterNodeProperty<String>("tokenId", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
		//identityCard, 
		    lastName, tokenId
        };
        return props;
    }
}
