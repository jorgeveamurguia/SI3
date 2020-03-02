package es.uned.si3.basededatos.manager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.*;
import es.uned.si3.persistencia.Mensaje.TipoMensaje;
import es.uned.si3.persistencia.Propuesta.EnumEstadoPropuesta;
import es.uned.si3.persistencia.Propuesta.EnumTipoPropuesta;

public class ManagerProfesor {


	GestorSql sql;

	public ManagerProfesor (GestorSql sql){
	
		this.sql=sql ;
		//sql = GestorSql.getInstance("test","root","");//inicio la base de datos
			
	}
	public void CrearDatos() throws SQLException{
		Departamento  departamento =new Departamento ();
		departamento.ID = sql.maxValorId(departamento);
		departamento.codigo="1101";
		departamento.departamento="LENGUAJES Y SISTEMAS INFORMATICOS";
		sql.crear(departamento);
		
		departamento.ID = sql.maxValorId(departamento);
		departamento.codigo="1102";
		departamento.departamento="INTELIGENCIA ARTIFICIAL";
		sql.crear(departamento);

		departamento.ID = sql.maxValorId(departamento);
		departamento.codigo="1103";
		departamento.departamento="INFORMATICA Y AUTOMATICA";
		sql.crear(departamento);
		 	
		departamento.ID = sql.maxValorId(departamento);
		departamento.codigo="1104";
		departamento.departamento="INGENIERIA DEL SOFTW. Y SIST. INFORMATICOS";
		sql.crear(departamento);
		
		departamento.ID = sql.maxValorId(departamento);
		departamento.codigo="1105";
		departamento.departamento="SISTEMAS DE COMUNICACION Y CONTROL";
		sql.crear(departamento);
		 		
	}
	
	public void CrearDatosTest() throws SQLException{

		Propuesta propuesta;
		Profesor profesor1 = new Profesor();
		
		profesor1.ID_Departamento=new Departamento();
		profesor1.nombre_usuario="p1";
		profesor1.nombre="pepe";
		profesor1.apellidos="Lopez";
		profesor1.password= "p1";
		profesor1.ID=1;

		Profesor profesor2 = new Profesor();
		profesor2.nombre_usuario="p2";
		profesor2.password= "p2";
		profesor2.nombre="Luis";
		profesor2.apellidos="Truñón";
		profesor2.ID_Departamento=new Departamento();
		profesor2.ID=2;

		sql.crear(profesor1);
		sql.crear(profesor2);

		for(int i=0;i<5;i++){
			propuesta = new Propuesta();
			boolean especifica= (i%2)==0;
			propuesta.tipo_general_especifica= especifica?Propuesta.EnumTipoPropuesta.especifica.toString():
				Propuesta.EnumTipoPropuesta.general.toString();
			propuesta.ID=sql.maxValorId(propuesta);//para el autonumerico
			propuesta.titulo= "propuesta"+((especifica)?"E_":"G_") + propuesta.ID; 
			propuesta.conocimientos_previos="haber cursado redes neuronales";
			propuesta.objetivos= "los objetivos " + i;
			propuesta.ID_Profesor=profesor1;
			propuesta.descripcion="Propuesta del profesor"+propuesta.ID_Profesor.ID;
			propuesta.anio="200"+i;
			propuesta.estado_abierta_cerrada= EnumEstadoPropuesta.abierta.toString();
			sql.crear(propuesta );
		}
		//creamos 10 propuestas para otro profesor
		for(int i=0;i<5;i++){
			propuesta = new Propuesta();
			propuesta.objetivos= "los objetivos " + i;
			boolean especifica= (i%2)==0;
			propuesta.tipo_general_especifica= especifica?Propuesta.EnumTipoPropuesta.especifica.toString():
				Propuesta.EnumTipoPropuesta.general.toString();
			propuesta.ID=sql.maxValorId(propuesta);//para el autonumerico
			propuesta.titulo= "propuesta"+((especifica)?"E_":"G_") + propuesta.ID; 
			propuesta.conocimientos_previos="haber cursado redes neuronales";
			propuesta.ID_Profesor=profesor2;
			propuesta.descripcion="Propuesta del profesor"+propuesta.ID_Profesor.ID;
			propuesta.anio="200"+i;
			sql.crear(propuesta );
		}


		Proyecto proyecto;
		//profesores nuevos
		
		//creamos 5 proyectos para este profesor
		for(int i=0;i<5;i++){
			proyecto = new Proyecto();
			proyecto.ID=sql.maxValorId(proyecto);//para el autonumerico
			proyecto.conocimientos_previos="haber cursado redes neuronales";
			proyecto.ID_Profesor=profesor1;
			//proyecto.ID_Profesor=new Profesor();
			proyecto.estado_cerrado_desarrollo="desarrollo";
			proyecto.descripcion="Propuesta del profesor"+proyecto.ID_Profesor.ID;
			proyecto.anio="200"+i;
			sql.crear(proyecto);
		}
		//creamos 10 proyectos para otro profesor
		for(int i=0;i<5;i++){
			proyecto = new Proyecto();
			proyecto.ID=sql.maxValorId(proyecto);//para el autonumerico
			proyecto.conocimientos_previos="haber cursado redes neuronales";
			proyecto.ID_Profesor=profesor2;
			proyecto.estado_cerrado_desarrollo="cerrado";
			proyecto.descripcion="Propuesta del profesor"+proyecto.ID_Profesor.ID;
			proyecto.anio="200"+i;
			sql.crear(proyecto);
		}
		//todas las propuestas del profesor 1

	}
	public void verPropuestaProfesor(Profesor profesor){

		Propuesta propuesta = new Propuesta();
		propuesta.ID_Profesor=profesor;
		Object [] objeto=sql.find(propuesta);
		
		for(Object propu :objeto){
			Propuesta pro=(Propuesta)propu;
			System.out.println("Propuesta del profesor 1 ->Id propuesta ["+pro.ID+ "]"+pro.descripcion);
		}

	}
	public Object [] proyectosAsignadosAlProfesor(Profesor profesor){

		Proyecto proyecto = new Proyecto();
		proyecto.ID_Profesor=profesor;
		Object [] objeto=sql.find(proyecto);
		
		for(Object propu :objeto){
			Proyecto pro=(Proyecto)propu;
			System.out.println("Proyecto Asignados del profesor 1 ->Id proyecto["+pro.ID+ "]"+pro.descripcion);
		}
		return objeto;
			
	}
	
	public void testCrearUnaPropuestaDeProyecto() throws SQLException{
		Propuesta propuesta =new Propuesta();
		propuesta.anio="2008";
		propuesta.conocimientos_previos="hay que saber de todo";
		propuesta.ID=sql.maxValorId(propuesta);
		propuesta.ID_Profesor=new Profesor();
		propuesta.objetivos="hacer una nave espacial";
		propuesta.tipo_general_especifica="general";
		propuesta.titulo="NASA";
		
		crearUnaPropuestaDeProyecto(propuesta);
	}
	public void testCrearPropuestasDeProyecto() throws SQLException{
		Propuesta propuesta =new Propuesta();
		propuesta.anio="2008";
		propuesta.conocimientos_previos="hay que saber de todo";
		propuesta.ID=sql.maxValorId(propuesta);
		propuesta.ID_Profesor=new Profesor();
		propuesta.ID_Profesor.nombre_usuario="p1";
		propuesta.objetivos="hacer una nave espacial";
		propuesta.tipo_general_especifica="general";
		propuesta.titulo="NASA";
		
		crearUnaPropuestaDeProyecto(propuesta);

		
		propuesta =new Propuesta();
		propuesta.anio="201";
		propuesta.conocimientos_previos="hay que saber de todo";
		propuesta.ID=sql.maxValorId(propuesta);
		propuesta.ID_Profesor=new Profesor();
		propuesta.ID_Profesor.nombre_usuario="p2";
		propuesta.objetivos="hacer una nave espacial";
		propuesta.tipo_general_especifica="general";
		propuesta.titulo="NASA";
		
		crearUnaPropuestaDeProyecto(propuesta);
	}
	
	public void crearUnaPropuestaDeProyecto(Propuesta propuesta) throws SQLException{
		sql.crear(propuesta);
	}
	
	public void CerrarPFCasignarNotaAdjuntarMemoria(Proyecto proyecto, String nota,File ficheroBinario) throws SQLException{
		
		try {
	      
		proyecto.estado_cerrado_desarrollo = "cerrado";
		proyecto.calificacion = nota;
		proyecto.memoria_pdf = new FileInputStream(ficheroBinario);

		sql.update(proyecto);
		
		sql.insertFile(proyecto.ID, "Proyecto", "memoria_pdf", proyecto.memoria_pdf  ); 
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//para leer la memoria de un proyecto seleccionamos el proyecto y llama al getFile se almacena el InputStream
	public void leerPFCMemoria(Proyecto proyecto) throws SQLException, Exception{
		
		proyecto.memoria_pdf  = sql.getFile(proyecto.ID, "Proyecto", "memoria_pdf");
	
		/*
		 * si quieres guardar el fichero descomenta el codigo
		try {
		
		if(proyecto.memoria_pdf !=null){
			File f=new File("memoriaPFC.txt");
		    OutputStream out=new FileOutputStream(f);
		    byte buf[]=new byte[1024];
		    int len;
		    while((len=proyecto.memoria_pdf.read(buf))>0)
		    	out.write(buf,0,len);
		    
		    out.close();
		    proyecto.memoria_pdf.close();

			}else{
				System.out.println("No encuentra el fichero");
			}
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		*/
	}

}

