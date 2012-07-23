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
        <f:view locale="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.locale}" >
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="Form" styleClass="form" onsubmit="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_ComboBoxBean.submit}">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.Picking}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>

                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages" styleClass="error"/> 
                    <table width="100%" border="0">
                        <tr>
                            <td>
                                <h:outputText value="#{bundle.ChooseOrder}"></h:outputText>
                            </td>
                        </tr>
                        
                        <tr>
                            <td>
                                <h:selectOneMenu id="orderComboBox" onchange="this.form.submit();" valueChangeListener="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_ComboBoxBean.valueChanged}"
                                                 value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_ComboBoxBean.selectedNumber}" style="width:100%;" >
                                    <f:selectItems
                                        value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_ComboBoxBean.numbers}" />
                                </h:selectOneMenu>
                                <%--                            <select multiple name="listboxOption1" onfocus="expand2(this);" size="10">                                 --%>
                            </td>
                            
                        </tr>
                    </table>
                    
                    <table  width="100%" border="0" cellspacing="0">    
                        <tr>
                            <td style="margin-left:5px;"><h:outputLabel id="orderLabel" value="#{bundle.Order}:" styleClass="param"/></td>
                            <td style="text-align: right;"><h:outputLabel id="orderLabelMessage" value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.order}" styleClass="label"/></td>
                        </tr>
                    </table>
                    
                    <table  width="100%" border="0" cellspacing="0">    
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="mandantLabel" value="#{bundle.Mandant}:" styleClass="param"/></td>
                            <td style="text-align: right;"><h:outputLabel id="mandantLabelMessage" value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.mandant}"/></td>                            
                        </tr>
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="createdLabel" value="#{bundle.Created}:" styleClass="param"/></td>
                            <td style="text-align: right;"><h:outputLabel id="createdLabelMessage" value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.created}"/></td>                            
                        </tr>                        
                        <tr>
                            <td style="margin-left:5px;padding-top:10px"><h:outputLabel id="positionLabel" value="#{bundle.Positions}:" styleClass="param" /></td>
                            <td style="text-align: right;"><h:outputLabel id="positionLabelMessage" value="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.position}"/></td>                            
                            
                        </tr>
                        <tr>
                            <td style="padding-bottom:0px"></td>
                        </tr>
                    </table>
                </div>
                    
                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="forwardButton" 
                    	value="#{bundle.Forward}" 
                    	action="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.forwardActionPerformedListener}" disabled = "#{!de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.forwardButtonEnabled}" 
                    	styleClass="commandButton"  />
                    <h:commandButton id="backButton" 
                    	value="#{bundle.back_to_menu}" 
                    	action="#{de_linogistix_mobile_processes_picking_chooseOrder_gui_bean_CenterBean.cancelActionPerformedListener}" 
                    	styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:orderComboBox').focus();
            }    
            
        </script>
    </body>
</html>
