/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.exception;

/**
 *
 * @author artur
 */
public class ErrorOccurredException extends Exception {

    /**
     * Only needed to flag something that goings wrong. e.g. by filling not all fields
     * 
     */
    public ErrorOccurredException() {
    }


    /**
     * Constructs an instance of <code>ErrorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ErrorOccurredException(String msg) {
        super(msg);
    }
}
