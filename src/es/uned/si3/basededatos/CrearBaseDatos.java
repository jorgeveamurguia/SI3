package es.uned.si3.basededatos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import es.uned.si3.basededatos.manager.ManagerMensajes;
import es.uned.si3.basededatos.manager.ManagerProfesor;
import es.uned.si3.persistencia.*;




public class CrearBaseDatos {

	GestorSql sql ;

	public CrearBaseDatos() throws Exception{
		
		sql =GestorSql.getInstance();
		
	}
	public static void main(String[] arg){
		try {
			new CrearBaseDatos().CrearBase();
		} catch (Exception e) {
			System.out.println("EXCEPTION en main: " + e.getMessage());
		}
		
	}
	
	/*public CrearBaseDatos(){
		sql =GestorSql.getInstance("mysql", "root", "");
		sql.crearSchema("prueba3");
		sql.close();
		sql =GestorSql.getInstance("prueba3", "root", "");

	}*/
	public void CrearBase() throws SQLException{
	    
		Alumno alumno=new Alumno();
		CentroAsociado centroAsociado=new CentroAsociado();
		Profesor profesor=new Profesor();
		Propuesta propuesta=new Propuesta();
		Proyecto proyecto=new Proyecto();
		Solicitud solicitud = new Solicitud();
		Mensaje mensaje = new Mensaje();
//		MensajeTipo mensajeTipo =new MensajeTipo();
		Administrador adminstrador=new Administrador();
		Departamento departamento=new Departamento();
		
		sql.createTable(alumno);
		sql.createTable(profesor);
		sql.createTable(propuesta);
		sql.createTable(proyecto);
		sql.createTable(solicitud );
		sql.createTable(mensaje);
		sql.createTable(centroAsociado);
		sql.createTable(adminstrador);
		sql.createTable(departamento);
	
	}
	public void crearDatos() throws SQLException{
	
		ManagerMensajes managerMensajes= new ManagerMensajes(sql);
		managerMensajes.CrearDatos();//necesarios para enviar mensajes
		ManagerProfesor mprofesor =new ManagerProfesor(sql);
		mprofesor.CrearDatos();//departamentos
		
	}
	/** genera un fichero en texto plano con los datos guardados en la base de datos.
	 * */
	public StringBuffer  backup(){//File filename){

		Alumno alumno=new Alumno();
		Profesor profesor=new Profesor();
		Propuesta propuesta=new Propuesta();
		Proyecto proyecto=new Proyecto();
		Solicitud solicitud = new Solicitud();
		Mensaje mensaje = new Mensaje();
		CentroAsociado centroAsociado=new CentroAsociado();
		Administrador adminstrador=new Administrador();
		Departamento departamento=new Departamento();

		//creo las tablas 
		StringBuffer cadenaSql=new StringBuffer();
		
		cadenaSql.append(sql.createTable(alumno,true))
			.append(sql.createTable(profesor,true))
			.append(sql.createTable(propuesta,true))
			.append(sql.createTable(proyecto,true))
			.append(sql.createTable(solicitud ,true))
			.append(sql.createTable(mensaje,true))
			.append(sql.createTable(adminstrador,true))
			.append(sql.createTable(departamento,true))
			.append(sql.createTable(centroAsociado,true));
		//creo los datos
		cadenaSql.append(
			sql.backup(new Alumno()).append(
			sql.backup(new Profesor())).append(
			sql.backup(new Propuesta())).append(
			sql.backup(new Solicitud())).append(
			sql.backup(new Mensaje())).append(
			sql.backup(new Departamento())).append(
			sql.backup(new CentroAsociado())).append(
			sql.backup(new Administrador())).append(
			sql.backup(new Proyecto())));

	    	return cadenaSql;/*      
	        BufferedWriter input;
			try {
				input = new BufferedWriter(new FileWriter(filename));
				input.write(cadenaSql.toString());
				input.flush();
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	        
	}
	/**restaura a partir de un fichero de texto la base de atos en la conexon instanciada 
	 * @throws SQLException 
	 * */
	public void  restore(File filename) throws SQLException{

		//sql.crearSchema(nombre);
		//sql.close();
		try {
			sql =GestorSql.getInstance();
		} catch (Exception e) {
			System.out.println("EXCEPTION en main: " + e.getMessage());
		}

		String cadenaSql=null;
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(filename));
			cadenaSql=input.readLine();

			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(String cadena :cadenaSql.split(";")) {
			sql.execute(cadena);
		}

		
/*		sql.crearSchema("jorge");
		sql.close();
		sql =GestorSql.getInstance("jorge");
	*/	
		
	}
	public void borrarTablas() throws SQLException{

		sql.execute("Drop Table Alumno");
		sql.execute("Drop Table Profesor");
		sql.execute("Drop Table Propuesta");
		sql.execute("Drop Table Proyecto");
		sql.execute("Drop Table Administrador");
		sql.execute("Drop Table Mensaje");
		sql.execute("Drop Table MensajeTipo");		
		sql.execute("Drop Table Solicitud");
		sql.execute("Drop Table Departamento");
		sql.execute("Drop Table CentroAsociado");
		
	}

	
}
