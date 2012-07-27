package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface LOSOrderReceipientsQueryRemote extends BusinessObjectQueryRemote<LOSOrderReceipients>{ 
	//public LOSResultList<BODTO<LOSOrderReceipients>> queryByDefault( String master, String child, QueryDetail detail ) throws BusinessObjectNotFoundException, BusinessObjectQueryException;

}
