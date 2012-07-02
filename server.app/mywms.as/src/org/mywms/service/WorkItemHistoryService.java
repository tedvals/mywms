package org.mywms.service;

//import java.util.List;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
//import org.mywms.model.Client;
import org.mywms.model.WorkItemHistory;
import org.mywms.model.ItemData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Local
public interface WorkItemHistoryService
    extends BasicService<WorkItemHistory> {

    public WorkItemHistory create(ItemData itemDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException;

}
