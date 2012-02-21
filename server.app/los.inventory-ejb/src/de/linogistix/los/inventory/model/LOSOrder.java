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

package de.linogistix.los.inventory.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_order")
public class LOSOrder extends LOSOrderRequest{
    
	private static final long serialVersionUID = 1L;
    
}
