/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.event;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.log4j.Logger;

@Stateless
public class LOSEventSenderBean implements LOSEventSender {
	private final Logger logger = Logger.getLogger(LOSEventSenderBean.class);

	
	@Resource(mappedName = "ConnectionFactory")
    private QueueConnectionFactory queueConnectionFactory;

    @Resource(mappedName = "queue/LOSEventQueue")
    private Queue losEventQueue;

    


	public void sendMsg( LOSEvent event ) throws JMSException {
		if( event == null ) {
			logger.error("No Event specified. Do nothing");
			return;
		}
		
		logger.debug("sendMsg: "+event.getEventName());
		QueueConnection con = null;

		con = queueConnectionFactory.createQueueConnection("LOSListenerBean", "lino_lib");
		if( con == null ) {
			logger.error("Cannot get connection to queue. Abort.");
			return;
		}

		QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	    QueueSender sender = session.createSender(losEventQueue);
	    
	    ObjectMessage msg = session.createObjectMessage( event );
	
	    sender.send(msg);
	    
	}
}
