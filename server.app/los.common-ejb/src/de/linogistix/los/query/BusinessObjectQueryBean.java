/*
 * BusinessObjectQueryBean.java
 *
 * Created on 13. September 2006, 12:40
 *
 * Copyright (tClass) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.UserService;

import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectNotUniqueException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportService;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.GenericTypeResolver;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * Queries BasicEntities / BusinessObejct
 * 
 * This session bean is designed to transfer entities directly to a remote
 * client. Queries are formulated in a specific {@link TemplateQuery}. To limit
 * number of results or give order information use class {@link QueryDetail}.
 * 
 * Pay attention to eager/lazy loading of entities: If properties (Collection of
 * properties) are fetched LAZY, your client might run into an exception when
 * accessing those properties.
 * 
 * This implementation guarantees that {@link #queryById} returns an eagerly
 * fetched instance of {@link BasicEntity}, i.e. all properties are
 * initialised.
 * 
 * Special attention must be paid if getBODTOConstructorProps includes pathelements that
 * point to other entity relationships (e.g. stockunit.unitload.storagelocation.name).
 *  Used in conjunction with a rich object model (polymorphy) and more than a few rows this 
 *  might . If you want to transfer dto with information from other entity classes like 
 *  the above example, here is the way how to handle this:
 *  
 *  Just overwrite in extended classes the enrichResultSet method, iterate through it and enrich the dto.
 *  An Example can be found in {@link StockUnitQueryBean}. This is okay when paging is used with just a few
 *  (let's say less than 100) entities per fetch.
 *  
 *  DON'T DO THIS IF you intend to fetch lots of data!!! Then you shoud consider writing (native?) optimized queries.
 *  
 * 
 * @see TemplateQuery
 * @see QueryDetail
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
// @SecurityDomain("myWMS")
// @Remote(BusinessObjectQueryRemote.class)
// @WebService(endpointInterface="de.linogistix.los.query.BusinessObjectQueryRemote")
// @SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
// @WebContext(contextRoot = "/webservice/query", authMethod="BASIC",
// transportGuarantee="NONE", secureWSDLAccess=true)
public abstract class BusinessObjectQueryBean<T extends BasicEntity> implements
		BusinessObjectQueryRemote<T> {

	private static final Logger log = Logger
			.getLogger(BusinessObjectQueryRemote.class.getName());

	@Resource
	EJBContext context;
	@EJB
	UserService userService;
	@EJB
	protected ContextService contextService;
	@EJB
	protected ClientService clientService;
	@EJB
	protected ReportService reportService;

	@Resource
	SessionContext ctx;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	protected Class<? extends Object> tClass;
	
	/** Creates a new instance of BusinessObjectQueryBean */
	public BusinessObjectQueryBean() {
		tClass = new GenericTypeResolver<T>().resolveGenericType(getClass());
	}

	@SuppressWarnings("unchecked")
	public T queryById(Long ID) throws BusinessObjectNotFoundException,
			BusinessObjectSecurityException {

		BasicEntity entity;

		try {
			entity = (BasicEntity) manager.find(tClass, ID);

			if (entity == null) {
				throw new BusinessObjectNotFoundException(ID, tClass);
			}

			if (!getCallersUser().getClient().isSystemClient()) {
				if (entity instanceof BasicClientAssignedEntity) {
					if (!((BasicClientAssignedEntity) entity).getClient()
							.equals(getCallersUser().getClient())) {
						throw new BusinessObjectSecurityException(
								getCallersUser());
					}
				}
			}
			return (T) eagerRead(entity);

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			log.warn("Entity not found with id " + ID + " of class " + tClass);
			throw new BusinessObjectNotFoundException(ID, tClass);
		}

	}
	
	public LOSResultList<BODTO<T>> queryHandlesById(List<Long> ids, QueryDetail detail) throws BusinessObjectNotFoundException, BusinessObjectQueryException {
				
		TemplateQuery q = new TemplateQuery();
		q.setBoClass(getBoClass());
		int i = 0 ;
		for (Long id : ids){
			TemplateQueryWhereToken t = new TemplateQueryWhereToken();
			t.setOperator(TemplateQueryWhereToken.OPERATOR_EQUAL);
			t.setValue(id);
			t.setParameter("id");
			t.setParameterName("id" + (i++));
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			q.addWhereToken(t);
		}

		// manually order items in 
		LOSResultList<BODTO<T>> val = queryByTemplateHandles(detail, q);
		
		LOSResultList<BODTO<T>> ret = new LOSResultList<BODTO<T>>();
		ret.setResultSetSize(val.getResultSetSize());
		ret.setStartResultIndex(val.getStartResultIndex());
		
		for (Long id : ids){
			i = 0;
			for (Iterator<BODTO<T>> it = val.iterator(); it.hasNext(); ){
				BODTO<T> to = it.next();
				if (to.getId().equals(id)){
					ret.add(to);
					break;
				}
				i++;
			}
		}
		return ret;
	}

	public LOSResultList<T> queryAll(QueryDetail detail)
			throws BusinessObjectQueryException {
		TemplateQuery tq;

		try {
			tq = new TemplateQuery();
			tq.setBoClass(tClass);
			return queryByTemplate(detail, tq);
		} catch (BusinessObjectNotFoundException bex) {
			return new LOSResultList<T>();
		} catch (BusinessObjectQueryException qex) {
			log.error(qex.getMessage(), qex);
			throw qex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}

	}

	public Class<? extends Object> getBoClass() {
		return tClass;
	}

	// ------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public LOSResultList<T> queryByTemplate(QueryDetail detail,
			TemplateQuery query) throws BusinessObjectNotFoundException,
			BusinessObjectQueryException {
		List<T> ret;
		Query q;
		Client callersClient;
		User user;

		long start = System.currentTimeMillis();

		if (detail == null) {
			log.error("detail must not be null");
			throw new NullPointerException();
		} else if (query == null) {
			log.error("query must not be null");
			throw new NullPointerException();
		}

		try {

			user = getCallersUser();

			if (user != null && (!user.getClient().isSystemClient())) {
				// restrict to result set belonging to this client
				callersClient = user.getClient();
				if (callersClient != null) {
					if (BasicClientAssignedEntity.class
							.isAssignableFrom(tClass)) {
						query.addNewFilter().addWhereToken(new TemplateQueryWhereToken(
								TemplateQueryWhereToken.OPERATOR_EQUAL,
								"client.id", callersClient.getId()));
					} else if (Client.class.isAssignableFrom(tClass)){
						query.addNewFilter().addWhereToken(new TemplateQueryWhereToken(
								TemplateQueryWhereToken.OPERATOR_EQUAL,
								"id", callersClient.getId()));
					}
				} else {
					throw new NullPointerException(
							"user without client is not allwoed");
				}
			}

			StringBuffer s = new StringBuffer(query.getStatement());

			if (detail.getOrderBy() != null && detail.getOrderBy().size() != 0) {
				s.append(" ORDER BY ");
				for (Iterator it = detail.getOrderBy().iterator(); it.hasNext();) {
					OrderByToken t = (OrderByToken) it.next();
					s.append("o.");
					s.append(t.getAttribute());
					s.append(" ");
					if (!t.isAscending()) {
						s.append("DESC ");
					}
					if (it.hasNext()) {
						s.append(",");
					}
				}
			} else {
				s.append(" ORDER BY ");
				s.append("o.");
				s.append(getUniqueNameProp());
				s.append(", ");
				s.append("o.id");
				s.append(" ");
			}

			String qs = new String(s);
			log.debug("Query String: " + qs);

			q = manager.createQuery(qs);
			Query countQuery = manager.createQuery(query.getCountStatement());

			if (query.getWhereTokens() != null) {
				for (TemplateQueryWhereToken wt : query.getWhereTokens()) {
					if (wt != null) {
						if (wt.getOperator().equals(
								TemplateQueryWhereToken.OPERATOR_LIKE)) {
							wt.transformLikeParameter();
						}
						if (wt.getOperator().equals(
								TemplateQueryWhereToken.OPERATOR_FALSE)
								|| wt.getOperator().equals(
										TemplateQueryWhereToken.OPERATOR_TRUE)) {
							continue;
						}
						if(wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_ISEMPTY)
							|| wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_ISNOTEMPTY)){
							continue;
						}
						if(wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_AT)
						   && wt.getValue() instanceof Date)
						{
							Date param = (Date) wt.getValue();
							
							Calendar cal00 = Calendar.getInstance();
				        	cal00.setTime(param);
				        	cal00.set(Calendar.HOUR_OF_DAY, 0);
				        	cal00.set(Calendar.MINUTE, 0);
				        	
				        	Calendar cal24 = Calendar.getInstance();
				        	cal24.setTime(param);
				        	cal24.set(Calendar.HOUR_OF_DAY, 23);
				        	cal24.set(Calendar.MINUTE, 59);
							
							q.setParameter(wt.getParameterName()+"1", cal00.getTime());
							q.setParameter(wt.getParameterName()+"2", cal24.getTime());
							
							countQuery.setParameter(wt.getParameterName()+"1", cal00.getTime());
							countQuery.setParameter(wt.getParameterName()+"2", cal24.getTime());
						}
						else{
							q.setParameter(wt.getParameterName(), wt.getValue());
							countQuery.setParameter(wt.getParameterName(), wt.getValue());
						}
					}
				}
			}

			if (detail != null) {
				q = q.setFirstResult(detail.getStartResultIndex());
				q = q.setMaxResults(detail.getMaxResults());
				// q = q.setFirstResult(detail.getStartIndex());
			} else {
				log.error("--- !!!     No QueryDetail     !!! ---");
			}

			ret = q.getResultList();

			if (ret == null) {
				throw new BusinessObjectNotFoundException();
			}

			int resultSize = ((Long) countQuery.getSingleResult()).intValue();

			log.debug("--- Result size : " + resultSize);

			LOSResultList<T> resultList = new LOSResultList<T>(ret);
			resultList.setResultSetSize(resultSize);
			resultList.setStartResultIndex(detail.getStartResultIndex());
			enrichResultSet(resultList);
			return resultList;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		} finally {
			long stop = System.currentTimeMillis();
			log.debug("query took " + (stop - start));
		}
	}

	@SuppressWarnings("unchecked")
	public LOSResultList<BODTO<T>> queryByTemplateHandles(QueryDetail detail,
			TemplateQuery query) throws BusinessObjectNotFoundException,
			BusinessObjectQueryException {
		LOSResultList ret;
		try {

			if (query == null || detail == null) {
				throw new BusinessObjectQueryException();
			}

			Class bodtoClass;
			
			if (query.getNewExprClass() == null){
				bodtoClass = getBODTOClass();
			}else{
				bodtoClass = query.getNewExprClass();
			}
			
			List<BODTOConstructorProperty> propList;
			
			if(query.getNewExprProperties() == null || query.getNewExprProperties().size()<1){
				
				propList = getBODTOConstructorProperties();
				
				String[] props = getBODTOConstructorProps();
				
				if(propList.size() == 0){
										
					for(String p:props){
						propList.add(new BODTOConstructorProperty(p, false));
					}
				}
			}
			else{
				propList = query.getNewExprProperties();
			}
			
			query.setSelectExpressionNew(bodtoClass, propList);
			
			ret = queryByTemplate(detail, query);
			if (ret == null) {
				throw new BusinessObjectNotFoundException();
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}
		return ret;

	}

	@SuppressWarnings("unchecked")
	public T queryByIdentity(String identity)
			throws BusinessObjectNotFoundException {
		
		List<T> ret;
		
		try {
			ret = manager.createQuery(
					"SELECT o FROM " + tClass.getName() + " o " + " WHERE o."
							+ getUniqueNameProp() + " = :identity")
					.setParameter("identity", identity).getResultList();

			if (ret == null || ret.size() < 1) {
				throw new BusinessObjectNotFoundException(identity);
			} else if (ret.size() > 1) {
				throw new BusinessObjectNotUniqueException(identity);
			} else {
				return (T) eagerRead(ret.get(0));
			}
		} catch (BusinessObjectNotFoundException ex){
			throw ex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectNotFoundException();
		} finally {

		}
	}

	@SuppressWarnings("unchecked")
	public List<T> queryByIdentity(Client c, String identity)
			throws BusinessObjectNotFoundException {
		List<T> ret;

		long start = System.currentTimeMillis();

		try {

			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE,
					"client.number", true);

			TemplateQueryWhereToken identityToken = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL,
					getUniqueNameProp(), identity);
			
			TemplateQueryWhereToken clientToken;
			
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(identityToken);

			q.setBoClass(getBoClass());

			clientToken = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client", c);
			q.addWhereToken(clientToken);
			
			ret = queryByTemplate(d, q);
			
			if (ret == null || ret.size() != 1) {
				throw new BusinessObjectNotFoundException();
			} else {
				for (int i = 0; i < ret.size(); i++) {
					ret.set(i, (T) eagerRead(ret.get(i)));
				}
				return ret;
			}
		} catch (Throwable t) {
			log.error(t.getMessage());
			throw new BusinessObjectNotFoundException();
		} finally {
			long stop = System.currentTimeMillis();
			log.debug("query took " + (stop - start));
		}
	}

	public LOSResultList<BODTO<T>> queryAllHandles(QueryDetail detail)
			throws BusinessObjectQueryException {

		TemplateQuery tq;

		try {
			tq = new TemplateQuery();
			tq.setBoClass(tClass);
			return queryByTemplateHandles(detail, tq);
		} catch (BusinessObjectNotFoundException bex) {
			return new LOSResultList<BODTO<T>>();
		} catch (BusinessObjectQueryException qex) {
			log.error(qex.getMessage(), qex);
			throw qex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}
	}

	protected boolean checkClient(BasicEntity entity) {
		return new BusinessObjectHelper(this.ctx, this.userService,
				this.context).checkClient(entity);
	}

	protected User getCallersUser() {
		return new BusinessObjectHelper(this.ctx, this.userService,
				this.context).getCallersUser();
	}

	protected BasicEntity eagerRead(BasicEntity entity) {
		return BusinessObjectHelper.eagerRead(entity);
	}

	/**
	 * Override to return class that should be used as handle
	 */
	@SuppressWarnings("unchecked")
	public Class getBODTOClass() {
		return BODTO.class;
	}

	/**
	 * Override to return names of Entitie's properties that should be used
	 * inside the constructor of BODTO as returned in {@link getBODTOClass()}.
	 */
	@Deprecated
	protected String[] getBODTOConstructorProps() {
		return new String[] { "id", "version", getUniqueNameProp() };
	}

	protected List<BODTOConstructorProperty> getBODTOConstructorProperties(){
		
		return new ArrayList<BODTOConstructorProperty>();
		
	}
	
	/**
	 * Overrdide to specify the name of a property that is unique and identify
	 * the entity.
	 */
	public String getUniqueNameProp() {
		return "id";
	}

	public  LOSResultList<BODTO<T>> autoCompletion(String typed, QueryDetail det) {
		return autoCompletion(typed, (Client)null, det);
	}
	
	public  LOSResultList<BODTO<T>> autoCompletion(String typed) {
		return autoCompletion(typed, (Client)null);
	}

	public  LOSResultList<BODTO<T>> autoCompletion(String typed, Client c) {
		return autoCompletion(typed, c, new TemplateQueryWhereToken[0], null);
	}
	
	public  LOSResultList<BODTO<T>> autoCompletion(String typed, Client c, QueryDetail det) {
		return autoCompletion(typed, c, new TemplateQueryWhereToken[0], det);
	}
	
	public  LOSResultList<BODTO<T>> autoCompletion(String typed, Client client,
			TemplateQueryWhereToken[] tokens) {
		return autoCompletion(typed, client, tokens, new QueryDetail(0,Integer.MAX_VALUE));
	}

	public  LOSResultList<BODTO<T>> autoCompletion(String typed, Client client,
			TemplateQueryWhereToken[] tokens, QueryDetail det) {

		TemplateQuery q = new TemplateQuery();
		q.setBoClass(tClass);

		

		if (det == null){
			det = new QueryDetail(0, Integer.MAX_VALUE,getUniqueNameProp(), true);
		}
		
		List<TemplateQueryWhereToken> toks;
		toks = getAutoCompletionTokens(typed);
		q.setWhereTokens(toks);
		
		if (client != null) {
			q.addNewFilter().addWhereToken(
					new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client", client));
		}
		if (tokens != null) {
			TemplateQueryFilter filter = q.addNewFilter();
			for (TemplateQueryWhereToken tmp : tokens) {
				filter.addWhereToken(tmp);
			}
		}

		
		
		
		try {
			LOSResultList<BODTO<T>> ret = queryByTemplateHandles(det, q);
			return ret;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return new LOSResultList<BODTO<T>>();
		}

	}
	
	/**
	 * Callback for manipulating dto on the fly. Works directly on the list entries! 
	 * 
	 */
	protected void enrichResultSet(LOSResultList<T> results){
		// can be used in subclasses.
	}
	
	/**
	 * Returns a List of TemplateQueryWhereToken used for autocompletion.
	 * <p>
	 * Every entry muat have {@link TemplateQueryWhereToken#setLogicalOperator} set to {@link TemplateQueryWhereToken#OPERATOR_OR})
	 * <p>
	 * Attention must be paid in implementations of {@link BusinessObjectQueryRemote} whose {@link BusinessObjectQueryRemote#getUniqueNameProp()}
	 * does not return a String type but e.g. a Long/id as this default implementation makes use of operator
	 *  {@link TemplateQueryWhereToken#OPERATOR_LIKE}
	 *  
	 * @return
	 */
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value){
		
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken t ;
		
		if (getUniqueNameProp().equals("id")){
			
			if (value.length() == 0){
				t = new TemplateQueryWhereToken(
						TemplateQueryWhereToken.OPERATOR_GREATER, getUniqueNameProp(),
						0L);
			} else{
				try{
					t = new TemplateQueryWhereToken(
							TemplateQueryWhereToken.OPERATOR_EQUAL, getUniqueNameProp(),
							Long.parseLong(value));
				} catch (NumberFormatException nex){
					t = new TemplateQueryWhereToken(
							TemplateQueryWhereToken.OPERATOR_EQUAL, getUniqueNameProp(),
							-1L);
				}
			}
		} else{
			t = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, getUniqueNameProp(),
				value);
		}
		
		ret.add(t);
		
		return ret;
		
	}
	
	//-----------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public byte[] exportExcel(
			String title,
			String methodName,
			Class[] methodArgTypes,
			Object[] methodArgs,
			Map<String, String> exportProperties
			) throws FacadeException{
		
		List exportList;
		
		
		try {
			Method m;
			m = this.getClass().getMethod(methodName, methodArgTypes);
			Object o = m.invoke(this, methodArgs);
			
			if (o instanceof List){
				exportList = (List) o;
			} else{
				exportList = new ArrayList();
				exportList.add(o);
			}
			byte[] bytes = reportService.typeExportExcelGeneric(title, exportList, exportProperties);
			return bytes;
			
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ReportException();
		} 
	}
	
	
}
