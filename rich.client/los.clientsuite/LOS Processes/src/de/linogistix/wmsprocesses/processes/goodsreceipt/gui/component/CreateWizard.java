/*
 * OrderByWizard.java
 *
 * Created on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.model.Client;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class CreateWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(CreateWizard.class.getName());

    GoodsReceiptController controller;
    
    BODTO<Client> client;
        
    String deliverer;
    
    String externNumber;
    
    String info;
    
    BODTO<LOSStorageLocation> gate;
    
    Date date = new Date();
    
    boolean allowChangeOfClient = true;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public CreateWizard(GoodsReceiptController controller) throws InstantiationException {
        super(createPanels());
        this.controller = controller;
        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
        putProperty("WizardDescriptor.setTitleFormat",
                "{0} " + NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "wizard") + "  ({1})");
        putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateWizard.create"));
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateWizard.create"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(WMSProcessesBundleResolver.class,"BOCreateWizard.HelpCtx"));

        CursorControl.showWaitCursor();
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        FinishablePanel p1 = new CreateWizardDetailPanel();
        panels.add(p1);
        
        FinishablePanel p2 = new CreateWizardInfoPanel();
        panels.add(p2);
        
        return (Panel[]) panels.toArray(new Panel[0]);
    }

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }

    public JButton getFinishOption() {
        for (Object o : getClosingOptions()) {
            if (o instanceof JButton) {
                JButton b = (JButton) o;
                return b;
            }
        }

        return null;
    }
    //-----------------------------------------------------------

   
    public void actionPerformed(ActionEvent e) {
        //
    }
}


