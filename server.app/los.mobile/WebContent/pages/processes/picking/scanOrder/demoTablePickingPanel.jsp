<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<f:view><html><body>
<h:form>
<br><br><br>
<h:dataTable id="dt1" value="#{TableBean.perInfoAll}" var="item" bgcolor="#F1F1F1" border="10" cellpadding="5" cellspacing="3" first="0" rows="4" width="50%" dir="LTR" frame="hsides" rules="all" summary="This is a JSF code to create dataTable." >

<f:facet name="header">
        <h:outputText value="This is 'dataTable' demo" />
</f:facet> 

<h:column>
        <f:facet name="header">
        <h:outputText value="id" />
        </f:facet> 
             <h:outputText value="#{item.id}"></h:outputText>
</h:column>

<h:column>
        <f:facet name="header">
        <h:outputText value="name"/>
        </f:facet> 
             <h:outputText value="#{item.name}"></h:outputText>
</h:column>

<h:column>
        <f:facet name="header">
        <h:outputText value="phone"/>
        </f:facet> 
             <h:outputText value="#{item.phone}" style="text-align:right" ></h:outputText>
</h:column>

<h:column>
        <f:facet name="header">
        <h:outputText value="city"/>
        </f:facet>
             <h:outputText value="#{item.city}"></h:outputText>
</h:column>

<h:column>
        <f:facet name="header">
        <h:outputText value="pin"/>
        </f:facet>
             <h:outputText value="#{item.pin}"></h:outputText>
</h:column>

<f:facet name="footer">
        <h:outputText value="The End" />
</f:facet> 

</h:dataTable><br><br>

</h:form>
</body></html></f:view>