/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * An OutputStream that writes contents to a Logger upon each call to flush() 
 */
public class LoggingOutputStream extends ByteArrayOutputStream {

    private String lineSeparator;
    private Logger logger;
    private Level level;

    /** 
     * Constructor 
     * @param logger Logger to write to 
     * @param level Level at which to write the log message 
     */
    public LoggingOutputStream(Logger logger, Level level) {
        super();
        this.logger = logger;
        this.level = level;
        lineSeparator = System.getProperty("line.separator");
    }

    /** 
     * upon flush() write the existing contents of the OutputStream
     * to the logger as a log record. 
     * @throws java.io.IOException in case of error 
     */
    public void flush() throws IOException {

        String record;
        synchronized (this) {
            super.flush();
            record = this.toString();
            super.reset();

            if (record.length() == 0 || record.equals(lineSeparator)) {
                // avoid empty records 
                return;
            }
            logger.logp(level, "", "",record);
        }
    }
} 
