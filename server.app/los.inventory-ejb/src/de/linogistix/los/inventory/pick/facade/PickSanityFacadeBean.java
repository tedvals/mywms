/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.LogItem;

import de.linogistix.los.inventory.pick.businessservice.PickOrderBusiness;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

public class PickSanityFacadeBean implements PickSanityFacade {

	private static final Logger log = Logger
			.getLogger(PickSanityFacadeBean.class);

	@EJB
	PickOrderBusiness pickOrderBusiness;
	@EJB
	LOSPickRequestQueryRemote pickQuery;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public List<LogItem> sanityCheck() {

		List<BODTO<LOSPickRequest>> picks;
		List<LogItem> ret = new ArrayList<LogItem>();

		try {
			picks = pickQuery.queryZombies(new QueryDetail(0, Integer.MAX_VALUE));

			for (BODTO<LOSPickRequest> dto : picks) {
				LOSPickRequest r = manager.find(LOSPickRequest.class, dto.getId());
				log.error("Found zombie PickRequest: " + r.toUniqueString());
			}

		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage(), e);
		}

		return ret;

	}

}
