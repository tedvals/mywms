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
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{ReplenishBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.supplies.ReplenishBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleDone}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>


                <div class="space">
                     
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>
						
						<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="locationLabel" value="#{bundle.LabelLocation}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="locationData" value="#{ReplenishBean.locationName}" styleClass="value" />
                            </td>
                        </tr>
						<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="itemLabel" value="#{bundle.LabelItemData}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="itemData" value="#{ReplenishBean.itemDataNumber}" styleClass="value" />
                            </td>
                        </tr>

                        <tr><td>&#160;</td></tr>

                    </table>
                    
                    <h:messages id="messages" styleClass="info"/>
                </div>
                    
                
	                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CONTINUE" 
		                				 value="#{bundle.ButtonContinue}" 
		                				 action="#{ReplenishBean.processStart}" 
		                				 styleClass="commandButton"  />
	                    <h:commandButton id="BUTTON_MENU" 
		                				 value="#{bundle.ButtonMenu}" 
		                				 action="#{ReplenishBean.processEnd}" 
		                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        
        <script type="text/javascript">
            
            <%--            function setRow() {
                document.getElementById("view<%=renderResponse.get Namespace()%>:dt1").rows[1].style.backgroundColor="red";            
            }    --%>
                
                function load() {            
                    setFocus();    
                }    
                
                function setFocus() {
                    document.getElementById('Form:BUTTON_CONTINUE').focus();
                }    
                
        </script>
        
    </body>
</html>
