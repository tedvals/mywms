package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.WorkVehicle;

import de.linogistix.los.inventory.query.dto.WorkVehicleTO;
//import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean; import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Stateless
public class WorkVehicleQueryBean extends BusinessObjectQueryBean<WorkVehicle>
    implements WorkVehicleQueryRemote {

    private static final Logger log = Logger
                                      .getLogger(WorkVehicleQueryBean.class);

    //@Override
    //public String getUniqueNameProp() {
    //return "labelId";
    //}

    private static final String[] dtoProps = new String[] {
        "id", "version",
        "vehicleDataId",
        "vehicleDataId.plateNumber",
        "remarks",
        "workTypeId",
        "workTypeId.worktype",
        "workerId",
        "workerId.name",
        "urgent",
        "scheduleTime",
        "executeDeadline",
    };

    @Override
    protected String[] getBODTOConstructorProps() {
        return dtoProps;
    }

    @Override
    public Class<WorkVehicleTO> getBODTOClass() {
        return WorkVehicleTO.class;
    }

    public LOSResultList<BODTO<WorkVehicle>> queryByDefault(String vehicleDataId, String workTypeId, String workerId, QueryDetail detail) throws BusinessObjectNotFoundException, BusinessObjectQueryException {
        TemplateQuery q = new TemplateQuery();
        q.setBoClass(WorkVehicle.class);

        if( vehicleDataId!= null && vehicleDataId.length() > 0 ) {
            TemplateQueryFilter filter = q.addNewFilter();

            TemplateQueryWhereToken t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.id", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.labelId", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.manufacturerName", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.modelName", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.plateNumber", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.chassisNumber", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

            t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId.engineNumber", vehicleDataId);
            t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            filter.addWhereToken(t);

	    //t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "parent.client.number", master);
	    //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
	    //filter.addWhereToken(t);

	    //ItemDataNumber idn = idnService.getByNumber(master);
	    //if( idn != null ) {
	    //t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "parent.id", idn.getItemData().getId());
	    //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
	    //filter.addWhereToken(t);
	    //}
        }
        //if( child != null && child.length() > 0 ) {
            //TemplateQueryFilter filter = q.addNewFilter();

            //TemplateQueryWhereToken t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "child.number", child);
            //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            //filter.addWhereToken(t);

            //t= new TemplateQueryWhereToken(	TemplateQueryWhereToken.OPERATOR_LIKE, "child.name", child);
            //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            //filter.addWhereToken(t);

            //t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "child.client.number", child);
            //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
            //filter.addWhereToken(t);

            //ItemDataNumber idn = idnService.getByNumber(child);
            //if( idn != null ) {
                //t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "child.id", idn.getItemData().getId());
                //t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
                //filter.addWhereToken(t);
            //}

        //}

	//try {
	//return queryByTemplate(detail, q);
	////return queryByTemplateHandles(detail, q);
	//} catch (BusinessObjectNotFoundException bex) {
	//return new LOSResultList<WorkVehicle>();
	//} catch (Throwable t) {
	//log.error(t.getMessage(), t);
	//throw new BusinessObjectQueryException();
	//}
		try {
			return queryByTemplateHandles(detail, q);
		} catch (BusinessObjectNotFoundException bex) {
			return new LOSResultList<BODTO<WorkVehicle>>();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}
    }

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

        TemplateQueryWhereToken vehicleDataId = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "vehicleDataId", value);
        vehicleDataId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken workTypeId = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "workTypeId", value);
        workTypeId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken workerId = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "workerId", value);
        workerId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        ret.add(vehicleDataId);
        ret.add(workTypeId);
        ret.add(workerId);

        return ret;
    }

}
