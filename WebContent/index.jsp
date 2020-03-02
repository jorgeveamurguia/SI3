<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<content tag="loginArea">
<%@ include file="/public/_loginArea.jsp"%>
</content>

<content tag="headerMenu">
<%@ include file="/public/_actions.jsp"%>
</content>

<title>UNED: Práctica SI3-Bienvenida</title>
</head>
<body>

	<span class="actionError"><s:actionerror /></span>
	<span class="actionMessage"><s:actionmessage /></span>

<s:if test="%{#userName == null}">
<div class="fuentepeq1" style="width: 100%;">El presente portal ofrece tanto a alumnos, como profesores de la UNED una herramienta que permite automatizar el proceso oficial para la realización del Proyecto Fin de Carrera (PFC) de las titulaciones pertenecientes a la ETSI 
Informática.<br/><br/>
A continuación deberá registrarse como alumno / profesor o Administrador. En caso de no haberse dado de alta, regístrese como nuevo usuario...<br/><br/></div>
	<s:form action='UserAction.login' method='post' validate="false"
		acceptcharset="UTF-8">
		<s:textfield name='nombre_usuario' label='Nombre único de usuario'
			required='true' maxlength="25" />
		<s:password name='password' label='Contraseña' required='true'
			maxlength="25" />
		<s:submit value='Acceder' />
	</s:form>
</s:if>
<s:else>
	<s:if test="%{#userTypeInActions == 'Alumno'}">
		<h2>Alumnos</h2> 
Las opciones a su disposición son:
<ul id="listaOpciones">
			<li><b>Ver propuestas de PFC: </b>Ver listado de propuestas
			disponibles</li>
			<li><b>Solicitar PFC:</b>Permite enviar una solicitud de PFC
			sobre una propuesta.</li>
			<li><b>Ver Mensajes: </b>Muestra los mensajes enviados al
			usuario.</li>
		</ul>
	</s:if>
	<s:elseif test="%{#userTypeInActions == 'Profesor'}">
		<h2>profesor.</h2>
Las opciones a su disposición son:
<ul id="listaOpciones">
			<li><b>Ver propuestas de PFC: </b>Lista las propuestas
			realizadas por el profesor</li>
			<li><b>Crear propuesta de PFC: </b>Crear una propuesta de
			proyecto.</li>
			<li><b>Ver PFC asignados: </b>Desde aquí verá los PFC que se le
			han asignado, y podrá cerrarlos, asignando nota y adjuntando la
			memoria del mismo.</li>
			<li><b>Ver Mensajes: </b>Muestra los mensajes enviados al
			usuario.</li>
		</ul>
	</s:elseif>
	<s:elseif test="%{#userTypeInActions == 'Admin'}">
		<h2>Administrador/Coordinador</h2> 
Las opciones a su disposición son:
<ul id="listaOpciones">
			<li><b>Ver propuestas de PFC: </b>Ver el listado de todas las
			propuestas realizadas, filtradas por año académico.</li>
			<li><b>Ver solicitudes de PFC: </b>Ver el listado de todas las
			solicitudes realizadas, filtradas por año académico. Asimismo, desde
			este menú, podrá aceptar solicitudes de PFC asignando a la misma un
			profesor (o bien rechazar la solicitud), es importante destacar que
			la solicitud no tiene porque ser asignada al mismo profesor que la
			realizó, en caso de ser un proyecto genérico.</li>
			<li><b>Ver PFC: </b>Este menú le permite ver el listado de todos
			los PFC en desarrollo, filtrados por año académico. Asimismo, esta
			opción le permite ver el listado de todos los PFC terminados,
			filtrados por año académico.</li>
			<li><b>Ver Mensajes: </b>Muestra los mensajes enviados al
			usuario.</li>
			<li>&nbsp;</li>
			<li><b>Resetear DB: </b>Inicializa la base de datos con alumnos
			(a1-a5), profesores (p1-p5) de pruebas</li>
			<li><b>Crear SQL: </b>Crea el script SQL de generación de base
			de datos</li>
		</ul>
	</s:elseif>
</s:else>
</body>
</html>
