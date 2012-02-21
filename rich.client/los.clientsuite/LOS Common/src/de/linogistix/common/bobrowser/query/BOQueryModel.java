/*
 * BOQueryModel.java
 *
 * Created on 13. September 2006, 00:36
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.exception.InternalErrorException;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryModel<E extends BasicEntity> {

  private static Logger log = Logger.getLogger(BOQueryModel.class.getName());
  
  private BONode boNode;

  private QueryDetail queryDetail;

  private String searchString = "";
  
  private BOQueryComponentProvider provider;
  
  private ExplorerManager manager;

  protected Object semaphore = new Object();
  
  private long resultSetSize;
  
  public BOQueryModel(BONode boNode) {
    try {
      this.setBoNode(boNode);
      this.provider = getDefaultBOQueryProvider();
    } catch (Throwable t) {
      ExceptionAnnotator.annotate(t);
    }
  }

  public long getResultSetSize() {
      return resultSetSize;
  }
  
  protected void setResultSetSize(long size){
      resultSetSize = size;
  }
  
  public void setSelected(Node[] selected) {
    synchronized(semaphore){
        Node[] toSelect = null;
        Node aNode;
        try {
          if (selected != null && selected.length > 0) {
            aNode = getManager().getExploredContext();
            if (aNode != null) {
              aNode = aNode.getChildren().findChild(selected[0].getName());
              if (aNode != null) {
                toSelect = new Node[]{aNode};
              }
            }
          }
          if (toSelect == null) {
            aNode = getManager().getRootContext();
            if (aNode != null && aNode.getChildren().getNodes() != null && aNode.getChildren().getNodes().length > 0) {
              aNode = aNode.getChildren().getNodes()[0];
              if (aNode != null) {
                toSelect = new Node[]{aNode};
                log.info("selected one: " + Arrays.toString(toSelect));
              }
            }
          }
          else if (toSelect.length > 0) {
            getManager().setSelectedNodes(toSelect);
            log.info("selected some:" + Arrays.toString(toSelect));
          }
        } catch (Throwable t) {
          log.warning("selection of node failed:");
          log.log(Level.INFO, t.getMessage(), t);
        }
    }
  }
  
  public BONode getBoNode() {
    return boNode;
  }

  public void setBoNode(BONode boNode) {
    this.boNode = boNode;
  }

  public QueryDetail getQueryDetail() {
    return queryDetail;
  }

  public void setQueryDetail(QueryDetail queryDetail) {
    this.queryDetail = queryDetail;
  }

  public LOSResultList<BODTO<E>> getResults() throws FacadeException {
    LOSResultList results;
    try {
//      log.info("service call: " + getQueryMethod().getName() + ":" + getQueryMethodParameters()[0].getClass().getName());
      if (getProvider().getQueryRemote() == null) {
        throw new NullPointerException("queryRemote must not be null");
      } 
      
      if (getProvider().getQueryMethodParameters(getQueryDetail(),getSearchString()) == null 
              ||getProvider().getQueryMethodParameters(getQueryDetail(),getSearchString()).length == 0) {
        results = (LOSResultList) getProvider().getMethod().invoke(getProvider().getQueryRemote());          
      } else{
          Object[] params = getProvider().getQueryMethodParameters(getQueryDetail(),getSearchString());
          Method m = getProvider().getMethod();
          Object o = getProvider().getQueryRemote();
          results = (LOSResultList) m.invoke(o, params);
      }
      
      resultSetSize = results.getResultSetSize();
      
    } catch (Throwable t) {
      log.log(Level.SEVERE, t.getMessage());
      log.log(Level.INFO, t.getMessage(), t);
      if (t.getCause() instanceof FacadeException) {
        throw (FacadeException) t.getCause();
      }
      throw new InternalErrorException(t);
    }

    return results;
  }

  public ExplorerManager getManager() {
    return manager;
  }

  public void setManager(ExplorerManager manager) {
    this.manager = manager;
  }

  /**
   *@return true if model is ready to query for entities
   */
  public boolean isInitialized() {

    if (getBoNode() == null) {
      log.warning("isInitialized false: boNode ");
        return false;
    }
    if (getManager() == null) {
         log.warning("isInitialized false: manager ");
      return false;
    }
    if (getQueryDetail() == null) {
         log.warning("isInitialized false: queryDetail ");
      return false;
    }
    if (getProvider() == null){
         log.warning("isInitialized false: provider ");
        return false;
    }
//    if (getProvider().getMethod() == null){
//         log.warning("isInitialized false: method ");
//        return false;
//    }
//    if (getProvider().getQueryMethodParameters(getQueryDetail(), searchString) == null){
//         log.warning("isInitialized false: searchString ");
//        return false;
//    }
//    if (getProvider().getQueryRemote() == null){
//         log.warning("isInitialized false: queryremote ");
//        return false;
//    }

    return true;
  }

    public BOQueryComponentProvider getProvider() {
        return provider;
    }
    
    public BOQueryComponentProvider getDefaultBOQueryProvider(){
        return getBoNode().getBo().getDefaultBOQueryProvider();
    }
    
    public List<BOQueryComponentProvider> getQueryComponentProviders(){
        return getBoNode().getQueryComponentProviders();
    }

    public void setProvider(BOQueryComponentProvider provider) {
        this.provider = provider;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
