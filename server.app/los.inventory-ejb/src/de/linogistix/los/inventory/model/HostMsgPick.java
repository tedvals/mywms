package de.linogistix.los.inventory.model;

import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.model.HostMsg;

public class HostMsgPick extends HostMsg{

	private LOSPickRequest pick;
	private String pickNumber;
	
	public HostMsgPick( LOSPickRequest pick ) {
		this.pick = pick;
		this.pickNumber = (pick == null ? "?" : pick.getNumber());
	}

	public LOSPickRequest getPick() {
		return pick;
	}

	public void setPick(LOSPickRequest pick) {
		this.pick = pick;
	}

	public String getPickNumber() {
		return pickNumber;
	}

	public void setPickNumber(String pickNumber) {
		this.pickNumber = pickNumber;
	}

	
}
