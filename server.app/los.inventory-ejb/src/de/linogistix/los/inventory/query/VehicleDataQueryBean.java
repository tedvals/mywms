package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

//import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
//import org.mywms.model.Client;
import org.mywms.model.VehicleData;
//import org.mywms.model.ItemDataNumber;
//import org.mywms.model.Lot;

import de.linogistix.los.inventory.query.dto.VehicleDataTO;
//import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
//import de.linogistix.los.query.LOSResultList;
//import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class VehicleDataQueryBean extends BusinessObjectQueryBean<VehicleData>
		implements VehicleDataQueryRemote {

	private static final Logger log = Logger
			.getLogger(VehicleDataQueryBean.class);

	// @EJB
	// ItemDataNumberService idnService;

	@Override
	public String getUniqueNameProp() {
		return "id";
	}

	private static final String[] dtoProps = new String[] { "id",
			// "version",
			"plateNumber", "chassisNumber", "engineNumber" };

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
		
//		TemplateQueryWhereToken id = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", value);
//		id.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(id);

		TemplateQueryWhereToken plateNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "plateNumber", value);
		plateNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(plateNumber);

		TemplateQueryWhereToken chassisNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "chassisNumber", value);
		chassisNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(chassisNumber);

		TemplateQueryWhereToken engineNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "engineNumber", value);
		engineNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(engineNumber);

		// I think, that it is not possible make this in one query with JPA and
		// Hibernate 3.1
		// ItemDataNumber idn = idnService.getByNumber(value);
		// if( idn != null ) {
		// TemplateQueryWhereToken numbers = new TemplateQueryWhereToken(
		// TemplateQueryWhereToken.OPERATOR_EQUAL, "id",
		// idn.getItemData().getId());
		// numbers.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		// ret.add(numbers);
		// }

		return ret;
	}

	// public LOSResultList<BODTO<ItemData>> autoCompletionClientAndLot(String
	// exp,
	// BODTO<Client> client,
	// BODTO<Lot> lot,
	// QueryDetail detail) {
	// try {

	// Lot l;
	// LOSResultList<BODTO<ItemData>> ret;

	// Client cl = null;
	// if(client != null) {
	// cl = manager.find(Client.class, client.getId());
	// }

	// if (lot != null) {
	// l = manager.find(Lot.class, lot.getId());
	// ret = new LOSResultList<BODTO<ItemData>>();
	// ret.add(new BODTO<ItemData>(l.getItemData().getId(),
	// l.getItemData().getVersion(), l.getItemData().getNumber()));
	// ret.setResultSetSize(1L);
	// ret.setStartResultIndex(0L);
	// return ret;
	// } else {
	// return this.autoCompletion(exp, cl, detail);
	// }

	// } catch (Throwable ex) {
	// log.error(ex.getMessage(), ex);
	// return new LOSResultList<BODTO<ItemData>>();
	// }

	// }

}
