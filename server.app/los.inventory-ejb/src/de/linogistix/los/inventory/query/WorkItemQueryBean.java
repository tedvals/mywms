package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.WorkItem;

import de.linogistix.los.inventory.query.dto.WorkItemTO;
//import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Stateless
public class WorkItemQueryBean extends BusinessObjectQueryBean<WorkItem>
		implements WorkItemQueryRemote {

	private static final Logger log = Logger
			.getLogger(WorkItemQueryBean.class);
	
	@Override
    public String getUniqueNameProp() {
        return "labelId";
    }

	private static final String[] dtoProps = new String[] { 
			"id", "version", "labelId",
			"itemDataId.number",
			"remarks",
			"workTypeId.worktype",
			"workerId.name",
			"urgent",
			"scheduleTime",
			"executeDeadline"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<WorkItemTO> getBODTOClass() {
		return WorkItemTO.class;
	}

	//public LOSResultList<BODTO<WorkItem>> queryByDefault(String itemData, String workType, String worker, QueryDetail d) throws BusinessObjectNotFoundException, BusinessObjectQueryException {
		//TemplateQuery q = new TemplateQuery();
		//q.setBoClass(WorkItem.class);

		//if (itemData != null && itemData.length() > 0) {
			//TemplateQueryFilter filter = q.addNewFilter();

			//TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE, "itemDataId.id",
					//itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.labelId", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.manufacturerName", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.modelName", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.plateNumber", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.chassisNumber", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//t = new TemplateQueryWhereToken(
					//TemplateQueryWhereToken.OPERATOR_LIKE,
					//"itemDataId.engineNumber", itemData);
			//t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//filter.addWhereToken(t);

			//// t = new
			//// TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
			//// "parent.client.number", master);
			//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//// filter.addWhereToken(t);

			//// ItemDataNumber idn = idnService.getByNumber(master);
			//// if( idn != null ) {
			//// t = new
			//// TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL,
			//// "parent.id", idn.getItemData().getId());
			//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			//// filter.addWhereToken(t);
			//// }
		//}
		//// if( child != null && child.length() > 0 ) {
		//// TemplateQueryFilter filter = q.addNewFilter();

		//// TemplateQueryWhereToken t= new
		//// TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
		//// "child.number", child);
		//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		//// filter.addWhereToken(t);

		//// t= new TemplateQueryWhereToken(
		//// TemplateQueryWhereToken.OPERATOR_LIKE, "child.name", child);
		//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		//// filter.addWhereToken(t);

		//// t = new
		//// TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
		//// "child.client.number", child);
		//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		//// filter.addWhereToken(t);

		//// ItemDataNumber idn = idnService.getByNumber(child);
		//// if( idn != null ) {
		//// t = new
		//// TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL,
		//// "child.id", idn.getItemData().getId());
		//// t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		//// filter.addWhereToken(t);
		//// }

		//// }

		//// try {
		//// return queryByTemplate(detail, q);
		//// //return queryByTemplateHandles(detail, q);
		//// } catch (BusinessObjectNotFoundException bex) {
		//// return new LOSResultList<WorkItem>();
		//// } catch (Throwable t) {
		//// log.error(t.getMessage(), t);
		//// throw new BusinessObjectQueryException();
		//// }
		//try {
			//return queryByTemplateHandles(d, q);
		//} catch (BusinessObjectNotFoundException bex) {
			//return new LOSResultList<BODTO<WorkItem>>();
		//} catch (Throwable t) {
			//log.error(t.getMessage(), t);
			//throw new BusinessObjectQueryException();
		//}
	//}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();
		
		//Long id;
		//try{
			//id = Long.parseLong(value);
		//} catch (Throwable t){
			//id = new Long(-1);
		//}
		//TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
				//TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		//idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		//ret.add(idt);
		
		TemplateQueryWhereToken labelId = new TemplateQueryWhereToken(
		TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
		value);
		labelId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(labelId);
		
		/*TemplateQueryWhereToken itemDataId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemDataId.labelId",
				value);
		itemDataId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken workTypeId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "workTypeId.worktype", value);
		workTypeId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken workerId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "workerId.name", value);
		workerId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		ret.add(itemDataId);
		ret.add(workTypeId);
		ret.add(workerId);*/
		
		return ret;
	}

/*	public LOSResultList<WorkItem> queryByLabelId(QueryDetail detail,
			String vdId) throws BusinessObjectNotFoundException,
			BusinessObjectQueryException {
		TemplateQuery q = new TemplateQuery();
		q.setBoClass(tClass);
		TemplateQueryWhereToken l = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "labelId", vdId);
		q.addWhereToken(l);
		
		return queryByTemplate(detail, q);
	}*/
}
