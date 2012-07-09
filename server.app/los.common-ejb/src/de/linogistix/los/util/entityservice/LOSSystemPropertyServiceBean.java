/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.model.LOSSystemProperty;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSSystemPropertyServiceBean extends BasicServiceBean<LOSSystemProperty>
										  implements LOSSystemPropertyService, LOSSystemPropertyServiceRemote
{

	@EJB
	ContextService ctxService;
	@EJB
	QueryClientService clientService;
	
	public LOSSystemProperty createSystemProperty(String key, String value) {
		return createSystemProperty(null, null, key, value, null, null, false, false);
	}
	public LOSSystemProperty createSystemProperty(Client client, String workstation, String key, String value, String groupName, String description, boolean hidden, boolean reinitialize) {

		if( client == null ) {
			client = ctxService.getCallersClient();
		}

		if( workstation == null || workstation.length()==0 ) {
			workstation = LOSSystemProperty.WORKSTATION_DEFAULT;
		}

		if( value == null ) {
			value = "null_val";
		}

		LOSSystemProperty sysProp = getByKey(client, workstation, key);
		if( sysProp == null ) {
			sysProp = new LOSSystemProperty();
			sysProp.setClient(client);
			sysProp.setWorkstation(workstation);
			sysProp.setKey(key);
			
			sysProp.setGroupName(groupName);
			sysProp.setDescription(description);
			sysProp.setHidden(hidden);
			sysProp.setValue(value);
			
			manager.persist(sysProp);
		}
		else if( reinitialize ) {
			sysProp.setGroupName(groupName);
			sysProp.setDescription(description);
			sysProp.setHidden(hidden);
			sysProp.setValue(value);
		}
		
		
		return sysProp;
	}

	
	
	public LOSSystemProperty getByKey(String key) {
		return getByKey(null, null, key);
	}
	public LOSSystemProperty getByKey(Client client, String workstation, String key) {
		if( client == null ) {
			client = ctxService.getCallersClient();
		}
		if( workstation == null || workstation.length()==0 ) {
			workstation = LOSSystemProperty.WORKSTATION_DEFAULT;
		}
		
		Query query = manager.createQuery(
							"SELECT sp FROM "+LOSSystemProperty.class.getSimpleName()+" sp "+
							"WHERE sp.key=:key and workstation=:workstation and client = :client");
		
		query.setParameter("key", key);
		query.setParameter("workstation", workstation);
		query.setParameter("client", client);
		
		try{
			return (LOSSystemProperty) query.getSingleResult();
		}catch(NoResultException ne){
			return null;
		}
	}


	public boolean getBoolean( String key ) {
		return getBooleanDefault(null, null, key, false);
	}
	public boolean getBoolean( String workstation, String key ) {
		return getBooleanDefault(null, workstation, key, false);
	}
	public boolean getBoolean( Client client, String workstation, String key ) {
		return getBooleanDefault(client, workstation, key, false);
	}
	public boolean getBooleanDefault( String key, boolean defaultValue ) {
		return getBooleanDefault(null, null, key, defaultValue);
	}
	public boolean getBooleanDefault( String workstation, String key, boolean defaultValue ) {
		return getBooleanDefault(null, workstation, key, defaultValue);
	}
	public boolean getBooleanDefault( Client client, String workstation, String key, boolean defaultValue ) {
		String valueS = getStringDefault(client, workstation, key, String.valueOf(defaultValue) );
		
		valueS = valueS.toLowerCase();
		if( "1".equals(valueS) ) {
			return true;
		}
		else if( "true".equals(valueS) ) {
			return true;
		}
		else if( "yes".equals(valueS) ) {
			return true;
		}

		return false;
	}
	
	
	
	public long getLong( String key ) {
		return getLongDefault(null, null, key, 0);
	}
	public long getLong( String workstation, String key ) {
		return getLongDefault(null, workstation, key, 0);
	}
	public long getLong( Client client, String workstation, String key ) {
		return getLongDefault(client, workstation, key, 0);
	}
	public long getLongDefault( String key, long defaultValue ) {
		return getLongDefault(null, null, key, defaultValue);
	}
	public long getLongDefault( String workstation, String key, long defaultValue ) {
		return getLongDefault(null, workstation, key, defaultValue);
	}
	public long getLongDefault( Client client, String terminal, String key, long defaultValue ) {
		String valueS = getStringDefault(client, terminal, key, String.valueOf(defaultValue) );

		long valueL = defaultValue;
		try {
			valueL = Long.valueOf(valueS);
		}
		catch( NumberFormatException e ) {
			valueL = defaultValue;
		}
		
		return valueL;
	}

	
	public String getString( String key ) {
		return getStringDefault( null, null, key, null );
	}
	public String getString( String workstation, String key ) {
		return getStringDefault( null, workstation, key, null );
	}
	public String getString( Client client, String workstation, String key ) {
		return getStringDefault( client, workstation, key, null );
	}
	public String getStringDefault( String key, String defaultValue ) {
		return getStringDefault( null, LOSSystemProperty.WORKSTATION_DEFAULT, key, defaultValue );
	}
	public String getStringDefault( String workstation, String key, String defaultValue ) {
		return getStringDefault(null, workstation, key, defaultValue);
	}
	public String getStringDefault( Client client, String workstation, String key, String defaultValue ) {
		if( client == null ) {
			client = ctxService.getCallersClient();
		}
		if( workstation == null || workstation.length()==0 ) {
			workstation = LOSSystemProperty.WORKSTATION_DEFAULT;
		}

		String value = null;
		
		Query query = manager.createQuery(
				"SELECT sp.value FROM "+LOSSystemProperty.class.getSimpleName()+" sp "+
				"WHERE sp.key=:key and sp.workstation=:workstation and client=:client");

		query.setParameter("key", key);
		query.setParameter("workstation", workstation);
		query.setParameter("client", client);

		try{
			value = (String)query.getSingleResult();
		}
		catch(NoResultException ne){
			if( ! client.isSystemClient() ) {
				return getStringDefault(clientService.getSystemClient(), workstation, key, defaultValue);
			}
			else if( ! LOSSystemProperty.WORKSTATION_DEFAULT.equals(workstation) ) {
				return getStringDefault(client, LOSSystemProperty.WORKSTATION_DEFAULT, key, defaultValue);
			}
			else {
				createSystemProperty(client, workstation, key, defaultValue, null, null, false, false);
				value = defaultValue;
			}
		}

		return value == null ? null : value.trim();
	}

	
	public void setValue(String key, String value) throws UnAuthorizedException {
		setValue(null, null, key, value);
	}
	public void setValue(String workstation, String key, String value) throws UnAuthorizedException {
		setValue(null, workstation, key, value);
	}
	public void setValue(Client client, String workstation, String key, String value) throws UnAuthorizedException {
		if( client == null ) {
			client = ctxService.getCallersClient();
		}
		if( workstation == null ) {
			workstation = LOSSystemProperty.WORKSTATION_DEFAULT;
		}
		if( value == null ) { 
			value = "";
		}
		
		LOSSystemProperty prop = getByKey(client, workstation, key);
		
		if( prop == null ) {
			createSystemProperty(client, workstation, key, value, null, null, false, false);
			return;
		}
		
		if( !ctxService.getCallersClient().isSystemClient() && client.isSystemClient() ) {
			throw new UnAuthorizedException();
		}
		if( !value.equals(prop.getValue()) ) {
			prop.setValue(value);
		}
	}

	public void setValue(String key, boolean value) throws UnAuthorizedException {
		setValue( null, null, key, Boolean.toString(value) );
	}
	public void setValue(String terminal, String key, boolean value) throws UnAuthorizedException {
		setValue( null, terminal, key, Boolean.toString(value) );
	}
	public void setValue(Client client, String terminal, String key, boolean value) throws UnAuthorizedException {
		setValue( client, terminal, key, Boolean.toString(value) );
	}
	
	public void setValue( String key, long value ) throws UnAuthorizedException {
		setValue( null, null, key, Long.toString(value) );
	}
	public void setValue( String terminal, String key, long value ) throws UnAuthorizedException {
		setValue( null, terminal, key, Long.toString(value) );
	}
	public void setValue( Client client, String terminal, String key, long value ) throws UnAuthorizedException {
		setValue( client, terminal, key, Long.toString(value) );
	}

}
