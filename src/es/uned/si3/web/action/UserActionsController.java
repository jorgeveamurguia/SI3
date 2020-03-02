package es.uned.si3.web.action;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.basededatos.manager.Manager;
import es.uned.si3.basededatos.manager.ManagerAdministrador;
import es.uned.si3.basededatos.manager.ManagerAlumos;
import es.uned.si3.basededatos.manager.ManagerMensajes;
import es.uned.si3.basededatos.manager.ManagerProfesor;
import es.uned.si3.persistencia.Administrador;
import es.uned.si3.persistencia.Alumno;
import es.uned.si3.persistencia.CentroAsociado;
import es.uned.si3.persistencia.Departamento;
import es.uned.si3.persistencia.Mensaje;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Propuesta;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.persistencia.UserType;
import es.uned.si3.util.LoggerHelper;

public class UserActionsController extends BaseController {
	private static final String NEW_USER = "newUser";
	private static final String EDIT_USER_DETAILS = "editUserDetails";

	String nombre = "pepe";
	String apellidos = "luis";
	String dni = "03103200V";
	String nombre_usuario = null;
	private String email = "guada@guada.com";
	boolean recibir_notificacion = true;
	private String password = "";
	private String passwordVieja = "";
	private String passwordNueva = "";
	// String password2="123";
	String tipo_usuario;
	String centro_asociado;
	String departamento;
	List<String> listaTiposUsuarios;
	List<String> listaCentrosAsociados;
	List<String> listaDepartamentos;

	public UserActionsController() {
		listaTiposUsuarios = new ArrayList<String>();
		listaTiposUsuarios.add(UserType.Alumno.toString());
		listaTiposUsuarios.add(UserType.Profesor.toString());
	}

	/**
	 * getter & setters<br>
	 * ================
	 */

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = normalize(nombre);
	}

	public List<String> getListaTiposUsuarios() {
		return this.listaTiposUsuarios;
	}

	public List<String> getListaCentrosAsociados() {
		if (this.listaCentrosAsociados != null)
			return this.listaCentrosAsociados;
		GestorSql gestor = null;
		try {

			gestor = GestorSql.getInstance();
			List<CentroAsociado> listaCentrosAsociadosInstances = Manager
					.getInstancesFromSql(gestor, new CentroAsociado());
			listaCentrosAsociados = new ArrayList<String>();
			for (CentroAsociado centro : listaCentrosAsociadosInstances) {
				listaCentrosAsociados.add(centro.CentroAsociado);
			}
		} catch (Exception e) {
			this.addActionError("Error cargando departamentos: "
					+ e.getMessage());
			LoggerHelper.getDefaultLogger().error(e);
			return new ArrayList<String>();
		} finally {
			// if (gestor != null)
			// gestor.close();
		}

		return this.listaCentrosAsociados;
	}

	public String getCentro_asociado() {
		return this.centro_asociado;
	}

	private CentroAsociado getCentro_asociadoRow() {
		CentroAsociado cAsociado = new CentroAsociado();
		cAsociado.CentroAsociado = getCentro_asociado();
		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();
			cAsociado = Manager.getInstanceFromSql(gestor, cAsociado, false);
			return cAsociado;
		} catch (Exception e) {
			this.addActionError("Error cargando centro asociado: "
					+ e.getMessage());
			LoggerHelper.getDefaultLogger().error(e);
			return null;
		}
	}

	public void setCentro_asociado(String centro_asociado) {
		this.centro_asociado = normalize(centro_asociado);
	}

	public List<String> getListaDepartamentos() {
		if (this.listaDepartamentos != null)
			return this.listaDepartamentos;
		this.listaDepartamentos = new ArrayList<String>();
		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();

			Departamento dpto = new Departamento();
			Object[] objs = gestor.find(dpto);
			if (objs == null || objs.length == 0)
				return null;

			for (Object ob : objs) {
				this.listaDepartamentos.add(((Departamento) ob).departamento);
			}
		} catch (Exception e) {
			this.addActionError("Error cargando departamentos: "
					+ e.getMessage());
			LoggerHelper.getDefaultLogger().error(e);
			return new ArrayList<String>();
		} finally {
			// if (gestor != null)
			// gestor.close();
		}

		return this.listaDepartamentos;
	}

	public String getDepartamento() {
		return this.departamento;
	}

	public void setdepartamento(String departamento) {
		this.departamento = normalize(departamento);
	}

	public String getTipo_usuario() {
		return this.tipo_usuario;
	}

	public void setTipo_usuario(String tipo_usuario) {
		this.tipo_usuario = normalize(tipo_usuario);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = normalize(email);
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = normalize(apellidos);
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = normalize(dni);
	}

	public String getNombre_usuario() {
		return nombre_usuario;
	}

	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = normalize(nombre_usuario);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = normalize(password);
	}

	public String getPasswordVieja() {
		return passwordVieja;
	}

	public void setPasswordVieja(String passwordVieja) {
		this.passwordVieja = normalize(passwordVieja);
	}

	public String getPasswordNueva() {
		return passwordNueva;
	}

	public void setPasswordNueva(String passwordNueva) {
		this.passwordNueva = normalize(passwordNueva);
	}

	// public String getPassword2() {
	// return password2;
	// }
	//
	// public void setPassword2(String password2) {
	// this.password2 = password2;
	// }

	public boolean getRecibir_notificacion() {
		return recibir_notificacion;
	}

	public void setRecibir_notificacion(boolean recibir_notificacion) {
		this.recibir_notificacion = recibir_notificacion;
	}

	public String goHome() {
		return START;
	}

	public String createSql() {
		return "PENDING";
	}

	public String newUser() throws Exception {
		if (getTipo_usuario() == null) {
			// Pide el tipo de usuario
			return NEW_USER;
		} else if (getNombre_usuario() == null) {
			nombre = "";
			apellidos = "";
			dni = "";
			email = "";
			// Pide los datos
			return NEW_USER;
		} else {
			// 3 registra al nuevo usuario
			GestorSql gestor = null;
			try {
				gestor = GestorSql.getInstance();

				Object[] obj = null;
				Alumno al = new Alumno();
				al.nombre_usuario = getNombre_usuario();
				obj = gestor.find(al);
				if (obj == null || obj.length == 0) {
					Profesor pl = new Profesor();
					pl.nombre_usuario = getNombre_usuario();
					obj = gestor.find(pl);
				}
				if (obj == null || obj.length == 0) {
					Administrador adl = new Administrador();
					adl.nombre_usuario = getNombre_usuario();
					obj = gestor.find(adl);
				}
				if (obj != null && obj.length > 0) {
					this.addActionError(String.format(
							"Error: Nombre de usuario %s ya registrado",
							getNombre_usuario()));
					return NEW_USER;
				}

				// Usuario es nuevo
				boolean rc = false;
				if (getTipo_usuario().equals(UserType.Alumno.toString())) {
					al = new Alumno();
					al.apellidos = getApellidos();
					al.ID = gestor.maxValorId(al) + 1;
					al.numero_expediente = Integer.toString(al.ID);
					al.ID_CentroAsociado = getCentro_asociadoRow();
					al.dni = getDni();
					al.email = getEmail();
					al.nombre = getNombre();
					al.nombre_usuario = getNombre_usuario();
					al.password = getPassword();
					al.recibir_notificacion = Boolean
							.toString(getRecibir_notificacion());
					al.numero_expediente = "111";
					al.ID_Propuesta = new Propuesta();
					al.ID_Proyecto = new Proyecto();
					rc = gestor.crear(al);
				} else {
					Departamento dpto = new Departamento();
					dpto.departamento = getDepartamento();
					Object[] objs = gestor.find(dpto);
					if (objs == null || objs.length == 0) {
						this.addActionError(String.format(
								"Error: Departamento %s no existe",
								getDepartamento()));
						return NEW_USER;
					}
					Profesor pl = new Profesor();
					pl.ID = gestor.maxValorId(pl) + 1;
					pl.ID_Departamento = (Departamento) objs[0];
					pl.apellidos = getApellidos();
					pl.dni = getDni();
					pl.email = getEmail();
					pl.nombre = getNombre();
					pl.password = getPassword();
					pl.nombre_usuario = getNombre_usuario();
					pl.recibir_notificacion = Boolean
							.toString(getRecibir_notificacion());
					rc = gestor.crear(pl);
				}

				if (!rc) {
					this.addActionError(String.format(
							"Error en la creación del %s %s, %s",
							getTipo_usuario(), getApellidos(), getNombre()));
					return NEW_USER;
				} else {
					this.addActionMessage(String.format(
							"%s %s, %s creado con éxito", getTipo_usuario(),
							getApellidos(), getNombre()));
					return START;
				}
			} catch (Exception e) {
				this.addActionError("Error creando el usuario: "
						+ e.getMessage());
				LoggerHelper.getDefaultLogger().error(e);
				return NEW_USER;
			} finally {
				if (gestor != null)
					gestor.commit();
			}
		}
	}

	public String editUserDetails() {
		LoggerHelper.getDefaultLogger().info("editUserDetails");
		GestorSql gestor = null;
		try {
			if (tipo_usuario == null) {
				return cargarDatos();
			} else {
				gestor = GestorSql.getInstance();

				Object[] obj = null;
				boolean rc = false;

				if (tipo_usuario.equals(UserType.Admin.toString())) {
					Administrador adl = new Administrador();
					adl.nombre_usuario = getLoggedUserName();
					Administrador admon = Manager.getInstanceFromSql(gestor,
							adl, true);
					if (admon == null) {
						return ErrorNonRegisteredUser();
					}
					admon.apellidos = getApellidos();
					admon.dni = getDni();
					admon.email = getEmail();
					admon.nombre = getNombre();

					admon.recibir_notificacion = Boolean
							.toString(getRecibir_notificacion());
					rc = gestor.update(admon);
				} else if (tipo_usuario.equals(UserType.Alumno.toString())) {
					Alumno al = new Alumno();
					al.nombre_usuario = getLoggedUserName();
					al = Manager.getInstanceFromSql(gestor, al, true);
					if (al == null) {
						return ErrorNonRegisteredUser();
					}
					if (getPasswordVieja() != null
							&& getPasswordVieja().length() > 0) {
						if (!al.password.equals(getPasswordVieja())) {
							this.addActionError("Error: Contraseña antigua incorrecta");
							return EDIT_USER_DETAILS;
						} else
							al.password = getPasswordNueva();
					}
					al.apellidos = getApellidos();
					al.numero_expediente = Integer.toString(al.ID);
					al.ID_CentroAsociado = getCentro_asociadoRow();
					al.dni = getDni();
					al.email = getEmail();
					al.nombre = getNombre();
					al.nombre_usuario = getNombre_usuario();
					al.recibir_notificacion = Boolean
							.toString(getRecibir_notificacion());
					rc = gestor.update(al);

				} else if (tipo_usuario.equals(UserType.Profesor.toString())) {
					Profesor pl = new Profesor();
					pl.nombre_usuario = getLoggedUserName();
					pl = Manager.getInstanceFromSql(gestor, pl, true);
					if (pl == null) {
						return ErrorNonRegisteredUser();
					}
					if (getPasswordVieja() != null
							&& getPasswordVieja().length() > 0) {
						if (!pl.password.equals(getPasswordVieja())) {
							this.addActionError("Error: Contraseña antigua incorrecta");
							return EDIT_USER_DETAILS;
						} else
							pl.password = getPasswordNueva();
					}
					pl.apellidos = getApellidos();
					if (!pl.ID_Departamento.departamento
							.equals(getDepartamento())) {
						pl.ID_Departamento = new Departamento();
						pl.ID_Departamento.departamento = getDepartamento();
						pl.ID_Departamento = Manager.getInstanceFromSql(gestor,
								pl.ID_Departamento, true);
						if (pl.ID_Departamento == null) {
							this.addActionError(String
									.format("Error: No hay departamento %s en la base de datos",
											getDepartamento()));
							return START;
						}
					}
					pl.dni = getDni();
					pl.email = getEmail();
					pl.nombre = getNombre();
					pl.recibir_notificacion = Boolean
							.toString(getRecibir_notificacion());
					rc = gestor.update(pl);
				}
				if (!rc) {
					this.addActionError(String.format(
							"Error en la creación del %s %s, %s",
							getTipo_usuario(), getApellidos(), getNombre()));
					return START;
				} else {
					SessionInfo sInfo = getSessionInfo();
					removeSessionInfo();
					saveSessionInfo(sInfo.getTipoUsuario(),
							sInfo.getNombreUsuario(),
							sInfo.getNombreCompleto(), null);
					this.addActionMessage(String.format(
							"%s (%s, %s) modificado con éxito",
							getTipo_usuario(), getApellidos(), getNombre()));

					return START;
				}
			}
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error(e);
			return START;
		} finally {
			if (gestor != null)
				gestor.commit();
		}
	}

	public String login() {
		GestorSql gestor = null;
		try {
			Administrador adl = new Administrador();
			adl.nombre_usuario = getNombre_usuario();
			adl.password = getPassword();
			if (adl.nombre_usuario.toLowerCase().equals(
					Manager.ADMIN_USER_NAME.toLowerCase())
					&& adl.password.toLowerCase().equals(
							Manager.ADMIN_USER_NAME.toLowerCase())) {
				saveSessionUserData(UserType.Admin, adl.nombre_usuario,
						adl.nombre, adl.apellidos);
				try {
					gestor = GestorSql.getInstance();
					Object[] obj = gestor.find(adl);
					if (obj != null && obj.length > 0) {
						adl = (Administrador) obj[0];
						removeSessionInfo();
						saveSessionUserData(UserType.Admin, adl.nombre_usuario,
								adl.nombre, adl.apellidos);
					}
				} catch (Exception e) {
				}
				return START;
			}

			gestor = GestorSql.getInstance();
			Object[] obj = null;
			Alumno al = new Alumno();
			al.nombre_usuario = getNombre_usuario();
			al.password = getPassword();
			obj = gestor.find(al);
			if (obj != null && obj.length > 0) {
				al = (Alumno) obj[0];
				saveSessionUserData(UserType.Alumno, al.nombre_usuario,
						al.nombre, al.apellidos);
				return START;
			}
			Profesor pl = new Profesor();
			pl.nombre_usuario = getNombre_usuario();
			pl.password = getPassword();
			obj = gestor.find(pl);
			if (obj != null && obj.length > 0) {
				pl = (Profesor) obj[0];
				saveSessionUserData(UserType.Profesor, pl.nombre_usuario,
						pl.nombre, pl.apellidos);
				return START;
			}

			this.addActionError("Usuario / contraseña desconocidos");
			return START;
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error(e);
			this.addActionError("Error registrando usuarios");

			return START;
		} finally {
			// if (gestor != null)
			// gestor.close();
		}
	}

	public String logout() {
		SessionInfo sInfo = removeSessionInfo();
		if (sInfo != null) {
			this.addActionMessage(String.format("Usuario %s desconectado",
					sInfo == null ? "" : sInfo.getNombreUsuario()));
		}
		return START;
	}

	private void saveSessionUserData(UserType userType, String nombre_usuario,
			String nombre, String apellidos) {
		saveSessionInfo(userType.toString(), nombre_usuario, nombre, apellidos);
	}

	private String ErrorNonRegisteredUser() {
		this.addActionError(String.format(
				"Error: Nombre de usuario %s no registrado",
				getNombre_usuario()));
		return START;
	}

	private String cargarDatos() {
		LoggerHelper.getDefaultLogger().info("cargarDatos");
		GestorSql gestor = null;
		try {
			gestor = GestorSql.getInstance();

			Alumno al = new Alumno();
			al.nombre_usuario = getLoggedUserName();
			al = Manager.getInstanceFromSql(gestor, al, true);
			if (al != null) {
				setTipo_usuario(UserType.Alumno.toString());
				setApellidos(al.apellidos);
				setNombre_usuario(al.nombre_usuario);
				setCentro_asociado(al.ID_CentroAsociado.CentroAsociado);
				setDni(al.dni);
				setEmail(al.email);
				setNombre(al.nombre);
				setRecibir_notificacion(Boolean
						.parseBoolean(al.recibir_notificacion));
				return EDIT_USER_DETAILS;
			}

			Profesor pl = new Profesor();
			pl.nombre_usuario = getLoggedUserName();
			pl = Manager.getInstanceFromSql(gestor, pl, true);
			if (pl != null) {
				setTipo_usuario(UserType.Profesor.toString());
				setNombre_usuario(pl.nombre_usuario);
				setApellidos(pl.apellidos);
				setdepartamento(pl.ID_Departamento.departamento);
				setDni(pl.dni);
				setEmail(pl.email);
				setNombre(pl.nombre);
				setNombre_usuario(pl.nombre_usuario);
				setRecibir_notificacion(Boolean
						.parseBoolean(pl.recibir_notificacion));
				return EDIT_USER_DETAILS;
			}
			Administrador admon = new Administrador();
			admon.nombre_usuario = getLoggedUserName();
			admon = Manager.getInstanceFromSql(gestor, admon, true);
			if (admon != null) {
				setTipo_usuario(UserType.Admin.toString());
				setNombre_usuario(admon.nombre_usuario);
				setApellidos(admon.apellidos);
				setDni(admon.dni);
				setEmail(admon.email);
				setNombre(admon.nombre);
				setRecibir_notificacion(Boolean
						.parseBoolean(admon.recibir_notificacion));
				return EDIT_USER_DETAILS;
			}
			this.addActionError(String.format(
					"Error: Nombre de usuario %s no registrado",
					getLoggedUserName()));
			return START;
		} catch (Exception e) {
			LoggerHelper.getDefaultLogger().error(e);
			return START;
		} finally {
			// if (gestor != null)
			// gestor.close();
		}
	}

	/**
	 * TODO DELETE this method
	 */
	public String resetDB() {
		GestorSql sql = null;
		try {
			// TODO restaurar y quitar lo siguiente
			String login = "si3_72";
			String password = "si3";
			String url = "jdbc:mysql://localhost/";
			String database = "si3_72";
			Connection conn = null;
			try {
				Class.forName("com.mysql.jdbc.Connection");
				conn = (Connection) DriverManager.getConnection(url, login,
						password);
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(e);
				this.addActionError("Error: " + e.getMessage());
				return ERROR;
			}
			if (conn == null)
				return START;
			try {
				Statement st = conn.createStatement();
				st.executeUpdate("DROP DATABASE " + database);
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(e);
			}
			try {
				Statement st = conn.createStatement();
				st.executeUpdate("CREATE DATABASE " + database);
				// TODO restaurar y quitar lo anterior
			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(e);
			}

			es.uned.si3.basededatos.CrearBaseDatos baseDedatos;
			ManagerProfesor mprofesor;
			try {
				baseDedatos = new es.uned.si3.basededatos.CrearBaseDatos();
				baseDedatos.CrearBase();// crea la base de datos

				sql = GestorSql.getInstance();
				sql.openTransaction();

				Manager.crearAdministrador(sql);

				List<CentroAsociado> listCentrosAsociados = Manager
						.crearCentrosAsociadosTest(sql);
				sql.commit();
				sql.openTransaction();

				// /* TODELETE */for (CentroAsociado centro :
				// listCentrosAsociados) {
				// this.addActionMessage(centro.toString());
				// }
				List<Departamento> listDepartamentos = Manager
						.crearDepartamentosTest(sql);
				// /* TODELETE */for (Departamento dpto : listDepartamentos) {
				// this.addActionMessage(dpto.toString());
				// }

				Profesor p1 = Manager.crearProfesorTest(sql, 1,
						listDepartamentos.get(0));
				// this.addActionMessage("Creado profesor " +
				// p1.nombre_usuario);
				Profesor p2 = Manager.crearProfesorTest(sql, 2,
						listDepartamentos.get(1));
				// this.addActionMessage("Creado profesor " +
				// p2.nombre_usuario);

				Propuesta propuesta1 = Manager.crearPropuestaTest(sql, 1, p1,
						false);
				// this.addActionMessage("Creada propuesta " +
				// propuesta1.titulo);
				Propuesta propuesta2 = Manager.crearPropuestaTest(sql, 2, p2,
						true);
				// this.addActionMessage("Creada propuesta " +
				// propuesta2.titulo);

				Alumno a1 = Manager.crearAlumnoTest(sql, 1,
						listCentrosAsociados.get(0), null, null);
				// this.addActionMessage("Creado alummno " + a1.nombre_usuario);
				Alumno a2 = Manager.crearAlumnoTest(sql, 2,
						listCentrosAsociados.get(1), null, null);
				// this.addActionMessage("Creado alummno " + a2.nombre_usuario);
				Alumno a3 = Manager.crearAlumnoTest(sql, 3,
						listCentrosAsociados.get(2), null, null);
				sql.commit();
				// this.addActionMessage("Creado alummno " + a3.nombre_usuario);
				// Alumno a4 = Manager.crearAlumnoTest(sql, 4,
				// listCentrosAsociados.get(2), propuesta2, null);
				// /* TODELETE */this.addActionMessage(a4.toString());
				// Alumno a5 = Manager.crearAlumnoTest(sql, 5,
				// listCentrosAsociados.get(2), null, null);
				// /* TODELETE */this.addActionMessage(a5.toString());

				// Solicitud sol = new Solicitud();
				// List<Solicitud> sols = Manager.getInstancesFromSql(sql, sol);
				// this.addActionMessage("sols: " + sols);
				// if (sols != null)
				// for (Solicitud solicitud : sols) {
				// this.addActionMessage("Sol: " + solicitud);
				// }
				// Mensaje msg = new Mensaje();
				// List<Mensaje> msgs = Manager.getInstancesFromSql(sql, msg);
				// this.addActionMessage("msgs: " + msgs);
				// if (msgs != null)
				// for (Mensaje mensaje : msgs) {
				// this.addActionMessage("msg: " + mensaje);
				// }

			} catch (Exception e) {
				LoggerHelper.getDefaultLogger().error(
						"EXCEPTION en main: " + e.getMessage());
				this.addActionError("Error: " + e.getMessage());
				try {
					sql.rollback();
				} catch (Exception e2) {
				}
				return ERROR;
			}

			this.addActionMessage("DB restaurada");
			return START;
		} finally {
		}
	}
}
