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

import org.apache.log4j.Logger;
import org.mywms.model.BasicEntity;

/**
 *
 * @author trautm
 */
public class DeleteServiceCompositionBean implements DeleteServiceComposition{

    private static final Logger log = Logger.getLogger(DeleteServiceCompositionBean.class);
    
    public void deleteEntity(BasicEntity e) {
        log.error("Just a dummy implementation");
    }

}
