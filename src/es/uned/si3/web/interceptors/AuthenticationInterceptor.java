package es.uned.si3.web.interceptors;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.dispatcher.SessionMap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;

import es.uned.si3.util.LoggerHelper;
import es.uned.si3.web.action.BaseController;
import es.uned.si3.web.action.SessionInfo;

public class AuthenticationInterceptor extends AbstractInterceptor {
	private Set excludeActions = Collections.EMPTY_SET;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		String actionName = invocation.getProxy().getActionName();
		LoggerHelper.getDefaultLogger().info(
				String.format("UserChecking.action=%s;isExecuted?%s",
						actionName, Boolean.toString(invocation.isExecuted())));
		if (invocation.isExecuted())
			return actionName;
		String result = "";
		if (excludeActions.contains(actionName)) {
			return invocation.invoke();
		}
		SessionInfo sInfo = BaseController.getSessionInfo();
		if (sInfo != null)
			return invocation.invoke();
		else
			return ActionSupport.ERROR;
		// if( invocation.getAction() instanceof AuthenticationAware) {
		// return null;
		// }
	}

	public void setExcludeActions(String values) {
		if (values != null) {
			this.excludeActions = TextParseUtil
					.commaDelimitedStringToSet(values);
		}
	}

}
