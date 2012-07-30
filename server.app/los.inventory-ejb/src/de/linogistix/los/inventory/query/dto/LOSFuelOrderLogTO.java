package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.query.BODTO;

public class LOSFuelOrderLogTO extends BODTO<LOSFuelOrderLog> {

	private static final long serialVersionUID = 1L;
	
	//private OrderReceiptPosition rcptPosId;
	private String storageLocation;
	private int stationPump;
	private String orderReceipient;
	private String orderType;
	private BigDecimal tankRemaining;
		
	public LOSFuelOrderLogTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public LOSFuelOrderLogTO(Long id, int version, String name, 
		String storageLocation, int stationPump, String orderReceipient, String orderType, BigDecimal tankRemaining){
		super(id, version, name);
		this.storageLocation	= storageLocation;
		this.stationPump	= stationPump;
		this.orderReceipient	= orderReceipient;
		this.orderType	= orderType;
		this.tankRemaining	= tankRemaining;
	}

	
	/**
	 * Get storageLocation.
	 *
	 * @return storageLocation as String.
	 */
	public String getStorageLocation()
	{
	    return storageLocation;
	}
	
	/**
	 * Set storageLocation.
	 *
	 * @param storageLocation the value to set.
	 */
	public void setStorageLocation(String storageLocation)
	{
	    this.storageLocation = storageLocation;
	}
	
	/**
	 * Get stationPump.
	 *
	 * @return stationPump as int.
	 */
	public int getStationPump()
	{
	    return stationPump;
	}
	
	/**
	 * Set stationPump.
	 *
	 * @param stationPump the value to set.
	 */
	public void setStationPump(int stationPump)
	{
	    this.stationPump = stationPump;
	}
	
	/**
	 * Get orderReceipient.
	 *
	 * @return orderReceipient as String.
	 */
	public String getOrderReceipient()
	{
	    return orderReceipient;
	}
	
	/**
	 * Set orderReceipient.
	 *
	 * @param orderReceipient the value to set.
	 */
	public void setOrderReceipient(String orderReceipient)
	{
	    this.orderReceipient = orderReceipient;
	}
	
	/**
	 * Get orderType.
	 *
	 * @return orderType as String.
	 */
	public String getOrderType()
	{
	    return orderType;
	}
	
	/**
	 * Set orderType.
	 *
	 * @param orderType the value to set.
	 */
	public void setOrderType(String orderType)
	{
	    this.orderType = orderType;
	}
	
	/**
	 * Get tankRemaining.
	 *
	 * @return tankRemaining as BigDecimal.
	 */
	public BigDecimal getTankRemaining()
	{
	    return tankRemaining;
	}
	
	/**
	 * Set tankRemaining.
	 *
	 * @param tankRemaining the value to set.
	 */
	public void setTankRemaining(BigDecimal tankRemaining)
	{
	    this.tankRemaining = tankRemaining;
	}
}
