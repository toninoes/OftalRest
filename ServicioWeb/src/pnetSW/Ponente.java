package pnetSW;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Ponente {
	private String dni;
	private String nombre, apellidos, afiliacion, pais;

	public Ponente() {}

	public Ponente(String dni, String nombre, String apellidos,
					String afiliacion, String pais) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.afiliacion = afiliacion;
		this.pais = pais;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getAfiliacion() {
		return afiliacion;
	}

	public void setAfiliacion(String afiliacion) {
		this.afiliacion = afiliacion;
	}

	public String toString() {
		String msg;

		msg = "DNI: " + this.dni;
		msg += "\nNOMBRE: " + this.nombre;
		msg += "\nAPELLIDOS: " + this.apellidos;
		msg += "\nAFILIACIÓN: " + this.afiliacion;
		msg += "\nPAÍS: " + this.pais;

		return msg;
	}
}
