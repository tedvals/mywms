/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report.businessservice;

public final class LabelTO {
	private String label;

	public LabelTO(String label2) {
		this.label = label2;
	}
	
	public LabelTO() {
		this.label = null;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
