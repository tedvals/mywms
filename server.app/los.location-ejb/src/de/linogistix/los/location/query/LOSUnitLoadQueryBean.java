/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSUnitLoadQueryBean 
        extends BusinessObjectQueryBean<LOSUnitLoad> 
        implements LOSUnitLoadQueryRemote
{
	
	private static final Logger logger = Logger.getLogger(LOSUnitLoadQueryBean.class);
	
	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"labelId",
		"lock",
		"storageLocation.name"
	};
	
	@Override
	public String getUniqueNameProp() {
		
		return "labelId";
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<UnitLoadTO> getBODTOClass() {
		return UnitLoadTO.class;
	}

	public List<BODTO<LOSUnitLoad>> queryAllEmpty(QueryDetail qd) {
		
		TemplateQueryWhereToken t = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_ISEMPTY, "stockUnitList", "");

		TemplateQuery q = new TemplateQuery();
		q.addWhereToken(t);
		q.setBoClass(LOSUnitLoad.class);
		try {
			return queryByTemplateHandles(qd, q);
		} catch (BusinessObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			return new ArrayList<BODTO<LOSUnitLoad>>();
		} catch (BusinessObjectQueryException e) {
			logger.error(e.getMessage(), e);
			return new ArrayList<BODTO<LOSUnitLoad>>();
		}
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		TemplateQueryWhereToken storageLocation = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "storageLocation.name",
				value);
		storageLocation.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(storageLocation);
		
		TemplateQueryWhereToken labelId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
				value);
		labelId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(labelId);
		
		
		
		return ret;
	}

}
