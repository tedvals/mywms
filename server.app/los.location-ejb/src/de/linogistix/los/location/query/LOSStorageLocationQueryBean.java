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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSStorageLocationQueryBean 
        extends BusinessObjectQueryBean<LOSStorageLocation> 
        implements LOSStorageLocationQueryRemote
{

	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"name",
		"lock",
		"reservedCapacity"
	};

	@EJB
	LOSStorageLocationService slService;
	
    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return dtoProps;
    }
    
    @Override
	public Class<StorageLocationTO> getBODTOClass() {
    	return StorageLocationTO.class;
    }

	public LOSResultList<BODTO<LOSStorageLocation>> autoCompletionClientAndAreaType(String searchString, 
											BODTO<Client> clientTO, 
											LOSAreaType areaType,
											QueryDetail detail) 
	{
		Client client;
		
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		if(clientTO != null){
			client = manager.find(Client.class, clientTO.getId());
		}
		else{
			client = clientService.getSystemClient();
		}
		
		if(areaType != null){
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area.areaType", areaType));
		}
		
		return autoCompletion(searchString, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
	}

	public LOSStorageLocation getClearing() {
		return slService.getClearing();
	}

	public LOSStorageLocation getNirwana() {
		return slService.getNirwana();
	}
	
	public String getNirwanaName(){
		return getNirwana().getName();
	}
	
}
