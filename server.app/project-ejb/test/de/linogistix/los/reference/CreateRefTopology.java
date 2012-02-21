/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference;

import junit.framework.TestCase;

import org.mywms.ejb.BeanLocator;

import de.linogistix.los.reference.facade.RefTopologyFacade;
import de.linogistix.los.test.TestUtilities;

public class CreateRefTopology extends TestCase {
    private BeanLocator beanLocator;
    private RefTopologyFacade topo=null;
    
    protected void setUp() throws Exception {
        super.setUp();
        beanLocator = TestUtilities.beanLocator;
		topo = beanLocator.getStateless(RefTopologyFacade.class);

    }

    public void testTopology() throws Exception {
		
		topo.createDemoTopology();
	}

}
