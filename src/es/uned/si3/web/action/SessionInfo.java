package es.uned.si3.web.action;

public class SessionInfo {
	private String nombreUsuario;
	private String tipoUsuario;
	private String nombreCompleto;

	public SessionInfo(String tipoUsuario, String nombreUsuario, String nombre,
			String apellidos) {
		this.nombreUsuario = nombreUsuario;
		this.tipoUsuario = tipoUsuario;
		if (nombre != null && apellidos != null)
			this.nombreCompleto = String.format("%s, %s", apellidos, nombre);
		else if (nombre != null)
			this.nombreCompleto = nombre;
		else if (apellidos != null)
			this.nombreCompleto = apellidos;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public String getTipoUsuario() {
		return this.tipoUsuario;
	}

	public String getNombreCompleto() {
		return this.nombreCompleto;
	}
}
