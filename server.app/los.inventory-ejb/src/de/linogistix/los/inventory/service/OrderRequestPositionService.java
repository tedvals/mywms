/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;


import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import javax.ejb.Local;

import org.mywms.service.BasicService;

/**
 *  A Service for {@link OrderRequestPosition}.
 * 
 * @author trautm
 */
@Local
public interface OrderRequestPositionService extends BasicService<LOSOrderRequestPosition> {
	
	public LOSOrderRequestPosition getByIndex( LOSOrderRequest order, int positionIndex );

}
