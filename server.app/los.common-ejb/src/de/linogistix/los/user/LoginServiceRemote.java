/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.user;

import javax.ejb.Remote;

import org.mywms.facade.Authentication;


/**
 * Tests whether the User login will be accepted.
 * 
 */
@Remote
public interface LoginServiceRemote extends Authentication{
  
  /**
   * User has to authenticate, otherwise a {@link SecurityException} is thrown.
   */
  boolean loginCheck() throws SecurityException;
  
}
