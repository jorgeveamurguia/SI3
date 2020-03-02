package es.uned.si3.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mysql.jdbc.PacketTooBigException;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.basededatos.manager.ManagerMensajes;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.UserType;
import es.uned.si3.util.LoggerHelper;

public class PFCController extends BaseController {
	private final String VER_PFCS = "verPFC";
	private final String VER_PFC_DETALLES = "verPFCDetalles";

	// Propuesta propuesta = null;
	List<PFCEx> pfcs = null;
	List<ParAtributoValor> detallesProyecto;

	public String anioAcademicoFilter = "*";
	public String estadoPFCFilter = "*";
	// public String selectedTitle = "";
	public SortedSet<String> setAniosAcademicos;
	public List<String> setEstadosPFC = new ArrayList<String>();
	String selectedID;

	public String getSelectedID() {
		return this.selectedID;
	}

	public List<ParAtributoValor> getDetallesProyecto() {
		return this.detallesProyecto;
	}

	public void setSelectedID(String selectedID) {
		this.selectedID = selectedID;
	}

	PFCEx proyectoEx;

	String codigoContentType;

	public String getCodigoContentType() {
		return this.codigoContentType;
	}

	public void setCodigoContentType(String codigoContentType) {
		this.codigoContentType = codigoContentType;
	}

	String ID;

	public String getID() {
		return this.ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	String calificacion;

	public String getCalificacion() {
		return this.calificacion;
	}

	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}

	public File uploadMemoria;
	private String uploadMemoriaContentType; // The content type of the file
	private String uploadMemoriaFileName; // The uploaded file name

	public File getUploadMemoria() {
		return uploadMemoria;
	}

	public void setUploadMemoria(File upload) {
		this.uploadMemoria = upload;
	}

	public String getUploadMemoriaContentType() {
		return uploadMemoriaContentType;
	}

	public void setUploadMemoriaContentType(String uploadMemoriaContentType) {
		this.uploadMemoriaContentType = uploadMemoriaContentType;
	}

	public String getUploadMemoriaFileName() {
		return uploadMemoriaFileName;
	}

	public void setUploadMemoriaFileName(String uploadFileName) {
		this.uploadMemoriaFileName = uploadFileName;
	}

	public File uploadCodigo;
	private String uploadCodigoContentType; // The content type of the file
	private String uploadCodigoFileName; // The uploaded file name

	public File getUploadCodigo() {
		return uploadCodigo;
	}

	public void setUploadCodigo(File upload) {
		this.uploadCodigo = upload;
	}

	public String getUploadCodigoContentType() {
		return uploadCodigoContentType;
	}

	public void setUploadCodigoContentType(String uploadCodigoContentType) {
		this.uploadCodigoContentType = uploadCodigoContentType;
	}

	public String getUploadCodigoFileName() {
		return uploadCodigoFileName;
	}

	public void setUploadCodigoFileName(String uploadFileName) {
		this.uploadCodigoFileName = uploadFileName;
	}

	public PFCController() {
		setEstadosPFC.add(Proyecto.FaseProyecto.desarrollo.toString());
		setEstadosPFC.add(Proyecto.FaseProyecto.cerrado.toString());
		// propuesta = new Propuesta();
		// propuesta.estado_abierta_cerrada =
		// Propuesta.EnumEstadoPropuesta.abierta
		// .toString();
		// setAniosAcademicos = new TreeSet();
	}

	@Override
	public String execute() throws Exception {
		return INPUT;
	};

	public List<PFCEx> getPfcs() {
		return this.pfcs;
	}

	public String verDetalles() {
		if (getID() == null) {
			this.addActionError("Faltan datos para ver el detalle del PFC");
			return START;
		}

		Proyecto proyecto = new Proyecto();
		proyecto.ID = Integer.parseInt(getID());
		GestorSql gestor;
		try {
			gestor = GestorSql.getInstance();
			proyecto = Manager.getInstanceFromSql(gestor, proyecto, true);
			if (proyecto == null) {
				this.addActionError(String.format("No hay PFC con ID=%s",
						getID()));
				return START;
			}

			pfcIsOpen = proyecto.estado_cerrado_desarrollo
					.equals(Proyecto.FaseProyecto.desarrollo.toString());
			nombreFicheroMemoria = proyecto.nombreFicheroMemoria;
			nombreFicheroCodigo = proyecto.nombreFicheroCodigo;
			calificacion = proyecto.calificacion;

			detallesProyecto = new ArrayList<PFCController.ParAtributoValor>();
			detallesProyecto
					.add(new ParAtributoValor("Título", proyecto.titulo));
			detallesProyecto.add(new ParAtributoValor("Profesor",
					proyecto.ID_Profesor.nombre_usuario));
			detallesProyecto.add(new ParAtributoValor("Alumno",
					getNombreAlumnoFromPFC(gestor, proyecto)));
			detallesProyecto.add(new ParAtributoValor("Tipo Proyecto",
					proyecto.tipo));
			detallesProyecto.add(new ParAtributoValor("Conocimientos previos",
					proyecto.conocimientos_previos));
			detallesProyecto.add(new ParAtributoValor("Descripción",
					proyecto.descripcion));
			detallesProyecto.add(new ParAtributoValor("Objetivos",
					proyecto.objetivos));
			detallesProyecto.add(new ParAtributoValor("Año", proyecto.anio));
			detallesProyecto.add(new ParAtributoValor("Estado",
					proyecto.estado_cerrado_desarrollo));
			detallesProyecto.add(new ParAtributoValor("Calificación",
					proyecto.calificacion));
			setCalificacion(proyecto.calificacion);
			return VER_PFC_DETALLES;
		} catch (Exception e) {
			this.addActionError("Error: " + e.getMessage());
			return START;
		}
	}

	public String cerrar() {
		if (getID() == null) {
			this.addActionError("Faltan datos para cerrar el PFC");
			return verDetalles();
		}

		Proyecto proyecto = new Proyecto();
		proyecto.ID = Integer.parseInt(getID());
		GestorSql gestor;
		try {
			gestor = GestorSql.getInstance();
			proyecto = Manager.getInstanceFromSql(gestor, proyecto, true);
			if (proyecto == null) {
				this.addActionMessage(String.format("No hay PFC con ID=%s",
						getID()));
				return verDetalles();
			}

			try {
				// String msg=
				// "Para cerrar el PFC ha de incluir:<br/>- una memoria del mismo<br/>- un archivo comprimido con el código del mismo<br/>- la calificación final<br/>";
				// if (getUploadMemoriaFileName() == null) {
				// msg+="Empiece subiendo la memoria del PFC...";
				// return verDetalles();
				// }
				boolean changed = false;
				if (!validString(proyecto.nombreFicheroMemoria)
						&& getUploadMemoriaFileName() != null) {
					if (!getUploadMemoriaFileName().toLowerCase().endsWith(
							".pdf")) {
						this.addActionError("La memoria del proyecto ha de ser un fichero pdf!");
						return verDetalles();
					}
					// Subiendo la memoria
					proyecto.nombreFicheroMemoria = getUploadMemoriaFileName();
					proyecto.memoria_pdf = new FileInputStream(
							getUploadMemoria());
					changed = true;
					gestor.openTransaction();
					gestor.insertFile(Integer.parseInt(getID()), "Proyecto",
							"memoria_pdf", proyecto.memoria_pdf);
					this.addActionMessage("Continue subiendo el archivo comprimido con el código del proyecto...");
				}
				if (!validString(proyecto.nombreFicheroCodigo)
						&& getUploadCodigoFileName() != null) {
					String codigoFileName = getUploadCodigoFileName()
							.toLowerCase();
					if (!codigoFileName.endsWith(".zip")
							&& !codigoFileName.endsWith(".rar")) {
						this.addActionError("El contenido del proyecto ha de incluirse en un archivo zip o un rar!");
						return verDetalles();
					}
					// Subiendo la memoria
					proyecto.nombreFicheroCodigo = getUploadCodigoFileName();
					proyecto.fichero_comprimido = new FileInputStream(
							getUploadCodigo());
					gestor.openTransaction();
					changed = true;
					gestor.insertFile(Integer.parseInt(getID()), "Proyecto",
							"fichero_comprimido", proyecto.fichero_comprimido);
					this.addActionMessage("Finalice asignando una calificación al PFC (0-10)");
				}
				if (!validString(proyecto.calificacion)
						&& getCalificacion() != null) {
					// Cierro el proyecto fin de carrera
					try {
						int nota = Integer.parseInt(getCalificacion());
						if (nota < 0 || nota > 10) {
							this.addActionError("La calificación ha de estar entre 0 (muy mal) y 10 (muy bien)!");
							return verDetalles();
						}
					} catch (Exception e) {
						this.addActionError("La calificación ha de estar entre 0 (muy mal) y 10 (muy bien)!");
						return verDetalles();
					}
					gestor.openTransaction();
					changed = true;
					// Guardando la calificación
					proyecto.calificacion = getCalificacion();
					proyecto.estado_cerrado_desarrollo = Proyecto.FaseProyecto.cerrado
							.toString();
					// Enviar mensaje a alumno
					ManagerMensajes managerMensajes = new ManagerMensajes(
							gestor);
					managerMensajes.formProfesorToAlumnoCalificacionPFC(
							proyecto.ID_Profesor, proyecto,
							getAlumnoFromPFC(gestor, proyecto));
					this.addActionMessage("PFC cerrado con éxito. Se ha enviado un mensaje al alumno!");
				}
				if (changed) {
					gestor.update(proyecto);
					gestor.commit();
				}
				return verDetalles();
			} catch (PacketTooBigException eb) {
				LoggerHelper.getDefaultLogger().error(
						"Error cerrando proyecto: " + eb.getMessage());
				this.addActionError("Error cerrando proyecto: El archivo que intentas subir es muy grande. Reducelo un poquito!");
				gestor.rollback();
				return verDetalles();
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(
						"Error cerrando proyecto: ");
				this.addActionError("Error cerrando proyecto: "
						+ e.getMessage());
				gestor.rollback();
				return verDetalles();
			}
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error("Error cerrando proyecto2: ",
					e);
			this.addActionError("Error cerrando proyecto2: " + e.getMessage());
			return verDetalles();
		}
	}

	public boolean getUserIsProfesor() {
		SessionInfo sInfo = getSessionInfo();
		boolean rc = sInfo.getTipoUsuario()
				.equals(UserType.Profesor.toString());
		return rc;
	}

	boolean pfcIsOpen = false;

	public boolean getPfcIsOpen() {
		return this.pfcIsOpen;
	}

	String nombreFicheroMemoria = null;

	public String getNombreFicheroMemoria() {
		return this.nombreFicheroMemoria;
	}

	String nombreFicheroCodigo = null;

	public String getNombreFicheroCodigo() {
		return this.nombreFicheroCodigo;
	}

	public String ver() {
		try {
			return cargarPFCs();
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error("Error cogiendo pfcs", e);
			this.addActionError("Error cargando las pfcs: " + e.getMessage());
			return START;
		} finally {
			// if (gestor != null)
			// gestor.close();
		}
	}

	//
	private String cargarPFCs() {
		GestorSql gestor;
		try {
			gestor = GestorSql.getInstance();

			Proyecto proyecto = new Proyecto();
			if (getUserIsProfesor()) {
				// Filtra por profesorID
				this.addActionMessage(String.format("PFC del profesor %s",
						getLoggedNombreCompleto()));

				proyecto.ID_Profesor = new Profesor();
				proyecto.ID_Profesor.nombre_usuario = getLoggedUserName();
				proyecto.ID_Profesor = Manager.getInstanceFromSql(gestor,
						proyecto.ID_Profesor, false);
			} else if (getLoggedUserType().equals(UserType.Admin.toString())) {
			} else if (getLoggedUserType().equals(UserType.Alumno.toString())) {
			}

			boolean useAnioFilter = getAnioAcademicoFilter() != null
					&& !getAnioAcademicoFilter().equals("*");
			boolean useEstadoFilter = getEstadoPFCFilter() != null
					&& !getEstadoPFCFilter().equals("*");

			if (useAnioFilter) {
				proyecto.anio = getAnioAcademicoFilter();
				// Filtra por profesorID
				this.addActionMessage(String.format("PFC del año %s",
						proyecto.anio));
			}
			if (useEstadoFilter) {
				proyecto.estado_cerrado_desarrollo = getEstadoPFCFilter();
				// Filtra por profesorID
				this.addActionMessage(String.format("PFC %s",
						getEstadoPFCFilter()));
			}

			List<Proyecto> proyectos = Manager.getInstancesFromSql(gestor,
					proyecto);
			setAniosAcademicos = new TreeSet<String>();
			pfcs = new ArrayList<PFCEx>();
			if (proyectos != null) {
				for (Proyecto myProyecto : proyectos) {
					pfcs.add(new PFCEx(myProyecto, !getUserIsProfesor()));
					setAniosAcademicos.add(myProyecto.anio);
				}
			} else {
				this.addActionMessage("No hay proyectos disponibles con este criterio");
			}

			if (!useAnioFilter && !useEstadoFilter)
				return VER_PFCS;

			// Cojo los años
			proyectos = Manager.getInstancesFromSql(gestor, new Proyecto());
			setAniosAcademicos.clear();
			if (proyectos != null) {
				for (Proyecto myProyecto : proyectos) {
					setAniosAcademicos.add(myProyecto.anio);
				}
			}
			return VER_PFCS;
		} catch (Exception e) {
			this.addActionError("Error: " + e.getMessage());
			return START;
		}

	}

	//
	// public File getUpload() {
	// return upload;
	// }
	//
	// public void setUpload(File upload) {
	// this.upload = upload;
	// }
	//
	// public String getUploadContentType() {
	// return uploadContentType;
	// }
	//
	// public void setUploadContentType(String uploadContentType) {
	// this.uploadContentType = uploadContentType;
	// }
	//
	// public String getUploadFileName() {
	// return uploadFileName;
	// }
	//
	// public void setUploadFileName(String uploadFileName) {
	// this.uploadFileName = uploadFileName;
	// }
	//
	public void setAnioAcademicoFilter(String anioAcademicoFilter) {
		this.anioAcademicoFilter = anioAcademicoFilter;
	}

	public String getAnioAcademicoFilter() {
		return this.anioAcademicoFilter;
	}

	public List<String> setEstadosPFC() {
		return this.setEstadosPFC;
	}

	public String getEstadoPFCFilter() {
		return this.estadoPFCFilter;
	}

	public void setEstadoPFCFilter(String estadoPFCFilter) {
		this.estadoPFCFilter = estadoPFCFilter;
	}

	public String getNombreAlumnoFromPFC(GestorSql gestor, Proyecto pfc) {
		Alumno alumno = getAlumnoFromPFC(gestor, pfc);
		if (alumno != null)
			return alumno.nombre_usuario;
		else
			return "desconocido";
	}

	public Alumno getAlumnoFromPFC(GestorSql gestor, Proyecto pfc) {
		Alumno alumno = new Alumno();
		alumno.ID_Proyecto = new Proyecto();
		alumno.ID_Proyecto.ID = pfc.ID;
		alumno = Manager.getInstanceFromSql(gestor, alumno, true);
		if (alumno != null)
			return alumno;
		else
			return null;
	}

	private class PFCEx extends Proyecto {
		private String nombreAlumno;
		private boolean isSelectionDisabled;

		public boolean getIsSelectionDisabled() {
			return this.isSelectionDisabled;
		}

		public String getNombreAlumno() {
			return this.nombreAlumno;
		}

		public void setNombreAlumno(String nombreAlumno) {
			this.nombreAlumno = nombreAlumno;
		}

		public PFCEx(Proyecto other, boolean isSelectionDisabled)
				throws Exception {
			this.isSelectionDisabled = isSelectionDisabled;
			GestorSql gestor = null;
			try {
				gestor = GestorSql.getInstance();
				this.anio = other.anio;
				this.calificacion = other.calificacion;
				this.conocimientos_previos = other.conocimientos_previos;
				this.nombreFicheroCodigo = other.nombreFicheroCodigo;
				this.descripcion = other.descripcion;
				this.estado_cerrado_desarrollo = other.estado_cerrado_desarrollo;
				this.fichero_comprimido = other.fichero_comprimido;
				this.ID = other.ID;
				this.ID_Profesor = other.ID_Profesor;
				this.memoria_pdf = other.memoria_pdf;
				this.objetivos = other.objetivos;
				this.tipo = other.tipo;
				this.titulo = other.titulo;
				this.nombreAlumno = getNombreAlumnoFromPFC(gestor, other);
			} finally {
				// if (gestor != null)
				// gestor.close();
			}
		}
		//
		// public String getID_Profesor() {
		// return String.format("%s, %s", this.ID_Profesor.apellidos,
		// this.ID_Profesor.nombre);
		// }
		// }
	}

	private class ParAtributoValor {
		private String atributo;
		private String valor;

		public ParAtributoValor(String atributo, String valor) {
			this.atributo = atributo;
			this.valor = valor;
		}

		public String getAtributo() {
			return this.atributo;
		}

		public String getValor() {
			return this.valor;
		}
	}
}