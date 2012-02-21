/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.log.gui.component;

import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.system.SystemUtil;
import de.linogistix.logviewer.processes.log.gui.gui_builder.AbstractCenterPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class CenterPanel extends AbstractCenterPanel implements TopComponentListener {
    TopComponentPanel topComponentPanel;
    LogTreeTableView logTreeTableView;
    private InitThread thInit;    
    
    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        logTreeTableView = new LogTreeTableView();
        add(logTreeTableView, BorderLayout.CENTER);       
        logTreeTableView.addMouseListener();
    }


    
    private void startJMSThread() {
        thInit = new InitThread();
        thInit.setPriority(Thread.MAX_PRIORITY);
        thInit.start();
    }

    private void stopJMSThread() {
        if (thInit != null) {
            thInit.interrupt();
        }    
    }

    public class InitThread extends Thread {

        public void run() {
            Thread thisThread = Thread.currentThread();
            while (thInit == thisThread) {
                try {
                    if (isInterrupted()) {
                        break;
                    }
                if (SystemUtil.isConnected()) {
                    if (logTreeTableView.hasTableFocus() == false) {
                        SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                                        
                                }
                            });
                    }                    
                }                    
                Thread.currentThread().sleep(5000);
                } catch (InterruptedException ex) {
                    
                }
            }
        }
    }
    
    int delay = 5000; //milliseconds
    ActionListener taskPerformer = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            if (logTreeTableView.hasTableFocus() == false) {
                if (SystemUtil.isConnected()) {
//                    logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                    
                }
            }
        }
    };

    
    protected void infoToggleButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                            
    }

    protected void errorToggleButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                    
    }
    
    protected void clearingToggleButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                    
    }
    

    public void componentOpened() {
        logTreeTableView.setLogNodes(getButttonState(),isClearingButtonSelected());                    
        startJMSThread();
    }

    public void componentClosed() {
        stopJMSThread();
    }

    public void componentActivated() {

    }

    public void componentDeactivated() {

    }

    public void componentHidden() {

    }

    public void componentShowing() {

    }
    
    
    
}
