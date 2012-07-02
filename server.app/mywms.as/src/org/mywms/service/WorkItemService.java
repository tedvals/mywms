package org.mywms.service;

//import java.util.List;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
//import org.mywms.model.Client;
import org.mywms.model.WorkItem;
import org.mywms.model.ItemData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Local
public interface WorkItemService
    extends BasicService<WorkItem> {

    public WorkItem create(ItemData itemDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException;

}
