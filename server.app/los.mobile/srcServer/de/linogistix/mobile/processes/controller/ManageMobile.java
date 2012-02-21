/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.controller;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ManageMobile {

	public List<MobileFunction> getFunctions();
	
	public int getMenuPageSize();

}
