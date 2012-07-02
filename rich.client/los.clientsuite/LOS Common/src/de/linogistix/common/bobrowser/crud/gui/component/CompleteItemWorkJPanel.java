package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.crud.gui.gui_builder.AbstractCompleteItemWorkPanel;
import de.linogistix.los.entityservice.WorkItemComplete;
import de.linogistix.los.entityservice.WorkItemCompletionStatus;

public class CompleteItemWorkJPanel extends AbstractCompleteItemWorkPanel {

    public CompleteItemWorkJPanel(BO bo) {
        super(bo);
    }

    @Override
    public void initCompletionSuccessComboBox() {
        completionSuccessComboBox.removeAllItems();
        WorkItemComboBox defaultL = null;
        for (WorkItemComplete l : bo.getWorkItemCompletionStatuses()) {
            WorkItemComboBox item = new WorkItemComboBox(l);
            completionSuccessComboBox.addItem(item);
            if (l == WorkItemCompletionStatus.SUCCESSFUL) {
                defaultL = item;
            }
        }

        if (defaultL != null) completionSuccessComboBox.setSelectedItem(defaultL);
    }


    public WorkItemComplete getSelectedStatus() {
	WorkItemComboBox item = (WorkItemComboBox) completionSuccessComboBox.getSelectedItem();

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

    final class WorkItemComboBox {
	    /*server side*/
        public WorkItemComplete complete;

        WorkItemComboBox(WorkItemComplete complete) {
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
        CompleteItemWorkWizard w = (CompleteItemWorkWizard)settings;
    }

    void implStoreSettings(Object settings) {
        CompleteItemWorkWizard w = (CompleteItemWorkWizard)settings;

	//w.setLock(getSelectedLock().getLock());
	w.setCompletionSuccess(getSelectedStatus().getStatus());

	//w.setLockCause(getLockCause());
	w.setCompletionRemarks(getCompletionRemarks());
    }
}
