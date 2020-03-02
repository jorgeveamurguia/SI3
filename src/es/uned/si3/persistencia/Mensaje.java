package es.uned.si3.persistencia;

public class Mensaje {
	public enum TipoMensaje {
		indefinido, peticionSolicitud, respuestaSolicitud, PFCAsignado, disponibleNota
	};

	public Integer ID;
	public Alumno ID_Alumno;// si el mensaje es de un alumno
	public Profesor ID_Profesor;// se le asigna un PFC
	// public Administrador ID_Aministrador;
	public Solicitud ID_Solicitud;
	public Proyecto ID_Proyecto;
	public String mensaje;//
	public String leido;// cierto si esta leido
	public String mensajeTipo; // enum TipoMensaje

	// solicitud (aceptada/denegada)-> alumno
	// asignado PFC -> profesor
	// nota de PFC -> alumno
	@Override
	public String toString() {
		String str = String.format(
				"Mensaje(id:%d,alumno:%s,profe:%s,soli:%s,proy:%s,"
						+ "msg:%s,leido:%s,tipo:%s)", ID,
				ID_Alumno == null ? "null" : "" + ID_Alumno.ID,
				ID_Profesor == null ? "null" : "" + ID_Profesor.ID,
				ID_Solicitud == null ? "null" : "" + ID_Solicitud.ID,
				ID_Proyecto == null ? "null" : "" + ID_Proyecto.ID,
				this.mensaje, this.leido, this.mensajeTipo);
		return str;
	}

}
