package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import de.linogistix.los.inventory.model.LOSOrderReceipients;

import de.linogistix.los.inventory.query.dto.LOSOrderReceipientsTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Stateless
public class LOSOrderReceipientsQueryBean extends BusinessObjectQueryBean<LOSOrderReceipients>
    implements LOSOrderReceipientsQueryRemote{

    private static final Logger log = Logger
                                      .getLogger(LOSOrderReceipientsQueryBean.class);

    @Override
    public String getUniqueNameProp() {
        return "identityCard";
    }

    private static final String[] dtoProps = new String[] { 
        "id", "version", "identityCard",
	"personnelId",
	"firstName",
	"lastName",
	"rankAbbr",
	"rank",
	"organizationUnit",
	"phone",
	"vpnPhone",
	"tokenId"
                                                          };

    @Override
	    protected String[] getBODTOConstructorProps() {
		    return dtoProps;
	    }

    @Override
    public Class<LOSOrderReceipientsTO> getBODTOClass() {
        return LOSOrderReceipientsTO.class;
    }

    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
        List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

        TemplateQueryWhereToken identityCard = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "identityCard", value);
        identityCard.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken lastName = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "lastName", value);
        lastName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        TemplateQueryWhereToken organizationUnit = new TemplateQueryWhereToken(
            TemplateQueryWhereToken.OPERATOR_LIKE, "organizationUnit", value);
        organizationUnit.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

        ret.add(identityCard);
        ret.add(lastName);
        ret.add(organizationUnit);

        return ret;
    }

    //public LOSResultList<VehicleData> queryByPlateNumber( QueryDetail detail, String vdId)
    //throws BusinessObjectNotFoundException, BusinessObjectQueryException {
        //TemplateQuery q = new TemplateQuery();
        //q.setBoClass(tClass);
        //TemplateQueryWhereToken l = new TemplateQueryWhereToken(
            //TemplateQueryWhereToken.OPERATOR_EQUAL, "plateNumber", vdId);
        //q.addWhereToken(l);

        //return queryByTemplate(detail, q);
    //}

}
