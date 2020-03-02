package es.uned.si3.basededatos.manager;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import es.uned.si3.basededatos.GestorSql;
import es.uned.si3.persistencia.*;

public class ManagerAlumos {

	
	GestorSql sql;

	public ManagerAlumos(GestorSql sql){
	
		//sql = GestorSql.getInstance("test","root","");//inicio la base de datos
		this.sql=sql;
	}
	public void CrearDatosTest () throws SQLException{
		//sql.setAutoCommit(false);
		Savepoint uno =sql.openTransaction("transaccion1");
		//guarda a partir de este punto 
		CentroAsociado centroAsociado = new CentroAsociado();
		centroAsociado.CentroAsociado="Almeria";
		centroAsociado.ID=50000;

		sql.crear(centroAsociado);

		Savepoint dos =sql.openTransaction("transaccion2");
		//guarda hasta este punto
		centroAsociado = new CentroAsociado();
		centroAsociado.CentroAsociado="Almeria- HUERCAL OVERA";
		centroAsociado.ID=50002;
		sql.crear(centroAsociado);

		centroAsociado = new CentroAsociado();
		centroAsociado.CentroAsociado="Almeria-EL EJIDO";
		centroAsociado.ID=50001;
		sql.crear(centroAsociado);
		sql.rollback(dos);
		//cancela la inserccion de los centros 50001 y 50002
		sql.openTransaction();
		centroAsociado = new CentroAsociado();
		centroAsociado.CentroAsociado="BAZA";
		centroAsociado.ID=64000;
		sql.crear(centroAsociado);

		CentroAsociado calatayud= new CentroAsociado();
		calatayud.CentroAsociado="CALATAYUD";
		calatayud.ID=8000;
		sql.crear(calatayud);

		centroAsociado = new CentroAsociado();
		centroAsociado.CentroAsociado="BARBASTRO";
		centroAsociado.ID=19000;
		sql.crear(centroAsociado);
		sql.commit();
		//inserta el resto
		//sql.setAutoCommit(true);
		
		Alumno alumno = new Alumno();
		alumno.ID = sql.maxValorId(alumno);
		alumno.apellidos="veamurguia";
		alumno.dni="45086199";
		alumno.nombre="jorge";
		alumno.nombre_usuario="a1";
		alumno.password= "a1";
		alumno.email="jorge.veamuguia@gmail.com";
		alumno.ID_CentroAsociado=calatayud;
		
		alumno.ID_Propuesta= new Propuesta();
		alumno.ID_Proyecto = new Proyecto();
		sql.crear(alumno);

		alumno.ID = sql.maxValorId(alumno);
		alumno.apellidos="gonzalez";
		alumno.dni="25000001";
		alumno.nombre="carlos";
		alumno.nombre_usuario="a2";
		alumno.password= "a2";
		alumno.email="carlos.gonzalez@gmail.com";
		alumno.ID_CentroAsociado =calatayud;
		
		alumno.ID_Propuesta= new Propuesta();
		alumno.ID_Proyecto = new Proyecto();
		sql.crear(alumno);
		
	}

	
	public Propuesta[] VerPropuestasAbiertas(){

		Propuesta propuesta= new Propuesta();
		propuesta.estado_abierta_cerrada=Propuesta.EnumEstadoPropuesta.abierta.toString();
		List<Propuesta> propuestas= Manager.getInstancesFromSql(sql, propuesta);
		return propuestas.toArray(new Propuesta[0]);
	}
	public Solicitud enviarSolicitudPropuestaAdministrador(Administrador administrador,Propuesta propuesta) throws SQLException{

		Solicitud solicitud = new Solicitud();
		solicitud.ID_Propuesta=propuesta;
		solicitud.ID=sql.maxValorId(solicitud);
//		solicitud.ID_Administrador =administrador;
		
		return solicitud;
		
	}
	//dame el primer administrador si no lo hay dev null
	public Administrador dameAdministrador (){
		Administrador administrador = new Administrador();
		
		Object [] objeto=sql.find(administrador );
		if (objeto.length==1)
			return 	(Administrador)objeto[0];
		else
			return 	null;
	}

}
