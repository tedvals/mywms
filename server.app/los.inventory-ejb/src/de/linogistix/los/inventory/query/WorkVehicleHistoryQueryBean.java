package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.WorkVehicleHistory;

import de.linogistix.los.inventory.query.dto.WorkVehicleHistoryTO;
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
public class WorkVehicleHistoryQueryBean extends BusinessObjectQueryBean<WorkVehicleHistory>
    implements WorkVehicleHistoryQueryRemote {

    private static final Logger log = Logger
                                      .getLogger(WorkVehicleHistoryQueryBean.class);

    @Override
    public String getUniqueNameProp() {
        return "labelId";
    }

    private static final String[] dtoProps = new String[] {
        "id", "version", "labelId",
        "vehicleDataId.labelId",
        "remarks",
        "workTypeId.worktype",
        "workerId.name",
        "urgent",
        "scheduleTime",
        "executeDeadline",
	"completionSuccess",
	"completionDate",
	"completionRemarks"
    };

    @Override
    protected String[] getBODTOConstructorProps() {
        return dtoProps;
    }

    @Override
    public Class<WorkVehicleHistoryTO> getBODTOClass() {
        return WorkVehicleHistoryTO.class;
    }

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

        TemplateQueryWhereToken labelId = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
            value);
        labelId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
        ret.add(labelId);

        return ret;
    }
}
