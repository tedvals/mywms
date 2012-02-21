/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.exception;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=false)
@Deprecated
public class CustomException extends Exception {

	private static final long serialVersionUID = 1L;

	private boolean rollback = false;

	private String bundleName = "";
    
	private String key;
    
	private Object[] parameters;
    
	// Helpful if proeprties cannot be loaded because of separate Classloaders, e.e. netbeans
    @SuppressWarnings("unchecked")
	private Class bundleResolver;
    
   
    /**
     * Constructs an instance of <code>FacadeException</code> with the
     * specified detail message.
     * 
     * @param msg the detail message.
     * @param resourcekey the key to load the translated resource
     * @param parameters the array of objects, which are formatted into
     *            the translated message, resolved from the resource
     *            key; the array can be null, if no parameters are used
     * @param bundleName
     */
    @SuppressWarnings("unchecked")
	public CustomException(
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName,
        Class bundleResolverClass,
        boolean rollback)
    {
        super(msg);
        this.key = resourcekey;
        this.parameters = parameters;
        this.bundleName = bundleName;
        this.rollback = rollback;
        this.bundleResolver = bundleResolverClass;
    }


    /**
     * This method resolves the key from a predefined ResourceBundle.
     * The resolved String may contain additional formatter information.
     * Sample: If the resolved String is <code>"%2$s %1$s"<code>
     * and the parameters are <code>{"a", "b"}</code>, the String returned by 
     * <code>resolve</code> is <code>"a b"</code>.
     *
     * If the resource bundle file cannot be found, the key is null or the key 
     * cannot be resolved message is returned directly.
     *
     * @param key the key to be resolved
     * @param message the message returned, if the key cannot be resolved
     * @param parameters the parameters to be formatted into the resolved key 
     *          can be null
     * @return the resolved String
     */
    private String resolve(String message, String key, Object[] parameters) {
        return resolve(message, key, parameters, Locale.getDefault());
    }

    /**
     * This method resolves the key from a predefined ResourceBundle.
     * The resolved String may contain additional formatter information.
     * Sample: If the resolved String is <code>"%2$s %1$s"<code>
     * and the parameters are <code>{"a", "b"}</code>, the String returned by 
     * <code>resolve</code> is <code>"a b"</code>.
     *
     * If the resource bundle file cannot be found, the key is null or the key 
     * cannot be resolved message is returned directly.
     *
     * @param key the key to be resolved
     * @param message the message returned, if the key cannot be resolved
     * @param parameters the parameters to be formatted into the resolved key 
     *          can be null
     * @param locale the locale to use for i18n
     * @return the resolved String
     */
    private String resolve(
        String message,
        String key,
        Object[] parameters,
        Locale locale)
    {
        // assertion
        if (key == null) {
            return message;
        }

        ResourceBundle bundle;
        String formatString;
        try {
            // check for bundle
            if (getBundleResolver() == null){
                bundle = ResourceBundle.getBundle(bundleName, locale);
            } else{
                bundle = ResourceBundle.getBundle(bundleName, locale,getBundleResolver().getClassLoader());
            }
            // resolving key
            String s = bundle.getString(key);
            formatString = String.format(s, getParameters());
            return formatString;
        }
        catch (MissingResourceException ex) {
            return message;
        }
        catch (IllegalFormatException ife){
        	return "--- CONVERSION EXCEPTION >>>>> "+key;
        }
    }

    /**
     * Creates a localized description of this throwable.
     * 
     * @see #resolve(String,String,Object[])
     * @return The localized description of this throwable.
     */
    public String getLocalizedMessage() {
        return resolve(getMessage(), key, parameters);
    }

    /**
     * Creates a localized description of this throwable.
     * 
     * @see #resolve(String,String,Object[])
     * @return The localized description of this throwable.
     */
    public String getLocalizedMessage(Locale locale) {
        return resolve(getMessage(), key, parameters, locale);
    }

    public String getKey() {
        return key;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getBundleName() {
        return bundleName;
    }
    /**
     * Set to a class (normally within the package that contains the properties file and often an empty class called BundleResolver)
     * which can be used for loading the property file
     * @param bundleResolver
     */
    @SuppressWarnings("unchecked")
	public void setBundleResolver(Class bundleResolver){
        this.bundleResolver = bundleResolver;
    }
    
    /**
      *Get the class (normally within the package that contains the properties file and often an empty class called BundleResolver)
     * which can be used for loading the property file
     * @return
     */
    @SuppressWarnings("unchecked")
	public Class getBundleResolver(){
        return this.bundleResolver;
    }
	
	public boolean isRollback() {
		return rollback;
	}

	public void setRollback(boolean rollback) {
		this.rollback = rollback;
	}
	
}
