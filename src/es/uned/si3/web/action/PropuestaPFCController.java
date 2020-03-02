package es.uned.si3.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import sun.rmi.log.LogHandler;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.basededatos.manager.ManagerAlumos;
import es.uned.si3.basededatos.manager.ManagerAlumosEx;
import es.uned.si3.persistencia.Administrador;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Propuesta;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.persistencia.UserType;
import es.uned.si3.util.LoggerHelper;
import es.uned.si3.web.interceptors.AuthenticationInterceptor;

public class PropuestaPFCController extends BaseController implements ValidationAware {
	private final String VISTA_CREAR= "crear";
	private final String VISTA_PROPUESTAS = "propuestasPFC";
	private final String VISTA_SOLICITAR = "solicitar";
	private final String VISTA_LISTAPROPUESTAS = "listaPropuestas";

	Propuesta propuesta = null;
	PropuestaEx[] propuestas = null;
	public String anioAcademicoFilter = "*";
	public String selectedID = "";
	public SortedSet<String> setAniosAcademicos;
	public File upload;
	private String uploadContentType; // The content type of the file
	private String uploadFileName; // The uploaded file name

	private List<String> listaTiposPropuestas= new ArrayList<String>();
	public PropuestaPFCController() {
		propuesta = new Propuesta();
		propuesta.estado_abierta_cerrada = Propuesta.EnumEstadoPropuesta.abierta
				.toString();
		setAniosAcademicos = new TreeSet();
		listaTiposPropuestas.add(Propuesta.EnumTipoPropuesta.especifica.toString());
		listaTiposPropuestas.add(Propuesta.EnumTipoPropuesta.general.toString());
	}
	
	public List<String> getListaTiposPropuestas() {
		return listaTiposPropuestas;
	}

	@Override
	public String execute() throws Exception {
		return INPUT;
	};

	public String crear() {
		if (getTitulo() == null || getTitulo().length() == 0)
			return VISTA_CREAR;

		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			Propuesta propuesta2 = new Propuesta();
			propuesta2.titulo = getTitulo();
			Object[] objs = gestor.find(propuesta2);
			if (objs != null && objs.length > 0) {
				this.addActionError(String
						.format("Error, ya existe una propuesta con el mismo título (%s)",
								getTitulo()));
				return VISTA_CREAR;
			}
			setAnio(Manager.getCurrentYearStr());
			// setConocimientos_previos( conocimientos_previos);
			// p.descripcion = descripcion;
			// p.estado_abierta_cerrada =
			// Boolean.toString(estado_abierta_cerrada);
			setID(gestor.maxValorId(propuesta) + 1);
			{
				Profesor pr = new Profesor();
				pr.nombre_usuario = (getLoggedUserName());
				objs = gestor.find(pr);
				if (objs == null || objs.length == 0) {
					this.addActionError("Se ha perdido al profesor");
					return START;
				}

				setID_Profesor((Profesor) objs[0]);
			}
			// p.objetivos = objetivos;
			// p.tipo_general_especifica = tipo_general_especifica;
			// p.titulo = titulo;
			boolean rc = gestor.crear(propuesta);
			if (!rc) {
				this.addActionError(String.format(
						"Error en la creación de la propuestaPFC %s",
						getTitulo()));
				return VISTA_CREAR;
			} else {
				this.addActionMessage(String.format("Éxito creando propuestaPFC de %s",
						getLoggedNombreCompleto()));
				return START;
			}

		} catch (Exception e) {
			LoggerHelper.getDefaultLogger()
					.error("Error creando propuestas", e);
			return START;
		} finally {
			if (gestor != null)
				gestor.commit();
		}
	}

	public String solicitar() {
		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			// Busca que el alumno no tenga ya un proyecto asignado
			if (getLoggedUserType().equals(UserType.Alumno.toString())) {
				Alumno alumno = new Alumno();
				alumno.nombre_usuario = getLoggedUserName();
				alumno = Manager.getInstanceFromSql(gestor, alumno, true);
				if (alumno == null) {
					this.addActionError(String.format(
							"no se encuentra al alumno registrado %s en al BD",
							alumno.nombre_usuario));
					return START;
				}
				Proyecto pr = alumno.ID_Proyecto;
				pr = Manager.getInstanceFromSql(gestor, pr, true);
				if (pr != null) {
					this.addActionError(String.format(
							"El alumno %s ya tiene un proyecto asignado (%s)",
							alumno.nombre_usuario,
							pr.titulo == null ? "Sin título" : pr.titulo));
					return START;
				}
				Propuesta propuesta = alumno.ID_Propuesta;
				propuesta = Manager.getInstanceFromSql(gestor, propuesta, true);
				if (propuesta != null) {
					this.addActionError(String
							.format("El alumno %s ya tiene una propuesta realizada (%s)",
									alumno.nombre_usuario,
									propuesta.titulo == null ? "Sin título"
											: propuesta.titulo));
					return START;
				}
			}

			if ((getUpload() == null || !getUpload().exists())
					|| (validString(getUploadFileName()) && !getUploadFileName()
							.toLowerCase().endsWith(".pdf"))
					|| (getSelectedID() == null || getSelectedID().length() == 0)) {
				if (getUpload() == null || !getUpload().exists())
					this.addActionError("Falta fichero de conocimientos");
				else if (validString(getUploadFileName())
						&& !getUploadFileName().toLowerCase().endsWith(".pdf"))
					this.addActionError("El fichero de conocimientos, no es un pdf");
				else if (getSelectedID() == null
						|| getSelectedID().length() == 0)
					this.addActionError("Seleccione una propuesta");

				Propuesta[] propuestasAbiertas = (new ManagerAlumos(gestor))
						.VerPropuestasAbiertas();
				if (propuestasAbiertas == null
						|| propuestasAbiertas.length == 0) {
					this.addActionError("No hay propuestas abiertas disponibles");
					return VISTA_SOLICITAR;
				}
				propuestas = new PropuestaEx[propuestasAbiertas.length];
				for (int n = 0; n < propuestasAbiertas.length; n++) {
					Propuesta propuesta = Manager.getInstanceFromSql(gestor,
							propuestasAbiertas[n], true);
					if (propuesta == null) {
						LoggerHelper
								.getDefaultLogger()
								.error(String
										.format("Propuesta %s no se encuentra en la BD",
												propuestasAbiertas[n].ID));
						propuestas[n] = new PropuestaEx(propuestasAbiertas[n]);
					} else
						propuestas[n] = new PropuestaEx(propuesta);
				}
				return VISTA_SOLICITAR;
			} else {
				// Crea la solicitud
				boolean result = ManagerAlumosEx.solicitarPropuesta(gestor,
						getLoggedUserName(), getSelectedID(), getUpload(),
						getUploadFileName());
				if (result == false) {
					this.addActionError(ManagerAlumosEx.getErrorMessage());
					return START;
				} else {
					this.addActionMessage(String
							.format("Éxito en la solicitud del proyeto %s. Espere la comunicación del administrador",
									getSelectedID()));
					return START;
				}
			}
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error("Error creando propuesta", e);
			this.addActionError("Error creando propuesta: " + e.getMessage());
			return START;
		} finally {
			if (gestor != null)
				gestor.commit();
		}
	}

	public String ver() {
		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			return getPropuestasPFC(gestor);
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error("Error cogiendo propuestas",
					e);
			this.addActionError("Error cargando las propuestasPFC: " + e.getMessage());
			return START;
		} finally {
			// if (gestor != null)
			// gestor.close();
		}
	}

	private String getPropuestasPFC(GestorSql gestor) {
		propuesta = new Propuesta();
		if (getLoggedUserType().equals(UserType.Profesor.toString())) {
			// Filtra por profesorID
			this.addActionMessage(String.format(
					"Propuestas de PFC del profesor %s",
					getLoggedNombreCompleto()));

			propuesta.ID_Profesor = new Profesor();
			propuesta.ID_Profesor.nombre_usuario = getLoggedUserName();
			propuesta.ID_Profesor = Manager.getInstanceFromSql(gestor,
					propuesta.ID_Profesor, false);
			// propuesta.ID_Profesor = getProfesorFromSql(gestor,
			// propuesta.ID_Profesor);
		} else if (getLoggedUserType().equals(UserType.Admin.toString())) {
		} else if (getLoggedUserType().equals(UserType.Alumno.toString())) {
			propuesta.estado_abierta_cerrada = "abierta";
		}

		if (getAnioAcademicoFilter() != null
				&& !getAnioAcademicoFilter().equals("*")) {
			propuesta.anio = getAnioAcademicoFilter();
			// Filtra por profesorID
			this.addActionMessage(String.format("Propuestas de PFC del año %s",
					propuesta.anio));
		}

		Object[] objs = gestor.find(propuesta);
		if( objs == null || objs.length == 0) {
			this.addActionMessage("No hay propuestas disponibles con estos criterios");
			return VISTA_LISTAPROPUESTAS;
		}
		propuestas = new PropuestaEx[objs.length];
		int n = 0;
		for (Object o : objs) {
			Propuesta pr = (Propuesta) o;
			propuestas[n++] = new PropuestaEx(pr);
			setAniosAcademicos.add(pr.anio);
		}

		if (getAnioAcademicoFilter() != null
				&& !getAnioAcademicoFilter().equals("*")) {
			setAniosAcademicos.clear();
			propuesta.anio = null;
			objs = gestor.find(propuesta);
			for (Object o : objs) {
				Propuesta pr = (Propuesta) o;
				setAniosAcademicos.add(pr.anio);
			}
		}

		return VISTA_LISTAPROPUESTAS;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public void setAnioAcademicoFilter(String anioAcademicoFilter) {
		this.anioAcademicoFilter = anioAcademicoFilter;
	}

	public String getAnioAcademicoFilter() {
		return this.anioAcademicoFilter;
	}

	public void setSelectedID(String selectedID) {
		this.selectedID = selectedID;
	}

	public String getSelectedID() {
		return this.selectedID;
	}

	public Set<String> getSetAniosAcademicos() {
		return this.setAniosAcademicos;
	}

	public void setID_Profesor(Profesor profesor) {
		this.propuesta.ID_Profesor = profesor;
	}

	public Profesor getID_Profesor() {
		return this.propuesta.ID_Profesor;
	}

	public void setID(int id) {
		this.propuesta.ID = id;
	}

	public Integer getID() {
		return this.propuesta.ID;
	}

	public void setAnio(String anio) {
		this.propuesta.anio = anio;
	}

	public String getAnio() {
		return this.propuesta.anio;
	}

	public String getTitulo() {
		return this.propuesta.titulo;
	}

	public void setTitulo(String titulo) {
		this.propuesta.titulo = titulo;
	}

	public String getConocimientos_previos() {
		return this.propuesta.conocimientos_previos;
	}

	public void setConocimientos_previos(String conocimientos_previos) {
		this.propuesta.conocimientos_previos = conocimientos_previos;
	}

	public String getDescripcion() {
		return this.propuesta.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.propuesta.descripcion = descripcion;
	}

	public String getObjetivos() {
		return this.propuesta.objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.propuesta.objetivos = objetivos;
	}

	public String getTipo_general_especifica() {
		return this.propuesta.tipo_general_especifica;
	}

	public void setTipo_general_especifica(String tipo_general_especifica) {
		this.propuesta.tipo_general_especifica = tipo_general_especifica;
	}

	public boolean getEstado_abierta_cerrada() {
		return propuesta.estado_abierta_cerrada
				.equals(Propuesta.EnumEstadoPropuesta.abierta.toString());
	}

	public void setEstado_abierta_cerrada(boolean esAbierta) {
		propuesta.estado_abierta_cerrada = (esAbierta) ? Propuesta.EnumEstadoPropuesta.abierta
				.toString() : Propuesta.EnumEstadoPropuesta.cerrada.toString();
	}

	public Propuesta[] getPropuestas() {
		return this.propuestas;
	}

	private class PropuestaEx extends Propuesta {
		public PropuestaEx(Propuesta other) {
			GestorSql gestor = null;
			try {
				gestor = GestorSql.getInstance();
				this.anio = other.anio;
				this.conocimientos_previos = other.conocimientos_previos;
				this.descripcion = other.descripcion;
				this.estado_abierta_cerrada = other.estado_abierta_cerrada;
				this.ID = other.ID;
				this.ID_Profesor = Manager.getInstanceFromSql(gestor,
						other.ID_Profesor, false);
				this.objetivos = other.objetivos;
				this.tipo_general_especifica = other.tipo_general_especifica;
				this.titulo = other.titulo;
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(
						"EXCEPTION en main: " + e.getMessage());
				addActionError("Error: " + e.getMessage());
			} finally {
				// if (gestor != null)
				// gestor.close();
			}
		}

		public String getID_Profesor() {
			return String.format("%s, %s", this.ID_Profesor.apellidos,
					this.ID_Profesor.nombre);
		}
	}
}
