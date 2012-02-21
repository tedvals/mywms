/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.exposed;

import junit.framework.TestCase;

import org.mywms.ejb.BeanLocator;
import org.mywms.facade.SanityCheck;

/**
 * Implements a basic setUp() method, operating the authentication stuff.
 * 
 * @author Olaf Krause
 * @version $Revision: 399 $ provided by $Author: okrause $
 */
public class TestInit 
	extends TestCase 
{

	public static final String LOGIN = "System Admin";
	public static final String PASSWD = "myWMS";
	
	/** Contains the locator for admin access to the myWMS app. */
	protected BeanLocator beanLocator;

	/**
	 * Sets up a bean locator with admin privileges.
	 *  
	 * @see TestCase#setUp()
	 */
	protected void setUp() 
		throws Exception 
	{
		super.setUp();
		//
		beanLocator = new BeanLocator();
		SanityCheck sanityCheck = beanLocator.getStateless(SanityCheck.class);
		sanityCheck.check();
		beanLocator = new BeanLocator(LOGIN,PASSWD);
	}
}
