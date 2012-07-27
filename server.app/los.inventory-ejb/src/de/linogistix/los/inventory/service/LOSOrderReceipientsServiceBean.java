package de.linogistix.los.inventory.service;

//import java.math.BigDecimal;
//import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import org.mywms.globals.ServiceExceptionKey;
//import org.mywms.facade.FacadeException;
//import org.mywms.model.ItemData;
import org.mywms.service.BasicServiceBean;
//import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;

//import de.linogistix.los.inventory.exception.InventoryException;
//import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderReceipients;

@Stateless
public class LOSOrderReceipientsServiceBean extends
    BasicServiceBean<LOSOrderReceipients> implements LOSOrderReceipientsService {

    private static final Logger log  = Logger.getLogger(LOSOrderReceipientsServiceBean.class);

    public LOSOrderReceipients create() {

        LOSOrderReceipients rcp = new LOSOrderReceipients();

        manager.persist(rcp);
        manager.flush();

        log.info("CREATED OrderReceipient: " + rcp.toDescriptiveString());
        return rcp;
    }



    @SuppressWarnings("unchecked")
    public LOSOrderReceipients getByIdentityCard(String identityCard) throws EntityNotFoundException {

        Query query =
            manager.createQuery("SELECT su FROM "
                                + LOSOrderReceipients.class.getSimpleName()
                                + " su "
                                + " WHERE su.identity_card= :adId");
        query = query.setParameter("adId", identityCard);

        try {
            LOSOrderReceipients ret = (LOSOrderReceipients) query.getSingleResult();
            return ret;
        } catch (NoResultException ex) {
            throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }
    }

}
