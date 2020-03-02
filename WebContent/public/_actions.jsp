<s:set name="userNameInActions" value="%{loggedUserName}" />
<s:set name="userTypeInActions" value="%{LoggedUserType}" />

<s:if test="%{#userTypeInActions == 'Alumno'}">
	<ul>
		<li><s:url id="url3" action="PropuestaPFC.ver" /> <s:a
				href="%{url3}" title="Ver propuestas de PFC">Ver propuestas de PFC</s:a>
		</li>
		<li><s:url id="url3" action="PropuestaPFC.solicitar" /> <s:a
				href="%{url3}" title="Solicitar PFC">Solicitar PFC</s:a></li>
		<li><s:url id="url4" action="Mensajes.ver" /> <s:a
				href="%{url4}" title="Ver Mensajes">Ver Mensajes</s:a></li>
	</ul>
</s:if>
<s:elseif test="%{#userTypeInActions == 'Profesor'}">
	<ul>
		<li><s:url id="url3" action="PropuestaPFC.ver" /> <s:a
				href="%{url3}" title="Ver propuestas de PFC">Ver propuestas de PFC</s:a>
		</li>
		<li><s:url id="url4" action="PropuestaPFC.crear" /> <s:a
				href="%{url4}" title="Crear propuesta PFC">Crear propuesta PFC</s:a>
		</li>
		<li><s:url id="url2" action="PFC.ver" /> <s:a href="%{url2}"
				title="Ver PFC asignados">Ver PFC asignados</s:a>
		</li>
		<li><s:url id="url4" action="Mensajes.ver" /> <s:a
				href="%{url4}" title="Ver Mensajes">Ver Mensajes</s:a></li>
	</ul>
</s:elseif>
<s:elseif test="%{#userTypeInActions == 'Admin'}">
	<ul>
		<li><s:url id="url2" action="PropuestaPFC.ver" /> <s:a
				href="%{url2}" title="Ver Propuestas de PFC">Ver Propuestas de PFC</s:a>
		</li>
		<li><s:url id="url1" action="SolicitudesPFC.ver" /> <s:a
				href="%{url1}" title="Ver Solicitudes de PFC">Ver Solicitudes de PFC</s:a>
		</li>
		<li><s:url id="url3" action="PFC.ver" /> <s:a href="%{url3}"
				title="Ver PFC">Ver PFC</s:a>
		</li>
		<li><s:url id="url4" action="Mensajes.ver" /> <s:a
				href="%{url4}" title="Ver Mensajes">Ver Mensajes</s:a></li>
		<li>&nbsp;</li>
		<li><s:url id="urlResetDB" action="UserAction.resetDB" /> <s:a
				href="%{urlResetDB}" title="Restaurar DB">Resetear DB</s:a>
		</li>
		<li><s:url id="urlCreateSql" action="UserAction.createSql" /> <s:a
				href="%{urlCreateSql}" title="Crear Script Sql">Crear SQL</s:a>
		</li>
	</ul>
</s:elseif>
<s:else>
	<ul>
		<li><s:url id="urlRegister" action="UserAction.newUser" /> <s:a
				href="%{urlRegister}" title="Nuevo usuario">Nuevo usuario</s:a>
			&nbsp;&nbsp;</li>
	</ul>
</s:else>