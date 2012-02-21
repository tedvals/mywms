/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.OptimisticLockException;

import org.apache.log4j.Logger;

@Stateless
public class LOSSequenceGeneratorServiceBean implements
		LOSSequenceGeneratorService {

	@EJB 
	private LOSSequenceTransactionService sequenceEngine;
	
	private static final Logger log = Logger.getLogger(LOSSequenceGeneratorServiceBean.class);
	
	
	@SuppressWarnings("unchecked")
	public long getNextSequenceNumber(Class assignedClass) {
        int tries = 0;
        final int maxTries = 100;
        long nextSeq = -1;
        do {
            try {
                nextSeq = sequenceEngine.getNextNoNewTransaction(assignedClass.getName());
            } catch (EJBException e) {
                if (e.getCausedByException() instanceof OptimisticLockException) {
                	log.warn("OptimisticLockException occured (" + tries + "). try again...");
                    tries++;
                } else {
                	log.error("EJBException occured. ", e);
                    throw e;
                }
            }
            log.info("Next No: " + nextSeq);

        } while (nextSeq < 0 && tries < maxTries);

        if (nextSeq < 0) {
        	log.error("Cannot get Sequence. Give it up after " + maxTries + " tries");
            final String msg = "Exceeded maxTries=" + maxTries + " attempts";
            throw new EJBException(msg);
        }

        return nextSeq;
	}

	@SuppressWarnings("unchecked")
	public void resetSequence(Class assignedClass) {
        int tries = 0;
        final int maxTries = 100;
        do {
            try {
                sequenceEngine.resetSequenceInNewTransaction(assignedClass.getName());
                break;
            } catch (EJBException e) {
                if (e.getCausedByException() instanceof OptimisticLockException) {
                	log.warn("OptimisticLockException occured (" + tries + "). try again...");
                    tries++;
                } else {
                	log.error("EJBException occured. ", e);
                    throw e;
                }
            }
        } while (tries < maxTries);

        if (tries >= maxTries) {
        	log.error("Cannot get Sequence. Give it up after " + maxTries + " tries");
            final String msg = "Cannot get Sequence. Exceeded maxTries=" + maxTries + " attempts";
            throw new EJBException(msg);
        }
	}

}
