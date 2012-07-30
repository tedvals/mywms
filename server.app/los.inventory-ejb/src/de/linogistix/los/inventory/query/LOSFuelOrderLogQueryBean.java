package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.inventory.query.dto.LOSFuelOrderLogTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class LOSFuelOrderLogQueryBean extends
		BusinessObjectQueryBean<LOSFuelOrderLog> implements
		LOSFuelOrderLogQueryRemote {

	private static final String[] props = new String[]{
		"id", "version", "labelId",
		"storLoc.name", "stationPump", 
		"orderReceipient.identityCard", "orderType", 
		"tankRemaining"
	};
	
	@Override
	public String getUniqueNameProp() {
		return "labelId";
	}

	@Override
	public Class<LOSFuelOrderLogTO> getBODTOClass() {
		return LOSFuelOrderLogTO.class;
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return props;
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

		/*List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name",
				value);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderRef = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderReference",
				value);
		orderRef.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber",
				value);
		orderNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(name);
		ret.add(orderNumber);
		ret.add(orderRef);
		
		return ret;*/
	}

}
