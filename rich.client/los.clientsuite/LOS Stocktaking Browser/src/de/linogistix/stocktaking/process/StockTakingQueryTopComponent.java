/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryTopComponent;
import de.linogistix.stocktaking.process.gui.StockTakingRecordPanel;
import de.linogistix.stocktaking.process.gui.StockTakingFooterPanel;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class StockTakingQueryTopComponent extends BOQueryTopComponent{
    StockTakingFooterPanel footerPanel;
    StockTakingRecordPanel stockTakingProcessPanel;
    StockTakingProcessController controller;
    
    StockTakingQueryTopComponent(BONode node, boolean editableDetail){
        super(node, editableDetail);
    }

    public StockTakingProcessController getController() {
        return controller;
    }

    @Override
    protected void postInit() {
        if (!hasBeenInitialized()){
            this.controller = new StockTakingProcessController(this);
            createTabbedPane();

            stockTakingProcessPanel = new StockTakingRecordPanel(this);
            addTab(NbBundle.getMessage(StocktakingBundleResolver.class, "Records"),getStockTakingProcessPanel());

            footerPanel = new StockTakingFooterPanel(controller);
            addFooterPanel(getFooterPanel());

            super.postInit();
        }
    }

    public StockTakingFooterPanel getFooterPanel() {
        return footerPanel;
    }

    public StockTakingRecordPanel getStockTakingProcessPanel() {
        return stockTakingProcessPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return  new HelpCtx("de.linogistix.stocktaking.dialog");
    }

    
}
