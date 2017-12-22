package pnetSW;

import java.util.HashMap;
import java.util.Map;

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
 * @author      Andrés Martínez
 * @author      Antonio Ruiz
 * @version     1.0
 * @since       1.0
 * @urlSwagger  http://localhost:8080/ServicioWeb/swagger.json
 */
@Path("/oftalmologia")
@Api(value = "/oftalmologia")
public class Servicio {

  /**
   * Objeto estático del servicio donde almacenar la colección de
   * ponentes del congreso para su consulta, actualización, etc.
   *
   * Este objeto es un Map, cuya clave será el DNI del ponente y el
   * valor es el Ponente que tiene ese dni.
   */
  private static Map<String, Ponente> bbdd = new HashMap<>();

  /**
   * Poblamos la colección inicialmente con un par de ponentes
   * oftalmólogos.
   */
  static {
    Ponente p1 = new Ponente("1", "Andrés", "Martínez Gavira",
                "Hospital Puerta del Mar", "España");
    Ponente p2 = new Ponente("2", "Antonio", "Ruiz Rondán",
                "Hospital Virgen del Rocio", "España");
    Ponente p3 = new Ponente("3", "Antonio", "Banderas Malageño",
                "Málaga", "España");
    bbdd.put(p1.getDni(), p1);
    bbdd.put(p2.getDni(), p2);
    bbdd.put(p3.getDni(), p3);
  }


  /**
   * OPERACIÓN 1: Obtener en JSON todos los miembros del comité de
   * programa (DNI, nombre, apellidos, afiliación, pais. Ej:
   *  1, Andrés,  Martínez Gavira, Hospital Puerta del Mar, España)
   *
   * @return  Mapa (diccionario), cuya clave será el DNI del ponente y
   *          el valor es el Ponente que tiene ese dni.
   */
  @GET
  @Path("/todosPonentes")
  @Produces({"application/json"})
  @ApiOperation(
    value = "Obtiene todos los ponentes",
    notes = "Duevuelve un Map con todos los ponentes"
  )
  public Map<String, Ponente> readAllDoctors() {
    return bbdd;
  }


  /**
   * OPERACIÓN 2: Obtener en modo texto los datos de un miembro
   * concreto del comité de programa. El miembro a buscar se podrá
   * especificar con su nombre (DNI) como parámetro del path.
   *
   * @param key  Entero que representa el dni del ponente a obtener
   * @return     Mensaje de error o éxito al obtener a un ponente
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
      if(bbdd.containsKey(key)) {
        msg = bbdd.get(key).toString();
      } else
        msg = "ERROR: No existe un ponente con DNI: " + key;
    } catch (Exception e) {
      msg = "ERROR (Excepción): " + e.getMessage();
    }

    return msg;
  }


  /**
   * OPERACIÓN 3: Actualizar la afiliación y nacionalidad de afiliación
   * de un miembro (pasando como parámetro del path el nombre (DNI) y
   * los nuevos datos como JSON)
   *
   * @param key  Dni del ponente a actualizar
   * @param p    Ponente serializado desde JSON a Ponente gracias a
   *             la notación XmlRootElement sobre la clase Ponente
   * @return     Mensaje de error o éxito al actualizar a un ponente
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
      if(bbdd.containsKey(key)) {
        Ponente actP = bbdd.get(key);
        if(p.getNombre() != null && !p.getNombre().isEmpty())
          actP.setNombre(p.getNombre());
        if(p.getApellidos() != null && !p.getApellidos().isEmpty())
          actP.setApellidos(p.getApellidos());
        if(p.getAfiliacion() != null && !p.getAfiliacion().isEmpty())
          actP.setAfiliacion(p.getAfiliacion());
        if(p.getPais() != null && !p.getPais().isEmpty())
          actP.setPais(p.getPais());
        bbdd.put(key, actP);
        msg = "Ponente actualizado correctamente";
      } else
        msg = "ERROR: No existe un ponente con DNI: " + key;
    } catch (Exception e) {
      msg = "ERROR (Excepción): " + e.getMessage();
    }

    return msg;
  }


  /**
   * OPERACIÓN 4: Borrar un miembro del comité de programa (pasando su
   * nombre (DNI) como parámetro del path)
   *
   * @param key  Entero que representa el dni del ponente a eliminar
   * @return     Mensaje de error o éxito al eliminar a un ponente
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
      if(bbdd.containsKey(key)) {
        bbdd.remove(key);
        msg = "Eliminado correctamente ponente con DNI: " + key;
      } else
        msg = "ERROR: No existe un ponente con DNI: " + key;
    } catch (Exception e) {
      msg = "ERROR (Excepción): " + e.getMessage();
    }

    return msg;
  }


  /**
   * OPERACIÓN 5: Añadir un nuevo miembro del comité de programa
   * pasándolo como JSON
   *
   * @param p  Ponente serializado desde JSON a Ponente gracias a la
   *           notación XmlRootElement indicada sobre la clase Ponente
   * @return   Mensaje de error o éxito al añadir al nuevo ponente
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
      if(p.getDni() == null || p.getDni().isEmpty()) {
        msg = "Tienes que indicar un DNI";
      } else if(!p.getDni().matches("\\d+")) {
        msg = "Tienes que introducir un número para el DNI";
      } else if(!bbdd.containsKey(p.getDni())) {
        bbdd.put(p.getDni(), p);
        msg = "Ponente añadido correctamente";
      } else
        msg = "ERROR: Existe un ponente con DNI: " + p.getDni();
    } catch (Exception e) {
      msg = "ERROR (Excepción): " + e.getMessage();
    }

    return msg;
  }


  /**
   * OPERACIÓN opcional 1
   * Añadir un nuevo miembro del comité de programa pasándolo como
   * parámetro de formulario
   *
   * @param dni   DNI/Pasaporte del nuevo Ponente
   * @param nom   Nombre del nuevo Ponente
   * @param ape   Apellidos del nuevo Ponente
   * @param afi   Afiliación del nuevo Ponente
   * @param pais  País del nuevo Ponente
   * @return      Mensaje de error o éxito al añadir al nuevo ponente
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
      if(dni == null || dni.isEmpty()) {
        msg = "Tienes que indicar un DNI";
      } else if(!dni.matches("\\d+")) {
        msg = "Tienes que introducir un número para el DNI";
      }else if(!bbdd.containsKey(dni)) {
        Ponente p = new Ponente(dni, nom, ape, afi, pais);
        bbdd.put(dni, p);
        msg = "Ponente añadido correctamente";
      } else
        msg = "ERROR: Ya existe un ponente con DNI: " + dni;
    } catch (Exception e) {
      msg = "ERROR (Excepción): " + e.getMessage();
    }

    return msg;
  }


  /**
   * OPERACIÓN opcional 3
   * Devuelve el nº de registros de la estructura Map.
   *
   * @return  nº de ponentes
   */
  @GET
  @Path("/contarPonentes1")
  @Produces(MediaType.TEXT_PLAIN)
  @ApiOperation(
    value = "Cuenta el total de ponentes en el servicio",
    notes = "Devuelve el total de elementos del Map"
  )
  public int countDoctors() {
    return bbdd.size();
  }

}
