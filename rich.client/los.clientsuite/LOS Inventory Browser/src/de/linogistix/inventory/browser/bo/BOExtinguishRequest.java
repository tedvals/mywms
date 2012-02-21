/*
 * BONodeClient.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.los.inventory.pick.model.ExtinguishRequest;
import org.mywms.model.BasicEntity;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOExtinguishRequest extends BOPickingRequest {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {};
    }

    @Override
    protected BasicEntity initEntityTemplate() {
        ExtinguishRequest c;

        c = new ExtinguishRequest();
        c.setNumber("123");
        return c;

    }

}
