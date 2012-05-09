/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.WorkTypeTO;
import de.linogistix.los.query.BODTO;
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
            
    private String remarks;
    private String worktype;
    private boolean periodic = false;
    private BigDecimal periodicCircle;
    private BigDecimal completionTime;

            BOMasterNodeProperty<String> worktype = new BOMasterNodeProperty<String>("worktype", String.class, to.getWorktype(), InventoryBundleResolver.class);
            sheet.put(worktype);
            BOMasterNodeProperty<boolean> periodic = new BOMasterNodeProperty<boolean>("periodic", boolean.class, to.isPeriodic(), InventoryBundleResolver.class);
            sheet.put(periodic);
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
