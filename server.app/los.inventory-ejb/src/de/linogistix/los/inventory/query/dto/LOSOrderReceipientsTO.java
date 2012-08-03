package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.query.BODTO;

public class LOSOrderReceipientsTO extends BODTO<LOSOrderReceipients> {

	private static final long serialVersionUID = 1L;

	private String personnelId;
	private String firstName;
	private String lastName;
	private String rankAbbr;
	private String rank;
	private String organizationUnit;
	private BigDecimal phone;
	private BigDecimal vpnPhone;
	private String tokenId;
	
	public LOSOrderReceipientsTO( LOSOrderReceipients rec ) {
		super(rec.getId(), rec.getVersion(), rec.getIdentityCard());
		this.personnelId	 = rec.getPersonnelId();     
		this.firstName		 = rec.getFirstName();       
		this.lastName		 = rec.getLastName();        
		this.rankAbbr		 = rec.getRankAbbr();        
		this.rank		 = rec.getRank();            
		this.organizationUnit	 = rec.getOrganizationUnit();
		this.phone		 = rec.getPhone();           
		this.vpnPhone		 = rec.getVpnPhone();        
		this.tokenId		 = rec.getTokenId();         
	}
	
	public LOSOrderReceipientsTO(Long id, int version, String name) {
		super(id, version, name);
	}
	
	public LOSOrderReceipientsTO(Long id, int version, String name,
		String personnelId, String firstName, String lastName, String rankAbbr, String rank, String organizationUnit,
		BigDecimal phone, BigDecimal vpnPhone, String tokenId) {
		super(id, version, name);
		this.personnelId	= personnelId;
		this.firstName		= firstName;
		this.lastName		= lastName;
		this.rankAbbr		= rankAbbr;
		this.rank		= rank;
		this.organizationUnit	= organizationUnit;
		this.phone		= phone;
		this.vpnPhone		= vpnPhone;
		this.tokenId		= tokenId;
	}
	
	/**
	 * Get personnelId.
	 *
	 * @return personnelId as String.
	 */
	public String getPersonnelId()
	{
	    return personnelId;
	}
	
	/**
	 * Set personnelId.
	 *
	 * @param personnelId the value to set.
	 */
	public void setPersonnelId(String personnelId)
	{
	    this.personnelId = personnelId;
	}
	
	/**
	 * Get firstName.
	 *
	 * @return firstName as String.
	 */
	public String getFirstName()
	{
	    return firstName;
	}
	
	/**
	 * Set firstName.
	 *
	 * @param firstName the value to set.
	 */
	public void setFirstName(String firstName)
	{
	    this.firstName = firstName;
	}
	
	/**
	 * Get lastName.
	 *
	 * @return lastName as String.
	 */
	public String getLastName()
	{
	    return lastName;
	}
	
	/**
	 * Set lastName.
	 *
	 * @param lastName the value to set.
	 */
	public void setLastName(String lastName)
	{
	    this.lastName = lastName;
	}
	
	/**
	 * Get rankAbbr.
	 *
	 * @return rankAbbr as String.
	 */
	public String getRankAbbr()
	{
	    return rankAbbr;
	}
	
	/**
	 * Set rankAbbr.
	 *
	 * @param rankAbbr the value to set.
	 */
	public void setRankAbbr(String rankAbbr)
	{
	    this.rankAbbr = rankAbbr;
	}
	
	/**
	 * Get rank.
	 *
	 * @return rank as String.
	 */
	public String getRank()
	{
	    return rank;
	}
	
	/**
	 * Set rank.
	 *
	 * @param rank the value to set.
	 */
	public void setRank(String rank)
	{
	    this.rank = rank;
	}
	
	/**
	 * Get organizationUnit.
	 *
	 * @return organizationUnit as String.
	 */
	public String getOrganizationUnit()
	{
	    return organizationUnit;
	}
	
	/**
	 * Set organizationUnit.
	 *
	 * @param organizationUnit the value to set.
	 */
	public void setOrganizationUnit(String organizationUnit)
	{
	    this.organizationUnit = organizationUnit;
	}
	
	/**
	 * Get phone.
	 *
	 * @return phone as BigDecimal.
	 */
	public BigDecimal getPhone()
	{
	    return phone;
	}
	
	/**
	 * Set phone.
	 *
	 * @param phone the value to set.
	 */
	public void setPhone(BigDecimal phone)
	{
	    this.phone = phone;
	}
	
	/**
	 * Get vpnPhone.
	 *
	 * @return vpnPhone as BigDecimal.
	 */
	public BigDecimal getVpnPhone()
	{
	    return vpnPhone;
	}
	
	/**
	 * Set vpnPhone.
	 *
	 * @param vpnPhone the value to set.
	 */
	public void setVpnPhone(BigDecimal vpnPhone)
	{
	    this.vpnPhone = vpnPhone;
	}
	
	/**
	 * Get tokenId.
	 *
	 * @return tokenId as String.
	 */
	public String getTokenId()
	{
	    return tokenId;
	}
	
	/**
	 * Set tokenId.
	 *
	 * @param tokenId the value to set.
	 */
	public void setTokenId(String tokenId)
	{
	    this.tokenId = tokenId;
	}
}
