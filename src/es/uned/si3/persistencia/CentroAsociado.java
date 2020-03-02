package es.uned.si3.persistencia;

public class CentroAsociado {
	public Integer ID;
	public String CentroAsociado;
	
	@Override
	public String toString() {
		String str = String.format(
				"centroAsociado(id:%d,%s)", ID, this.CentroAsociado);
		return str;
	}

}
