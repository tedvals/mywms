/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.mywms.facade.Authentication;
import org.mywms.facade.AuthenticationInfoTO;

/**
 *
 * @author trautm
 */
@Stateless
@RolesAllowed( { org.mywms.globals.Role.ADMIN_STR,
	org.mywms.globals.Role.OPERATOR_STR,
	org.mywms.globals.Role.FOREMAN_STR,
	org.mywms.globals.Role.INVENTORY_STR,
	org.mywms.globals.Role.CLEARING_STR,
    org.mywms.globals.Role.GUEST_STR})
public class LoginServiceBean  implements de.linogistix.los.user.LoginServiceRemote {
  
  @EJB
  Authentication authentification;
  
  public boolean loginCheck() throws SecurityException {
    return true;
  }

  public AuthenticationInfoTO getUserInfo() {
    return this.authentification.getUserInfo();
  }
  
  
}
