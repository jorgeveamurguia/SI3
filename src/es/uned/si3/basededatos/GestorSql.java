package es.uned.si3.basededatos;

import com.mysql.jdbc.Connection;

import es.uned.si3.util.LoggerHelper;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * GestorSql esta clase singleton, instancia una única clase que conecta a la
 * base de datos y es capaz de insertar, actualizar objetos java en SLQ.
 * 
 * Los objetos que son serializados en la base de datos deben tener el campo ID
 * como entero y el de clave foranea ID_objetojava
 * 
 * */

public class GestorSql {

	static String bd = "si3_72";
	static String backUp = "jorge-backup";

	static String login = "si3_72";
	static String password = "si3";
	static String url = "jdbc:mysql://localhost/";

	final static String paquete = "es.uned.si3.persistencia.";

	Connection conn = null;
	java.sql.Savepoint savepoint;
	private static GestorSql INSTANCE_REAL = null;

	private synchronized static void createInstance() throws Exception {
		if (INSTANCE_REAL == null) {
			INSTANCE_REAL = new GestorSql(bd, login, password);
			if (INSTANCE_REAL.conn == null)
				INSTANCE_REAL = null;
		}
	}

	public static GestorSql getInstance(String basededatos, String login,
			String password) throws Exception {
		if (INSTANCE_REAL == null)
			createInstance(basededatos, login, password);
		return INSTANCE_REAL;
	}

	private synchronized static void createInstance(String basededatos,
			String login, String password) throws Exception {
		if (INSTANCE_REAL == null) {
			INSTANCE_REAL = new GestorSql(basededatos, login, password);
			if (INSTANCE_REAL.conn == null)
				INSTANCE_REAL = null;
		}
	}

	public static GestorSql getInstance() throws Exception {
		if (INSTANCE_REAL == null)
			createInstance();
		return INSTANCE_REAL;
	}

	// constructor privado para no generar mas de una instancias el singleton
	private GestorSql(String database, String login, String password) throws SQLException {

		try {
			conn = (Connection) DriverManager.getConnection(url + database,
					login, password);
			//conn.setAutoCommit(false);
			if (conn != null) {
				System.out.println("Conexión a base de datos " + url
						+ database + " ... Ok");
			}
			
		} catch (SQLException ex) {
			System.out
					.println("Hubo un problema al intentar conectarse con la base de datos "
							+ url);
			throw ex;
		}
	}
	public void openTransaction()throws SQLException{
		try {
			conn.setAutoCommit(false);
			savepoint =conn.setSavepoint();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	public Savepoint openTransaction(String trasaccion)throws SQLException{
		try {
			conn.setAutoCommit(false);
			return conn.setSavepoint(trasaccion);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	/*public void setAutoCommit(boolean autoCommit){
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	public void commit(){
		try {
			conn.commit();
			//conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	public void rollback(Savepoint savepoint)throws SQLException{
		try {
			conn.rollback(savepoint);
			//conn.setAutoCommit(true);
			//conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	public void rollback()throws SQLException{
		try {
			conn.rollback(savepoint);
			//conn.setAutoCommit(true);
			//conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	public boolean isConnect() {
		return conn != null;
	}

	/**
	 * cierra la base de datos
	 * */
	public void close() throws SQLException{
		try {
			conn.close();
			INSTANCE_REAL = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

	}

	public boolean executeBack(String cadenaSql) throws  SQLException{
		Statement s;
		try {
			s = (Statement) conn.createStatement();

			s.execute(cadenaSql);

		} catch (SQLException ex) {
			System.out.println("Hubo un problema al intentar ejecutar ["
					+ cadenaSql + "]" + ex);
			throw ex;
			//return false;
		}
		return true;
	}

	public boolean execute(String cadenaSql)throws  SQLException {
		Statement s;
		try {
			s = (Statement) conn.createStatement();
			System.out.println("SQL: " + cadenaSql);

			s.executeUpdate(cadenaSql);

		} catch (SQLException ex) {
			System.out.println("Hubo un problema al intentar ejecutar ["
					+ cadenaSql + "]" + ex);
			throw ex;
		}
		return true;
	}

	/**
	 * ejecuta un insert en la base de datos del objeto
	 * @throws Exception 
	 * */

	public void crearSchema(String nombre) throws Exception {
		new CrearBaseDatos().CrearBase();
		execute("Create Database " + nombre);
	}
	// INSERT INTO personas (nombre,telefono,URL) VALUES
	// ('Fede','4255','http://blogjapon.com.ar')"
	public boolean crear(Object clazz) throws  SQLException{

		return execute(crear(clazz, true));

	}

	public String crear(Object clazz, boolean backup){

		String tabla = clazz.getClass().getSimpleName();
		String cadenaSql = "INSERT INTO " + tabla + "  ";

		String cadenaCampos = new String();
		String cadenaValores = new String();
		for (Field campo : clazz.getClass().getFields()) {
			try {
				if (campo.getName().startsWith("ID_")) {
					// Class.forName(nombreCampo.substring(3));
					Field campoForaneo;
					campoForaneo = campo.get(clazz).getClass().getField("ID");
					if (campoForaneo.get(campo.get(clazz)) == null) { 
						cadenaCampos = cadenaCampos + campo.getName() + ",";
						cadenaValores = cadenaValores + "null" + ",";  
					} else {
						int id = (Integer) campoForaneo.get(campo.get(clazz));
						cadenaCampos = cadenaCampos + campo.getName() + ",";
						cadenaValores = cadenaValores + id + ",";
					}
				} else {
					if(campo.get(clazz)!=null ){
						if (!campo.getType().getName().contains("InputStream")) {
							cadenaCampos = cadenaCampos + campo.getName() + ",";
						} else {
							// insterta fichero despues de insertar la fila
						}
						if (!campo.getType().getName().contains("InputStream")) {
	
							if (campo.getName().compareTo("ID") != 0
									&& !campo.getType().getName().contains("Float"))// recojo
																					// el
																					// ID
								cadenaValores = cadenaValores
										+ ("'" + campo.get(clazz) + "',");
							else
								cadenaValores = cadenaValores + campo.get(clazz)
										+ ",";// el ID es numerico
						}
				}
			 }

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		cadenaSql = cadenaSql + "("
				+ cadenaCampos.substring(0, cadenaCampos.length() - 1)
				+ ") VALUES " + "("
				+ cadenaValores.substring(0, cadenaValores.length() - 1) + ");";

		return cadenaSql;
	}

	/**
	 * recogo los campos con datos para actualizar, si los campos no estan
	 * asociados a datos es decir un campo esta a null no lo actualizamos, (no
	 * se puede actualizar un campo a null) utilizamos el campo de ID para la
	 * ejecucion de la consula.
	 * 
	 * */

	public boolean update(Object clazz) throws SQLException{

		return execute(update(clazz, true));

	}

	public String update(Object clazz, boolean backup) {
		Statement s;
		String cadenaSql = "UPDATE " + clazz.getClass().getSimpleName()
				+ " SET ";
		String cadenaWhere = "";
		for (Field campo : clazz.getClass().getFields()) {
			try {
				if (campo.getName().compareTo("ID") != 0) {// recojo el ID
					if (campo.get(clazz) != null || campo.getType().getName().contains("InputStream"))// si es null no lo actualices
					{
						if (campo.getName().startsWith("ID_")) {
							// Class.forName(nombreCampo.substring(3));
							Field campoForaneo;
							campoForaneo = campo.get(clazz).getClass()
									.getField("ID");
							int id = (Integer) campoForaneo.get(campo
									.get(clazz));
							cadenaSql = cadenaSql + " " + campo.getName()
									+ " = " + id + ",";
						} else {
							if (!campo.getType().getName()
									.contains("InputStream"))
								cadenaSql = cadenaSql + " " + campo.getName()
										+ " = '" + campo.get(clazz) + "' ,";
						}
					}
				} else {
					cadenaWhere = " Where " + campo.getName() + " = "
							+ campo.get(clazz);
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cadenaSql = cadenaSql.substring(0, cadenaSql.length() - 2)
				+ cadenaWhere;

		// System.out.print("Actualizar::"+cadenaSql);
		return cadenaSql;
	}

	/**
	 * dev el max valor del campo id de un tabla determinada
	 * 
	 * **/
	public int maxValorId(Object clazz) throws SQLException{
		int id = 1;
		String cadenaSql = "select max(id) from "
				+ clazz.getClass().getSimpleName();

		Statement s;
		try {
			s = (Statement) conn.createStatement();
			ResultSet rs = s.executeQuery(cadenaSql);
			while (rs.next()) {
				id = rs.getInt(1) + 1;
				System.out.println(id);
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return id;
	}

	/**
	 * por cada objeto pasado al método se busca el campo ID para realizar el
	 * borrado o por el campo ID_ foraneo Ningún otro atributo será utilizado
	 * por el método
	 * */
	public boolean borrar(Object clazz) throws  SQLException{

		return execute(borrar(clazz, true));

	}

	public String borrar(Object clazz, boolean backup) {
		Statement s;
		String cadenaSql = "Delete from " + clazz.getClass().getSimpleName();
		String cadenaWhere = "";
		for (Field campo : clazz.getClass().getFields()) {
			try {
				// solo seleccion ID_ o por ID
				if (campo.get(clazz) != null) {// el !null marca el campo por el
												// que se borrar
					if (campo.getName().startsWith("ID_")) {
						Field campoForaneo;
						campoForaneo = campo.get(clazz).getClass()
								.getField("ID");
						int id = (Integer) campoForaneo.get(campo.get(clazz));

						cadenaWhere = " Where  " + campo.getName() + " = " + id
								+ " ";
						break;
					} else if (campo.getName().compareTo("ID") == 0) {// recojo
																		// el ID
						cadenaWhere = " Where " + campo.getName() + " = "
								+ campo.get(clazz);
						break;
					}
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		cadenaSql = cadenaSql + cadenaWhere;

		return cadenaSql;

	}

	// cadenaSql="select * from personas";
	/**
	 * a través de un objeto entidad genero una consulta de búsqueda contra la
	 * base de datos devolviendo un array de objetos del mismo objeto rellenos
	 * de los datos pasados a la búsqueda Cada uno de los campos introducidos
	 * genera una consulta con and, si se quiere realizar consultaws con OR
	 * solamente hay que realizar varias consultas con cada uno de los campos y
	 * agregar los resultados al array
	 * */
	public Object[] find(Object clazz) {

		return find(clazz, false);
	}

	public Object[] find(Object clazz, boolean filter) {

		Statement s;
		String cadenaSql = "select * from " + clazz.getClass().getSimpleName();
		String cadenaWhere = "";
		for (Field campo : clazz.getClass().getFields()) {
			try {
				String nombreCampo = campo.getName();
				Object value=campo.get(clazz);
				if (value!= null && !value.toString().toLowerCase().equals("null")) {
					if (nombreCampo.startsWith("ID_")) {
						Field campoForaneo;

						campoForaneo = value.getClass()
								.getField("ID");
						int id = (Integer) campoForaneo.get(value);

						if (filter)
							cadenaWhere = cadenaWhere + " " + nombreCampo
									+ " like '%" + id + "%' AND ";
						else
							cadenaWhere = cadenaWhere + " " + nombreCampo
									+ " = '" + id + "' AND ";
					} else if (campo.getType().getName().contains("Float")) {
						// no se puede filtrar por Float
					} else if (nombreCampo.compareTo("ID") != 0) {// recojo el
																	// ID
						if (filter)
							cadenaWhere = cadenaWhere + " " + nombreCampo
									+ " like '%" + value + "%' AND ";
						else
							cadenaWhere = cadenaWhere + " " + nombreCampo
									+ " = '" + value + "' AND ";
					} else {
						cadenaWhere = cadenaWhere + nombreCampo + " = "
								+ value+ " AND ";
					}

				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cadenaWhere.length() != 0) {
			cadenaSql = cadenaSql + " where "
					+ cadenaWhere.substring(0, cadenaWhere.length() - 4);
		}

		// los datos de la consulta se rellenan en los objetos java
		try {
			s = (Statement) conn.createStatement();

			ResultSet rs = s.executeQuery(cadenaSql);
			Vector objetos = new Vector();
			Object newClass = null;
			Object newClassInside = null;

			while (rs.next()) {
				newClass = clazz.getClass().newInstance();
				objetos.add(newClass);
				for (Field campo : newClass.getClass().getFields()) {

					if (campo.getName().startsWith("ID_")) {
						// genero una clase del tipo de campo del objeto de la
						// base de datos
						newClassInside = Class.forName(
								paquete + campo.getName().substring(3))
								.newInstance();
						// asocio el valor de la base de datos al objeto nuevo
						newClassInside
								.getClass()
								.getField("ID")
								.set(newClassInside,
										rs.getObject(campo.getName()));
						// asocio al valor del objeto de la base de datos al
						// objeto nuevo
						campo.set(newClass, newClassInside);
					} else if (!campo.getType().getName()
							.contains("InputStream"))
						campo.set(newClass, rs.getObject(campo.getName()));

				}

			}
			System.out.println(" ejecutar [" + cadenaSql + "]");
			return objetos.toArray();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException ex) {
			System.out.println("Hubo un problema al intentar ejecutar ["
					+ cadenaSql + "]" + ex);

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/*
	 * 
	 * CREATE TABLE Customer (SID integer, Last_Name varchar(30), First_Name
	 * varchar(30), PRIMARY KEY (SID));
	 */
	public boolean createTable(Object clazz) throws  SQLException{

		return execute(createTable(clazz, true));

	}

	public String createTable(Object clazz, boolean backup){
		Statement s;
		String cadenaSql = "Create Table " + clazz.getClass().getSimpleName()
				+ "(";
		for (Field campo : clazz.getClass().getFields()) {
			try {
				if (campo.getName().matches("ID")) {// recojo el ID
					cadenaSql = cadenaSql + campo.getName()
							+ " Integer NOT NULL AUTO_INCREMENT, ";
				} else if (campo.getName().contains("ID")) {// recojo el ID
					cadenaSql = cadenaSql + campo.getName() + " Integer , ";
				} else if (campo.getType().getName().contains("String")) {
					cadenaSql = cadenaSql + campo.getName() + " Varchar (250),";
				} else if (campo.getType().getName().toLowerCase()
						.contains("float")) {
					cadenaSql = cadenaSql + campo.getName() + " float,";
				} else if (campo.getType().getName().contains("Date")) {
					cadenaSql = cadenaSql + campo.getName() + " Date,";
				} else if (campo.getType().getName().contains("Integer")) {
					cadenaSql = cadenaSql + campo.getName() + " Integer,";
				} else if (campo.getType().getName().contains("InputStream")) {
					cadenaSql = cadenaSql + campo.getName() + " LONGBLOB,";
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cadenaSql = cadenaSql + "PRIMARY KEY (ID)) ENGINE = InnoDB;";

		return cadenaSql;

	}

	/**
	 * por cada clase que se pasa por parametro crea una consulta SLQ para hacer
	 * una copia de seguridad;
	 * */
	public StringBuffer backup(Object clazz) {

		StringBuffer cadenaSql = new StringBuffer();

		cadenaSql.append(this.createTable(clazz, true));// recoge el texto de
														// crear la tabla

		for (Object art : this.find(clazz)) {// encuentra todos los objetos de
												// esta clase
			cadenaSql.append(this.crear(art, true));// genera las consulta SQL
		}

		System.out.print(cadenaSql.toString());
		return cadenaSql;// dev la tabla y los datos
	}

	public void insertFile(Integer ID, String tabla, String campo,
			InputStream file) throws  SQLException{

		String UPDATE_FILE = //"set global max_allowed_packet=1000000000;"+
		"update  " + tabla + " set  " + campo
				+ " = ? where  ID = ? ";

		PreparedStatement ps = null;
		try {
			int tamanio = file.available();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(UPDATE_FILE);
			ps.setInt(2, ID);
			ps.setBinaryStream(1, file, tamanio);

			int result = ps.executeUpdate();

			System.out.println("tamanio fichero->" + tamanio + " filas "
					+ result);
			conn.commit();
			ps.close();
			file.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * finally { ps.close(); fis.close(); }
		 */catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public InputStream getFile(Integer ID, String tabla, String campo) throws SQLException,Exception {

		InputStream file = null;
		try {

			String query = //"set global max_allowed_packet=1000000000;"+
				"select " + campo + " from " + tabla
					+ " where ID = ?";

			PreparedStatement stmt;
			stmt = conn.prepareStatement(query);

			stmt.setInt(1, ID);
			ResultSet result = stmt.executeQuery();
			result.next();

			file = result.getBinaryStream(1); // reading image as InputStream
			System.out.println("tamanio fichero..->" + ((file==null)?"null":file.available()));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return file;
	}

}
