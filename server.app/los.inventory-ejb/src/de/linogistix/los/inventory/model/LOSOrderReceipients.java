package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

@Entity
@Table(name = "los_orderreceipients", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {
				"identityCard", "tokenId"}) })
public class LOSOrderReceipients extends BasicEntity {
	
	private static final long serialVersionUID = 1L;

	private String personnelId;
	private String identityCard;
	private String firstName;
	private String lastName;
	private String rankAbbr;
	private String rank;
	private String organizationUnit;
	private BigDecimal phone;
	private BigDecimal vpnPhone;
	private String tokenId;
	
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
	 * Get identityCard.
	 *
	 * @return identityCard as String.
	 */
	@Column(nullable=false)
	public String getIdentityCard()
	{
	    return identityCard;
	}
	
	/**
	 * Set identityCard.
	 *
	 * @param identityCard the value to set.
	 */
	public void setIdentityCard(String identityCard)
	{
	    this.identityCard = identityCard;
	}
	
	/**
	 * Get firstName.
	 *
	 * @return firstName as String.
	 */
	@Column(nullable=false)
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
	@Column(nullable=false)
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
	@Column(nullable=false)
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
	@Column(nullable=false, precision=19)
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
	@Column(precision=19)
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
	@Column(nullable=false)
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
