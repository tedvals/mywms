/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.exception;

import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.pick.res.BundleResolver;

/**
 *
 * @author trautm
 */
@ApplicationException(rollback = true)
public class PickingException extends FacadeException {

    private static final long serialVersionUID = 1L;
    
    private static final String resourceBundle = "de.linogistix.los.inventory.pick.res.Bundle";
    
    private PickingExceptionKey key;
    
    public PickingException(PickingExceptionKey key, String param1){
        super(key.toString(), key.toString(),new Object[]{param1}, resourceBundle);
        setBundleResolver(BundleResolver.class);
        this.key = key;
    }

    public PickingException(PickingExceptionKey key,
			Object[] params) {
    	super(key.toString(), key.toString(),params, resourceBundle);
        setBundleResolver(BundleResolver.class);
        this.key = key;

	}

	@Override
    public String getBundleName() {
        return super.getBundleName();
    }
    
    public PickingExceptionKey getPickingExceptionKey(){
        return key;
    }
    
    
}
