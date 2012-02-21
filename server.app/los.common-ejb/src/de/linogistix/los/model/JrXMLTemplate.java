/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.mywms.model.BasicClientAssignedEntity;

/**
 *
 * @author trautm
 */
@Entity
@Table(name="los_jrxml", uniqueConstraints={
    @UniqueConstraint(columnNames={"name","client_id"})
})
public class JrXMLTemplate extends BasicClientAssignedEntity{
    
	private static final long serialVersionUID = 1L;

	private String name;
    
    private byte[] jrXML;

    public byte[] getJrXML() {
        return jrXML;
    }

    public void setJrXML(byte[] jrXML) {
        this.jrXML = jrXML;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toUniqueString() {
        return getName() + ": " + getClient().toUniqueString();
    }
    
    
    
}
