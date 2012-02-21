/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.los.reference.customization.mobile;

import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.mobile.processes.controller.ManageMobile;
import de.linogistix.mobile.processes.controller.ManageMobileBean;
import de.linogistix.mobile.processes.controller.MobileFunction;

@Stateless
public class Ref_ManageMobileBean extends ManageMobileBean implements ManageMobile {

	@Override
	public List<MobileFunction> getFunctions() {
		return super.getFunctions();
	}
	
	@Override
	public int getMenuPageSize() {
		return super.getMenuPageSize();
	}
}
