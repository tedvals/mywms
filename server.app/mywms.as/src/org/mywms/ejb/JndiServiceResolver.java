/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.ejb;

import java.util.Properties;

/**
 * Resolves jndi name by a given SessionBean remote interface This
 * interface is used by JndiServiceNameFactory. Implementation is
 * Application Server specific This interface is used by
 * JndiServiceNameFactory. Implementation is Application Server specific
 * 
 * @author Andreas Trautmann
 * @version $Revision: 526 $ provided by $Author: trautm $
 */
public abstract class JndiServiceResolver {

    private Properties properties;

    public JndiServiceResolver(Properties p) {
        this.setProperties(p);
    }

    /**
     * Resolves jndi name by a given SessionBean remote interface
     * 
     * @param remoteSessionBeanInterface
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract String resolve(Class remoteSessionBeanInterface);

    @SuppressWarnings("unchecked")
    public abstract String resolveLocal(Class remoteSessionBeanInterface);
    
    void setProperties(Properties properties) {
        this.properties = properties;
    }

    Properties getProperties() {
        return properties;
    }
}
