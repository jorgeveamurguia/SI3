package es.uned.si3.basededatos;

import java.io.File;
import java.sql.SQLException;

import es.uned.si3.basededatos.manager.ManagerAdministrador;
import es.uned.si3.basededatos.manager.ManagerAlumos;
import es.uned.si3.basededatos.manager.ManagerMensajes;
import es.uned.si3.basededatos.manager.ManagerProfesor;
import es.uned.si3.persistencia.*;
//import junit.framework.Assert;
//import junit.framework.TestCase;
import es.uned.si3.util.LoggerHelper;


public class Test {//extends TestCase{

	GestorSql sql;
	CrearBaseDatos baseDedatos;
	ManagerProfesor mprofesor ;
	ManagerAlumos malumno;
	ManagerMensajes mmensajes ;
	ManagerAdministrador madministrador;

	public Test (){
	try {
		sql = GestorSql.getInstance("test","root","");//inicio la base de datos
		
		baseDedatos = new CrearBaseDatos();
	} catch (Exception e) {
		System.out.println("EXCEPTION en main: " + e.getMessage());
		return;
	} 

		mprofesor = new ManagerProfesor (sql);
		mmensajes = new ManagerMensajes(sql);		
		madministrador = new ManagerAdministrador(sql);
		malumno =new ManagerAlumos(sql);
		
	}
	
	public void testCrearTablas() throws SQLException{
		
		try {
			baseDedatos.CrearBase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//crea la base de datos 
		
		baseDedatos.crearDatos();//crear los datos necesarios 
		
		mprofesor.CrearDatosTest();//datos de tes de profesor
		madministrador.CrearDatosTest();//crea un administrador
		malumno.CrearDatosTest();//creo 2 alumnos
	}
	//test crea una solicitud a partir de una propuesta y se la envia al administrador
	public void testEnviarMensajes() throws SQLException{
		
		Alumno alumno = new Alumno ();
		alumno.ID=1;
		Object [] objeto=sql.find(alumno);
		if (objeto.length==1)
			alumno = (Alumno)objeto[0];
		else 
			System.out.print("No encuentra al alumno");

		
		Propuesta[] propuesta =malumno.VerPropuestasAbiertas();
		
		//selecciono la primera propuesta
		if(propuesta.length>1){
			Administrador administrador =malumno.dameAdministrador();
			Solicitud solicitud =malumno.enviarSolicitudPropuestaAdministrador(
					administrador 
					,propuesta[0]);
			mmensajes.formAdministradoToAlumnoSolicitud(alumno, solicitud, administrador) ;
		
		}else 
			System.out.print("No encuentra ninguna propuesta ");
		

		
	}
	public void testverPropuestasDelProfesor(){
		
		//creamos 5 propuestas para este profesor
		//Busca todas las propuestas del profesor 1
		Profesor profesor1 =new Profesor ();
		profesor1.ID=1;
		profesor1.ID_Departamento =new Departamento();
		
		mprofesor.verPropuestaProfesor(profesor1);
		
	}

	
	public void testproyectosAsignadosAlProfesor(){
		Profesor profesor1 =new Profesor ();
		profesor1.ID=1;
		
		Object [] proyectos= mprofesor.proyectosAsignadosAlProfesor(profesor1);
		System.out.println("proyectos"+proyectos.length);
	//	Assert.assertTrue(proyectos.length==5);	
	}
	public void testCerrarProyecto() throws SQLException{

		Profesor profesor1 =new Profesor ();
		profesor1.ID=1;
		
		Object [] proyectos= mprofesor.proyectosAsignadosAlProfesor(profesor1);

		mprofesor.CerrarPFCasignarNotaAdjuntarMemoria(
				(Proyecto) proyectos[0], "20",new File("memoria.txt") );
	
	}

	public void testBuscarDepartamento(){
		Departamento dpto = new Departamento();
		dpto.departamento="INFORMAT";
		Object[] object =sql.find(dpto,true);
		
		System.out.print("nº de pdtos de IA "+object.length );
			
	}
	public void testborrarTablas(){
		
		try {
			baseDedatos.borrarTablas();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
		

}
