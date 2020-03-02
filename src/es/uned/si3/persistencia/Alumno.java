package es.uned.si3.persistencia;

public class Alumno {

	public Integer ID;
	public String dni;
	public String nombre;
	public String nombre_usuario;
	public String apellidos;
	public CentroAsociado ID_CentroAsociado;
	public String numero_expediente;
	public String email;
	public String recibir_notificacion;// boolean corregir el crear un boolean
	public Propuesta ID_Propuesta;//
	public Proyecto ID_Proyecto;//
	public String password;

	@Override
	public String toString() {
		String str = String
				.format("Alumno(id:%d,dni:%s,nombre:%s,nombre_usuario:%s,apellidos:%s,"
						+ "cAsoc:%s,numExp:%s,email:%s,prop:%s,proy:%s,pwd:%s)",
						ID, dni, nombre, nombre_usuario, apellidos,
						ID_CentroAsociado == null ? "null" : ""
								+ ID_CentroAsociado.ID, numero_expediente,
						email, ID_Propuesta == null ? "null" : ""
								+ ID_Propuesta.ID, ID_Proyecto == null ? "null"
								: "" + ID_Proyecto.ID, password);
		return str;
	}
}
