package es.uned.si3.basededatos.manager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sun.net.www.protocol.http.HttpURLConnection;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.Administrador;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.CentroAsociado;
import es.uned.si3.persistencia.Departamento;
import es.uned.si3.persistencia.Mensaje;
import es.uned.si3.persistencia.Mensaje.TipoMensaje;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Propuesta;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.util.LoggerHelper;

public class Manager {
	public static final String ADMIN_USER_NAME = "Admin";

	public static String getCurrentYearStr() {
		return Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstanceFromSql(GestorSql gestor, T reference,
			boolean returnNull) {
		if (reference == null)
			return null;

		Object[] objs = gestor.find(reference);
		if (objs == null || objs.length == 0)
			return returnNull ? null : reference;
		else {
			fillRelationships(gestor, objs[0], returnNull);
		}
		return (T) objs[0];
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstanceFromSqlIfValidId(GestorSql gestor,
			T reference, boolean returnNull) {
		if (reference.getClass().equals(Profesor.class)
				&& ((Profesor) reference).ID == null) {
			return null;
		} else if (reference.getClass().equals(Alumno.class)
				&& ((Alumno) reference).ID == null) {
			return null;
		} else if (reference.getClass().equals(Propuesta.class)
				&& ((Propuesta) reference).ID == null) {
			return null;
		} else if (reference.getClass().equals(Mensaje.class)
				&& ((Mensaje) reference).ID == null) {
			return null;
		} else if (reference.getClass().equals(Proyecto.class)
				&& ((Proyecto) reference).ID == null) {
			return null;
		} else
			return getInstanceFromSql(gestor, reference, returnNull);

	}

	private static <T> void fillRelationships(GestorSql gestor,
			Object reference, boolean returnNull) {
		// Profesor
		if (reference.getClass().equals(Profesor.class)) {
			((Profesor) reference).ID_Departamento = getInstanceFromSqlIfValidId(
					gestor, ((Profesor) reference).ID_Departamento, returnNull);
			// Alumno
		} else if (reference.getClass().equals(Alumno.class)) {
			((Alumno) reference).ID_Propuesta = getInstanceFromSqlIfValidId(
					gestor, ((Alumno) reference).ID_Propuesta, returnNull);
			((Alumno) reference).ID_Proyecto = getInstanceFromSqlIfValidId(
					gestor, ((Alumno) reference).ID_Proyecto, returnNull);
			((Alumno) reference).ID_CentroAsociado = getInstanceFromSqlIfValidId(
					gestor, ((Alumno) reference).ID_CentroAsociado, returnNull);
			// Propuesta
		} else if (reference.getClass().equals(Propuesta.class)) {
			((Propuesta) reference).ID_Profesor = getInstanceFromSqlIfValidId(
					gestor, ((Propuesta) reference).ID_Profesor, returnNull);
			// Mensaje
		} else if (reference.getClass().equals(Mensaje.class)) {
			((Mensaje) reference).ID_Alumno = getInstanceFromSqlIfValidId(
					gestor, ((Mensaje) reference).ID_Alumno, returnNull);
			((Mensaje) reference).ID_Profesor = getInstanceFromSqlIfValidId(
					gestor, ((Mensaje) reference).ID_Profesor, returnNull);
			((Mensaje) reference).ID_Proyecto = getInstanceFromSqlIfValidId(
					gestor, ((Mensaje) reference).ID_Proyecto, returnNull);
			((Mensaje) reference).ID_Solicitud = getInstanceFromSqlIfValidId(
					gestor, ((Mensaje) reference).ID_Solicitud, returnNull);
			// Solicitud
		} else if (reference.getClass().equals(Solicitud.class)) {
			((Solicitud) reference).ID_Alumno = getInstanceFromSqlIfValidId(
					gestor, ((Solicitud) reference).ID_Alumno, returnNull);
			((Solicitud) reference).ID_Propuesta = getInstanceFromSqlIfValidId(
					gestor, ((Solicitud) reference).ID_Propuesta, returnNull);
			// PFC
		} else if (reference.getClass().equals(Proyecto.class)) {
			((Proyecto) reference).ID_Profesor = getInstanceFromSqlIfValidId(
					gestor, ((Proyecto) reference).ID_Profesor, returnNull);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getInstancesFromSql(GestorSql gestor, T reference) {
		Object[] objs = gestor.find(reference);
		if (objs == null || objs.length == 0)
			return null;
		else {
			ArrayList<T> results = new ArrayList<T>();
			for (Object obj : objs) {
				fillRelationships(gestor, obj, false);
				results.add((T) obj);
			}
			return results;
		}
	}

	public static List<CentroAsociado> crearCentrosAsociadosTest(GestorSql sql)
			throws Exception {
		List<CentroAsociado> list = new ArrayList<CentroAsociado>();
		try {
			// sql.openTransaction("cat");
			CentroAsociado centroAsociado = new CentroAsociado();

			centroAsociado.CentroAsociado = "Almeria";
			centroAsociado.ID = 50000;
			sql.crear(centroAsociado);
			list.add(centroAsociado);

			centroAsociado = new CentroAsociado();
			centroAsociado.CentroAsociado = "Almeria- HUERCAL OVERA";
			centroAsociado.ID = 50002;
			sql.crear(centroAsociado);
			list.add(centroAsociado);

			centroAsociado = new CentroAsociado();
			centroAsociado.CentroAsociado = "Almeria-EL EJIDO";
			centroAsociado.ID = 50001;
			sql.crear(centroAsociado);
			list.add(centroAsociado);

			centroAsociado = new CentroAsociado();
			centroAsociado.CentroAsociado = "BAZA";
			centroAsociado.ID = 64000;
			sql.crear(centroAsociado);
			list.add(centroAsociado);

			CentroAsociado calatayud = new CentroAsociado();
			calatayud.CentroAsociado = "CALATAYUD";
			calatayud.ID = 8000;
			sql.crear(calatayud);
			list.add(calatayud);

			centroAsociado = new CentroAsociado();
			centroAsociado.CentroAsociado = "BARBASTRO";
			centroAsociado.ID = 19000;
			sql.crear(centroAsociado);
			list.add(centroAsociado);
		} finally {
//			sql.commit();
		}
		return list;
	}

	public static List<Departamento> crearDepartamentosTest(GestorSql sql)
			throws Exception {
		List<Departamento> list = new ArrayList<Departamento>();
		try {
			// sql.openTransaction("cdt");
			Departamento departamento = new Departamento();
			departamento.ID = sql.maxValorId(departamento);
			departamento.codigo = "1101";
			departamento.departamento = "LENGUAJES Y SISTEMAS INFORMATICOS";
			sql.crear(departamento);
			list.add(departamento);

			departamento.ID = sql.maxValorId(departamento);
			departamento.codigo = "1102";
			departamento.departamento = "INTELIGENCIA ARTIFICIAL";
			sql.crear(departamento);
			list.add(departamento);

			departamento.ID = sql.maxValorId(departamento);
			departamento.codigo = "1103";
			departamento.departamento = "INFORMATICA Y AUTOMATICA";
			sql.crear(departamento);
			list.add(departamento);

			departamento.ID = sql.maxValorId(departamento);
			departamento.codigo = "1104";
			departamento.departamento = "INGENIERIA DEL SOFTW. Y SIST. INFORMATICOS";
			sql.crear(departamento);
			list.add(departamento);

			departamento.ID = sql.maxValorId(departamento);
			departamento.codigo = "1105";
			departamento.departamento = "SISTEMAS DE COMUNICACION Y CONTROL";
			sql.crear(departamento);
			list.add(departamento);
		} finally {
//			sql.commit();
		}
		return list;
	}

	public static Alumno crearAlumnoTest(GestorSql sql, int idAlumno,
			CentroAsociado centroAsociado, Propuesta propuesta,
			Proyecto proyecto) throws Exception {
		try {
			// sql.openTransaction("calt");
			if (centroAsociado == null)
				throw new Exception("No hay centro asociado");
			Alumno alumno = new Alumno();

			alumno.ID = idAlumno;
			alumno.apellidos = String.format("apellido %d", idAlumno);
			alumno.dni = String.format("%d%d%d%d%d%d%d%d%c", idAlumno,
					idAlumno, idAlumno, idAlumno, idAlumno, idAlumno, idAlumno,
					idAlumno, ('A' + idAlumno));
			alumno.email = String.format("email_%d@uned.es", idAlumno);
			alumno.ID_CentroAsociado = centroAsociado;
			alumno.ID_Propuesta = propuesta == null ? new Propuesta()
					: propuesta;
			alumno.ID_Proyecto = proyecto == null ? new Proyecto() : proyecto;
			alumno.nombre = String.format("Pepe %d", idAlumno);
			alumno.nombre_usuario = String.format("a%d", idAlumno);
			alumno.numero_expediente = "" + idAlumno;
			alumno.password = alumno.nombre_usuario;

			if (propuesta != null) {
				Solicitud solicitud = crearSolicitud(sql, alumno, propuesta);
				Mensaje mensaje = crearMensaje(sql,
						Mensaje.TipoMensaje.peticionSolicitud, alumno,
						propuesta.ID_Profesor, null, solicitud, false);
			}
			sql.crear(alumno);
			return alumno;
		} finally {
//			sql.commit();
		}

	}

	private static Mensaje crearMensaje(GestorSql sql, TipoMensaje tipoMensaje,
			Alumno alumno, Profesor profesor, Proyecto proyecto,
			Solicitud solicitud, boolean leido) throws SQLException {
		try {
			// sql.openTransaction("cm");
			Mensaje mensaje = new Mensaje();
			int idMensaje = sql.maxValorId(mensaje);
			mensaje.ID = idMensaje;
			mensaje.ID_Alumno = alumno == null ? new Alumno() : alumno;
			mensaje.ID_Profesor = profesor == null ? new Profesor() : profesor;
			mensaje.ID_Proyecto = proyecto == null ? new Proyecto() : proyecto;
			mensaje.ID_Solicitud = solicitud == null ? new Solicitud()
					: solicitud;
			mensaje.leido = Boolean.toString(leido);
			mensaje.mensajeTipo = tipoMensaje.toString();
			mensaje.mensaje = String.format("%s solicitado",
					mensaje.mensajeTipo);
			sql.crear(mensaje);
			return mensaje;
		} finally {
//			sql.commit();
		}
	}

	private static Solicitud crearSolicitud(GestorSql sql, Alumno alumno,
			Propuesta propuesta) throws Exception {
		try {
			// sql.openTransaction("cs");
			if (alumno == null || propuesta == null)
				throw new Exception("Alumno o propuesta nulos");
			Solicitud solicitud = new Solicitud();
			int idSolicitud = sql.maxValorId(solicitud);
			solicitud.ID = idSolicitud;
			solicitud.aceptada_rechazada = Solicitud.EstadoSolicitud.pendiente
					.toString();
			solicitud.anio = Integer.toString(2005 + idSolicitud);
			solicitud.ID_Alumno = alumno;
			solicitud.ID_Propuesta = propuesta;

			URLConnection url = new HttpURLConnection(new URL(
					"http://dl.dropbox.com/u/226030/test.pdf"), null);
			solicitud.pdfConocimientos = url.getInputStream();
			sql.crear(solicitud);
			sql.insertFile(idSolicitud, "Solicitud", "pdfConocimientos",
					solicitud.pdfConocimientos);
			return solicitud;
		} finally {
//			sql.commit();
		}

	}

	public static Profesor crearProfesorTest(GestorSql sql, int idProfesor,
			Departamento departamento) throws Exception {
		try {
			// sql.openTransaction("cpt");
			if (departamento == null)
				throw new Exception("No hay departamento");
			Profesor profesor = new Profesor();

			profesor.ID = idProfesor;
			profesor.apellidos = String.format("apellido %d", idProfesor);
			profesor.dni = String.format("%d%d%d%d%d%d%d%d%c", idProfesor,
					idProfesor, idProfesor, idProfesor, idProfesor, idProfesor,
					idProfesor, idProfesor, ('A' + idProfesor));
			profesor.email = String.format("email_%d@uned.es", idProfesor);
			profesor.ID_Departamento = departamento;
			profesor.nombre = String.format("Pepe %d", idProfesor);
			profesor.nombre_usuario = String.format("p%d", idProfesor);
			profesor.password = profesor.nombre_usuario;
			sql.crear(profesor);
			return profesor;
		} finally {
//			sql.commit();
		}
	}

	public static Propuesta crearPropuestaTest(GestorSql sql, int idPropuesta,
			Profesor p1, boolean isGeneral) throws Exception {
		try {
			// sql.openTransaction("cpt");
			if (p1 == null)
				throw new Exception("No hay profesor");
			Propuesta propuesta = new Propuesta();

			propuesta.ID = idPropuesta;
			propuesta.anio = Integer.toString(2005 + idPropuesta);
			propuesta.conocimientos_previos = String.format(
					"%d conocimientos previos", idPropuesta);
			propuesta.descripcion = String.format(
					"Entre las %d mejores propuestas de proyecto", idPropuesta);
			propuesta.estado_abierta_cerrada = Propuesta.EnumEstadoPropuesta.abierta
					.toString();
			propuesta.ID_Profesor = p1;
			propuesta.objetivos = String.format(
					"Aprender las %d mejores técnicas de PM", idPropuesta);
			propuesta.tipo_general_especifica = isGeneral ? Propuesta.EnumTipoPropuesta.general
					.toString() : Propuesta.EnumTipoPropuesta.especifica
					.toString();
			propuesta.titulo = String.format("%dª propuesta-%s", idPropuesta,
					propuesta.tipo_general_especifica);
			sql.crear(propuesta);
			return propuesta;
		} finally {
//			sql.commit();
		}

	}

	public static Administrador crearAdministrador(GestorSql sql)
			throws SQLException {
		try {
			// sql.openTransaction("cadm");
			Administrador administrador = new Administrador();

			administrador.nombre_usuario = "admin";
			administrador.password = "admin";
			administrador.ID = sql.maxValorId(administrador);
			administrador.nombre = "yo asigno profesor";
			sql.crear(administrador);
			return administrador;
		} finally {
//			sql.commit();
		}
	}
}
