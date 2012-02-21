/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.log;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




import java.io.PrintStream;
import org.openide.windows.OutputWriter;

public class SystemStream extends PrintStream {


    protected OutputWriter out;


    @Override
    public void print(boolean b) {
        super.print(b);
        out.print(b);
        out.flush();        
    }

    @Override
    public void print(char c) {
        super.print(c);
        out.print(c);
        out.flush();        
    }

    @Override
    public void print(int i) {
        super.print(i);
        out.print(i);
        out.flush();        
    }

    @Override
    public void print(long l) {
        super.print(l);
        out.print(l);
        out.flush();        
    }

    @Override
    public void print(float f) {
        super.print(f);
        out.print(f);
        out.flush();        
    }

    @Override
    public void print(double d) {
        super.print(d);
        out.print(d);
        out.flush();        
    }

    @Override
    public void print(char[] s) {
        super.print(s);
        out.print(s);
        out.flush();        
    }

    @Override
    public void print(String s) {
        super.print(s);
        out.print(s);
        out.flush();        
    }

    @Override
    public void print(Object obj) {
        super.print(obj);
        out.print(obj);
        out.flush();        
    }

    @Override
    public void println() {
        super.println();
        out.println();
        out.flush();        
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        out.print(x);
        out.flush();                
    }

    @Override
    public void println(char x) {
        super.println(x);
        out.println(x);
    }

    @Override
    public void println(int x) {
        super.println(x);
        out.println(x);
        out.flush();        
    }

    @Override
    public void println(long x) {
        super.println(x);
        out.println(x);
    }

    @Override
    public void println(float x) {
        super.println(x);
        out.println(x);
        out.flush();        
    }

    @Override
    public void println(double x) {
        super.println(x);
        out.println(x);
        out.flush();        
    }

    @Override
    public void println(char[] x) {
        super.println(x);
        out.println(x);
        out.flush();        
    }

    @Override
    public void println(String x) {
        super.println(x);
        out.println(x);
        out.flush();
    }

    @Override
    public void println(Object x) {
        super.println(x);
        out.println(x);
        out.flush();        
    }
     
    
    public SystemStream(PrintStream out1, OutputWriter out2) { 
        super(out1);
        this.out = out2;
        out.flush();        
    }

    public void flush() {
        super.flush();
        out.flush();
    }

    
    public static void replaceStandardOutput(OutputWriter out) {
            PrintStream tee = new SystemStream(System.out, out);
            System.setOut(tee);

    }

 

    public static void replaceStandardError(OutputWriter out) { 
            PrintStream tee = new SystemStream(System.err, out);
            System.setErr(tee); 


    }
    

}