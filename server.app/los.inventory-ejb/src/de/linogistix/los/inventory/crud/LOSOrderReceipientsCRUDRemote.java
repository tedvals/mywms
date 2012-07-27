package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.model.LOSOrderReceipients;

@Remote
public interface LOSOrderReceipientsCRUDRemote extends BusinessObjectCRUDRemote<LOSOrderReceipients>{

}
