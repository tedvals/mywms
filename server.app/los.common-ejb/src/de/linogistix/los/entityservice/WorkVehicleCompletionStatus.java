package de.linogistix.los.entityservice;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.linogistix.los.res.BundleResolver;

public enum WorkVehicleCompletionStatus implements WorkVehicleComplete{
	NOT_SUCCESSFUL(false),
	SUCCESSFUL(true);
	
	//int lock;
	boolean status;
	
	WorkVehicleCompletionStatus(boolean status){
		this.status = status;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public static WorkVehicleComplete resolve(boolean status){
		if(status==true)
		return SUCCESSFUL;
		else
		return NOT_SUCCESSFUL;
		//switch (status){
		//case true: return NOT_SUCCESSFUL;
		//case false: return SUCCESSFUL;
		//default: throw new IllegalArgumentException();
		//}
	}

	public String getMessage() {
		
	       String key = getMessageKey();

	        ResourceBundle bundle;
	        String formatString = key;
	        try {
	            // check for bundle
	            bundle = ResourceBundle.getBundle("Bundle",  Locale.getDefault(),getBundleResolver().getClassLoader());
	            // resolving key
	            formatString = bundle.getString(key);
	            return formatString;
	        }
	        catch (MissingResourceException ex) {
	            return name() + "(" + status + ")";
	        }
	}

	public String getMessageKey(){
		return this.name() + "_" + status;
	}
	
	public Class<BundleResolver> getBundleResolver() {
		return BundleResolver.class;
	}

	
}
