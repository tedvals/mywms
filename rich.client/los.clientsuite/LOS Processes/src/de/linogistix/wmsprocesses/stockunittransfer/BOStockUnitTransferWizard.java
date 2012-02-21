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

package de.linogistix.wmsprocesses.stockunittransfer;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.stockunittransfer.gui.component.SUTransferInfoPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class BOStockUnitTransferWizard extends WizardDescriptor implements ActionListener, ChangeListener{
  
  private static final Logger log = Logger.getLogger(BOStockUnitTransferWizard.class.getName());
  
  private BODTO<StockUnit> su;
  
  private BODTO<LOSUnitLoad> ul;
  
  
  private StockUnit stockUnit;
  
  private LOSUnitLoad unitLoad;
  
  private boolean removeReservationFromSu;
  
  private boolean removeLockFromSu;
  
  private String info;
  
  /**
   * Creates a new instance of OrderByWizard
   */
  @SuppressWarnings("unchecked")
  public BOStockUnitTransferWizard(BODTO suTO) throws InstantiationException {
    super(createPanels());
        
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
    putProperty("WizardPanel_contentData", getContentData());
    putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/wmsprocesses/res/img/TransferStockUnit.png"));
    putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferStockUnitToUnitLoad"));
    setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferStockUnitToUnitLoad"));

    setHelpCtx(new HelpCtx("de.linogistix.wmsprocesses.stockunittransfer"));
        
    CursorControl.showWaitCursor();
    try{
      J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
      if (suTO != null){
         setSu(suTO);
      }
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }finally{
      CursorControl.showNormalCursor();
    }
    
    setButtonListener(this);
    
  }
  
  //-------------------------------------------------------------------------------

    public final static Panel[] createPanels() throws InstantiationException{
    List<Panel> panels = new ArrayList<Panel>();
    
    ValidatingPanel p1 = new BOStockUnitTransferPanelChoose(); 
    panels.add(p1);
    
    FinishablePanel p3 = new SUTransferInfoPanel(); 
    panels.add(p3);
    
    return (Panel[])panels.toArray(new Panel[0]);
  }

    public BODTO<StockUnit> getSu() {
        return su;
    }

    public void setSu(BODTO<StockUnit> su) {
        this.su = su;
    }

    public BODTO<LOSUnitLoad> getUl() {
        return ul;
    }

    public void setUl(BODTO<LOSUnitLoad> ul) {
        this.ul = ul;
    }

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }
    
    public JButton getFinishOption(){
        for (Object o : getClosingOptions()){
            if (o instanceof JButton){
                JButton b  = (JButton) o;
                return b;
            }
        }
        
        return null;
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("de.linogistix.wmsprocesses.stockunittransfer");
    }
       
    
    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOStockUnitTransferPanelChoose.contentData"),
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "SUTransferInfoPanel.contentData"),
       
       };
    }
    //-----------------------------------------------------------
            

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(StockUnit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public LOSUnitLoad getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(LOSUnitLoad unitLoad) {
        this.unitLoad = unitLoad;
    }

    public boolean isRemoveReservationFromSu() {
        return removeReservationFromSu;
    }

    public void setRemoveReservationFromSu(boolean removeReservationFromSu) {
        this.removeReservationFromSu = removeReservationFromSu;
    }

    public boolean isRemoveLockFromSu() {
        return removeLockFromSu;
    }

    public void setRemoveLockFromSu(boolean removeLockFromSu) {
        this.removeLockFromSu = removeLockFromSu;
    }

    public void actionPerformed(ActionEvent e) {
        //
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
   

}


