/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

//import java.util.List;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.Zone;

@Local
public interface VehicleDataService
    extends BasicService<VehicleData>
{
    public VehicleData create();

	public VehicleData getByLabelId(String labelId) throws EntityNotFoundException;

}
