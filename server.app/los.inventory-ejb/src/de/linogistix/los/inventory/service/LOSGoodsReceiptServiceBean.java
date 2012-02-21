/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.UserService;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.util.BusinessObjectHelper;

@Stateless
public class LOSGoodsReceiptServiceBean
        extends BasicServiceBean<LOSGoodsReceipt>
        implements LOSGoodsReceiptService {

    @Resource
    SessionContext ctx;
    @EJB
    UserService userService;
    @Resource
    EJBContext context;

    public LOSGoodsReceipt createGoodsReceipt(Client client, String number) {

        
        User  operator = new BusinessObjectHelper(this.ctx,this.userService,this.context).getCallersUser();
		
        LOSGoodsReceipt grr = new LOSGoodsReceipt();
        grr.setClient(client);
        grr.setGoodsReceiptNumber(number);
        grr.setOperator(operator);

        manager.persist(grr);

        return grr;
    }

    public LOSGoodsReceipt getByGoodsReceiptNumber(String number) {

        Query query = manager.createQuery(
                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
                "WHERE gr.goodsReceiptNumber=:n");

        query.setParameter("n", number);

        try {
            return (LOSGoodsReceipt) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;

        }
    }
    

	public void delete(LOSGoodsReceipt r){
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		List<LOSGoodsReceiptPosition> pos = r.getPositionList();
		for (LOSGoodsReceiptPosition p : pos){
			p = manager.find(LOSGoodsReceiptPosition.class, p.getId());
			if (p != null){
				manager.remove(p);
			}
		}
		manager.remove(r);
		manager.flush();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceipt> getByAdvice( LOSAdvice adv ) {
        Query query = manager.createQuery(
                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
                "WHERE :adv MEMBER OF gr.assignedAdvices");

        query.setParameter("adv", adv);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
		
	}

}
