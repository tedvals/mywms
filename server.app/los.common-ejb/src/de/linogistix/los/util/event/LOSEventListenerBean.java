/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.event;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;



@MessageDriven(mappedName = "jms/LOSListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Client-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/LOSEventQueue"),
		@ActivationConfigProperty(propertyName = "user", propertyValue = "LOSListenerBean"),
		@ActivationConfigProperty(propertyName = "password", propertyValue = "lino_lib"),
		@ActivationConfigProperty(propertyName = "useDLQ", propertyValue = "true"),
        @ActivationConfigProperty(propertyName = "DLQJNDIName", propertyValue = "queue/LOSDeadLetterQueue"),
        @ActivationConfigProperty(propertyName = "DLQUser", propertyValue = "LOSListenerBean"),
        @ActivationConfigProperty(propertyName = "DLQPassword", propertyValue = "lino_lib"),
        @ActivationConfigProperty(propertyName = "minSession", propertyValue="1"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue="1")
})
public class LOSEventListenerBean implements MessageListener {
	private final Logger logger = Logger.getLogger(LOSEventListenerBean.class);

	@EJB
	private LOSEventConsumer eventConsumer;
	
	public void onMessage(Message msg) {
		if (msg instanceof ObjectMessage) {
			
            try {
                Object msgObject = ((ObjectMessage) msg).getObject();
                
                if( msgObject instanceof LOSEvent ) {
                	LOSEvent event = (LOSEvent) msgObject;
                	
                	eventConsumer.processEvent(event.getEventName(), event.getParameter());
                	
                }
                else {
    	        	logger.error("Wrong Messageobjecttype <" + msgObject.getClass().getCanonicalName() + ">");
                	return;
                }
                
				msg.acknowledge();

            }
	        catch(JMSException jex) {
	        	logger.error("Could not get Object from JMS message");
	        	try {
					msg.acknowledge();
					
				} catch (JMSException e) {
					logger.error("Could not acknowledge JMS message in error condition");
				}
				
	        } 
        	catch( Throwable e ) {
				// don't acknowledge the message, so it will be redelivered
	        	logger.error("Force Redelivery: " + e.getMessage());
	        	
        	}
		}
		else {
			logger.error("Received none ObjectMessage as JMS message");
        	try {
				msg.acknowledge();
				
			} catch (JMSException e) {
				logger.error("Could not acknowledge none ObectMessage in error condition");
			}
		}
		
	}

	
}
