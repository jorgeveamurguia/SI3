package es.uned.si3.persistencia;

public class Profesor {

	public Integer ID;
	public String dni;
	public String nombre;
	public String nombre_usuario;
	public String apellidos;
	public Departamento ID_Departamento;// int
	public String email;
	public String recibir_notificacion;// boolean
	public String password;

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("\nID:");
		result.append(ID);
		result.append("\ndni:\t\t");
		result.append(dni);
		result.append("\nnombre:\t\t");
		result.append(nombre);
		result.append("\nnombre_usuario:\t\t");
		result.append(nombre_usuario);
		result.append("\napellidos:\t");
		result.append(apellidos);
		if (ID_Departamento != null) {
			result.append("\ndepartamento:\t");
			result.append(ID_Departamento.departamento);
		}
		result.append("\nemail:\t\t");
		result.append(email);
		result.append("\nrecibir_notificacion:\t");
		result.append(recibir_notificacion);
		result.append("\n");
		return result.toString();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Profesor) {
			Profesor oObjetoLocal = (Profesor) obj;

			if (this.dni.compareTo(oObjetoLocal.dni) == 0) {
				return true;
			}
		}
		return false;
	}

}
