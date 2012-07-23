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
        <f:view locale="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.locale}" >
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="Form" styleClass="form" onreset="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.reset}" onsubmit="return checkInput()">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.PICKING_SHORT} #{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.position}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>

                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages"  styleClass="error"/> 
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="orderLabel" value="#{bundle.Order}:" styleClass="param"/></td>
                            <td colspan="3" style="text-align: right;"><strong><h:outputLabel id="orderLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.order}" /></strong></td>
                        </tr>
                        <%--                      <tr>
                        <td style="text-indent:"5px;margin-left:5px"><h:outputLabel id="positionLabel" value="#{bundle.Pos} #{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.position}" styleClass="label"/></td>
                        <td colspan="3" style="text-align: right;"><h:outputLabel id="positionLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.positionStatus}"/></td>
                        </tr> --%>
                        <tr> 
                            <td style="margin-left:5px"><h:commandLink id="link" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.articelDescriptionActionPerformedListener}" styleClass="link" ><h:outputText id="articelLabel" value="#{bundle.Articel}:"  /></h:commandLink></td>
                            <td colspan="3" style="text-align: right;"><strong><h:outputLabel id="articelLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.articel}"/></strong></td>
                        </tr>
                    </table>
                    <table  width="100%" border="0" cellspacing="0">
						<tr>
                            <td style="margin-left:5px"><h:outputLabel id="unitLoadLabel" value="#{bundle.UnitLoad}:" styleClass="param"/></td>
                            <td colspan="3" style="text-align: right;"><strong><h:outputLabel id="unitLoadLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.unitLoad}" /></strong></td>
                        </tr>
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="storageLabel" for="storageTextField" value="#{bundle.PickingEnterlocation}" styleClass="label" /> </td>
                            <td colspan="3" style="text-align: right;"><h:outputLabel id="storageLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocation}" styleClass="labelBoldBlue" /></td>
                        </tr>
                        <%--                    <tr>
                        <td colspan="2" style="text-indent:"5px;margin-left:5px"><h:inputText id="storageTextField" value="" styleClass="input" size="#{Constants.TEXTFIELD_SIZE}" /></td>
                        <td style="text-align: right;"><h:commandButton id="storageButton" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.detail}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationButton}" styleClass="qeryButton" disabled="false" /></td>
                        </tr> --%>
                        <tr>
                            <td colspan="4" style="margin-left:5px;">
                                <table  class="table" width="100%">
                                    <tr topmargin="0">
                                        <th scope="col">
                                        <h:inputText id="storageLocationTextField" 
                                        	validator="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationValidator}" 
                                        	value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationTextField}" 
                                        	styleClass="input" 
                                        	disabled="#{!de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationTextFieldEnable}" 
                                        	required="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.emptyMessage}"
                                        	onblur="enableSend()" />
                                        </th>
                                        <%-- <th scope="col"><h:commandButton id="storageButton" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.detail}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationButton}" styleClass="qeryButton" disabled="true" /></th>--%>
                                    </tr>
                            </table></td>
                        </tr>
                        <%-- <tr>
                        <td colspan="4" style="text-indent:"5px;margin-left:5px"><h:inputText id="storageTextField" value="" styleClass="input" size="#{Constants.TEXTFIELD_SIZE}" /></td>
                                                    <td width="12" style="text-indent:"5px;margin-left:5px"><h:commandButton id="storageButton" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.detail}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.storageLocationButton}" styleClass="qeryButton" disabled="false" /></td> 
                        </tr> --%>
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="amountLabel" value="#{bundle.TakingAmount}" styleClass="label" /></td>
                            <td colspan="3" style="text-align: right;"><strong><h:outputLabel id="amountLabelMessage" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.amount}" styleClass="labelBoldBlue" /></strong></td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;" >
                                <table class="table" width="100%" border="0" >
                                    <tr >
                                        <th scope="col">
                                            <h:inputText id="amountTextField" validator="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.amountValidator}" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.amountTextField}" disabled="#{!de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.amountTextFieldEnable}" required="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.emptyMessage}"  styleClass="input" > 
                                               <%-- <f:validateLongRange minimum="1" maximum="100000"/>  --%>
                                            </h:inputText>
                                        </th>
                                        <%--<th scope="col"><h:commandButton  id="takingAmountButton" value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.detail}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.amountButton}" styleClass="qeryButton" disabled="true"/></th>--%>
                                    </tr>
                            </table></td>
                        </tr>
                        <tr>
                        <td colspan="5" class="nopadding" >
                        	<h:selectBooleanCheckbox 
                                title="takeWholeUnitLoadCheckBox"
                                value="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.takeWholeUnitLoadCheckBox}" 
                                disabled="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.takeWholeUnitLoadCheckBoxDisabled}" > 
                            </h:selectBooleanCheckbox> 
                            <h:outputText 
                            	value="#{bundle.TakeWholeUnitLoadCheckBox}"  
                            	styleClass="label"
                            /> 
                        </td>
                        </tr>        
                    </table>
                </div>
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="forwardButton" value="#{bundle.Forward}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.forwardActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.forwardButtonEnable}" styleClass="commandButton"  />
                    <h:commandButton id="backButton" value="#{bundle.Back}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.backdActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.backwardButtonEnable}" styleClass="commandButton"  />
                    <h:commandButton id="finishUlButton" value="#{bundle.FinishUl}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.finishUlActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.finishUlButtonEnable}" styleClass="commandButton"  /> 
                    <h:commandButton id="cancelButton" value="#{bundle.Cancel}" action="#{de_linogistix_mobile_processes_picking_scanOrder_gui_bean_CenterBean.cancelActionPerformedListener}" styleClass="commandButton"  />
                </div>
                
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:storageLocationTextField').focus();
            }

            lock = true;
            function enableSend() {
                lock = false;
            }
            
            function checkInput() {
                if( lock ) {
                    lock = false;
                    document.getElementById('Form:amountTextField').focus();
                    return false;
                }
                return true;
            }
            
        </script>
        
    </body>
</html>
