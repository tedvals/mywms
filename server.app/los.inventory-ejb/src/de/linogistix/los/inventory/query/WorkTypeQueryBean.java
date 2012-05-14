package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.WorkType;

import de.linogistix.los.inventory.query.dto.WorkTypeTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Stateless
public class WorkTypeQueryBean extends BusinessObjectQueryBean<WorkType>
    implements WorkTypeQueryRemote {

    private static final Logger log = Logger
                                      .getLogger(WorkTypeQueryBean.class);

    @Override
    public String getUniqueNameProp() {
        return "worktype";
    }

    private static final String[] dtoProps = new String[] { 
        "id", "version", "worktype",
        "remarks",
    	"periodic",
    	"periodicCircle",
    	"completionTime"
                                                          };

    @Override
	    protected String[] getBODTOConstructorProps() {
		    return dtoProps;
	    }

    @Override
    public Class<WorkTypeTO> getBODTOClass() {
        return WorkTypeTO.class;
    }

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

//TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
        //TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
        //getUniqueNameProp(),
        //value);
//idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken worktype = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "worktype", value);
        worktype.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

	//TemplateQueryWhereToken periodicCircle = new TemplateQueryWhereToken(
		//TemplateQueryWhereToken.OPERATOR_EQUAL, "periodicCircle", value);
	//periodicCircle.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

	//TemplateQueryWhereToken completionTime = new TemplateQueryWhereToken(
		//TemplateQueryWhereToken.OPERATOR_EQUAL, "completionTime", value);
	//completionTime.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

//ret.add(idt);
        ret.add(worktype);
	//ret.add(periodicCircle);
	//ret.add(completionTime);

        return ret;
    }

    //public LOSResultList<WorkType> queryByWorkType(QueryDetail detail, String vdId)
    //throws BusinessObjectNotFoundException, BusinessObjectQueryException {
        //TemplateQuery q = new TemplateQuery();
        //q.setBoClass(tClass);
        //TemplateQueryWhereToken l = new TemplateQueryWhereToken(
            //TemplateQueryWhereToken.OPERATOR_EQUAL, "worktype", vdId);
        //q.addWhereToken(l);

        //return queryByTemplate(detail, q);
    //}

}
