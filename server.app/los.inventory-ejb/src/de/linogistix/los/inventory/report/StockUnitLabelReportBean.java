/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.jasperreports.engine.design.JasperDesign;

import org.apache.log4j.Logger;
import org.mywms.globals.DocumentTypes;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.StockUnitService;

import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.StockUnitLabelQueryRemote;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportExceptionKey;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.report.businessservice.ReportServiceBean;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 *
 * @author trautm
 */
@Stateless
public class StockUnitLabelReportBean implements StockUnitLabelReport {

    private static final Logger log = Logger.getLogger(StockUnitLabelReportBean.class);
    @EJB
    StockUnitService suService;
    @EJB
    ReportService repService;
    @EJB
    ClientService clientService;
    @EJB
    StockUnitLabelQueryRemote stockUnitQuery;
    @EJB
    private LOSSystemPropertyService propertyService;
    
    @PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
    
    public StockUnitLabel createStockUnitLabelGR(LOSGoodsReceiptPosition pos, String printer) throws ReportException {
    	StockUnitLabel label = generateStockUnitLabelGR(pos.getClient(), DocumentTypes.APPLICATION_PDF.toString(), pos);
    	manager.persist(label);
    	printStockUnitLabel(label, printer);
    	return label;
    }
    
    public StockUnitLabel createStockUnitLabel(StockUnit su, String printer) throws ReportException {
    	StockUnitLabel label = generateStockUnitLabel(DocumentTypes.APPLICATION_PDF.toString(), su);
    	manager.persist(label);
    	printStockUnitLabel(label, printer);
    	return label;
    }
    
    public StockUnitLabel generateStockUnitLabelGR(Client c, String type, LOSGoodsReceiptPosition pos) throws ReportException {
    	return generateStockUnitLabel(type, pos.getStockUnit());
    }
    
    public StockUnitLabel generateStockUnitLabel(String type, StockUnit su) throws ReportException {
        StockUnitLabel l;


        try {
            su = suService.get(su.getId());
        } catch (EntityNotFoundException ex) {
            throw new ReportException(ReportExceptionKey.ENTITY_NOT_LONGER_PERTINENT, "" + su.toUniqueString());
        }
        l = new StockUnitLabel();
        l.setClient(su.getClient());
        l.setName(su.toUniqueString());
        l.setType(type);

        l.setClientRef(su.getClient().getNumber());
        l.setDateRef(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(su.getCreated()));
        l.setItemdataRef(su.getItemData().getNumber());
        l.setItemNameRef(su.getItemData().getName());
        l.setItemUnit(su.getItemUnit().getUnitName());
        l.setScale(su.getItemData().getScale());
        l.setLotRef(su.getLot()!=null?su.getLot().getName():"");
        l.setLabelID(su.getUnitLoad().getLabelId());
        l.setAmount(su.getAmount().setScale(l.getScale()));
        
        if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
            try {
            	
                List<StockUnitLabel> export = new ArrayList<StockUnitLabel>();
                
                export.add(l);

                JasperDesign d = repService.getJrxmlResource(su.getClient(), InventoryBundleResolver.class, "StockUnitLabelGR.jrxml");
                byte[] bytes = repService.typeExportPdf(l.getName(), l.getType(), d, export);
                l.setDocument(bytes);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new ReportException();
            }
        } else {
            throw new IllegalArgumentException("only pdf supported");
        }

        return l;
    }

    /**
     * Prints a persisted StockUnitLabel.
     * 
     * @param c
     * @param type
     * @param pos
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    public StockUnitLabel printStockUnitLabel(StockUnit su, String printer) throws ReportException{
    	
    	String label = su.getUnitLoad().getLabelId();
    	StockUnitLabel l;
		try {
			l = stockUnitQuery.queryByIdentity(label);
			printStockUnitLabel(l, printer);
		} catch (BusinessObjectNotFoundException e) {
			throw new ReportException(ReportExceptionKey.PRINT_FAILED, label);
		}
        
    	return l;	
    	
    }
    
    /**
     * Prints a StockUnitLabel.
     * 
     * @param c
     * @param type
     * @param pos
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    public StockUnitLabel printStockUnitLabel(StockUnitLabel label, String printer) throws ReportException{

    	if( printer == null ) {
    		printer = propertyService.getStringDefault(LOSGoodsReceiptComponent.PROPERTY_KEY_PRINTER_GR, ReportService.NO_PRINTER);
    	}

		if (repService == null){
			repService = new ReportServiceBean();
		}
		
		repService.print(printer, label.getDocument(), label.getType());

		return label;

    	
    }
    
    StockUnitLabel generateStockUnitLabelGR(String clientRef, String label, String itemDataRef, String lotRef, Date dateRef) throws ReportException {
        String type = DocumentTypes.APPLICATION_PDF.toString();
        StockUnitLabel l;
        Client c;
        
        l = new StockUnitLabel();
        l.setName(label);
        l.setType(type);

        l.setClientRef(clientRef);
        l.setDateRef(dateRef.toString());
        l.setItemdataRef(itemDataRef);
        l.setLotRef(lotRef);
        l.setLabelID(label);

        if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
        	JasperDesign d;
        	
        	try {
            	if (clientService != null){
            		c = clientService.getByNumber(clientRef);
            	} else{
            		c = null;
            	}
                List<StockUnitLabel> export = new ArrayList<StockUnitLabel>();
                export.add(l);
                if (repService == null) {
                    repService = new ReportServiceBean();
                }
                if (c != null){
                	d = repService.getJrxmlResource(c, InventoryBundleResolver.class, "StockUnitLabelGR.jrxml");
                } else{
                	d = repService.getJrxmlResource(InventoryBundleResolver.class, "StockUnitLabelGR.jrxml");
                }
                byte[] bytes = repService.typeExportPdf(l.getName(), l.getType(), d, export);
                l.setDocument(bytes);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new ReportException();
            }
        } else {
            throw new IllegalArgumentException("only pdf supported");
        }

        return l;
    }


}
