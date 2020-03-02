<%@page import="es.uned.si3.persistencia.UserType"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<content tag="loginArea"> 
<%@ include file="/public/_loginArea.jsp"%> 
</content>

<content tag="headerMenu"> <%@ include file="/public/_actions.jsp"%>
</content>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="loginMessage" content="" />

<title>UNED: Práctica SI3-Nuevo usuario</title>
<s:set name="tipoUsuario" value="%{tipo_usuario}" />
</head>
<body>
	<span class="actionError"><s:actionerror /></span>
	<span class="actionMessage"><s:actionmessage /></span>

	<s:if test="%{#tipoUsuario == null}">
		<s:form action='UserAction.newUser' method='post' validate="true" acceptcharset="UTF-8">
			<s:select name='tipo_usuario' label="Tipo de usuario" required='true'
				headerValue="--Selecciona--" headerKey="" emptyOption="false"
				list="listaTiposUsuarios"></s:select>
			<s:submit value='Siguiente...' />
		</s:form>
	</s:if>
	<s:else>
		<s:form action='UserAction.newUser' method='post' validate="true" acceptcharset="UTF-8">
			<s:hidden name="tipo_usuario"></s:hidden>
			<s:label value="Rellene los datos relativos al nuevo %{#tipoUsuario}"></s:label>
			<s:textfield name='nombre' label='Nombre' required='true'
				maxlength="25" />
			<s:textfield name='apellidos' label='Apellidos' required='true'
				maxlength="125" />
			<s:textfield name='nombre_usuario' label='Nombre único de usuario' required='true'
				maxlength="25" />
			<s:textfield name='dni' label='Dni' required='true' maxlength="125" />
			<s:textfield name='email' label='EMail' required='true'
				maxlength="25" />

			<s:if test="%{#tipoUsuario == 'Alumno'}">
				<s:select name='centro_asociado' label='Centro asociado'
					headerKey="" required='true' headerValue="--Selecciona--"
					list="listaCentrosAsociados" />
			</s:if>
			<s:else>
				<s:select name='departamento' label='Departamento' headerKey=""
					required='true' headerValue="--Selecciona--"
					list="listaDepartamentos" />
			</s:else>
			<s:checkbox name='recibir_notificacion'
				label="Recibir notificaciones"></s:checkbox>
			<s:password name='password' label='Contraseña' required='true'
				maxlength="25" />
			<!--  			<s:password name='password2' label='Repetir Contraseña'
				required='true' /> -->
			<s:submit value='Crear' />
		</s:form>
	</s:else>
</body>
</html>