package es.uned.si3.persistencia;

import java.io.InputStream;

public class Solicitud {
	public enum EstadoSolicitud {
		pendiente, aceptada, rechazada
	};

	public Integer ID;
	public Alumno ID_Alumno;
	public Propuesta ID_Propuesta;
	// public Administrador ID_Administrador;
	public String anio;
	public InputStream pdfConocimientos;
	public String aceptada_rechazada;

	@Override
	public String toString() {
		String str = String.format(
				"Solicitud(id:%d,idA:%s,idProp:%s,anio:%s,aceptada?%s)", ID,
				ID_Alumno == null ? "null" : "" + ID_Alumno.ID,
				ID_Propuesta == null ? "null" : "" + ID_Propuesta.ID, anio,
				aceptada_rechazada);
		return str;
	}
}
