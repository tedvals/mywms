/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

public class LOSCommonPropertyKey {

	/**
	 * The value for the sender of a mail
	 */
	public static final String MAIL_SENDER = "MAIL_SENDER";
	
	/**
	 * The name of the mail-server
	 */
	public static final String MAIL_SERVER = "MAIL_SERVER";
	
	/**
	 * Set this to true, if authorization for sending a mail is required.  
	 */
	public static final String MAIL_AUTHOZIZE = "MAIL_AUTHORIZE";
	
	/**
	 * The authorized user on the mail-server. Only used, if authorization for sending a mail is required.  
	 */
	public static final String MAIL_HOST_USER = "MAIL_HOST_USER";
	
	/**
	 * The password of the user on the mail-server. Only used, if authorization for sending a mail is required.  
	 */
	public static final String MAIL_HOST_PASSWD = "MAIL_HOST_PASSWD";

	
}
