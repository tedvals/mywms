/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.ejb;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.transaction.UserTransaction;

/**
 * The locator creates remote instances of session beans, served by the
 * application server. This implementation makes use of jndi.properties.
 * For application server specific configuration you might want to have
 * a look at appserver.properties. Name resolution is delegated to an
 * implementation of JndiServiceResolver. This class has been founded
 * with the friendly support of Rene Preissel, tutor at OOSE.
 * 
 * @see JndiServiceResolver
 * @see JndiServiceResolverJBoss
 * @version $Revision: 632 $ provided by $Author: mkrane $
 */
public class BeanLocator
    implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private static final String JNDI_NAME_USER_TRANSACTION =
        "java:comp/UserTransaction";
    private static final String JNDI_NAME_CONNECTION_FACTORY =
        "ConnectionFactory";

    private transient InitialContext initialContext;
    private transient Map<String, Object> statelessCache =
        new HashMap<String, Object>();
    private transient QueueConnectionFactory connectionFactory;

    private Properties initialContextProperties;

    private JndiServiceResolver jndiServiceResolver;

    private Properties appserverProperties;

    /**
     * Creates a new instance of BeanLocator.
     */
    public BeanLocator() {
        this(null);
    }

    /**
     * Creates a new instance of BeanLocator.
     * 
     * @param user the user account, used to connect to the application
     *            server
     * @param passwd the password of the user account
     */
    public BeanLocator(String user, String passwd) {

        checkJndiContext();

        if (user != null) {
            initialContextProperties.put(Context.SECURITY_PRINCIPAL, user);
            if (passwd != null) {
                initialContextProperties.put(
                    Context.SECURITY_CREDENTIALS,
                    passwd);
            }
        }

        initJndiServiceResolver();
    }

    /**
     * Creates a new instance of BeanLocator.
     * 
     * @param user the user account, used to connect to the application
     *            server
     * @param passwd the password of the user account
     */
    public BeanLocator(
        String user,
        String passwd,
        Properties jndiProps,
        Properties appserverProps)
    {

        if (appserverProps != null) {
            this.appserverProperties = appserverProps;
        }

        if (jndiProps != null) {
            initialContextProperties = jndiProps;
        }
        checkJndiContext();

        if (user != null) {
            // System.out.println("---- Setting " +
            // Context.SECURITY_PRINCIPAL + "=" + user);
            initialContextProperties.put(Context.SECURITY_PRINCIPAL, user);
            if (passwd != null) {
                initialContextProperties.put(
                    Context.SECURITY_CREDENTIALS,
                    passwd);
            }
            // System.out.println("---- Setting " +
            // Context.INITIAL_CONTEXT_FACTORY );
            initialContextProperties.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.security.jndi.JndiLoginInitialContextFactory");
        }

        initJndiServiceResolver();
    }

    /**
     * Creates a new instance of BeanLocator.
     * 
     * @param jndiProperties the properties to use
     */
    public BeanLocator(Properties jndiProperties) {
        if (jndiProperties != null) {
            initialContextProperties = jndiProperties;
        }
        checkJndiContext();
        
        initialContextProperties.put(
            Context.INITIAL_CONTEXT_FACTORY,
            "org.jnp.interfaces.NamingContextFactory");
        initJndiServiceResolver();
    }

    /**
     * Creates a new instance of BeanLocator.
     * 
     * @param jndiProperties the properties to use
     * @param appserverProperties the properties to use
     */
    public BeanLocator(Properties jndiProperties, Properties appserverProperties)
    {

        if (appserverProperties != null) {
            this.appserverProperties = appserverProperties;
        }

        if (jndiProperties != null) {
            initialContextProperties = jndiProperties;
        }
        checkJndiContext();

        initialContextProperties.put(
            Context.INITIAL_CONTEXT_FACTORY,
            "org.jnp.interfaces.NamingContextFactory");
        initJndiServiceResolver();
    }

    
    /**
     * Reads from jndi.properties file in default package
     */
    private void checkJndiContext() {
    	if( initialContextProperties == null ) {
            initialContextProperties = new Properties();
    	}
    	
    	String p = initialContextProperties.getProperty("java.naming.factory.url.pkgs");
    	if( p == null ) {
            p = System.getProperty("java.naming.factory.url.pkgs");
    		initialContextProperties.put("java.naming.factory.url.pkgs", p == null ? "org.jboss.naming:org.jnp.interfaces" : p);
    	}
    	
    	p = initialContextProperties.getProperty("java.naming.factory.initial");
    	if( p == null ) {
            p = System.getProperty("java.naming.factory.initial");
			initialContextProperties.put("java.naming.factory.initial", p == null ? "org.jboss.security.jndi.JndiLoginInitialContextFactory" : p);
    	}
    	
    	p = initialContextProperties.getProperty("java.naming.provider.url");
    	if( p == null ) {
            p = System.getProperty("java.naming.provider.url");
    		initialContextProperties.put("java.naming.provider.url", p == null ? "localhost:1099" : p);
    	}
    }

    @SuppressWarnings("unchecked")
    private void initJndiServiceResolver() {

        if (appserverProperties == null) {
            appserverProperties = new Properties();
        }
        
    	String p = appserverProperties.getProperty("org.mywms.ejb.JndiServiceResolver");
    	if( p == null ) {
            p = System.getProperty("org.mywms.ejb.JndiServiceResolver");
    		appserverProperties.put("org.mywms.ejb.JndiServiceResolver", p == null ? "org.mywms.ejb.JndiServiceResolverJBoss" : p);
    	}
    	
    	p = appserverProperties.getProperty("org.mywms.env.applicationName");
    	if( p == null ) {
            p = System.getProperty("org.mywms.env.applicationName");
    		appserverProperties.put("org.mywms.env.applicationName", p == null ? "los" : p);
    	}
    	
        
        try {
            String jndiServiceNameResolverClass = appserverProperties.getProperty("org.mywms.ejb.JndiServiceResolver");
            
            Constructor c = Class.forName(jndiServiceNameResolverClass).getConstructor( new Class[] {Properties.class} );
            jndiServiceResolver = (JndiServiceResolver) c.newInstance(new Object[] {appserverProperties});
        }
        catch (Exception e) {
            System.err.println("Cannot start JndiServiceResolver");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Returns the initial context.
     * 
     * @return the initial context
     * @throws BeanLocatorException
     */
    private Context getInitialContext() throws BeanLocatorException {
        if (initialContext == null) {
            try {
                initialContext = new InitialContext(initialContextProperties);
            }
            catch (NamingException ne) {
                throw new BeanLocatorException(ne);
            }
            catch (Exception e) {
                throw new BeanLocatorException(e);
            }
        }
        return initialContext;
    }

    public <T> T getStateless(Class<T> interfaceClazz) {
        return getStateless(interfaceClazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStateless(Class<T> interfaceClazz, String jndiName)
        throws BeanLocatorException
    {
        if (jndiName == null) {
            jndiName = jndiServiceResolver.resolve(interfaceClazz);
            System.out.println("---- get Stateless Session Bean " + jndiName);
        }
        try {
            T result = (T) statelessCache.get(jndiName);
            if (result == null) {
                result = (T) getInitialContext().lookup(jndiName);
                statelessCache.put(jndiName, result);
            }
            return result;
        }
        catch (NamingException ne) {
            throw new BeanLocatorException(ne);
        }
    }
    
    public <T> T getStatelessLocal(Class<T> interfaceClazz) throws BeanLocatorException{
    	return getStatelessLocal(interfaceClazz, null);
	}

    @SuppressWarnings("unchecked")
    public <T> T getStatelessLocal(Class<T> interfaceClazz, String jndiName)
        throws BeanLocatorException
    {
        if (jndiName == null) {
            jndiName = jndiServiceResolver.resolveLocal(interfaceClazz);
            System.out.println("---- get Stateless Session Bean " + jndiName);
        }
        try {
            T result = (T) statelessCache.get(jndiName);
            if (result == null) {
                result = (T) getInitialContext().lookup(jndiName);
                statelessCache.put(jndiName, result);
            }
            return result;
        }
        catch (NamingException ne) {
            throw new BeanLocatorException(ne);
        }
    }

    public <T> T getStateful(Class<T> interfaceClazz) {
        return getStateful(interfaceClazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStateful(Class<T> interfaceClazz, String jndiName)
        throws BeanLocatorException
    {
        if (jndiName == null) {
            jndiName = interfaceClazz.getName();
        }
        try {
            T result = (T) getInitialContext().lookup(jndiName);
            return result;
        }
        catch (NamingException ne) {
            throw new BeanLocatorException(ne);
        }
    }

    public QueueConnectionFactory getQueueConnectionFactory()
        throws BeanLocatorException
    {
        if (connectionFactory == null) {
            try {
                Object ref =
                    getInitialContext().lookup(JNDI_NAME_CONNECTION_FACTORY);
                connectionFactory =
                    (QueueConnectionFactory) PortableRemoteObject.narrow(
                        ref,
                        QueueConnectionFactory.class);
            }
            catch (NamingException e) {
                throw new BeanLocatorException(JNDI_NAME_CONNECTION_FACTORY
                    + " konnte nicht erzeugt werden", e);
            }
        }
        return connectionFactory;
    }

    public UserTransaction getUserTransaction() throws BeanLocatorException {
        try {
            UserTransaction ut =
                (UserTransaction) getInitialContext().lookup(
                    JNDI_NAME_USER_TRANSACTION);
            return ut;
        }
        catch (NamingException e) {
            throw new BeanLocatorException(JNDI_NAME_USER_TRANSACTION
                + " konnte nicht erzeugt werden", e);
        }
    }

    public Queue getQueue(String queuename) throws BeanLocatorException {
        try {
            return (Queue) getInitialContext().lookup(queuename);
        }
        catch (NamingException e) {
            throw new BeanLocatorException(e);
        }
    }

    public Topic getTopic(String topicname) throws BeanLocatorException {
        try {
            return (Topic) getInitialContext().lookup(topicname);
        }
        catch (NamingException e) {
            throw new BeanLocatorException(e);
        }
    }

    public void readExternal(ObjectInput in)
        throws IOException,
            ClassNotFoundException
    {
        statelessCache = new HashMap<String, Object>();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

}