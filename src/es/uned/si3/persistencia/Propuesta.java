package es.uned.si3.persistencia;

public class Propuesta {

	public Integer ID;//Código de la propuesta, permitirá identificar las distintas propuestas.
	public String titulo;
	public String objetivos;
	public String descripcion;
	public String conocimientos_previos;// requeridos por parte del alumno.
	public Profesor ID_Profesor;// para sacar el DNI del profesor que ha realizado la propuesta.
	//se saca del profesor: Departamento al que esta adscrito el profesor y por tanto la propuesta.
	public String  tipo_general_especifica;//Tipo de PFC: Puede ser de tipo general o especifico.
	public String  anio;//anio academico
	public String estado_abierta_cerrada;//"abierta" "cerrada"

	public enum EnumEstadoPropuesta {abierta, cerrada};
	public enum EnumTipoPropuesta {general, especifica};

	@Override
	public String toString() {
		String str = String
				.format("Propuesta(id:%d,titulo:%s,objs:%s,descr:%s,cono:%s,"
						+ "prof:%s,tipo:%s,anio:%s,estado:%s)",
						ID, titulo, objetivos, descripcion, conocimientos_previos,
						ID_Profesor== null ? "null" : ""
								+ ID_Profesor.ID, tipo_general_especifica,
						anio, estado_abierta_cerrada);
		return str;
	}
}
