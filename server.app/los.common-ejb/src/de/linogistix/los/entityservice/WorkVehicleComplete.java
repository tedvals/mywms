package de.linogistix.los.entityservice;

import java.io.Serializable;

public interface WorkVehicleComplete extends Serializable{
	
	boolean getStatus();
	
	String getMessage();
	
	@SuppressWarnings("unchecked")
	Class getBundleResolver();
	
	String getMessageKey();
	
}
