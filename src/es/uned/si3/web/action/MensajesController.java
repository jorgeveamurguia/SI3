package es.uned.si3.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.basededatos.manager.ManagerMensajes;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.Mensaje;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.persistencia.UserType;
import es.uned.si3.util.LoggerHelper;

public class MensajesController extends BaseController {
	private final String VER_MENSAJES = "verMensajes";
	String tipoMensaje;

	public String getTipoMensaje() {
		return this.tipoMensaje;
	}

	public void setTipoMensaje(String tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}

	// Propuesta propuesta = null;
	List<String> listaTiposMensaje = null;

	public List<String> getListaTiposMensaje() {
		return this.listaTiposMensaje;
	}

	public void setListaTiposMensaje(List<String> listaTiposMensaje) {
		this.listaTiposMensaje = listaTiposMensaje;
	}

	List<MensajeEx> listaMensajes;

	public List<MensajeEx> getListaMensajes() {
		return this.listaMensajes;
	}

	public void setListaMensajes(List<MensajeEx> listaMensajes) {
		this.listaMensajes = listaMensajes;
	}

	public MensajesController() {
		listaTiposMensaje = new ArrayList<String>();
		listaTiposMensaje.add(Mensaje.TipoMensaje.disponibleNota.toString());
		listaTiposMensaje.add(Mensaje.TipoMensaje.peticionSolicitud.toString());
		listaTiposMensaje.add(Mensaje.TipoMensaje.PFCAsignado.toString());
		listaTiposMensaje
				.add(Mensaje.TipoMensaje.respuestaSolicitud.toString());
	}

	@Override
	public String execute() throws Exception {
		return INPUT;
	};

	public String ver() {
		Mensaje mensaje = new Mensaje();

		GestorSql gestor;
		try {
			// boolean useFilter = getTipoMensaje() != null
			// && !getTipoMensaje().equals("*");
			// if (useFilter) {
			// mensaje.mensajeTipo = getTipoMensaje();
			// addMessage(String.format("Mensajes de tipo %s",
			// getTipoMensaje()));
			// }
			//
			gestor = GestorSql.getInstance();
			String userName = getLoggedUserName();
			String userType = getLoggedUserType();

			List<Mensaje> mensajes = Manager.getInstancesFromSql(gestor,
					mensaje);
			if( mensajes == null || mensajes.size() == 0) {
				this.addActionError("No hay mensajes actualmente");
				return START;
			}

			listaMensajes = new ArrayList<MensajesController.MensajeEx>();
			for (Mensaje msg : mensajes) {
				boolean valid = false;
				try {
					if (userType.equals(UserType.Profesor.toString())) {
						valid = msg.mensajeTipo.equals(Mensaje.TipoMensaje.PFCAsignado.toString());
						if( !valid)
							continue;
						valid= (msg.ID_Profesor != null
								&& msg.ID_Profesor.nombre_usuario != null && msg.ID_Profesor.nombre_usuario
								.equals(userName));
					} else if (userType.equals(UserType.Alumno.toString())) {
						valid = msg.mensajeTipo.equals(Mensaje.TipoMensaje.disponibleNota.toString()) ||
								msg.mensajeTipo.equals(Mensaje.TipoMensaje.respuestaSolicitud.toString());
						if( !valid)
							continue;
						valid = msg.ID_Alumno != null
								&& msg.ID_Alumno.nombre_usuario != null
								&& msg.ID_Alumno.nombre_usuario
										.equals(userName);
					} else {
						valid = msg.mensajeTipo != null
								&& msg.mensajeTipo
										.equals(Mensaje.TipoMensaje.peticionSolicitud
												.toString());
					}
				} catch (Exception e) {
					addActionError(e.getMessage());
					return START;
				}
				if (valid)
					listaMensajes.add(new MensajeEx(msg));
			}
			if (listaMensajes.size() == 0) {
				addActionMessage(String.format("No hay mensajes para el usuario %s",
						userName));
				return START;
			}
			
			return VER_MENSAJES;
		} catch (Exception e) {
			addActionError(e.getMessage());
			return START;
		}
	}

	private class MensajeEx extends Mensaje {
		public String de;
		public String informacion;

		public MensajeEx(Mensaje other) throws Exception {
			try {
				this.ID = other.ID;
				this.ID_Alumno = other.ID_Alumno;
				this.ID_Profesor = other.ID_Profesor;
				this.ID_Proyecto = other.ID_Proyecto;
				this.ID_Solicitud = other.ID_Solicitud;
				this.leido = other.leido;
				this.mensaje = other.mensaje;
				this.mensajeTipo = other.mensajeTipo;
				if (this.mensajeTipo.equals(Mensaje.TipoMensaje.disponibleNota
						.toString())) {
					this.de = "Profesor " + this.ID_Profesor.nombre_usuario;
					this.informacion = String.format("Nota PFC=%s",
							this.ID_Proyecto.calificacion);
				} else if (this.mensajeTipo
						.equals(Mensaje.TipoMensaje.peticionSolicitud
								.toString())) {
					this.de = "Alumno " + this.ID_Alumno.nombre_usuario;
					this.informacion = String.format(
							"Solicitud de la propuesta=%s",
							this.ID_Solicitud.ID_Propuesta.titulo);
				} else if (this.mensajeTipo
						.equals(Mensaje.TipoMensaje.PFCAsignado.toString())) {
					this.de = "Administrador";
					this.informacion = String.format("Asignación de PFC=%s",
							this.ID_Proyecto.titulo);
				} else if (this.mensajeTipo
						.equals(Mensaje.TipoMensaje.respuestaSolicitud
								.toString())) {
					this.de = "Administrador";
					this.informacion = String.format(
							"Respuesta %s de solicitud de %s",
							this.ID_Solicitud.aceptada_rechazada,
							this.ID_Solicitud.ID_Propuesta.titulo);
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

//	public void addMessage(String newMessage) {
//		this.addActionMessage(aMessage)
//		if (this.message == null)
//			this.message = "";
//		if (this.message.length() > 0)
//			this.message += "; ";
//		this.message += newMessage;
//	}
}