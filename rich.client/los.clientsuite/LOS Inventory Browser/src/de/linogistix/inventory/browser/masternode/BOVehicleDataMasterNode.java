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
import de.linogistix.los.inventory.query.dto.VehicleDataTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

public class BOVehicleDataMasterNode extends BOMasterNode {

    VehicleDataTO to;

    /** Creates a new instance of BODeviceNode */
    public BOVehicleDataMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (VehicleDataTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
	    //BOMasterNodeProperty<String> plateNumber = new BOMasterNodeProperty<String>("plateNumber", String.class, to.getPlateNumber(), InventoryBundleResolver.class);
	    //sheet.put(plateNumber);
            BOMasterNodeProperty<String> chassisNumber = new BOMasterNodeProperty<String>("chassisNumber", String.class, to.getChassisNumber(), InventoryBundleResolver.class);
            sheet.put(chassisNumber);
            BOMasterNodeProperty<String> engineNumber= new BOMasterNodeProperty<String>("engineNumber", String.class, to.getEngineNumber(), InventoryBundleResolver.class);
            sheet.put(engineNumber);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
	    //BOMasterNodeProperty<String> plateNumber = new BOMasterNodeProperty<String>("plateNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> chassisNumber = new BOMasterNodeProperty<String>("chassisNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> engineNumber= new BOMasterNodeProperty<String>("engineNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
		//plateNumber, 
		    chassisNumber, engineNumber
        };

        return props;
    }
}
