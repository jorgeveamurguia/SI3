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

<title>UNED: Práctica SI3-Ver PFC</title>
<s:set name="userIsProfesor" value="%{userIsProfesor}" />
</head>
<body>
	<span class="actionError"><s:actionerror /></span>
	<span class="actionMessage"><s:actionmessage /></span>
	

	<s:form name="form1" action='PFC.ver' method='post' validate="false"
		acceptcharset="UTF-8" cssStyle="width: 250px;">
		<s:select name='anioAcademicoFilter'
			label="- Seleccione año académico" list="setAniosAcademicos"
			headerValue="Todos" headerKey="*" labelposition="left"
			onchange="this.form.submit()"></s:select>
		<s:select name='estadoPFCFilter' label="- Filtrar por estado"
			list="setEstadosPFC" headerValue="Todos" headerKey="*"
			labelposition="left" onchange="this.form.submit()"></s:select>
	</s:form>
	<table class="customTable" width="650px">
		<tr>
			<th>PFC.titulo</th>
			<th>Año</th>
			<th>Alumno</th>
			<th>Estado</th>
			<th>Calificación</th>
			<th>Acciones</th>
		</tr>
		<s:iterator value="pfcs" status="status">
			<tr
				class="<s:if test="#status.odd == true ">odd</s:if><s:else>even</s:else>">
				<td><s:property value="titulo" />
				</td>
				<td><s:property value="anio" />
				</td>
				<td><s:property value="nombreAlumno" />
				</td>
				<td><s:property value="estado_cerrado_desarrollo" />
				</td>
				<td><s:property value="calificacion" />
				</td>
				<td><s:url id="url3" action="PFC.verDetalles?ID=%{ID}" /> <s:a
						href="%{url3}" title="Ver detalles...">Ver detalles/Cerrar
						</s:a>
				</td>
			</tr>
		</s:iterator>
	</table>
</body>
</html>