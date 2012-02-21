/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mywms.model.Request;
import org.mywms.model.User;

@Entity
@Table(name = "los_outreq")
public class LOSGoodsOutRequest extends Request{
	
	private static final long serialVersionUID = 1L;

	private List<LOSGoodsOutRequestPosition> positions = new ArrayList<LOSGoodsOutRequestPosition>();
	
	private LOSGoodsOutRequestState outState = LOSGoodsOutRequestState.RAW;

	private User operator;
	
	private LOSOrderRequest parentRequest;
	
	public void setPositions(List<LOSGoodsOutRequestPosition> positions) {
		this.positions = positions;
	}
	
	@OneToMany(mappedBy="goodsOutRequest")
	public List<LOSGoodsOutRequestPosition> getPositions() {
		return positions;
	}
//	@Transient
//	public boolean isSolved(){
//		boolean ret = false;
//		
//		for (LOSGoodsOutRequestPosition pos : getPositions()){
//			if (pos.getOutState().equals(LOSGoodsOutRequestPositionState.FINISHED)){
//				ret = true;
//			} else{
//				ret = false;
//				break;
//			}
//		}
//		return ret;
//	}
	
	public void setOutState(LOSGoodsOutRequestState outState) {
		this.outState = outState;
	}
	
	@Enumerated(EnumType.STRING)
	public LOSGoodsOutRequestState getOutState() {
		return outState;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	@ManyToOne
	public User getOperator() {
		return operator;
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
