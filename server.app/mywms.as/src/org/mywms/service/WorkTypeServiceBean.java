package org.mywms.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.WorkType;

@Stateless
public class WorkTypeServiceBean
    extends BasicServiceBean<WorkType>
    implements WorkTypeService
{
	private static final Logger log  = Logger.getLogger(WorkTypeServiceBean.class);
    
    public WorkType create()
    {
        WorkType worktype = new WorkType();
        manager.persist(worktype);

        log.info("CREATED WorkType: " + worktype.toDescriptiveString());
        
		manager.flush();

        return worktype;
    }

	public WorkType getByWorkType(String worktype) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                + WorkType.class.getSimpleName()
                + " su "
                + " WHERE su.worktype= :adId");
        query = query.setParameter("adId", worktype);

        try{
        	WorkType ret = (WorkType) query.getSingleResult();
        	return ret;
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }

        
    }

}
