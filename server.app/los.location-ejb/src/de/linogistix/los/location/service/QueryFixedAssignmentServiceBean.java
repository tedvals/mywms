package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.ItemData;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Stateless
public class QueryFixedAssignmentServiceBean implements QueryFixedAssignmentService, QueryFixedAssignmentServiceRemote {
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl) {
		
		StringBuffer sb = new StringBuffer("SELECT fla FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName()+" fla ");
		sb.append("WHERE fla.assignedLocation=:sl ");
		
		Query query = manager.createQuery(sb.toString());
		query.setParameter("sl", sl);
		
		try{
			return (LOSFixedLocationAssignment) query.getSingleResult();
		}catch(NoResultException nre){
			return null;
		}
	}

	public boolean existsFixedLocationAssignment(ItemData item) {
		
		StringBuffer sb = new StringBuffer("SELECT count(fla) FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName()+" fla ");
		sb.append("WHERE fla.itemData=:it");
		
		Query query = manager.createQuery(sb.toString());
		query.setParameter("it", item);
		
		Long  count = (Long) query.getSingleResult();
		
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<LOSFixedLocationAssignment> getByItemData(ItemData item) {
		List<LOSFixedLocationAssignment> ret;
		StringBuffer sb = new StringBuffer("SELECT fla FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName()+" fla ");
		sb.append("WHERE fla.itemData=:it");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("it", item);
		
		ret =  query.getResultList();
		return ret;
		
	}

	
}
