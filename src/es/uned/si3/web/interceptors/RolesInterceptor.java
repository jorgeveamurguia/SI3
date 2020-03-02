package es.uned.si3.web.interceptors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;

import es.uned.si3.util.LoggerHelper;
import es.uned.si3.web.action.BaseController;
import es.uned.si3.web.action.SessionInfo;

public class RolesInterceptor extends AbstractInterceptor {
	private Map<String, Set> roleActions = Collections.EMPTY_MAP;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String actionName = invocation.getProxy().getActionName();
//		LoggerHelper.getDefaultLogger().info(
//				String.format("RolesInterceptor.action=%s;isExecuted?%s",
//						actionName, Boolean.toString(invocation.isExecuted())));
		if (invocation.isExecuted())
			return actionName;

		Map session = invocation.getInvocationContext().getSession();
		SessionInfo sInfo = BaseController.getSessionInfo();
		String tipoUser = (sInfo == null) ? "*" : sInfo.getTipoUsuario();
		if (hasSufficientRole("*", actionName))
			return invocation.invoke();
		if (hasSufficientRole(tipoUser, actionName))
			return invocation.invoke();
		else
			return ActionSupport.ERROR;
		// if( invocation.getAction() instanceof AuthenticationAware) {
		// return null;
		// }
	}

	public void setRoleActions(String roleActionsParam) {
		StringTokenizer roleActionsParamTokenizer = new StringTokenizer(
				roleActionsParam, ";");
		this.roleActions = new HashMap<String, Set>(
				roleActionsParamTokenizer.countTokens());
		while (roleActionsParamTokenizer.hasMoreTokens()) {
			String roleActionArray[] = roleActionsParamTokenizer.nextToken()
					.trim().split(":");
			if (roleActionArray.length == 2) {
				String role = roleActionArray[0].toLowerCase();
				Set actions = TextParseUtil
						.commaDelimitedStringToSet(roleActionArray[1]);
				this.roleActions.put(role, actions);
			}
		}
	}

	public boolean hasSufficientRole(Object userRole, String actionName) {
		if (roleActions.containsKey("*")
				&& roleActions.get("*").contains(actionName))
			return true;
		if (userRole != null) {
			String userRoleString = userRole.toString().toLowerCase();
			if (roleActions.containsKey(userRoleString)) {
				for (Object action : roleActions.get(userRoleString)) {
					if (actionName.startsWith(action.toString()))
						return true;
				}
			}
		}

		return false;
	}
}
