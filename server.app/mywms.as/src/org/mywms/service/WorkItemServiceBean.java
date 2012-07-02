package org.mywms.service;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.WorkItem;
import org.mywms.model.ItemData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Stateless
public class WorkItemServiceBean
    extends BasicServiceBean<WorkItem>
    implements WorkItemService {

    private static final Logger log  = Logger.getLogger(WorkItemServiceBean.class);

    public WorkItem create(ItemData itemDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException {

        if ( itemDataId == null ) {
            throw new NullPointerException("itemDataId  == null");
        }

        if ( workTypeId == null ) {
            throw new NullPointerException("workTypeId  == null");
        }

        if ( workerId == null ) {
            throw new NullPointerException("workerId  == null");
        }

        WorkItem workItem = new WorkItem();

        workItem.setItemDataId(itemDataId);
        workItem.setWorkTypeId(workTypeId);
        workItem.setWorkerId(workerId);

        manager.persist(workItem);

        log.info("CREATED workItem: " + workItem.toDescriptiveString());

        manager.flush();

        return workItem;
    }

}
