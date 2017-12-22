package pnetSW;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author		Andrés Martínez <andres.martinezgavira@alum.uca.es>
 * @author		Antonio Ruiz <antonio.ruizrondan@alum.uca.es>
 * @version		1.0
 * @since		1.0
 * @urlSwagger	http://localhost:8080/ServicioWeb/swagger.json
 */
@Path("/oftalmologia2")
@Api(value = "/oftalmologia2")
public class Servicio2 {
	private Connection conexion = null;
	private Statement comando = null;
	private PreparedStatement ps = null;
	private ResultSet registro = null;
	private String consulta;
	private static AtomicInteger totalPonentes = new AtomicInteger(total());
	
	/**
	 * Establece conexión a SGBD MySQL, con las siguientes caracteristicas.
	 * 
	 * Nombre BBDD: 		'pnet'
	 * Usuario:				'pnet'
	 * Contraseña:			'pnet'
	 * IP Servidor MySQL:	'127.0.0.1'
	 * Puerto MySQL:		'3306'
	 * 
	 * @return	objeto Connection
	 */
	private static Connection MySQLConnect() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String user = "pnet";
			String pass = "pnet";
			String name = "pnet";
			String host = "127.0.0.1";
			int port = 3306;
			String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, name);
			con  = DriverManager.getConnection(url, user, pass);

			if (con != null) {
				System.out.println("Conexión a base de datos " + url + " ... Ok");
			}
		} catch(Exception ex) {
			System.err.println(ex);
		}
		
		return con;
	}
	
	
	/**
	 * OPERACIÓN 1: Obtener en JSON todos los miembros del comité de
	 * programa (DNI, nombre, apellidos, afiliación, nacionalidad. Ej:
	 *  1, Andrés,  Martínez Gavira, Hospital Puerta del Mar, España)
	 *
	 * @return	Mapa (diccionario), cuya clave será el DNI del ponente y
	 * 			el valor es el Ponente que tiene ese dni.
	 */
	@GET
	@Path("/todosPonentes")
	@Produces({"application/json"})
	@ApiOperation(
		value = "Obtiene todos los ponentes",
		notes = "Duevuelve un Map con todos los ponentes"
	)
	public Map<String, Ponente> readAllDoctors() {
		Map<String, Ponente> bbdd = new HashMap<>();

		try {
			conexion = MySQLConnect();
			consulta = "SELECT * FROM ponentes";
			comando = conexion.createStatement();
			registro = comando.executeQuery(consulta);
			System.out.println(consulta);
			
			while(registro.next()) {
				bbdd.put(registro.getString(1), 
					new Ponente(registro.getString(1), registro.getString(2), 
								registro.getString(3), registro.getString(4),
								registro.getString(5)));
			}
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			cerrarTodo();
		}
		
		return bbdd;
	}
	
	
	/**
	 * OPERACIÓN 2: Obtener en modo texto los datos de un miembro
	 * concreto del comité de programa. El miembro a buscar se podrá
	 * especificar con su nombre (DNI) como parámetro del path.
	 *
	 * @param key	Entero que representa el dni del ponente a obtener
	 * @return		Mensaje de error o éxito al obtener a un ponente
	 */
	@GET
	@Path("/obtenerPonente/{dni}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Busca un ponente por DNI",
		notes = "Devuelve los datos relativos a un ponente"
	)
	public String readOneDoctor(
			@ApiParam(value = "DNI del ponente", required = true)
			@PathParam("dni") String key) {
		String msg;
		try {
			conexion = MySQLConnect();
			consulta = "SELECT * FROM ponentes WHERE dni = ?";
			ps = conexion.prepareStatement(consulta);
			ps.setString(1, key);
			registro = ps.executeQuery();
			System.out.println(ps.toString());
			registro.next();
			msg = "DNI: " + registro.getString(1);
			msg += "\nNOMBRE: " + registro.getString(2);
			msg += "\nAPELLIDOS: " + registro.getString(3);
			msg += "\nAFILIACIÓN: " + registro.getString(4);
			msg += "\nPAÍS: " + registro.getString(5);
		} catch (SQLException ex) {
			msg = "<div style='color: red; font-weight: bold;'>ERROR: No existe un ponente con DNI: " + key + "</div>";
		}catch (Exception ex) {
			msg = "<div style='color: orange; font-weight: bold;'>ERROR (Excepción): " + ex.getMessage() + "</div>";
		} finally {
			cerrarTodo();
		}
		
		return msg;
	}
	
	
	/**
	 * OPERACIÓN 3: Actualizar la afiliación y nacionalidad de afiliación
	 * de un miembro (pasando como parámetro del path el nombre (DNI) y
	 * los nuevos datos como JSON)
	 *
	 * @param key	Dni del ponente a actualizar
	 * @param p		Ponente serializado desde JSON a Ponente gracias a
	 * 				la notación XmlRootElement sobre la clase Ponente
	 * @return		Mensaje de error o éxito al actualizar a un ponente
	 */
	@PUT
	@Path("/modificarPonente/{dni}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Actualiza los datos de un ponente",
		notes = "Actualiza los datos del ponente que se corresponda con el dni. "
				+ "El ponente debe existir"
	)
	public String updateDoctor(
			@ApiParam(value = "DNI del ponente", required = true)
			@PathParam("dni") String key, 
			@ApiParam(value = "Datos del ponente a actualizar", required = true)
			Ponente p) {
		String msg;

		try {
			conexion = MySQLConnect();
			consulta = "SELECT COUNT(*) FROM ponentes WHERE dni = ?";
			ps = conexion.prepareStatement(consulta);
			ps.setString(1, key);
			registro = ps.executeQuery();
			registro.next();
			if(registro.getInt(1) > 0) {
				consulta = "UPDATE ponentes SET nombre = ?, apellidos = ?";
				consulta += ", afiliacion = ?, pais = ?  WHERE dni = ?";
				ps = conexion.prepareStatement(consulta);
				ps.setString(1, p.getNombre());
				ps.setString(2, p.getApellidos());
				ps.setString(3, p.getAfiliacion());
				ps.setString(4, p.getPais());
				ps.setString(5, key);
				ps.executeUpdate();
				System.out.println(ps.toString());
				msg = "<div style='color: green; font-weight: bold;'>Ponente actualizado correctamente</div>";
			} else
				msg = "<div style='color: red; font-weight: bold;'>ERROR: No existe un ponente con DNI: " + key + "</div>";
		} catch (Exception e) {
			msg = "<div style='color: orange; font-weight: bold;'>ERROR (Excepción): " + e.getMessage() + "</div>";
		} finally {
			cerrarTodo();
		}

		return msg;
	}
	
	
	/**
	 * OPERACIÓN 4: Borrar un miembro del comité de programa (pasando su
	 * nombre (DNI) como parámetro del path)
	 *
	 * @param key	Entero que representa el dni del ponente a eliminar
	 * @return		Mensaje de error o éxito al eliminar a un ponente
	 */
	@DELETE
	@Path("/eliminarPonente/{dni}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Elimina un ponente",
		notes = "Elimina los datos del ponente que se corresponda con el dni. "
				+ "El ponente debe existir"
	)
	public String deleteDoctor(
			@ApiParam(value = "DNI del ponente", required = true)
			@PathParam("dni") String key){
		String msg;

		try {
			conexion = MySQLConnect();
			consulta = "SELECT COUNT(*) FROM ponentes WHERE dni = ?";
			ps = conexion.prepareStatement(consulta);
			ps.setString(1, key);
			registro = ps.executeQuery();
			registro.next();
			if(registro.getInt(1) > 0) {
				consulta = "DELETE FROM ponentes WHERE dni = ?";
				ps = conexion.prepareStatement(consulta);
				ps.setString(1, key);
				ps.executeUpdate();
				totalPonentes.decrementAndGet();
				msg = "<div style='color: green; font-weight: bold;'>Eliminado correctamente ponente con DNI: " + key + "</div>";
				System.out.println(ps.toString());
			} else
				msg = "<div style='color: red; font-weight: bold;'>ERROR: No existe un ponente con DNI: " + key + "</div>";
		} catch (Exception e) {
			msg = "<div style='color: orange; font-weight: bold;'>ERROR (Excepción): " + e.getMessage() + "</div>";
		} finally {
			cerrarTodo();
		}

		return msg;
	}
	
	
	/**
	 * OPERACIÓN 5: Añadir un nuevo miembro del comité de programa
	 * pasándolo como JSON
	 *
	 * @param p	Ponente serializado desde JSON a Ponente gracias a la
	 * 			notación XmlRootElement indicada sobre la clase Ponente
	 * @return	Mensaje de error o éxito al añadir al nuevo ponente
	 */
	@POST
	@Path("/nuevoPonente")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Da de alta un nuevo ponente",
		notes = "Crea un nuevo ponente. El ponente no debe existir"
	)
	public String createDoctor(
			@ApiParam(value = "Datos del nuevo ponente", required = true)
			Ponente p){
		String msg;

		try {
			conexion = MySQLConnect();
			consulta = "SELECT COUNT(*) FROM ponentes WHERE dni = ?";
			ps = conexion.prepareStatement(consulta);
			ps.setString(1, p.getDni());
			registro = ps.executeQuery();
			registro.next();
			if(p.getDni() == null || p.getDni().isEmpty()) {
				msg = "<div style='color: red; font-weight: bold;'>Tienes que indicar un DNI</div>";
			} else if(!p.getDni().matches("\\d+")) { 
				msg = "<div style='color: red; font-weight: bold;'>Tienes que introducir un número para el DNI</div>";
			} else if(registro.getInt(1) == 0) {
				consulta = "INSERT INTO ponentes (dni, nombre, apellidos, afiliacion, pais)";
				consulta += "VALUES (?, ?, ?, ?, ?)";
				ps = conexion.prepareStatement(consulta);
				ps.setString(1, p.getDni());
				ps.setString(2, p.getNombre());
				ps.setString(3, p.getApellidos());
				ps.setString(4, p.getAfiliacion());
				ps.setString(5, p.getPais());
				ps.executeUpdate();
				totalPonentes.incrementAndGet();
				System.out.println(ps.toString());
				msg = "<div style='color: green; font-weight: bold;'>Ponente añadido correctamente</div>";
			} else
				msg = "<div style='color: red; font-weight: bold;'>ERROR: Existe un ponente con DNI: " + p.getDni() + "</div>";
		} catch (Exception e) {
			msg = "<div style='color: orange; font-weight: bold;'>ERROR (Excepción): " + e.getMessage() + "</div>";
		} finally {
			cerrarTodo();
		}

		return msg;
	}
	
	
	/**
	 * OPERACIÓN opcional 1
	 * Añadir un nuevo miembro del comité de programa pasándolo como
	 * parámetro de formulario
	 *
	 * @param dni	DNI/Pasaporte del nuevo Ponente
	 * @param nom	Nombre del nuevo Ponente
	 * @param ape	Apellidos del nuevo Ponente
	 * @param afi	Afiliación del nuevo Ponente
	 * @param pais	País del nuevo Ponente
	 * @return		Mensaje de error o éxito al añadir al nuevo ponente
	 */
	@POST
	@Path("/nuevoPonenteForm")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Da de alta un nuevo ponente (usando un formulario html)",
		notes = "Crea un nuevo ponente. No debe existir ponente con un dni igual"
	)
	public String createDoctorForm(
			@ApiParam(value = "DNI del nuevo ponente", required = true)
			@FormParam("dni") String dni,
			@ApiParam(value = "Nombre del nuevo ponente", required = false)
			@FormParam("nom") String nom, 
			@ApiParam(value = "Apellidos del nuevo ponente", required = false)
			@FormParam("ape") String ape,
			@ApiParam(value = "Afiliación del nuevo ponente", required = false)
			@FormParam("afi") String afi, 
			@ApiParam(value = "País del nuevo ponente", required = false)
			@FormParam("pais") String pais) {
		String msg;

		try {
			conexion = MySQLConnect();
			consulta = "SELECT COUNT(*) FROM ponentes WHERE dni = ?";
			ps = conexion.prepareStatement(consulta);
			ps.setString(1, dni);
			registro = ps.executeQuery();
			registro.next();
			if(dni == null || dni.isEmpty()) {
				msg = "<div style='color: red; font-weight: bold;'>Tienes que indicar un DNI</div>";
			} else if(!dni.matches("\\d+")) { 
				msg = "<div style='color: red; font-weight: bold;'>Tienes que introducir un número para el DNI</div>";
			}else if(registro.getInt(1) == 0) {
				consulta = "INSERT INTO ponentes (dni, nombre, apellidos, afiliacion, pais) ";
				consulta += "VALUES (?, ?, ?, ?, ?)";
				ps = conexion.prepareStatement(consulta);
				ps.setString(1, dni);
				ps.setString(2, nom);
				ps.setString(3, ape);
				ps.setString(4, afi);
				ps.setString(5, pais);
				ps.executeUpdate();
				totalPonentes.incrementAndGet();
				System.out.println(ps.toString());
				msg = "<div style='color: green; font-weight: bold;'>Ponente añadido correctamente</div>";
			} else
				msg = "<div style='color: red; font-weight: bold;'>ERROR: Ya existe un ponente con DNI: " + dni + "</div>";
		} catch (Exception e) {
			msg = "<div style='color: orange; font-weight: bold;'>ERROR (Excepción): " + e.getMessage() + "</div>";
		} finally {
			cerrarTodo();
		}

		return msg;
	}
	
	
	/**
	 * OPERACIÓN opcional 3
	 * Devuelve el nº total de ponentes.
	 * 
	 * @return	nº de ponentes
	 */
	@GET
	@Path("/contarPonentes2")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "Cuenta el total de ponentes en el servicio",
		notes = "Devuelve el total de registros de la tabla ponentes de la BBDD"
	)
	public String countDoctors() {
		return totalPonentes.toString();
	}
	
	
	/**
	 * Devuelve el total de registros contenidos en la tabla ponentes.
	 * 
	 * @return	nº filas de la tabla ponentes. -1 si error.
	 */
	private static int total() {
		Connection con = null;
		Statement sta = null;
		ResultSet res = null;
		int t = -1;
		
		try {
			con = MySQLConnect();
			String consulta = "SELECT COUNT(*) FROM ponentes";
			sta = con.createStatement();
			res = sta.executeQuery(consulta);
			res.next();
			t = res.getInt(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (res != null) {
					res.close();
				}

				if (sta != null) {
					sta.close();
				}

				if (con != null) {
					con.close();
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
		
		return t;
	}
	
	
	/**
	 * Cierra resultset, conexión, etc...
	 */
	private void cerrarTodo() {
		try {
			if (registro != null) {
				registro.close();
			}

			if (ps != null) {
				ps.close();
			}
			
			if (comando != null) {
				comando.close();
			}

			if (conexion != null) {
				conexion.close();
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
}