/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import org.mywms.model.VehicleData;

import de.linogistix.los.query.BODTO;

public class VehicleDataTO extends BODTO<VehicleData> {

	private static final long serialVersionUID = 1L;

	// private String remarks = "";
	// private String manufacturerName = "";
	// private String modelName = "";
	private String plateNumber;
	private String chassisNumber;
	private String engineNumber;
	private Long id;

	// private Date receiptDate;
	// private Date storageDate;
	// private BigDecimal mileage;

	// private String nameX;
	// private String number;
	// private String clientNumber;
	// private String itemUnitName;
	// private int scale;

	public VehicleDataTO(VehicleData idat) {
		super(idat.getId(), idat.getVersion(), idat.getId());
		this.plateNumber = idat.getPlateNumber();
		this.chassisNumber = idat.getChassisNumber();
		this.engineNumber = idat.getEngineNumber();
	}

	public VehicleDataTO(Long id, int version, String number) {
		super(id, version, number);
	}

	public VehicleDataTO(Long id, int version, String number,
			String plateNumber, String chassisNumber, String engineNumber) {
		super(id, version, number);
		this.plateNumber = plateNumber;
		this.chassisNumber = chassisNumber;
		this.engineNumber = engineNumber;
	}

	public VehicleDataTO(Long id, String plateNumber, String chassisNumber,
			String engineNumber) {
		// super(id, version, id);
		this.id = id;
		this.plateNumber = plateNumber;
		this.chassisNumber = chassisNumber;
		this.engineNumber = engineNumber;
	}

	public VehicleDataTO(String plateNumber, String chassisNumber,
			String engineNumber) {
		// super(id, version, id);
		this.plateNumber = plateNumber;
		this.chassisNumber = chassisNumber;
		this.engineNumber = engineNumber;
	}

	// public VehicleDataTO(){}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}
}
