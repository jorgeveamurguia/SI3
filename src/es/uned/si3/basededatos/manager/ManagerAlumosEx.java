package es.uned.si3.basededatos.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.*;
import es.uned.si3.persistencia.Mensaje.TipoMensaje;
import es.uned.si3.persistencia.Propuesta.EnumEstadoPropuesta;
import es.uned.si3.persistencia.Propuesta.EnumTipoPropuesta;
import es.uned.si3.persistencia.Solicitud.EstadoSolicitud;

public class ManagerAlumosEx extends Manager {
	private static String message = "";

	public static boolean solicitarPropuesta(GestorSql gestor,
			String nombreAlumno, String IDPropuesta, File fileConocimientos,
			String fileName) throws Exception {
		try {
			gestor.openTransaction();

			// 2- Hace la solicitud
			Solicitud solicitud = new Solicitud();
			solicitud.ID = gestor.maxValorId(solicitud);
			solicitud.anio = getCurrentYearStr();
			solicitud.aceptada_rechazada = Solicitud.EstadoSolicitud.pendiente
					.toString();
			solicitud.pdfConocimientos = new FileInputStream(fileConocimientos);
			Administrador administrador = new Administrador();
			administrador.nombre_usuario = ADMIN_USER_NAME;
			administrador = getInstanceFromSql(gestor, administrador, false);

			// solicitud.ID_Administrador = administrador;
			Alumno alumno = new Alumno();
			alumno.nombre_usuario = nombreAlumno;
			alumno = getInstanceFromSql(gestor, alumno, false);
			solicitud.ID_Alumno = alumno;
			Propuesta propuesta = new Propuesta();
			propuesta.ID = Integer.parseInt(IDPropuesta);
			propuesta = getInstanceFromSql(gestor, propuesta, true);
			if (propuesta == null) {
				throw new Exception(String.format(
						"Error: propuesta %s no existe", IDPropuesta));
			}
			alumno.ID_Propuesta = propuesta;
			gestor.update(alumno);

			Profesor profesor = propuesta.ID_Profesor;

			// propuesta.ID_Profesor = profesor;
			solicitud.ID_Propuesta = propuesta;
			if (solicitud.ID_Propuesta.estado_abierta_cerrada
					.equals(Propuesta.EnumEstadoPropuesta.cerrada.toString())) {
				throw new Exception(String.format(
						"Error: propuesta %s cerrada", IDPropuesta));
			}

			// 3- Se crea la solicitud
			gestor.crear(solicitud);
			gestor.insertFile(solicitud.ID, "Solicitud", "pdfConocimientos",
					solicitud.pdfConocimientos);

			// // 4- Si no es genérica, se cierra a expensas de confirmación
			// para
			// que
			// // nadie más lo solicite
			// if (solicitud.ID_Propuesta.tipo_general_especifica
			// .equals(Propuesta.EnumTipoPropuesta.especifica.toString())) {
			// propuesta.estado_abierta_cerrada =
			// Propuesta.EnumEstadoPropuesta.cerrada
			// .toString();
			// gestor.update(propuesta);
			// }

			// 5- Envía un mensaje al administrador
			(new ManagerMensajes(gestor)).fromAlumnoToAdministrador(solicitud,
					alumno, profesor);

			// hace commit de la transacción
			gestor.commit();
			return true;
		} catch (Exception e) {
			setMessage("Excepción solicitando proyecto: " + e.getMessage());
			gestor.rollback();
			return false;
		}
	}

	public static boolean aceptarPropuesta(GestorSql gestor, String idMessage,
			Profesor profesorAsignado) throws Exception {
		try {
			gestor.openTransaction();

			// 1-Recupera el mensaje
			Mensaje msg = new Mensaje();
			msg.ID = Integer.parseInt(idMessage);
			msg = Manager.getInstanceFromSql(gestor, msg, true);
			if (msg == null)
				return false;
			// 1.1- Modifica el mensaje
			msg.leido = Boolean.toString(true);
			gestor.update(msg);

			// 2- Marca la solicitud como aceptada
			Solicitud solicitud = msg.ID_Solicitud;
			if (solicitud == null)
				return false;
			solicitud.aceptada_rechazada = EstadoSolicitud.aceptada.toString();
			gestor.update(solicitud);

			// 3- Crea un proyecto en base a la solicitud
			Propuesta propuesta = solicitud.ID_Propuesta;
			Proyecto proyecto = new Proyecto(propuesta);
			proyecto.ID = gestor.maxValorId(proyecto);
			proyecto.ID_Profesor = profesorAsignado;
			gestor.crear(proyecto);

			// 3- Asigna dicho proyecto al alumno
			Alumno alumno = msg.ID_Alumno;
			alumno.ID_Proyecto = proyecto;
			gestor.update(alumno);

			// 4- Cierra la propuesta si esta no es genérica
			if (propuesta.tipo_general_especifica
					.equals(EnumTipoPropuesta.especifica.toString())) {
				propuesta.estado_abierta_cerrada = EnumEstadoPropuesta.cerrada
						.toString();
				gestor.update(propuesta);
				// 4.1- Rechaza todas las solicitudes pendiente para
				// este proyecto en caso que no sea generica.
				Mensaje msg2 = new Mensaje();
				List<Mensaje> msgs = Manager.getInstancesFromSql(gestor, msg2);
				boolean rc = false;
				for (Mensaje msg3 : msgs) {
					if (msg3.ID != Integer.parseInt(idMessage)) {
						if (msg3.ID_Solicitud != null &&
							msg3.ID_Solicitud.ID_Propuesta != null &&
							solicitud.ID_Propuesta != null && 
							msg3.ID_Solicitud.ID_Propuesta.ID == solicitud.ID_Propuesta.ID) {
							rc = rechazarPropuesta(gestor,
									Integer.toString(msg3.ID));
							// Si un rechace da error. Rollback de todo!
							if (rc == false) {
								throw new Exception(getErrorMessage());
							}
						}
					}
				}
			}

			// 5- Envía un mensaje del administrador al alumno
			Administrador administrador = new Administrador();
			administrador.nombre_usuario = ADMIN_USER_NAME;
			administrador = Manager.getInstanceFromSql(gestor, administrador,
					false);
			
			(new ManagerMensajes(gestor)).formAdministradoToAlumnoSolicitud(
					alumno, solicitud, administrador);

			// 6- Envía un mensaje del administrador al profesor
			(new ManagerMensajes(gestor)).fromAdministradorToProfesor(
					administrador, proyecto, profesorAsignado);

			// hace commit de la transacción
			gestor.commit();
			return true;
		} catch (Exception e) {
			setMessage("Excepción solicitando proyecto: " + e.getMessage());
			gestor.rollback();
			return false;
		}
	}

	public static boolean rechazarPropuesta(GestorSql gestor, String idMessage)
			throws Exception {
		try {
			gestor.openTransaction();

			// 1-Recupera el mensaje
			Mensaje msg = new Mensaje();
			msg.ID = Integer.parseInt(idMessage);
			msg = Manager.getInstanceFromSql(gestor, msg, true);
			if (msg == null)
				return false;
			// 1.1- Modifica el mensaje
			msg.leido = Boolean.toString(true);
			gestor.update(msg);

			// 2- Marca la solicitud como rechazada
			Solicitud solicitud = msg.ID_Solicitud;
			if (solicitud == null)
				return false;
			solicitud.aceptada_rechazada = EstadoSolicitud.rechazada.toString();
			gestor.update(solicitud);

			// 5- Envía un mensaje del administrador al alumno
			Administrador administrador = new Administrador();
			administrador.nombre_usuario = ADMIN_USER_NAME;
			administrador = Manager.getInstanceFromSql(gestor, administrador,
					false);

			// 6- Anula la propuesta realizada
			Alumno alumno = msg.ID_Alumno;
			alumno.ID_Propuesta = new Propuesta();
			alumno.ID_Propuesta.ID = -1;
			gestor.update(alumno);

			(new ManagerMensajes(gestor)).formAdministradoToAlumnoSolicitud(
					alumno, solicitud, administrador);

			// hace commit de la transacción
			gestor.commit();
			return true;
		} catch (Exception e) {
			setMessage("Excepción solicitando proyecto: " + e.getMessage());
			gestor.rollback();
			return false;
		}
	}

	public static String getErrorMessage() {
		return message;
	}

	private static void setMessage(String newMessage) {
		message = newMessage;
	}

}
