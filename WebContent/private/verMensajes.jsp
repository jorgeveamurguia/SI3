<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<s:set name="userName" value="%{loggedUserName}" />

<content tag="customStyles">
<style type="text/css">
table.wwFormTable {
	width: 650px;
}
</style>
</content>

<content tag="loginArea"> <%@ include
	file="/public/_loginArea.jsp"%> </content>

<content tag="headerMenu"> <%@ include
	file="/public/_actions.jsp"%> </content>

<title>UNED: Práctica SI3-Ver Mensajes</title>
</head>
<body>
	<span class="actionError"><s:actionerror />
	</span>
	<span class="actionMessage"><s:actionmessage />
	</span>

	<h4>Mensajes</h4>
	<s:form name="form1" action='Mensajes.ver' method='post'
		validate="false" acceptcharset="UTF-8" cssStyle="width: 250px;">

	</s:form>
	<table class="customTable" width="650px">
		<tr>
			<th>ID</th>
			<th>Tipo</th>
			<th>De</th>
			<th>Información</th>
		</tr>
		<s:iterator value="listaMensajes" status="status">
			<tr
				class="<s:if test="#status.odd == true ">odd</s:if><s:else>even</s:else>">
				<td><s:property value="ID" /></td>
				<td><s:property value="mensajeTipo" /></td>
				<td><s:property value="de" /></td>
				<td><s:property value="informacion" /></td>
			</tr>
		</s:iterator>
	</table>
</body>
</html>