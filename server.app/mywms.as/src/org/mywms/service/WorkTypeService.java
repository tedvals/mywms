package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
import org.mywms.model.WorkType;

@Local
public interface WorkTypeService
    extends BasicService<WorkType>
{
    public WorkType create();

	public WorkType getByWorkType(String worktype) throws EntityNotFoundException;

}
