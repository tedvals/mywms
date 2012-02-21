/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.service;

import org.mywms.globals.ServiceExceptionKey;

/**
 * This exception wraps the exception key. The key can be resolved to a
 * localized message.
 * 
 * @author Markus Jordan
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class ServiceException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private ServiceExceptionKey messageKey;

    /**
     * Creates a new ServiceException instance.
     * 
     * @param messageKey the ressource key of the exception
     */
    public ServiceException(ServiceExceptionKey messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Returns the ressource key of the message of the exception.
     * 
     * @return the exception's ressource key
     */
    public ServiceExceptionKey getMessageKey() {
        return messageKey;
    }
}
