package de.linogistix.los.inventory.query;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.WorkType;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface WorkTypeQueryRemote extends BusinessObjectQueryRemote<WorkType>{ 
	//public LOSResultList<WorkType> queryByWorkType(QueryDetail d, String vdId) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
}
