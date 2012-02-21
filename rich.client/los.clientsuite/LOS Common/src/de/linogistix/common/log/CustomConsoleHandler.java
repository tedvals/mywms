/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.openide.windows.OutputWriter;

public class CustomConsoleHandler extends java.util.logging.ConsoleHandler {

//    public MessageConsoleStream consoleStream = null;
    OutputWriter out;
    OutputWriter err;

    public CustomConsoleHandler(OutputWriter out, OutputWriter err) {
        this.out = out;
        this.err = err;
    }

    public void publish(LogRecord record) {
        if ((record.getLevel() == Level.SEVERE) ||
                (record.getLevel() == Level.WARNING)) {
            out.println(this.getFormatter().format(record));
            //Make sure that the data will refreshed in the output window
            synchronized (this) {
                out.flush();
            }
        /*    } else if (record.getLevel().intValue() == 853) {
        //STDOUT 
        }
        else if (record.getLevel().intValue() == 854) {
        //STDERR*/

        } else {
            
            //out.println("MYBEGINN "+this.getFormatter().format(record)+" MYEND");
            if (record.getMessage() != null) out.println(record.getMessage());                    
            synchronized (this) {
                out.flush();
            }
        }
    }
}
