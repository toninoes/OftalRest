/*
 * @fileoverview  Invocaciones a los servicios web REST implementados en
 *                Servicio.java y Servicio2.java
 *
 * @version       1.1
 *
 * @author        Andrés Martínez
 * @author        Antonio Ruiz
 * @copyright     Antonio&Andrés
 *
 * History
 *
 * v1.1  -  Se mejoró pudiendo consultar a diferentes servicios que implementan
 *          las mismas operaciones.
 *
 * La primera versión se hizo solamente para Servicio.java, el cual mantenía
 * una estructura estática en memoria de los datos (con un Map).
 */


//Variables globales para construir la url
var protocolo = window.location.protocol + "//";
var host = window.location.host; //en producción indicar IP pública o dominio
var servicio = "/ServicioWeb/";
var url = protocolo + host + servicio;


/**
 * Solicita a los servicios web implementados que le envíe todos los ponentes que
 * actualmente almacenan. Estos datos los recibe del servicio en formato json.
 *
 * @param {String} u  Representa el Servicio al que queremos invocar, es decir
 *                    "oftalmologia":  Servicio.java;
 *                    "oftalmologia2": Servicio2.java
 * @returns {String}  Devuelve una cadena de texto en formato html conteniendo dichos
 *                    datos
 */
function todosPonentes(u) {
  $.ajax({
    type: "GET",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/todosPonentes",
    //url: "http://localhost:8080/ServicioWeb/" +u+ "/todosPonentes",
    // mejor lo de abajo, así puede accederse desde otros equipos, no solo localhost.
    url: url + u + "/todosPonentes",
    dataType: "json",
    cache: false,
    success: function(data) {
      var html = "<table><tr><th>DNI</th><th>Nombre</th><th>Apellidos</th>";
      html += "<th>Afiliación</th><th>País</th></tr>";
      $.each(data, function(k, v) {
        html += "<tr>";
        html += "<td>" + v.dni + "</td>" + "<td>" + v.nombre + "</td>";
        html += "<td>" + v.apellidos + "</td>" + "<td>" + v.afiliacion + "</td>";
        html += "<td>" + v.pais + "</td>";
        html += "</tr>";
      });
      html += "</table>";
      $("#consola").html(html);
    },
    error:function(res) {
      $("#consola").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita a los servicios web implementados que le envíe un ponente concreto,
 * especificado por su dni (#dni2). Este dato lo recibe del servicio en formato texto.
 *
 * @param {String} u  Representa el Servicio al que queremos invocar, es decir
 *                    "oftalmologia":  Servicio.java;
 *                    "oftalmologia2": Servicio2.java
 * @returns {String}  Devuelve una cadena de texto en formato html conteniendo dicho
 *                    dato
 */
function obtenerPonente(u) {
  var dni = $("#dni2").val();
  $.ajax({
    type: "GET",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/obtenerPonente/" + dni,
    //url: "http://localhost:8080/ServicioWeb/" +u+ "/obtenerPonente/" + dni,
    url: url + u + "/obtenerPonente/" + dni,
    dataType: "text",
    cache: false,
    success: function(data) {
      var html = "<pre>" + data + "</pre>";
      $("#consola").html(html);
    },
    error:function(res) {
      $("#consola").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita a los servicios web implementados que actualice un ponente, especificado
 * por su dni (#dni3). Este dato lo envía al servicio en formato json y recibe una
 * confirmación exitosa o no en formato texto.
 *
 * @param {String} u  Representa el Servicio al que queremos invocar, es decir
 *                    "oftalmologia":  Servicio.java;
 *                    "oftalmologia2": Servicio2.java
 * @returns {String}  Devuelve una cadena de texto de confirmación
 */
function modificarPonente(u) {
  var dni = $("#dni3").val();
  var nom = $("#nom3").val();
  var ape = $("#ape3").val();
  var afi = $("#afi3").val();
  var pais = $("#pais3").val();
  $.ajax({
    type: "PUT",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/modificarPonente/" + dni,
    //url: "http://localhost:8080/ServicioWeb/" +u+ "/modificarPonente/" + dni,
    url: url + u + "/modificarPonente/" + dni,
    contentType: "application/json",
    dataType: "text",
    cache: false,
    data: JSON.stringify({
      "dni": dni, "nombre": nom, "apellidos": ape,
      "afiliacion": afi, "pais": pais
    }),
    success: function(data) {
      $("#consola").html(data);
    },
    error:function(res) {
      $("#consola").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita a los servicios web implementados que elimine un ponente concreto, especificado
 * por su dni (#dni4). Recibe una confirmación exitosa o no en formato texto.
 *
 * @param {String} u  Representa el Servicio al que queremos invocar, es decir
 *                    "oftalmologia":  Servicio.java;
 *                    "oftalmologia2": Servicio2.java
 * @returns {String}  Devuelve una cadena de texto de confirmación
 */
function eliminarPonente(u) {
  var dni = $("#dni4").val();
  $.ajax({
    type: "DELETE",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/eliminarPonente/" + dni,
    //url: "http://localhost:8080/ServicioWeb/" +u+ "/eliminarPonente/" + dni,
    url: url + u + "/eliminarPonente/" + dni,
    dataType: "text",
    cache: false,
    success: function(data) {
      $("#consola").html(data);
    },
    error:function(res) {
      $("#consola").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita a los servicios web implementados que añada un ponente, especificando
 * todos sus campos. Este dato lo envía al servicio en formato json y recibe una
 * confirmación exitosa o no en formato texto.
 *
 * @param {String} u  Representa el Servicio al que queremos invocar, es decir
 *                    "oftalmologia":  Servicio.java;
 *                    "oftalmologia2": Servicio2.java
 * @returns {String}  Devuelve una cadena de texto de confirmación
 */
function nuevoPonente(u) {
  var dni = $("#dni5").val();
  var nom = $("#nom5").val();
  var ape = $("#ape5").val();
  var afi = $("#afi5").val();
  var pais = $("#pais5").val();
  $.ajax({
    type: "POST",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/nuevoPonente",
    //url: "http://localhost:8080/ServicioWeb/" +u+ "/nuevoPonente",
    url: url + u + "/nuevoPonente",
    contentType: "application/json",
    dataType: "text",
    cache: false,
    data: JSON.stringify({
      "dni": dni, "nombre": nom, "apellidos": ape,
      "afiliacion": afi, "pais": pais
    }),
    success: function(data) {
      $("#consola").html(data);
    },
    error:function(res) {
      $("#consola").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita a los servicios web implementados que añada un ponente, especificando
 * todos sus campos mediante el identificador de un formulario html. Recibe una
 * confirmación exitosa o no en formato texto.
 *
 * @returns {String}  Devuelve una cadena de texto de confirmación
 */
function nuevoPonenteForm() {
  var frm = $('#idForm');
  $.ajax({
    dataType: "text",
    type: frm.attr('method'),
    url: frm.attr('action'),
    data: frm.serialize(),
    cache: false,
    success: function(data) {
      $("#consolaForm").html(data);
    },
    error:function(res) {
      $("#consolaFormError").html("ERROR: " + res.statusText);
    }
  });
}


/**
 * Solicita al servicio web Servicio que le envíe el número actual de
 * ponentes que almacena.
 *
 * @returns {String}  Devuelve una cadena que representa dicho número
 */
function contarPonentes1() {
  $.ajax({
    type: "GET",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia/contarPonentes1",
    url: url + "oftalmologia/contarPonentes1",
    cache: false,
    success: function(data) {
      $("#numPonentes").html(data);
    }
  });
}


/**
 * Solicita al servicio web Servicio2 que le envíe el número actual de
 * ponentes que almacena.
 *
 * @returns {String}  Devuelve una cadena que representa dicho número
 */
function contarPonentes2() {
  $.ajax({
    type: "GET",
    //url: "http://localhost:8080/ServicioWeb/oftalmologia2/contarPonentes2",
    url: url + "oftalmologia2/contarPonentes2",
    cache: false,
    success: function(data) {
      $("#numPonentes").html(data);
    }
  });
}
