package es.uned.si3.web.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.basededatos.manager.ManagerAlumosEx;
import es.uned.si3.persistencia.Mensaje;
import es.uned.si3.persistencia.Mensaje.TipoMensaje;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Propuesta;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.util.LoggerHelper;

public class SolicitudesPFCController extends BaseController {
	private static final String REVISAR_SOLICITUDES = "revisarSolicitudes";
	private static final String VER_SOLICITUDES = "verSolicitudes";

	public String anioAcademicoFilter = "*";
	public String estadoSolicitudFilter = "*";
	public SortedSet<String> setAniosAcademicos = null;
	public SortedSet<String> setEstadosSolicitud = null;
	boolean propuestaEspecifica;
	List<SolicitudEx> solicitudes = null;

	public SolicitudesPFCController() {
		setAniosAcademicos = new TreeSet<String>();
		setEstadosSolicitud = new TreeSet<String>();
		setEstadosSolicitud.add(Solicitud.EstadoSolicitud.aceptada.toString());
		setEstadosSolicitud.add(Solicitud.EstadoSolicitud.pendiente.toString());
		setEstadosSolicitud.add(Solicitud.EstadoSolicitud.rechazada.toString());
		this.solicitudes = new ArrayList<SolicitudesPFCController.SolicitudEx>();
	}

	public List<SolicitudEx> getSolicitudes() {
		return this.solicitudes;
	}

	private int numeroSolicitudes = 0;

	public int getNumeroSolicitudes() {
		return numeroSolicitudes;
	}

	public void setAnioAcademicoFilter(String anioAcademicoFilter) {
		this.anioAcademicoFilter = anioAcademicoFilter;
	}

	public String getAnioAcademicoFilter() {
		return this.anioAcademicoFilter;
	}

	public void setPropuestaEspecifica(boolean propuestaEspecifica) {
		this.propuestaEspecifica = propuestaEspecifica;
	}

	public boolean getPropuestaEspecifica() {
		return this.propuestaEspecifica;
	}

	public void setEstadoSolicitudFilter(String estadoSolicitudFilter) {
		this.estadoSolicitudFilter = estadoSolicitudFilter;
	}

	public String getEstadoSolicitudFilter() {
		return this.estadoSolicitudFilter;
	}

	String[] listaProfesores = null;

	public String[] getListaProfesores() {
		return listaProfesores;
	}

	String selectedProfesor = null;

	public String getSelectedProfesor() {
		return this.selectedProfesor;
	}

	public void setSelectedProfesor(String profesor) {
		this.selectedProfesor = profesor;
	}

	private String selectedIDMensaje = null;

	public String getSelectedIDMensaje() {
		return this.selectedIDMensaje;
	}

	public void setSelectedIDMensaje(String selectedIDMensaje) {
		this.selectedIDMensaje = selectedIDMensaje;
	}

	public String revisar() {
		if (getSelectedIDMensaje() == null) {
			this.addActionMessage("Seleccione  una propuesta para aceptar/rechazar");
			return cargarSolicitudes();
		} else {
			GestorSql gestor = null;
			try {
				gestor = GestorSql.getInstance();
				// Busca el mensaje para ver si es realmente pendiente
				Mensaje mensaje = new Mensaje();
				mensaje.ID = Integer.parseInt(getSelectedIDMensaje());
				mensaje = Manager.getInstanceFromSql(gestor, mensaje, true);
				if (mensaje == null
						|| mensaje.ID_Solicitud.aceptada_rechazada == null
						|| !mensaje.ID_Solicitud.aceptada_rechazada
								.equals(Solicitud.EstadoSolicitud.pendiente
										.toString())) {
					this.addActionError(String.format(
							"Mensaje %s no está pendiente en la BD",
							getSelectedIDMensaje()));
					return cargarSolicitudes();
				}

				// Carga la lista de solicitudes con 1 sola
				solicitudes = new ArrayList<SolicitudesPFCController.SolicitudEx>();
				SolicitudEx mySolicitudEx = new SolicitudEx(mensaje);
				solicitudes.add(mySolicitudEx);

				propuestaEspecifica = mensaje.ID_Solicitud.ID_Propuesta.tipo_general_especifica
						.equals(Propuesta.EnumTipoPropuesta.especifica
								.toString());
				if (propuestaEspecifica) {
					// message =
					// "Profesor no se puede cambiar al tratarse de una propuesta específica";
					listaProfesores = new String[1];
					listaProfesores[0] = mensaje.ID_Profesor.nombre_usuario;
					// setSelectedProfesor(listaProfesores[0]);
				} else {
					List<Profesor> lista = Manager.getInstancesFromSql(gestor,
							new Profesor());
					if (lista == null || lista.size() == 0) {
						this.addActionError("No hay profesores registrados");
						return VER_SOLICITUDES;
					}
					listaProfesores = new String[lista.size()];
					for (int n = 0; n < lista.size(); n++) {
						listaProfesores[n] = lista.get(n).nombre_usuario;
					}
				}

				return REVISAR_SOLICITUDES;
			} catch (Exception e) {
				this.addActionError("Error revisando solicitudes "
						+ e.getMessage());
				setSelectedIDMensaje(null);
				return cargarSolicitudes();
			} finally {
			}

		}
	}

	public String ver() {
		return cargarSolicitudes();
	}

	public String aceptar() {
		if (!validString(getSelectedIDMensaje())) {
			this.addActionError("Error: Seleccione  una propuesta para aceptar");
			return cargarSolicitudes();
		}

		if (!validString(getSelectedProfesor())) {
			this.addActionError("Error: Seleccione un profesor para que se encargue del proyecto");
			return cargarSolicitudes();
		}

		// Extrae el idSolicitud del título
		String idMensaje = getSelectedIDMensaje();

		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			Mensaje mensaje = new Mensaje();
			mensaje.ID = Integer.parseInt(idMensaje);
			mensaje = Manager.getInstanceFromSql(gestor, mensaje, false);

			Profesor profesorAsignado = new Profesor();
			profesorAsignado.nombre_usuario = getSelectedProfesor();
			profesorAsignado = Manager.getInstanceFromSql(gestor,
					profesorAsignado, false);
			boolean rc = ManagerAlumosEx.aceptarPropuesta(gestor,
					mensaje.ID.toString(), profesorAsignado);
			if (rc) {
				this.addActionMessage("Solicitud de proyecto aceptada");
				setSelectedIDMensaje(null);
				return START;
			} else {
				this.addActionError("Solicitud de proyecto no pudo ser aceptada: "
						+ ManagerAlumosEx.getErrorMessage());
				setSelectedIDMensaje(null);
				return START;
			}
		} catch (Exception e) {
			this.addActionError("Error aceptando propuestas " + e.getMessage());
			setSelectedIDMensaje(null);
			return START;
		} finally {
			if (gestor != null)
				gestor.commit();
		}

	}

	public String rechazar() {
		if (getSelectedIDMensaje() == null) {
			this.addActionError("Seleccione una propuesta para rechazar");
			return revisar();
		}
		// Extrae el idSolicitud del título
		String idMensaje = getSelectedIDMensaje();

		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			Mensaje mensaje = new Mensaje();
			mensaje.ID = Integer.parseInt(idMensaje);
			mensaje = Manager.getInstanceFromSql(gestor, mensaje, false);

			boolean rc = ManagerAlumosEx.rechazarPropuesta(gestor,
					mensaje.ID.toString());
			if (rc) {
				try {
					addActionMessage(String.format(
							"Solicitud de proyecto %s rechazada",
							mensaje.ID_Solicitud.ID_Propuesta.titulo));
				} catch (Exception e) {
					addActionMessage("Solicitud de proyecto rechazada");
				}
				setSelectedIDMensaje(null);
				return revisar();
			} else {
				this.addActionError("Solicitud de proyecto no pudo ser rechazada: "
						+ ManagerAlumosEx.getErrorMessage());
				setSelectedIDMensaje(null);
				return START;
			}
		} catch (Exception e) {
			this.addActionError("Error rechazando propuestas " + e.getMessage());
			setSelectedIDMensaje(null);
			return revisar();
		} finally {
			if (gestor != null)
				gestor.commit();
		}

	}

	// private String cargarSolicitudesPendientes() {
	// GestorSql gestor = null;
	// try {
	// gestor = GestorSql.getInstance();
	// List<Profesor> lista = Manager.getInstancesFromSql(gestor,
	// new Profesor());
	// if (lista == null || lista.size() == 0) {
	// setMessage("No hay profesores registrados");
	// return REVISAR_SOLICITUDES;
	// }
	// listaProfesores = new String[lista.size()];
	// for (int n = 0; n < lista.size(); n++) {
	// listaProfesores[n] = lista.get(n).nombre_usuario;
	// }
	//
	// Mensaje mensaje = new Mensaje();
	// // mensaje.leido = Boolean.toString(false);
	// // MensajeTipo mensajeTipo = new MensajeTipo();
	// // mensajeTipo.ID = 1;
	// mensaje.mensajeTipo = TipoMensaje.peticionSolicitud.toString();
	// List<Mensaje> mensajes = Manager.getInstancesFromSql(gestor,
	// mensaje);
	// if (mensajes == null || mensajes.size() == 0) {
	// setMessage("No hay solicitudes pendientes");
	// return START;
	// }
	// solicitudes = new ArrayList<SolicitudesPFCController.SolicitudEx>();
	// for (int n = 0; n < mensajes.size(); n++) {
	// mensaje = mensajes.get(n);
	// if (mensaje.ID_Solicitud.aceptada_rechazada
	// .equals(Solicitud.EstadoSolicitud.pendiente.toString()))
	// solicitudes.add(new SolicitudEx(mensaje));
	// }
	//
	// // Solicitud solicitud = new Solicitud();
	// // solicitud.aceptada_rechazada =
	// // Solicitud.EstadoSolicitud.pendiente
	// // .toString();
	// // List<Solicitud> mySolicitudes =
	// // Manager.getInstancesFromSql(gestor,
	// // solicitud);
	// // solicitudes = new SolicitudEx[mySolicitudes.size()];
	// // for (int n = 0; n < mySolicitudes.size(); n++) {
	// // Solicitud s = mySolicitudes.get(n);
	// // s.ID_Propuesta = Manager.getInstanceFromSql(gestor,
	// // s.ID_Propuesta, false);
	// // solicitudes[n] = new SolicitudEx(s.ID_Propuesta.titulo, s);
	// // }
	//
	// return VER_SOLICITUDES;
	// } catch (Exception e) {
	// LoggerHelper.getDefaultLogger().error(e);
	// setMessage(e.getMessage());
	// return START;
	// } finally {
	// // if (gestor != null)
	// // gestor.close();
	// }
	// }

	private String cargarSolicitudes() {
		GestorSql gestor = null;
		try {
			boolean useYear = getAnioAcademicoFilter() != null
					&& !getAnioAcademicoFilter().equals("*");
			boolean useEstado = getEstadoSolicitudFilter() != null
					&& !getEstadoSolicitudFilter().equals("*");
			if (setAniosAcademicos == null)
				setAniosAcademicos = new TreeSet<String>();
			else 
				setAniosAcademicos.clear();
//			this.addActionMessage("useYear: " + useYear);
//			this.addActionMessage("useEstado: " + useEstado);
			gestor = GestorSql.getInstance();
//			this.addActionMessage("gestor: " + gestor);
			Solicitud solicitud = new Solicitud();
			if (useYear) {
				this.addActionMessage(String.format(
						"Solicitudes de PFC del año %s",
						getAnioAcademicoFilter()));
			}
			if (useEstado) {
				this.addActionMessage((String.format(
						"Solicitudes de PFC con estado %s",
						getEstadoSolicitudFilter())));
			}

			Mensaje mensaje = new Mensaje();
			mensaje.ID_Alumno = new Alumno();
			mensaje.ID_Profesor = new Profesor();
			mensaje.ID_Proyecto = new Proyecto();
			mensaje.ID_Solicitud = new Solicitud();

			// mensaje.leido = Boolean.toString(false);
			// MensajeTipo mensajeTipo = new MensajeTipo();
			// mensajeTipo.ID = 1;
			mensaje.mensajeTipo = TipoMensaje.peticionSolicitud.toString();
//			this.addActionMessage("mensaje: " + mensaje);

			List<Mensaje> mensajes = Manager.getInstancesFromSql(gestor,
					mensaje);
//			this.addActionMessage("mensajes: " + mensajes);
//			this.addActionMessage("mensajes.COUNT: " + mensajes.size());
			if (mensajes == null || mensajes.size() == 0) {
				this.addActionMessage("No hay solicitudes pendientes");
				return START;
			}
			solicitudes = new ArrayList<SolicitudesPFCController.SolicitudEx>();
			for (int n = 0; n < mensajes.size(); n++) {
				mensaje = mensajes.get(n);
//				this.addActionMessage("mensajes " + n + ":" + mensaje);
				if (useYear
						&& mensaje.ID_Solicitud != null
						&& !mensaje.ID_Solicitud.anio
								.equals(getAnioAcademicoFilter()))
					continue;
				if (useEstado
						&& mensaje.ID_Solicitud != null
						&& !mensaje.ID_Solicitud.aceptada_rechazada
								.equals(getEstadoSolicitudFilter()))
					continue;

				SolicitudEx mySolicitudEx = new SolicitudEx(mensaje);
//				this.addActionMessage("mySolEx: " + mySolicitudEx);
				solicitudes.add(mySolicitudEx);
				setAniosAcademicos.add(mySolicitudEx.anio);
			}
			if (solicitudes.size() == 0)
				this.addActionMessage("No hay solicitudes disponibles");
			numeroSolicitudes = solicitudes.size();

			if (getAnioAcademicoFilter() == null
					|| getAnioAcademicoFilter().equals("*")) {
				return VER_SOLICITUDES;
			}

			// Completa los años académicos
			solicitud = new Solicitud();
//			this.addActionMessage("Solicitud:"+solicitud);

			List<Solicitud> mySolicitudes = Manager.getInstancesFromSql(gestor,
					solicitud);
			if (mySolicitudes == null) {
//				this.addActionMessage("Solicitudes=null");
				setAniosAcademicos.clear();
				return VER_SOLICITUDES;
			}
			Iterator<Solicitud> iterator = mySolicitudes.iterator();
			int n = 0;
			while (iterator.hasNext()) {
				Solicitud mySolicitud = iterator.next();
//				this.addActionMessage("mySolicitud:"+mySolicitud);
				setAniosAcademicos.add(mySolicitud.anio);
			}

			return VER_SOLICITUDES;
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error(e);
			this.addActionError(e.getMessage());
			return START;
		} finally {
//			this.addActionMessage("FIN");
		}
	}

	private class SolicitudEx extends Solicitud {
		String tituloMensaje = null;
		String IDMensaje = null;
		boolean notIsPendiente;

		public boolean getNotIsPendiente() {
			return notIsPendiente;
		}

		public void setNotIsPendiente(boolean notIsPendiente) {
			this.notIsPendiente = notIsPendiente;
		}

		public String getTituloMensaje() {
			return this.tituloMensaje;
		}

		public String getIDMensaje() {
			return this.IDMensaje;
		}

		public SolicitudEx(Mensaje mensaje) throws Exception {
			// String tituloMensaje, Solicitud other) {
			// }
			if (mensaje == null)
				throw new Exception("Mensaje es nulo completando la solicitud");
			this.IDMensaje = Integer.toString(mensaje.ID);
//			addActionMessage("mensajeID: " + mensaje.ID);

			Solicitud other = mensaje.ID_Solicitud;
//			addActionMessage("mensaje.solicitud: " + other);
			GestorSql gestor = null;
			try {
				gestor = GestorSql.getInstance();
				this.anio = other.anio;
//				addActionMessage("año: " + other.anio);
				this.notIsPendiente = other.aceptada_rechazada == null ? true
						: !other.aceptada_rechazada
								.equals(Solicitud.EstadoSolicitud.pendiente
										.toString());
				this.aceptada_rechazada = other.aceptada_rechazada;
				this.pdfConocimientos = other.pdfConocimientos;
				// this.ID_Administrador = Manager.getInstanceFromSql(gestor,
				// other.ID_Administrador);
				this.ID_Alumno = Manager.getInstanceFromSql(gestor,
						other.ID_Alumno, false);
				this.ID_Propuesta = Manager.getInstanceFromSql(gestor,
						other.ID_Propuesta, false);
				this.tituloMensaje = this.ID_Propuesta == null ? "título nulo"
						: this.ID_Propuesta.titulo;
				this.ID = other.ID;
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(
						"EXCEPTION en main: " + e.getMessage());
				addActionError("Error: " + e.getMessage());
			} finally {
				// if (gestor != null)
				// gestor.close();
			}
		}

		public String getTipo() {
			return this.ID_Propuesta.tipo_general_especifica;
		}

		public int getID() {
			return this.ID;
		}

		public String getEstado() {
			return this.aceptada_rechazada;
		}

		public String getProfesor() {
			return String.format("%s, %s",
					this.ID_Propuesta.ID_Profesor.apellidos,
					this.ID_Propuesta.ID_Profesor.nombre);
		}

		public String getAlumno() {
			return String.format("%s, %s", this.ID_Alumno.apellidos,
					this.ID_Alumno.nombre);
		}
	}
}