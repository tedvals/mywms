/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.LogItem;

@Remote
public interface PickSanityFacade {

	List<LogItem> sanityCheck();
}
