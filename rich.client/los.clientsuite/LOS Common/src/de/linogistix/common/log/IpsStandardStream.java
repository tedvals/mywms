/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.common.log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class IpsStandardStream extends PrintStream { 

    protected PrintStream out;


     
    public IpsStandardStream(PrintStream out1, PrintStream out2) { 
        super(out1);
        this.out = out2;
    }

    public void write(byte buf[], int off, int len) {
        try {
            super.write(buf, off, len);
            out.write(buf, off, len); 
        } catch (Exception e) {
        }
    }

    public void flush() {
        super.flush();
        out.flush();
    }



    public static void replaceStandardOutput() {

        try {
            PrintStream outPut = new PrintStream(
                    new FileOutputStream("c://out.log"));
            PrintStream tee = new IpsStandardStream(System.out, outPut);
            System.setOut(tee);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 

    }

 

    public static void replaceStandardError() { 

        try {
            PrintStream err = new PrintStream(new FileOutputStream("c://error.log"));
            PrintStream tee = new IpsStandardStream(System.err, err);
            System.setErr(tee); 

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    
    
    public static void replaceStandardOutput(ByteArrayOutputStream out) {

            PrintStream outPut = new PrintStream(out);
                    
            PrintStream tee = new IpsStandardStream(System.out, outPut);
            System.setOut(tee);

    }

 

    public static void replaceStandardError(ByteArrayOutputStream out) { 

            PrintStream err = new PrintStream(out);
            PrintStream tee = new IpsStandardStream(System.err, err);
            System.setErr(tee); 


    }
    

/*    public static void main(String[] arg) {

        IpsStandardStream.replaceStandardOutput(); 
        IpsStandardStream.replaceStandardError();

        // Write to standard output and error and the log files
        Timestamp now;
        for (int i = 0; i < 1; i++) {
            now = new Timestamp( System.currentTimeMillis());
            System.out.println(now);
            System.out.println("welcome " + i);
            System.err.println(now);
            System.err.println("error " + i); 
        }
    }*/
}