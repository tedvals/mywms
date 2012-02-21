/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mywms.model.Lot;
import org.mywms.model.User;

@Entity
@Table(name = "los_extorder")
public class ExtinguishOrder extends LOSOrderRequest {

	private static final long serialVersionUID = 1L;

	private User authorizedBy;

	private Lot lot;

	@OneToOne
	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public void setAuthorizedBy(User authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	@ManyToOne
	public User getAuthorizedBy() {
		return authorizedBy;
	}

}
