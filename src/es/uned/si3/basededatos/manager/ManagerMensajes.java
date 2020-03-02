package es.uned.si3.basededatos.manager;

import java.sql.SQLException;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.*;
import es.uned.si3.persistencia.Mensaje.TipoMensaje;

/* El sistema debe tener un sistema de almacén de mensajes, que podrá ser consultado por
 los usuarios, tal que se muestren los mensajes creados en las siguientes situaciones:
 * Mensaje para el Administrador/Coordinador cuando un alumno envíe una
 solicitud, para que esta sea aceptada/rechazada y sea asignado un profesor en
 caso de ser un proyecto genérico (en los proyectos específicos el profesor
 asignado debe ser el mismo que creó la propuesta).
 * Mensaje para un alumno en caso de que su solicitud sea denegada o aceptada.
 * Mensaje para un profesor cuando se le asigne algún PFC.
 * Mensaje para un alumno cuando el PFC se cierre introduciendo la calificación.
 */
public class ManagerMensajes {

	GestorSql sql;
	Mensaje mensaje;

	// MensajeTipo mensajeTipo ;
	public ManagerMensajes(GestorSql sql) {

		this.sql = sql;// GestorSql.getInstance("test","root","");//inicio la
						// base de datos
	}

	public void CrearDatos() {
		// solicitud (aceptada/denegada)-> alumno
		// asignado PFC -> profesor
		// nota de PFC -> alumno
		// mensajeTipo =new MensajeTipo();
		//
		// mensajeTipo.ID=1;
		// mensajeTipo.significado="Peticion de solicitud";
		// sql.crear(mensajeTipo);
		//
		// mensajeTipo.ID=2;
		// mensajeTipo.significado="Ya esta disponible el resultado de la solicitud";
		// sql.crear(mensajeTipo);
		//
		// mensajeTipo.ID=3;
		// mensajeTipo.significado="Le ha sido asignado un pPFC";
		// sql.crear(mensajeTipo);
		//
		// mensajeTipo.ID=4;
		// mensajeTipo.significado="Ya esta disponible la nota de PFC ";
		// sql.crear(mensajeTipo);

	}

	// Envia un alumno una una solicitud a un administrador para que se le
	// asocien a un proyecto
	public void fromAlumnoToAdministrador(Solicitud solicitud, Alumno alumno,
			Profesor profesor) throws SQLException {
		mensaje = mensaje();
		// if( mensajeTipo == null)
		// mensajeTipo= new MensajeTipo();
		// mensajeTipo.ID=1;
		mensaje.ID = sql.maxValorId(mensaje);
		mensaje.mensajeTipo = TipoMensaje.peticionSolicitud.toString();
		// mensaje.ID_Aministrador=administrador;
		mensaje.ID_Alumno = alumno;
		mensaje.ID_Profesor = profesor;
		mensaje.ID_Proyecto = new Proyecto();
		mensaje.mensaje = String.format("%s:%s", mensaje.ID,
				solicitud.ID_Propuesta.titulo);
		mensaje.ID_Solicitud = solicitud;
		mensaje.leido= Boolean.toString(false);
		sql.crear(mensaje);

	}

	// envia un administrador si ha sido concedida la solicitud o rechazada un
	// alumno
	public void formAdministradoToAlumnoSolicitud(Alumno alumno,
			Solicitud solicitud, Administrador administrador) throws SQLException {
		mensaje = mensaje();
		// if( mensajeTipo == null)
		// mensajeTipo= new MensajeTipo();
		 mensaje.ID_Alumno=alumno;
		// mensajeTipo.ID=2;
		mensaje.mensajeTipo = TipoMensaje.respuestaSolicitud.toString();
		mensaje.ID_Solicitud = solicitud;
		sql.crear(mensaje);
	}

	// envia el administrador un mensaje al profesor que se le asigna un
	// proyecto(PFC)
	public void fromAdministradorToProfesor(Administrador administrador,
			Proyecto proyecto, Profesor profesor) throws SQLException {
		mensaje = mensaje();
		// if( mensajeTipo == null)
		// mensajeTipo= new MensajeTipo();
		// mensajeTipo.ID=3;
		mensaje.mensajeTipo = TipoMensaje.PFCAsignado.toString();
		mensaje.ID_Profesor = profesor;
		mensaje.ID_Proyecto = proyecto;
		mensaje.ID= sql.maxValorId(mensaje);
		mensaje.ID_Alumno= new Alumno();
		mensaje.ID_Solicitud= new Solicitud();
		mensaje.leido= Boolean.toString(false);
		mensaje.mensaje="Mensaje del administrador al profesor";
		sql.crear(mensaje);

	}

	// envia un profesor a un alumno que la nota esta disponible
	public void formProfesorToAlumnoCalificacionPFC(Profesor profesor,
			Proyecto proyecto, Alumno alumno) throws SQLException {
		mensaje = mensaje();
		// if( mensajeTipo == null)
		// mensajeTipo= new MensajeTipo();
		// mensajeTipo.ID=4;
		mensaje.mensajeTipo = TipoMensaje.disponibleNota.toString();
		mensaje.ID_Proyecto = proyecto;
		mensaje.ID_Alumno = alumno;
		mensaje.ID_Profesor = profesor;
		sql.crear(mensaje);

	}

	// usado para iniciar un tipo mensaje
	private Mensaje mensaje() throws SQLException {
		Mensaje mensaje = new Mensaje();
		mensaje.ID_Alumno = new Alumno();// si el mensaje es de un alumno
		// mensaje.ID_Aministrador=new Administrador();
		mensaje.ID_Solicitud = new Solicitud();
		mensaje.ID_Profesor = new Profesor();
		mensaje.ID_Proyecto = new Proyecto();
		mensaje.ID = sql.maxValorId(mensaje);// id incremental
		return mensaje;
	}

}
