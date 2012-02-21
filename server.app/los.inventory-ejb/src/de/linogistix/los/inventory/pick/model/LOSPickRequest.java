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

package de.linogistix.los.inventory.pick.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.mywms.globals.PickingRequestState;
import org.mywms.model.Request;
import org.mywms.model.User;

import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * 
 * @author trautm
 */
@Entity
@Table(name = "los_pickreq")
public class LOSPickRequest extends Request{

	private static final long serialVersionUID = 1L;

	private LOSOrderRequest parentRequest;
	
	private LOSStorageLocation cart;

	private LOSStorageLocation destination;	
	
	private PickingRequestState state = PickingRequestState.RAW;

    private String customerNumber;

    private User user;
    
    private List<LOSPickRequestPosition> positions;
    
	public void setCart(LOSStorageLocation sl) {
		this.cart = sl;
	}

	@OneToOne
	public LOSStorageLocation getCart() {
		return cart;
	}

	public void setDestination(LOSStorageLocation destination) {
		this.destination = destination;
	}

	@ManyToOne(optional = false)
	public LOSStorageLocation getDestination() {
		return destination;
	}

	@Enumerated(EnumType.STRING)
	public PickingRequestState getState() {
		return state;
	}

	public void setState(PickingRequestState state) {
		this.state = state;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@OneToMany(mappedBy="pickRequest")
	@OrderBy("index")
	public List<LOSPickRequestPosition> getPositions(){
		return positions;		
	}
	
	public void setPositions(List<LOSPickRequestPosition> positions){
		this.positions = positions;
	}
	
	boolean indexedPositions(List<LOSPickRequestPosition> pos){
		Set<Integer> s = new HashSet<Integer>();
		
		if (pos == null){
			return false;
		}
		
		for (LOSPickRequestPosition p : pos){
			if (s.contains(p.getIndex())){
				return false;
			} else{
				s.add(p.getIndex());
			}
		}
		
		return true;
	}

	@ManyToOne(optional=false)
	public LOSOrderRequest getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(LOSOrderRequest parentRequest) {
		this.parentRequest = parentRequest;
		if (this.parentRequest != null){
			setParentRequestNumber(parentRequest.getNumber());
        }
	}



}
