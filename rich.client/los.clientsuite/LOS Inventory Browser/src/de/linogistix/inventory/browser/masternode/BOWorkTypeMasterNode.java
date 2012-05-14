package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.WorkTypeTO;
import de.linogistix.los.query.BODTO;
import java.math.BigDecimal;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOWorkTypeMasterNode extends BOMasterNode {

    WorkTypeTO to;

    /** Creates a new instance of BODeviceNode */
    public BOWorkTypeMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (WorkTypeTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
	    //BOMasterNodeProperty<String> worktype = new BOMasterNodeProperty<String>("worktype", String.class, to.getworktype(), InventoryBundleResolver.class);
	    //sheet.put(worktype);
	    //BOMasterNodeProperty<Boolean> periodic = new BOMasterNodeProperty<Boolean>("periodic", Boolean.class, to.isPeriodic(), InventoryBundleResolver.class);
	    //sheet.put(periodic);
            BOMasterNodeProperty<BigDecimal> periodicCircle= new BOMasterNodeProperty<BigDecimal>("periodicCircle", BigDecimal.class, to.getPeriodicCircle(), InventoryBundleResolver.class);
            sheet.put(periodicCircle);
            BOMasterNodeProperty<BigDecimal> completionTime= new BOMasterNodeProperty<BigDecimal>("completionTime", BigDecimal.class, to.getCompletionTime(), InventoryBundleResolver.class);
            sheet.put(completionTime);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
	    //BOMasterNodeProperty<String> worktype = new BOMasterNodeProperty<String>("worktype", String.class, "", InventoryBundleResolver.class);
	    //BOMasterNodeProperty<Boolean> periodic = new BOMasterNodeProperty<Boolean>("periodic", Boolean.class, Boolean.FALSE, InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> periodicCircle = new BOMasterNodeProperty<BigDecimal>("periodicCircle", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> completionTime = new BOMasterNodeProperty<BigDecimal>("completionTime", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
		//worktype, 
		//periodic, 
		periodicCircle, completionTime
        };

        return props;
    }
}
