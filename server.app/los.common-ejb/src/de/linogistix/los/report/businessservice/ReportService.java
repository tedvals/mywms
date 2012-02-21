/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report.businessservice;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

/**
 *
 * @author trautm
 */
@Local
public interface ReportService {

    String DEFAULT_PRINTER = "default";
    
    String 	NO_PRINTER = "none";
    
    Image typeExportImage(String name, String type, JasperDesign jsxml, List<? extends Object> exportList) throws IOException;

    byte[] typeExportPdf(String name, String type, JasperDesign jsxml, List<? extends Object> exportList) throws IOException;

    byte[] typeExportPdf(String name, String type, JasperDesign jsxml, List<? extends Object> exportList, Map<String, Object> parameters) throws IOException;
 
    byte[] typeExportPdf(String name, String type, InputStream is, List<? extends Object> exportList, Map<String, Object> parameters) throws IOException;
    
    byte[] typeExportExcel(String name, JasperDesign jsxml, List<? extends Object> exportList, Map<String, Object> parameter) throws IOException;
    
    void print(String printer, byte[] bytes, String type) throws ReportException;
        
    public JasperDesign getJrxmlResource(Client c, Class<? extends Object> bundleResolver, String name) throws JRException;
    
    public JasperDesign getJrxmlResource(Class<? extends Object> bundleResolver, String name) throws JRException;
    
    byte[] createGenericBarcodeLabels(String name, String[] labels) throws ReportException;
    
    /**
     * Gets a document (e.g. pdf) via http get as byte Array.
     * 
     * @param urlStr
     * @return
     */
    public byte[] httpGet(String urlStr) ;
    
    /**
     * Simple and generic excel report. 
     * One line per Object, a column per bean property, retrieved via reflection.
     *   
     * @param name name of report that appears in sheet header
     * @param exportList list of objects
     * @param keys: propertiy names of bean properties for objects that should be exported, value: displayname used in the report
     * @return Excel documents as byte Array
     * @throws IOException
     * @throws FacadeException 
     */
    public byte[] typeExportExcelGeneric(String name, List<? extends Object> exportList, Map<String, String> properties) throws IOException, FacadeException ;
    
}
