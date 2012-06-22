package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.crud.gui.gui_builder.AbstractCompleteVehicleWorkPanel;
import de.linogistix.los.entityservice.WorkVehicleComplete;
import de.linogistix.los.entityservice.WorkVehicleCompletionStatus;

public class CompleteVehicleWorkJPanel extends AbstractCompleteVehicleWorkPanel {

    public CompleteVehicleWorkJPanel(BO bo) {
        super(bo);
    }

    @Override
    public void initCompletionSuccessComboBox() {
        completionSuccessComboBox.removeAllItems();
        WorkVehicleComboBox defaultL = null;
        for (WorkVehicleComplete l : bo.getWorkVehicleCompletionStatuses()) {
            WorkVehicleComboBox item = new WorkVehicleComboBox(l);
            completionSuccessComboBox.addItem(item);
            if (l == WorkVehicleCompletionStatus.SUCCESSFUL) {
                defaultL = item;
            }
        }

        if (defaultL != null) completionSuccessComboBox.setSelectedItem(defaultL);
    }


    public WorkVehicleComplete getSelectedStatus() {
	WorkVehicleComboBox item = (WorkVehicleComboBox) completionSuccessComboBox.getSelectedItem();

        return item.complete;
    }

    //public String getLockCause() {
    public String getCompletionRemarks() {
        return completionRemarksTextArea.getText();
    }

    public void clear() {
        initCompletionSuccessComboBox();
        completionRemarksTextArea.setText("");
    }

    final class WorkVehicleComboBox {
	    /*server side*/
        public WorkVehicleComplete complete;

        WorkVehicleComboBox(WorkVehicleComplete complete) {
            this.complete = complete;
        }

        @Override
        public String toString() {
            return complete.getMessage();
        }
    }

    boolean implIsValid() {
        return true;
    }

    void implReadSettings(Object settings) {
        CompleteVehicleWorkWizard w = (CompleteVehicleWorkWizard)settings;
    }

    void implStoreSettings(Object settings) {
        CompleteVehicleWorkWizard w = (CompleteVehicleWorkWizard)settings;

	//w.setLock(getSelectedLock().getLock());
	w.setCompletionSuccess(getSelectedStatus().getStatus());

	//w.setLockCause(getLockCause());
	w.setCompletionRemarks(getCompletionRemarks());
    }
}
