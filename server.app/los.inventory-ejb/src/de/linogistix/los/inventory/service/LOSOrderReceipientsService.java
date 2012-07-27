package de.linogistix.los.inventory.service;

//import java.math.BigDecimal;
//import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.BusinessException;
//import org.mywms.model.ItemData;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;
//import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.inventory.model.LOSOrderReceipients;

@Local
public interface LOSOrderReceipientsService extends BasicService<LOSOrderReceipients> {
	
	public LOSOrderReceipients create();
	
	public LOSOrderReceipients getByIdentityCard(String identityCard) throws EntityNotFoundException;

}
