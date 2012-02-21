/*
 * OpenBOQueryTopComponentAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.logviewer.processes.clearing.gui.component;

import de.linogistix.common.bobrowser.util.TypeResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.userlogin.LoginState;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.logviewer.res.BundleResolver;
import de.linogistix.los.query.ClearingItemQueryRemote;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportExceptionKey;
import de.linogistix.los.report.businessservice.ReportServiceBean;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.mywms.globals.DocumentTypes;
import org.mywms.globals.Role;
import org.mywms.model.ClearingItem;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class PrintClearingItemsAction extends NodeAction {

    private static final Logger log = Logger.getLogger(PrintClearingItemsAction.class.getName());
    
    String DEFAULT_PRINTER = "default";
    
    String 	NO_PRINTER = "none";
    
    private String[] roles = new String[]{
        Role.ADMIN.toString(),
        Role.CLIENT_ADMIN.toString(),
        Role.INVENTORY.toString(),
        Role.OPERATOR_STR
    };

    protected boolean enable(Node[] node) {

//        if ((node == null) || (node.length != 1)) {
//            //System.out.println("--> BONode " + node.length);
//            return false;
//        }

        return checkRoles();

    }

    /**
     * Checks whether the logged in user is allowed to see this node.
     */
    public boolean checkRoles() {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(getRoles());
    }

    protected void performAction(Node[] node) {

        try {

            List<ClearingItem> clearingList = getClearingItems();
            log.info("found " + clearingList.size() + " clearing items.");

            for (ClearingItem item : clearingList){
                ClearingPrintTO to = new ClearingPrintTO();
                to.setMessage(ClearingPrintTO.resolveMessage(item));
                to.setCreated(to.getCreated());
            }
            
            List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
            PropertyDescriptor pd = new PropertyDescriptor("created", ClearingItem.class, "getCreated", null);
            pd.setDisplayName("Datum");
            props.add(pd);

            pd = new PropertyDescriptor("message", ClearingItem.class);
            pd.setDisplayName("Klärfall");
            props.add(pd);

            byte[] bytes = export("Klärfallliste", clearingList, props);
            log.info("received " + bytes.length + " bytes");

            log.info("going to print");

            ReportServiceBean reportService = new ReportServiceBean();
            reportService.print("default", bytes, DocumentTypes.APPLICATION_PDF.toString());

//            print("default", bytes, DocumentTypes.APPLICATION_PDF.toString());
            log.info("printing on default");

        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
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
            if (type == null || type.length() == 0) {
                fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                printObject = bytes;
            } else if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
                fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                printObject = bytes;
            } else if (type.equals(DocumentTypes.TEXT_XML.toString())) {
                fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                printObject = bytes;
            } else {
                log.info("Unknown type: " + type);
                fl = DocFlavor.INPUT_STREAM.AUTOSENSE;
                printObject = new ByteArrayInputStream(bytes);
            }

            if (printer != null && printer.equals(NO_PRINTER)) {
                log.info("Won't print. Printer: " + printer);
                return;
            }
            if (printer == null || printer.length() == 0 || printer.equals(DEFAULT_PRINTER)) {

                printService = PrintServiceLookup.lookupDefaultPrintService();

            } else {
                try {
                    printService = getNamedPrintService(fl, printer);
                } catch (Exception ex) {
                    log.log(Level.INFO, ex.getMessage(), ex);
                    throw new IllegalArgumentException(
                            "Printer cannot be selected");
                }
            }

            if (printService == null) {
                log.info("printer not found: " + printer);
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

            if (watcher != null) {
                watcher.waitForDone();
                ((InputStream) printObject).close();
            }


        } catch (PrintException ex) {
            log.log(Level.INFO, ex.getMessage(), ex);
            throw new ReportException(ReportExceptionKey.PRINT_FAILED, printer);
        } catch (Throwable ex) {
            log.log(Level.INFO, ex.getMessage(), ex);
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
                log.info("Named Printer found: " + prnSvcs[ii].getName());
                if (prnSvcs[ii].getName().equalsIgnoreCase(prnName)) {
                    prnSvc = prnSvcs[ii];
                    log.info("Named Printer selected: " + prnSvcs[ii].getName() + "*");
                    break;
                }
                ii++;
            }
        }

        if (prnSvc == null) {
            throw new Exception("Printer " + prnName + " was not found on this system.");
        }

        DocFlavor[] flavors = prnSvc.getSupportedDocFlavors();

        if (flavors != null) {
            for (int i = 0; i < flavors.length; i++) {
                log.info("Supported flavor: " + flavors[i].toString());
            }
        } else {
            log.info("No Supported flavors");
        }

        return prnSvc;
    }

    @SuppressWarnings("unchecked")
    public List<ClearingItem> getClearingItems() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ClearingItemQueryRemote r = (ClearingItemQueryRemote) loc.getStateless(ClearingItemQueryRemote.class);
            de.linogistix.common.userlogin.LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
            if (login.getState() == LoginState.AUTENTICATED) {
                return r.getUnresolvedClearingItemList();
            }
        } catch (J2EEServiceLocatorException ex) {
            Exceptions.printStackTrace(ex);
        }
        //return empty list
        return new ArrayList();
    }

    public HelpCtx getHelpCtx() {
        return null;
    }

    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "PrintClearingItemAction");
    }

    protected boolean asynchronous() {
        return false;
    }

    public String[] getRoles() {
        return roles;
    }

    @SuppressWarnings("unchecked")
    public byte[] export(String title, List<ClearingItem> exportList,
            List<PropertyDescriptor> props) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (exportList == null || exportList.size() == 0) {
            return new byte[0];
        }

        try {
            Object bean = exportList.get(0);
            BeanInfo infoTo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] d = infoTo.getPropertyDescriptors();

            if (props == null || props.isEmpty()) {

                props = new ArrayList<PropertyDescriptor>();

                for (int i = 0; i < d.length; i++) {
                    try {

                        Class pType = d[i].getPropertyType();

                        if (pType.isAssignableFrom(Class.class)) {
                            continue;
                        }

                        if (TypeResolver.isBooleanType(pType)) {
                            //ok
                        } else if (TypeResolver.isPrimitiveType(pType)) {
                            //ok
                        } else if (TypeResolver.isDateType(pType)) {
                            //ok
                        } else if (TypeResolver.isEnumType(pType)) {
                            //ok
                        } else {
                            log.warning("Skip type " + pType);
                            continue;
                        }

                        if (d[i].getName().equals("className") || d[i].getName().equals("id") || d[i].getName().equals("version")) {
                            continue;
                        }

                        props.add(d[i]);
                    } catch (Exception ex) {
                        log.severe(ex.getMessage());
                        continue;
                    }

                }
            }

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("ReportTitle", title);

            JasperDesign design = getJasperDesign(props);

            JasperReport jasperReport = JasperCompileManager.compileReport(design);

            JasperPrint jasperPrint = null;

            jasperPrint = JasperFillManager.fillReport(jasperReport,
                    parameters, new JRBeanCollectionDataSource(exportList));

            JRPdfExporter exporter = new JRPdfExporter();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                    jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
                    Boolean.TRUE);
            exporter.setParameter(
                    JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                    Boolean.TRUE);
            exporter.setParameter(
                    JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                    Boolean.FALSE);

            // writeHeader(response, exportName);

            exporter.exportReport();

            byte[] ret = out.toByteArray();

            out.close();

            return ret;

        } catch (Throwable t) {
            log.log(Level.INFO, t.getMessage(), t);
            return null;
        }

    }

    private static JasperDesign getJasperDesign(List<PropertyDescriptor> props)
            throws JRException {
        // JasperDesign
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("NoXmlDesignReport");
        jasperDesign.setPageWidth(595);
        jasperDesign.setPageHeight(842);
        jasperDesign.setColumnWidth(515);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setColumnCount(1);
        jasperDesign.setLeftMargin(40);
        jasperDesign.setRightMargin(40);
        jasperDesign.setTopMargin(50);
        jasperDesign.setBottomMargin(50);

        // Fonts
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("DejaVu Sans");
        normalStyle.setFontSize(12);
        normalStyle.setPdfFontName("Helvetica");
        normalStyle.setPdfEncoding("Cp1252");
        normalStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(normalStyle);

        JRDesignStyle boldStyle = new JRDesignStyle();
        boldStyle.setName("Sans_Bold");
        boldStyle.setFontName("DejaVu Sans");
        boldStyle.setFontSize(12);
        boldStyle.setBold(true);
        boldStyle.setPdfFontName("Helvetica-Bold");
        boldStyle.setPdfEncoding("Cp1252");
        boldStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(boldStyle);

        JRDesignStyle italicStyle = new JRDesignStyle();
        italicStyle.setName("Sans_Italic");
        italicStyle.setFontName("DejaVu Sans");
        italicStyle.setFontSize(12);
        italicStyle.setItalic(true);
        italicStyle.setPdfFontName("Helvetica-Oblique");
        italicStyle.setPdfEncoding("Cp1252");
        italicStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(italicStyle);

        // Parameters
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setName("ReportTitle");
        parameter.setValueClass(java.lang.String.class);
        jasperDesign.addParameter(parameter);

        // Fields
        int i = 1;
        for (PropertyDescriptor p : props) {
            JRDesignField field = new JRDesignField();
            field.setName(p.getName());
            field.setValueClass(resolveType(p));
            jasperDesign.addField(field);
            i++;
        }

        // Title
        JRDesignBand band = new JRDesignBand();
        band.setHeight(50);

        JRDesignTextField textField = new JRDesignTextField();
        textField.setBlankWhenNull(true);
        textField.setX(0);
        textField.setY(20);
        textField.setWidth(510);
        textField.setHeight(30);
        textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
        textField.setStyle(normalStyle);
        textField.setFontSize(22);
        JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(java.lang.String.class);
        expression.setText("$P{ReportTitle}");
        textField.setExpression(expression);
        band.addElement(textField);
        jasperDesign.setTitle(band);

        // Page header
        band = new JRDesignBand();
        band.setHeight(20);
        int x = 0;
        for (PropertyDescriptor p : props) {
            JRDesignStaticText staticText = new JRDesignStaticText();
            staticText.setX(x);
            staticText.setY(0);

            if (p.getName().equals("message")) {
                staticText.setWidth(400);
                x += 400;
            } else {
                staticText.setWidth(100);
                x += 100;
            }
            staticText.setHeight(20);
            staticText.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
            staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
            staticText.setStyle(boldStyle);
            staticText.setStretchType(JRDesignStaticText.STRETCH_TYPE_RELATIVE_TO_TALLEST_OBJECT);

            staticText.setText(p.getDisplayName());
            band.addElement(staticText);
            
        }

        jasperDesign.setPageHeader(band);

        // Column header
        band = new JRDesignBand();
        jasperDesign.setColumnHeader(band);

        band = new JRDesignBand();
        band.setHeight(25);
        x = 0;
        for (PropertyDescriptor p : props) {
            textField = new JRDesignTextField();
            textField.setX(x);
            textField.setY(0);
            if (p.getName().equals("message")) {
                textField.setWidth(400);
                 x += 400;
            } else {
                textField.setWidth(100);
                 x += 100;
            }
            textField.setHeight(20);
            textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
            textField.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
            textField.setStyle(normalStyle);
            expression = new JRDesignExpression();
            Class type = resolveType(p);
            expression.setValueClass(type);
            expression.setText("$F{" + p.getName() + "}");
            textField.setExpression(expression);
            textField.setStretchWithOverflow(true);
            band.addElement(textField);
           
        }

        jasperDesign.setDetail(band);

        // Column footer
        band = new JRDesignBand();
        jasperDesign.setColumnFooter(band);

        // Page footer
        band = new JRDesignBand();
        jasperDesign.setPageFooter(band);

        // Summary
        band = new JRDesignBand();
        jasperDesign.setSummary(band);

        return jasperDesign;
    }

    private static Class resolveType(PropertyDescriptor d) {
        if (TypeResolver.isIntegerType(d.getPropertyType())) {
            return Integer.class;
        } else if (TypeResolver.isLongType(d.getPropertyType())) {
            return Long.class;
        } else if (TypeResolver.isBooleanType(d.getPropertyType())) {
            return Boolean.class;
        } else {
            return d.getPropertyType();
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


