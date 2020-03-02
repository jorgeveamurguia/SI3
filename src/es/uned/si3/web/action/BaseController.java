package es.uned.si3.web.action;



import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.Departamento;
import es.uned.si3.util.LoggerHelper;

public class BaseController extends ActionSupport {
	private static final String SESSION_INFO = "session-info";
	public static final String START = "start";

//	public String message;

	public BaseController() {
	}

	@Override
	public String execute() throws Exception {
		return INPUT;
	};

//	public void setMessage(String message) {
//		this.message = normalize(message);
//	}

	protected String normalize(String str) {
		if (str == null)
			return "";
		else if (str.equals("null"))
			return "";
		else
			return str;
	}

	protected boolean validString(String str) {
		return str != null && str.length() > 0;
	}
	
//	public String getMessage() {
//		return this.message;
//	}

	public String getLoggedUserName() {
		SessionInfo sInfo = getSessionInfo();
		if (sInfo == null)
			return null;
		else
			return sInfo.getNombreUsuario();
	}

	public String getLoggedUserType() {
		SessionInfo sInfo = getSessionInfo();
		if (sInfo == null)
			return null;
		else
			return sInfo.getTipoUsuario();
	}

	public String getLoggedNombreCompleto() {
		SessionInfo sInfo = getSessionInfo();
		if (sInfo == null)
			return null;
		else
			return sInfo.getNombreCompleto();
	}

	public static SessionInfo removeSessionInfo() {
		try {
			Map session = ActionContext.getContext().getSession();
			SessionInfo user = (SessionInfo) session.get(SESSION_INFO);
			if (session instanceof SessionMap) {
				((SessionMap) session).invalidate();
				session = ActionContext.getContext().getSession();
			}
			return user;
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error("Error removing sessionInfo",
					e);
			return null;
		}
	}

	public static void saveSessionInfo(String tipoUsuario,
			String nombreUsuario, String nombre, String apellidos) {
		Map session = ActionContext.getContext().getSession();
		if (session instanceof SessionMap) {
			((SessionMap) session).invalidate();
			session = ActionContext.getContext().getSession();
		}

		session.put(SESSION_INFO, new SessionInfo(tipoUsuario, nombreUsuario,
				nombre, apellidos));
	}

	public static SessionInfo getSessionInfo() {
		Map session = ActionContext.getContext().getSession();
		SessionInfo user = (SessionInfo) session.get(SESSION_INFO);
		return user;
	}

	// protected Profesor getProfesorFromSql(GestorSql gestor,
	// Profesor profesor) {
	// Object[] objs = gestor.find(profesor);
	// if (objs == null || objs.length == 0)
	// return profesor;
	// else
	// return (Profesor) objs[0];
	// }

	// protected Alumno getAlumnoFromSql(GestorSql gestor, Alumno alumno) {
	// Object[] objs = gestor.find(alumno);
	// if (objs == null || objs.length == 0)
	// return alumno;
	// else
	// return (Alumno) objs[0];
	// }

	// protected Propuesta getPropuestaFromSql(GestorSql gestor,
	// Propuesta Propuesta) {
	// Object[] objs = gestor.find(Propuesta);
	// if (objs == null || objs.length == 0)
	// return Propuesta;
	// else
	// return (Propuesta) objs[0];
	// }

}
