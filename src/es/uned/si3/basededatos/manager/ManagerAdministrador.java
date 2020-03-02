package es.uned.si3.basededatos.manager;

import java.sql.SQLException;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.Administrador;
import es.uned.si3.persistencia.Profesor;
import es.uned.si3.persistencia.Propuesta;
import es.uned.si3.persistencia.Proyecto;
import es.uned.si3.persistencia.Solicitud;
import es.uned.si3.persistencia.Solicitud.EstadoSolicitud;

public class ManagerAdministrador {

	

	/*pasa de propuesta a proyecto y se cierra la propuesta
	 * si el proyecto es generico envia un profesor new Profesor ()
	 * */

	GestorSql sql;

	public ManagerAdministrador (GestorSql sql){
	
		this.sql=sql ;
		//sql = GestorSql.getInstance("test","root","");//inicio la base de datos
			
	}

	public void CrearDatosTest() throws SQLException{

		crearAdministrador();
	}
	
	private void crearAdministrador() throws SQLException{

		Administrador administrador = new Administrador();
		administrador.nombre_usuario="admin";
		administrador.password= "admin";
		administrador.ID=sql.maxValorId(administrador);
		administrador.nombre="yo asigno profesor";
		sql.crear(administrador);


	}
	
	
	public Proyecto aceptarSolicitudAsignarProfesor(Solicitud solicitud, Profesor profesor) throws SQLException{
		Proyecto proyecto;//el nuevo proyecto que será creado a partir de la solicitud y del profesor
		Propuesta propuesta;
		//encuentro la propuesta de la solicitud
		Object [] objeto=sql.find(solicitud.ID_Propuesta);
		if (objeto.length==1)
			propuesta=(Propuesta)objeto[0];
		else
			return null; //no existe la propuesta
		
		if(solicitud.aceptada_rechazada.equals("aceptada") ||
			solicitud.aceptada_rechazada.equals("rechazada")){
			return null; //la solicitud ya fue aceptada o rechazada
		}
		
		if(propuesta.estado_abierta_cerrada=="cerrada"){//la propuesta ya esta cerrada
			return null;		
		}else {
			//creo un nuevo proyecto a partir de la propuesta
			proyecto =new Proyecto();
			solicitud.aceptada_rechazada=EstadoSolicitud.aceptada.toString();
			proyecto.anio= propuesta.anio;
			proyecto.objetivos=propuesta.objetivos;
			proyecto.titulo=propuesta.tipo_general_especifica;
			proyecto.estado_cerrado_desarrollo="desarrollo";
			if(proyecto.tipo.equals("generico"))
				proyecto.ID_Profesor=propuesta.ID_Profesor;
			else
				proyecto.ID_Profesor=profesor;//asigno profesor
			
			sql.crear(proyecto);
			//actualizo la propuesta a cerrada
			propuesta.estado_abierta_cerrada="cerrada";
			sql.update(propuesta);
			//actualizo la solicitud a aceptada
			solicitud.aceptada_rechazada="aceptada";
			sql.update(solicitud);
		}
		
		
		return proyecto;
	}
	public void solicitudesPorAnio(){
		
	}
	public void verPropuestasPorAnio(String anio){
		Propuesta propuesta = new Propuesta();
		propuesta.anio=anio;
		Object [] objeto=sql.find(propuesta);
		
		for(Object propu :objeto){
			Propuesta pro=(Propuesta)propu;
			System.out.println("Propuesta por año "+propuesta.anio+" ["+pro.ID+ "]"+pro.anio);
		}
		
	}
	public void verPfcDesarrolloPorAnio(String anio){
		
		
		Proyecto proyecto= new Proyecto();
		proyecto.estado_cerrado_desarrollo="desarrollo";
		proyecto.anio=anio;
	
		Object [] objeto=sql.find(proyecto);
//		Assert.assertTrue(objeto.length==1);
				
		for(Object propu :objeto){
			Proyecto pro=(Proyecto)propu;
			System.out.println("Proyectos por año "+proyecto.anio+" en desarrollo["+pro.ID+ "]"+pro.anio);
		}
	
	}
	public void pfcTerminadosPorAnio(String anio){
		
		
		Proyecto proyecto= new Proyecto();
		proyecto.estado_cerrado_desarrollo="cerrado";
		proyecto.anio=anio;
	
		Object [] objeto=sql.find(proyecto);
//		Assert.assertTrue(objeto.length==1);
				
		for(Object propu :objeto){
			Proyecto pro=(Proyecto)propu;
			System.out.println("Proyectos por año "+proyecto.anio+" en desarrollo["+pro.ID+ "]"+pro.anio);
		}
	
		
	}

}
