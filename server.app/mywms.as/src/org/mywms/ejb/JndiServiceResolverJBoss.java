/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.ejb;

import java.util.Properties;

/**
 * Jndi service name resolution for JBoss
 * 
 * @author Olaf Krause
 * @version $Revision: 526 $ provided by $Author: trautm $
 */
public class JndiServiceResolverJBoss
    extends JndiServiceResolver
{

    public JndiServiceResolverJBoss(Properties p) {
        super(p);
    }

    /**
     * A typical JBoss jndi name is
     * <code>[applicationName]/remoteSessionBeanInterfaceSimpleName/remote</code>
     */
    @SuppressWarnings("unchecked")
    public String resolve(Class remoteSessionBeanInterface) {
        String s;
        String jndiName;

        s =
            remoteSessionBeanInterface.getSimpleName().replaceFirst(
                "Remote",
                "");
        jndiName =
            getProperties().getProperty("org.mywms.env.applicationName")
                + "/"
                + s
                + "Bean/remote";
        return jndiName;
    }
    
    /**
     * A typical JBoss jndi name is
     * <code>[applicationName]/remoteSessionBeanInterfaceSimpleName/remote</code>
     */
    @SuppressWarnings("unchecked")
    public String resolveLocal(Class remoteSessionBeanInterface) {
        String s;
        String jndiName;

        s =
            remoteSessionBeanInterface.getSimpleName().replaceFirst(
                "Remote",
                "");
        jndiName =
            getProperties().getProperty("org.mywms.env.applicationName")
                + "/"
                + s
                + "Bean/local";
        return jndiName;
    }

}
