/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.stockunittransfer.gui.component;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.stockunittransfer.BOStockUnitTransferWizard;
import de.linogistix.wmsprocesses.stockunittransfer.gui.gui_builder.AbstractChooseSourceDestinationPanel;
import java.util.logging.Logger;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class ChooseSourceDestinationPanel extends AbstractChooseSourceDestinationPanel {

    private static final Logger log = Logger.getLogger(ChooseSourceDestinationPanel.class.getName());
    
    private BOStockUnitTransferWizard w;
            
    public ChooseSourceDestinationPanel(BOStockUnitTransferWizard w) {
        this.w=w;
        getStockUnitAutoFilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "stockUnit"));
        getStockUnitAutoFilteringComboBox().setEditorLabelText();
        getStockUnitAutoFilteringComboBox().setEnabled(true);
       
        getUnitLoadAutofilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "destinationUnitLoad"));
        getUnitLoadAutofilteringComboBox().setEditorLabelText();
        getUnitLoadAutofilteringComboBox().setEnabled(true);
        
        clear();
    }

    void clear() {

        getStockUnitAutoFilteringComboBox().clear();
        getUnitLoadAutofilteringComboBox().clear();
        setAdditionalFields();
    }

    public boolean implIsValid() {      
        return true;
    }

    public void implReadSettings(Object settings) {

    }

    public void implStoreSettings(Object settings) {      
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOStockUnitTransferPanelChoose.contentData");
    }
    

    public void setAdditionalFields() {
        StockUnit su = getStockUnitAutoFilteringComboBox().getSelectedAsEntity();
        LOSUnitLoad ul = getUnitLoadAutofilteringComboBox().getSelectedAsEntity();

        getItemDataField().setText("");
        getAmountField().setText("");
        getUnitLoadField().setText("");
        getLocationField().setText("");
        getLocationUlField().setText("");

        if( ul != null ) {
            LOSStorageLocation loc = ul.getStorageLocation();
            if( loc != null )
                getLocationUlField().setText(loc.getName());
        }
        if( su != null ) {
            ItemData idat = su.getItemData();
            if( idat != null ) {
                getItemDataField().setText( idat.getNumber() + " ("+idat.getName()+")");
                String amount = su.getAmount()+" "+idat.getHandlingUnit().getUnitName();
                getAmountField().setText(amount);
            }
            LOSUnitLoad sul = (LOSUnitLoad)su .getUnitLoad();
            if( sul != null ) {
                getUnitLoadField().setText(sul.getLabelId());
                LOSStorageLocation loc = sul.getStorageLocation();
                if( loc != null )
                    getLocationField().setText(loc.getName());
            }
        }
    }

    public void setRemoveReservationFromSu(boolean removeReservationFromSu) {
        this.w.setRemoveReservationFromSu(removeReservationFromSu);
        this.w.stateChanged(null);
    }

    public void setRemoveLockFromSu(boolean removeLockFromSu) {
        this.w.setRemoveLockFromSu(removeLockFromSu);
        this.w.stateChanged(null);
    }

}
