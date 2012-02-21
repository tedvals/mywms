/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.log;

import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.PrintStream;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class RedirectStream {

    public void toConsole() {
        try {
            InputOutput io = IOProvider.getDefault().getIO("", true);
            LogManager logManager = LogManager.getLogManager();
//            printLogManager(logManager);
            //if you reset the Exceptionsdialog from netbeans not work            
            //logManager.reset();            
            // log file max size 10K, 3 rolling files, append-on-open
            //Handler fileHandler = new FileHandler("c://log.log", 10000, 3, true);
            Handler fileHandler = new CustomConsoleHandler(io.getOut(), io.getErr());
            fileHandler.setFormatter(new SimpleFormatter());
            Logger.getLogger("").addHandler(fileHandler);
            //1 solution. Write direct to consolfe
/*            Logger logger;                                                                     
            SystemStream.replaceStandardOutput(io.getOut()); 
            SystemStream.replaceStandardError(io.getErr());
             */
            //2 solution. Write it to log and redirect log to console
            PrintStream stdout = System.out;
            PrintStream stderr = System.err;
            // now rebind stdout/stderr to logger                                  
            Logger logger;
            LoggingOutputStream los;
            logger = Logger.getLogger("stdout");
            los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
            System.setOut(new PrintStream(los, true));
            los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
            System.setErr(new PrintStream(los, true));
        // now show stderr stack trace going to logger
        // testLog(logger);
        // and output on the original stdout        
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void printLogManager(LogManager logManager) {
        Enumeration<String> manager = logManager.getLoggerNames();
        while (manager.hasMoreElements()) {
            System.out.println("LogManager = " + manager.nextElement());
        }

    }

    private void testLog(Logger logger) {
        // show stdout going to logger
        try {
            throw new RuntimeException("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Hello world!");

        // now log a message using a normal logger
        logger = Logger.getLogger("");
        logger.info("This is a test log message");

    }
}
