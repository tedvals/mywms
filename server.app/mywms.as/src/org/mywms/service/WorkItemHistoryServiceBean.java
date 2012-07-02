package org.mywms.service;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.WorkItemHistory;
import org.mywms.model.ItemData;
import org.mywms.model.WorkType;
import org.mywms.model.User;

@Stateless
public class WorkItemHistoryServiceBean
    extends BasicServiceBean<WorkItemHistory>
    implements WorkItemHistoryService {

    private static final Logger log  = Logger.getLogger(WorkItemHistoryServiceBean.class);

    public WorkItemHistory create(ItemData itemDataId, WorkType workTypeId, User workerId) throws EntityNotFoundException {

        if ( itemDataId == null ) {
            throw new NullPointerException("itemDataId  == null");
        }

        if ( workTypeId == null ) {
            throw new NullPointerException("workTypeId  == null");
        }

        if ( workerId == null ) {
            throw new NullPointerException("workerId  == null");
        }

        WorkItemHistory workItem = new WorkItemHistory();

        workItem.setItemDataId(itemDataId);
        workItem.setWorkTypeId(workTypeId);
        workItem.setWorkerId(workerId);

        manager.persist(workItem);

        log.info("CREATED workItemHistory: " + workItem.toDescriptiveString());

        manager.flush();

        return workItem;
    }

}
