/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mywms.model.Lot;


@Entity
@Table(name = "los_extinguishreq")
public class ExtinguishRequest extends LOSPickRequest {

	private static final long serialVersionUID = 1L;

	private Lot lot;

	@OneToOne
	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

}
