<s:set name="userName" value="%{loggedUserName}" />
<s:set name="userType" value="%{LoggedUserType}" />

<s:if test="%{#userName != null}">
	<table class="loginTable">
		<s:label value="Bienvenido %{#userName}" />
	</table>

	<ul>
		<li><s:url id="urlEdit" action="UserAction.editUserDetails" /> <s:a
				href="%{urlEdit}" title="Editar">Editar usuario</s:a></li>
		<li>&nbsp;&nbsp;&nbsp;&nbsp;</li>
		<li><s:url id="urlLogout" action="UserAction.logout" /> <s:a
				href="%{urlLogout}" title="Cerrar sesión">Cerrar sesión</s:a>
		</li>
	</ul>
</s:if>
<s:else>
	<span class="fuentepeq1"> <s:url id="urlRegister"
			action="UserAction.newUser" /> <s:a href="%{urlRegister}"
			title="Nuevo usuario">Nuevo usuario</s:a> 
	</span>
</s:else>