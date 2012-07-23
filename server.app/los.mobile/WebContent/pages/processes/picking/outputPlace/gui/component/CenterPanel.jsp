<%-- 
  Copyright (c) 2006 - 2010 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MELISA</title>
        <%--    <link rel="stylesheet" type="text/css" href="pages/picking/stylesheet.css" />--%>
        <%--  <link rel="stylesheet" type="text/css" href="stylesheet.css" /> --%>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="Form" styleClass="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.Picking}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>

                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages" styleClass="error"/> 
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="orderLabel" value="#{bundle.Order}:" styleClass="param"/></td>
                            <td style="text-align: right;"><h:outputLabel id="orderLabelMessage" value="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.order}" styleClass="label"/></td>
                        </tr>
                    </table>
                    <table  width="100%" border="0" cellspacing="0">
                        <tr><td>&#160;</td></tr>
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="outputPlaceLabel" for="storageTextField" value="#{bundle.Targetplace}" styleClass="label" /> </td>
                            <td style="text-align: right;"><strong><h:outputLabel id="outputPlaceLabelMessage" value="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.storage}" styleClass="labelBoldBlue" /></strong></td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;width:100%">
                                <table width="100%" border="0">
                                    <tr >
                                        <th scope="col" style="width:100%"><h:inputText id="outputPlaceTextField" value="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.storageTextField}" styleClass="input" /></th>
                                    </tr>
                            </table>
                            </td>
                        </tr>
                        
                    </table>
                </div>
				<h:inputText value="IE-Dummy" style="display:none" />

                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="finishedButton" value="#{bundle.Done}" action="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.finishActionPerformedListener}" styleClass="commandButton"  />
                    <h:commandButton id="cancelButton" value="#{bundle.Cancel}" action="#{de_linogistix_mobile_processes_picking_outputPlace_gui_bean_CenterBean.cancelActionPerformedListener}" styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:outputPlaceTextField').focus();
            }    
            
        </script>

    </body>
</html>
