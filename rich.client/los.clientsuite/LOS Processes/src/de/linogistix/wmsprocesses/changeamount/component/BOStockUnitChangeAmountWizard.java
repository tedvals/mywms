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
package de.linogistix.wmsprocesses.changeamount.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.model.StockUnit;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOStockUnitChangeAmountWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(BOStockUnitChangeAmountWizard.class.getName());
    private ManageInventoryFacade manageInventoryFacade;
    private BODTO<StockUnit> su;
    private StockUnit stockUnit;
    private BigDecimal amount = new BigDecimal(0);
    BigDecimal reserveAmount = new BigDecimal(0);
    private boolean releaseReservation = false;
    private String info = null;

    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public BOStockUnitChangeAmountWizard(BODTO suTO) throws InstantiationException {
        super(createPanels());


        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/wmsprocesses/res/img/ChangeAmountStockUnit.png"));
        putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(WMSProcessesBundleResolver.class, "changeAmount"));
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "changeAmount"));

        setHelpCtx(new HelpCtx("de.linogistix.wmsprocesses.changeamount"));

        CursorControl.showWaitCursor();
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            this.manageInventoryFacade = loc.getStateless(ManageInventoryFacade.class);
            if (suTO != null) {
                setSu(suTO);
            }
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList();

        ValidatingPanel p1 = new BOStockUnitChangeAmountPanelData();
        panels.add(p1);

        FinishablePanel p3 = new ChangeAmountInfoPanel();
        panels.add(p3);

        return (Panel[]) panels.toArray(new Panel[0]);
    }

    public BODTO<StockUnit> getSu() {
        return su;
    }

    public void setSu(BODTO<StockUnit> su) {
        this.su = su;
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

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(StockUnit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public void actionPerformed(ActionEvent e) {
        //
    }

    public ManageInventoryFacade getManageInventoryFacade() {
        return manageInventoryFacade;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(BigDecimal reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public boolean isReleaseReservation() {
        return releaseReservation;
    }

    public void setReleaseReservation(boolean releaseReservation) {
        this.releaseReservation = releaseReservation;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOStockUnitChangeAmountPanelData.contentData"),
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ChangeAmountInfoPanel.contentData"),
       
       };
    }
    
}


