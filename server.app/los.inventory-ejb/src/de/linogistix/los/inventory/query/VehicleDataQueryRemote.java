package de.linogistix.los.inventory.query;

import de.linogistix.los.query.BODTO;
//import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
//import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.Lot;

@Remote
public interface VehicleDataQueryRemote extends BusinessObjectQueryRemote<VehicleData>{ 
  
    //public LOSResultList<BODTO<ItemData>> autoCompletionClientAndLot(String exp, 
	//BODTO<Client> client, 
	//BODTO<Lot> lot, 
	//QueryDetail detail);

}
