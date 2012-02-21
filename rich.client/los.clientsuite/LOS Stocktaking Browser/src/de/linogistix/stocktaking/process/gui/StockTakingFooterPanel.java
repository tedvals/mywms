/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process.gui;


import de.linogistix.common.gui.gui_builder.AbstractFooterPanel;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.stocktaking.process.StockTakingProcessController;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.netbeans.api.javahelp.Help;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class StockTakingFooterPanel extends AbstractFooterPanel {
    
    private JButton recountButton;

    JButton finishButton;
    
    StockTakingProcessController controller;
    
    public StockTakingFooterPanel(StockTakingProcessController controller) {
        this.controller = controller;
        this.recountButton.setEnabled(false);
        this.finishButton.setEnabled(false);
    }
   
    @Override
    public List<JButton> getButtonList() {
       
        recountButton = new JButton();
        getRecountButton().setText(NbBundle.getMessage(StocktakingBundleResolver.class, "recount"));
        getRecountButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recountButtonActionPerformedListener(evt);
            }
        });
        
        finishButton = new JButton();
        getFinishButton().setText(NbBundle.getMessage(StocktakingBundleResolver.class, "acceptCount"));
        getFinishButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishButtonActionPerformedListener(evt);
            }
        });
        
        List<JButton> buttonList = new ArrayList<JButton>();
        buttonList.add(getRecountButton());
        buttonList.add(getFinishButton());
        
        return buttonList;
    }

    protected void recountButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        
         String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "ReallyProcessRecount");
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            DialogDisplayer.getDefault().notify(d);
            if (d.getValue().equals(NotifyDescriptor.OK_OPTION)){
                try {
                    controller.processRecount();
                } catch (Throwable t) {
                    ExceptionAnnotator.annotate(t);
                }
            } else if (d.getValue().equals(NotifyDescriptor.CANCEL_OPTION)){
                return;
            }

    }

    protected void finishButtonActionPerformedListener(ActionEvent evt) {
        String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "ReallyProcessAccept");
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            DialogDisplayer.getDefault().notify(d);
            if (d.getValue().equals(NotifyDescriptor.OK_OPTION)){
                try {
                    controller.processAccept();
                } catch (Throwable t) {
                    ExceptionAnnotator.annotate(t);
                }
            } else if (d.getValue().equals(NotifyDescriptor.CANCEL_OPTION)){
                return;
            }
       
    }

    public JButton getRecountButton() {
        return recountButton;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    @Override
    protected void flatButtonActionPerformedListener(ActionEvent evt) {
        Help help = (Help) Lookup.getDefault().lookup(Help.class);
        help.showHelp(controller.getTopComponent().getHelpCtx());
    }
    
    
    
    
    
}
