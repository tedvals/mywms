package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.VehicleData;

import de.linogistix.los.inventory.query.dto.VehicleDataTO;
//import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Stateless
public class VehicleDataQueryBean extends BusinessObjectQueryBean<VehicleData>
    implements VehicleDataQueryRemote {

    private static final Logger log = Logger
                                      .getLogger(VehicleDataQueryBean.class);

    @Override
    public String getUniqueNameProp() {
        return "labelId";
    }

    private static final String[] dtoProps = new String[] { 
        "id", "version", "plateNumber",
        "remarks",
        "manufacturerName",
        "modelName",
        "chassisNumber",
        "engineNumber",
        "receiptDate",
        "storageDate",
        "mileage",
	"hoursMeter",
	"categoryId",
	"typeId",
	"stnr",
	"labelId",
	"fuelType",
	"organizationUnit",
	"workingCondition"
                                                          };

    @Override
	    protected String[] getBODTOConstructorProps() {
		    return dtoProps;
	    }

    @Override
    public Class<VehicleDataTO> getBODTOClass() {
        return VehicleDataTO.class;
    }

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

//TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
        //TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
        //getUniqueNameProp(),
        //value);
//idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

//TemplateQueryWhereToken plateNumber = new TemplateQueryWhereToken(
//TemplateQueryWhereToken.OPERATOR_LIKE, "plateNumber", value);
//plateNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken chassisNumber = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "chassisNumber", value);
        chassisNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken engineNumber = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "engineNumber", value);
        engineNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

//ret.add(idt);
//ret.add(plateNumber);
        ret.add(chassisNumber);
        ret.add(engineNumber);

        return ret;
    }

    public LOSResultList<VehicleData> queryByPlateNumber( QueryDetail detail, String vdId)
    throws BusinessObjectNotFoundException, BusinessObjectQueryException {
        TemplateQuery q = new TemplateQuery();
        q.setBoClass(tClass);
        TemplateQueryWhereToken l = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_EQUAL, "plateNumber", vdId);
        q.addWhereToken(l);

        return queryByTemplate(detail, q);
    }

}
