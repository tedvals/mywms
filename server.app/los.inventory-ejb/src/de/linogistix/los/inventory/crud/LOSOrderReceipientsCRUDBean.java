package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.service.LOSOrderReceipientsService;

@Stateless
public class LOSOrderReceipientsCRUDBean extends BusinessObjectCRUDBean<LOSOrderReceipients> implements LOSOrderReceipientsCRUDRemote {

	@EJB 
	LOSOrderReceipientsService service;
	
	@Override
	protected BasicService<LOSOrderReceipients> getBasicService() {
		
		return service;
	}
	
	@Override
	public LOSOrderReceipients create(LOSOrderReceipients entity)
			throws BusinessObjectExistsException,
			BusinessObjectCreationException, BusinessObjectSecurityException {
		
		if (entity.getIdentityCard() == null || entity.getIdentityCard().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"id card"}, BundleResolver.class);
		if (entity.getFirstName() == null || entity.getFirstName().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"first name"}, BundleResolver.class);
		if (entity.getLastName() == null || entity.getLastName().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"last name"}, BundleResolver.class);
		if (entity.getOrganizationUnit() == null || entity.getOrganizationUnit().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"organization unit"}, BundleResolver.class);
		if (entity.getPhone() == null) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"phone"}, BundleResolver.class);
				
		return super.create(entity);
	}
}
