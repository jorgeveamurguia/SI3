package es.uned.si3.persistencia;

public class Departamento {

	public Integer ID;
	public String codigo;
	public String departamento;

	@Override
	public String toString() {
		String str = String.format(
				"departamento(id:%d,cod:%s,%s)", ID, this.codigo, this.departamento);
		return str;
	}
}
