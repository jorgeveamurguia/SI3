package es.uned.si3.persistencia;

import java.io.InputStream;

import es.uned.si3.basededatos.manager.Manager;

public class Proyecto {
	public enum FaseProyecto {
		desarrollo, cerrado
	};
	
	/*enum content_Type_rar_zip{
		rar, zip
	};*/
	public Proyecto() {

	}

	public Proyecto(Propuesta propuesta) {
		this.titulo = propuesta.titulo;
		this.objetivos = propuesta.objetivos;
		this.descripcion = propuesta.descripcion;
		this.conocimientos_previos = propuesta.conocimientos_previos;
		this.ID_Profesor = propuesta.ID_Profesor;
		this.tipo = propuesta.tipo_general_especifica;
		this.anio = Manager.getCurrentYearStr();
		this.estado_cerrado_desarrollo = FaseProyecto.desarrollo.toString();
		this.calificacion = null;
	}

	public Integer ID;// Código de la propuesta, permitirá identificar las
						// distintas propuestas.
	public String titulo;
	public String objetivos;
	public String descripcion;
	public String conocimientos_previos;// requeridos por parte del alumno.
	public Profesor ID_Profesor;// para sacar el DNI del profesor que va a ser
								// director del proyecto.
	// se saca del profesor: Departamento al que esta adscrito el profesor y por
	// tanto la propuesta.
	public String tipo;// Tipo de PFC: Puede ser de tipo general o especifico.
	public String anio;// anio academico
	public String estado_cerrado_desarrollo;// Estado del proyecto: Si está
											// cerrado porque ha sido ya
											// defendido o si está en
											// elavoracion
	public String calificacion;// Calificación obtenida por el alumno.
	public InputStream memoria_pdf;
	public String nombreFicheroMemoria;
	/*
	 * Memoria del PFC: Cuando se cierre un PFC y se asigne la calificación, se
	 * deberá adjuntar un fichero pdf conteniendo la memoria del propio
	 * proyecto.
	 */
	public String nombreFicheroCodigo;
	public InputStream fichero_comprimido;/*
										 * Código comprimido del PFC: Junto con
										 * la memoria del PFC, se deberá
										 * adjuntar tambien un fichero
										 * comprimido (zip, rar), que contenga
										 * el código del proyecto.
										 */

}
