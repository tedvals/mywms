/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.changeamount.component;

import de.linogistix.wmsprocesses.changeamount.gui_builder.AbstractDataPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.math.BigDecimal;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class DataPanel extends AbstractDataPanel{

    public DataPanel(){
        getStockUnitAutoFilteringComboBox().setEditorLabelTitle(
                NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "stockUnit"));
        getStockUnitAutoFilteringComboBox().setEditorLabelText();
    }
    
    void implReadSettings(Object settings) {
        //
    }

    void implStoreSettings(Object settings) {
       //
    }

    public BigDecimal getAmount(){
        return (BigDecimal) amountTextField.getValue();
    }
    
    public BigDecimal getReservedAmount(){
        return (BigDecimal) reservedAmountTextfield.getValue();
    }

    void setAmount(BigDecimal amount) {
        amountTextField.setValue(amount);
    }

    void setReserveAmount(BigDecimal reserveAmount) {
        reservedAmountTextfield.setValue(reserveAmount);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOStockUnitChangeAmountPanelData.contentData");
    }
    
    
    
    

    
}
