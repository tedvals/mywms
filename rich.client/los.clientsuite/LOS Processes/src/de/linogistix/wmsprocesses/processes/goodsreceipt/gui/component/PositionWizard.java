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
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class PositionWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(PositionWizard.class.getName());

    public PositionWizardModel model;
    
    public static int goodsInDefaultLock = 0;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public PositionWizard(BODTO<Client> clientTO, LOSGoodsReceipt gr, LOSAdviceTO selectedAdvice, boolean isSingleUnitLoad) throws InstantiationException {
        super(createPanels());
        
        this.model = new PositionWizardModel();
        this.model.gr = gr;
        this.model.client = clientTO;
        this.model.lock = goodsInDefaultLock;
        this.model.isSingleUnitLoad = isSingleUnitLoad;
        
        if(selectedAdvice != null){
            
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSAdviceQueryRemote advQuery;
            
            try {
                advQuery = loc.getStateless(LOSAdviceQueryRemote.class);

                this.model.selectedAdvice = advQuery.queryById(selectedAdvice.getId());
                this.model.selectedAdviceTO = selectedAdvice;
                
                UnitLoadType ult = model.selectedAdvice.getItemData().getDefaultUnitLoadType();
                if(ult != null){
                    model.ulType = new BODTO<UnitLoadType>(ult.getId(), ult.getVersion(), ult.getName());
                }

            } catch(FacadeException fe){
                ExceptionAnnotator.annotate(fe);
            }

        }
        
        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardDescriptor.setTitleFormat",
                "{0} " + NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "wizard") + "  ({1})");
        putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.createPosition"));
        
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.createPosition"));      

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        
        List<Panel> panels = new ArrayList<Panel>();
        
        ValidatingPanel p1 = new PositionWizardULPanel();
        panels.add(p1);
        
        ValidatingPanel p2 = new PositionWizardSUPanel();
        panels.add(p2);
        
        ValidatingPanel p2a = new PositionWizardLotPanel();
        panels.add(p2a);
        
        goodsInDefaultLock = 0;
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            
            LOSSystemPropertyServiceRemote propQuery;
            propQuery = loc.getStateless(LOSSystemPropertyServiceRemote.class);

            goodsInDefaultLock = (int)propQuery.getLongDefault(loc.getWorkstationName(), "GOODS_IN_DEFAULT_LOCK", 0);
            
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
           
        }
        
        if(goodsInDefaultLock == 0){
            ValidatingPanel p3 = new PositionWizardQMPanel();
            panels.add(p3);
        }
        
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


