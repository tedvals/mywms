/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report.businessservice;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;
import org.mywms.model.Client;


import de.linogistix.los.report.util.GenericExcelExporter;
import de.linogistix.los.res.BundleResolver;

@Stateless
public class ReportServiceBean implements ReportService {

	private static final Logger log = Logger.getLogger(ReportServiceBean.class);
	
	public byte[] typeExportPdf(String name, String type, JasperDesign jrxml,
			List<? extends Object> exportList) throws IOException {
		return typeExportPdf(name, type, jrxml, exportList, null);
	}

	public byte[] typeExportPdf(String name, 
						 		String type, 
						 		InputStream is, 
						 		List<? extends Object> exportList, 
						 		Map<String, Object> parameters) throws IOException
	{
		JasperDesign jasperDesign;
		try {
			jasperDesign = JRXmlLoader.load(is);
		} catch (JRException e) {
			throw new IOException(e.getMessage());
		}
		
		return typeExportPdf(name, type, jasperDesign, exportList, parameters);
	}
	
	public byte[] typeExportPdf(String name, String type, JasperDesign jrxml,
			List<? extends Object> exportList, Map<String, Object> parameters) throws IOException {
		try {
			JRExporter exporter = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (parameters == null) {
				parameters = new HashMap<String, Object>();
				parameters.put("REPORT_LOCALE", Locale.GERMANY);
			}

			// Fill the requested report with the specified data
			JRBeanCollectionDataSource jbCollectionDS = new JRBeanCollectionDataSource(
					exportList);
			JasperDesign jasperDesign = jrxml;
			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = null;
			// First, load JasperDesign from XML and compile it into
			// JasperReport
			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, jbCollectionDS);

			exporter = new JRPdfExporter();
			// response.resetBuffer();
			exporter
					.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);

			exporter.exportReport();
			return out.toByteArray();
		} catch (JRException ex) {
			log.error(ex, ex);
			throw new IOException(ex.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public byte[] typeExportExcelGeneric(String name, List<? extends Object> exportList, Map<String, String> properties)
			throws IOException, FacadeException{
		GenericExcelExporter exporter = new GenericExcelExporter();
		List l = exportList;
		return exporter.export(name, l);
	}

	public byte[] typeExportExcel(String name, JasperDesign jrxml,
			List<? extends Object> exportList, Map<String, Object> parameters) throws IOException {

		try {

			JRXlsExporter xlsExporter = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (parameters == null) {
				parameters = new HashMap<String, Object>();
				parameters.put("REPORT_LOCALE", Locale.GERMANY);
			}	

			// Fill the requested report with the specified data
			JRBeanCollectionDataSource jbCollectionDS = new JRBeanCollectionDataSource(
					exportList);
			JasperDesign jasperDesign = jrxml;
			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = null;
			// First, load JasperDesign from XML and compile it into
			// JasperReport
			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, jbCollectionDS);

			xlsExporter = new JRXlsExporter();
			xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,
					jasperPrint);
			xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			xlsExporter
					.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
							Boolean.FALSE);
			xlsExporter.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);
			xlsExporter.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);

			// writeHeader(response, exportName);

			xlsExporter.exportReport();
			return out.toByteArray();
		} catch (JRException ex) {
			log.error(ex, ex);
			throw new IOException(ex.getMessage());
		}
	}

	public Image typeExportImage(String name, String type, JasperDesign jrxml,
			List<? extends Object> dto) throws IOException {
		try {

			// JRExporter exporter = null;
			List<Object> exportList = new ArrayList<Object>();
			exportList.add(dto);

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("LOGO", "");
			parameters.put("REPORT_LOCALE", Locale.GERMANY);

			// Fill the requested report with the specified data
			JRBeanCollectionDataSource jbCollectionDS = new JRBeanCollectionDataSource(
					exportList);
			JasperDesign jasperDesign = jrxml;
			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = null;
			// First, load JasperDesign from XML and compile it into
			// JasperReport
			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, jbCollectionDS);

			Image image = JRPrinterAWT.printPageToImage(jasperPrint, 0, 1);
			return image;
		} catch (JRException ex) {
			log.error(ex, ex);
			return null;
		}
	}

	public void print(String printer, byte[] bytes, String type)
			throws ReportException {
		try {

			PrintService printService;
			DocPrintJob job;
			DocFlavor fl;
			Doc doc;

			DocAttributeSet das = new HashDocAttributeSet();
			Object printObject;
			if (type == null || type.length() == 0){
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else if (type.equals(DocumentTypes.TEXT_XML.toString())) {
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else {
				log.warn("Unknown type: " + type);
				fl = DocFlavor.INPUT_STREAM.AUTOSENSE;
				printObject = new ByteArrayInputStream(bytes);
			}
			
			if (printer == null || printer.length() == 0) {
				log.info("Won't print. Printer not defined");
				return;
			}
			if (printer != null && printer.equals(NO_PRINTER)){
				log.info("Won't print. Printer: " + printer);
				return;
			}
			if (printer.equals(DEFAULT_PRINTER)) {
				printService = PrintServiceLookup.lookupDefaultPrintService();
				
			} else {
				try {
					printService = getNamedPrintService(fl,printer);
				} catch (Exception ex) {
					log.error(ex, ex);
					throw new IllegalArgumentException(
							"Printer cannot be selected");
				}
			}

			if (printService == null) {
				log.error("printer not found: " + printer);
				throw new ReportException(ReportExceptionKey.PRINT_FAILED,
						printer);
			}
			
			doc = new SimpleDoc(printObject, fl, das);
			job = printService.createPrintJob();	
			PrintJobWatcher watcher = null;
			
			if (fl instanceof DocFlavor.INPUT_STREAM) {
				watcher = new PrintJobWatcher(job);
			}
			
			job.print(doc, null);
			
			if (watcher != null){
				watcher.waitForDone();
				((InputStream)printObject).close();
			}
			
			
		} catch (PrintException ex) {
			log.error(ex, ex);
			throw new ReportException(ReportExceptionKey.PRINT_FAILED, printer);
		} catch (Throwable ex) {
			log.error(ex, ex);
			throw new ReportException(ReportExceptionKey.PRINT_FAILED, printer);
		}

	}

	protected PrintService getNamedPrintService(DocFlavor flav, String prnName)
			throws Exception {
		PrintService[] prnSvcs;
		PrintService prnSvc = null;
		
		// get all print services for this machine
		prnSvcs = PrintServiceLookup.lookupPrintServices(flav, null);

		if (prnSvcs.length > 0) {
			int ii = 0;
			while (ii < prnSvcs.length) {
				log.debug("Named Printer found: " + prnSvcs[ii].getName());
				if (prnSvcs[ii].getName().equalsIgnoreCase(prnName)) {
					prnSvc = prnSvcs[ii];
					log.debug("Named Printer selected: "
							+ prnSvcs[ii].getName() + "*");
					break;
				}
				ii++;
			}
		}

		if (prnSvc == null) {
			throw new Exception("Printer " + prnName
					+ " was not found on this system.");
		}

		DocFlavor[] flavors = prnSvc.getSupportedDocFlavors();
		
		if (flavors != null) {
			for (int i = 0; i < flavors.length; i++) {
				log.debug("Supported flavor: " + flavors[i].toString());
			}
		} else {
			log.debug("No Supported flavors");
		}
		
		return prnSvc;
	}

	public JasperDesign getJrxmlResource(Client c, Class<? extends Object> bundleResolver,
			String name) throws JRException {
		return getJrxmlResource(bundleResolver, name);
	}

	public JasperDesign getJrxmlResource(Class<? extends Object> bundleResolver, String name)
			throws JRException {
		InputStream is;

		String dir = bundleResolver.getPackage().toString();
		dir = dir.replaceAll("package", "/");
		dir = dir.replaceAll("\\.", "/");
		dir = dir.replaceAll("\\s", "");

		name = dir + "/" + name;
		log.info("+++ read from " + name);
		is = bundleResolver.getResourceAsStream(name);

		if (is == null) {
			is = this.getClass().getClassLoader().getResourceAsStream(name);
			if (is == null) {
				log.error("Cannot read resource with name="+name);
				throw new NullPointerException();
			}
		}
		JasperDesign jasperDesign = JRXmlLoader.load(is);
		
		try{
			is.close();
		} catch (IOException ex){
			log.error("Exception reading resource: "+ex.getMessage(), ex);
		}
		
		return jasperDesign;

	}

	public byte[] createGenericBarcodeLabels(String docName, String[] labels)
			throws ReportException {
		try {
			List<LabelTO> list = new ArrayList<LabelTO>();
			for (String label : labels) {
				list.add(new LabelTO(label));
			}

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("REPORT_LOCALE", Locale.GERMANY);

			byte[] bytes = typeExportPdf(docName, DocumentTypes.APPLICATION_PDF
					.toString(), getJrxmlResource(BundleResolver.class,
					"GenericBarcodeLabels.jrxml"), list);
			return bytes;
		} catch (Throwable ex) {
			log.error(ex, ex);
			throw new ReportException();
		}
	}

	public byte[] httpGet(String urlStr) {
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL url = new URL(urlStr); // Create the URL
			in = url.openStream(); // Open a stream to it
			// Now copy bytes from the URL to the output stream
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = in.read(buffer)) != -1){
				out.write(buffer, 0, bytes_read);
			}	
			
			return out.toByteArray();
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		} finally { // Always close the streams, no matter what.
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	public final class GenericReportTO {

		String c1;

		String c2;

		String c3;

		String c4;

		String c5;

		String c6;

		String c7;

		String c8;

		String c9;

		public String getC1() {
			return c1;
		}

		public void setC1(String c1) {
			this.c1 = c1;
		}

		public String getC2() {
			return c2;
		}

		public void setC2(String c2) {
			this.c2 = c2;
		}

		public String getC3() {
			return c3;
		}

		public void setC3(String c3) {
			this.c3 = c3;
		}

		public String getC4() {
			return c4;
		}

		public void setC4(String c4) {
			this.c4 = c4;
		}

		public String getC5() {
			return c5;
		}

		public void setC5(String c5) {
			this.c5 = c5;
		}

		public String getC6() {
			return c6;
		}

		public void setC6(String c6) {
			this.c6 = c6;
		}

		public String getC7() {
			return c7;
		}

		public void setC7(String c7) {
			this.c7 = c7;
		}

		public String getC8() {
			return c8;
		}

		public void setC8(String c8) {
			this.c8 = c8;
		}

		public String getC9() {
			return c9;
		}

		public void setC9(String c9) {
			this.c9 = c9;
		}
	}
	
	static class PrintJobWatcher {
        // true if it is safe to close the print job's input stream
        boolean done = false;
        
        PrintJobWatcher(DocPrintJob job) {
            // Add a listener to the print job
            job.addPrintJobListener(new PrintJobAdapter() {
                public void printJobCanceled(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobCompleted(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobFailed(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    allDone();
                }
                void allDone() {
                    synchronized (PrintJobWatcher.this) {
                        done = true;
                        PrintJobWatcher.this.notify();
                    }
                }
            });
        }
        public synchronized void waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
        }
    }

}
