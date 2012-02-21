/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Copyright (c) 2006-2008 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.customization;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 *
 * @author trautm
 */
public class ImportDataServiceDispatcherBean implements ImportDataServiceDispatcher{
    
    private static final Logger log = Logger.getLogger(ImportDataServiceDispatcherBean.class);

    public Object handleDataRecord(String className, HashMap<String, String> dataRecord) {
        log.error("Just a dummy implementation");
        return null;
    }
}
