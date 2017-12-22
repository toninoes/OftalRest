function validarFormulario() {

  patron = /^([a-zA-ZáéíóúAÉÍÓÚÑñ]+)$/i;
  var nombre = document.getElementById("nombre").value;
  var apellidos = document.getElementById("apellidos").value;
  var pago = document.getElementById("pago").selectedIndex;
  var grupo = document.getElementById("grupo").selectedIndex;
  var fini = new Date(document.getElementById("llegada").value);
  var ffin = new Date(document.getElementById("salida").value);
  var eini = new Date("2017-10-27");
  var efin = new Date("2017-10-29");

  var respuesta = true;
  document.getElementById("errornombre").innerHTML = '';
  document.getElementById("errorapellidos").innerHTML = '';
  document.getElementById("errorpago").innerHTML = '';
  document.getElementById("errorgrupo").innerHTML = '';
  document.getElementById("errorllegada").innerHTML = '';
  document.getElementById("errorsalida").innerHTML = '';

  if (!nombre.match(patron)) {
    document.getElementById("errornombre").innerHTML = "El NOMBRE sólo puede contener caracteres alfabéticos";
    respuesta = false;
  }

  if (!apellidos.match(patron)) {
    document.getElementById("errorapellidos").innerHTML = "Los APELLIDOS sólo pueden contener caracteres alfabéticos";
    respuesta = false;
  }

  if (pago == null || pago == 0) {
    document.getElementById("errorpago").innerHTML = "Debes seleccionar una forma de PAGO";
    respuesta = false;
  }

  if (grupo == null || grupo == 0) {
    document.getElementById("errorgrupo").innerHTML = "Debes seleccionar un GRUPO";
    respuesta = false;
  }

  if (isNaN(fini.getDate())) {
    document.getElementById("errorllegada").innerHTML = "Fecha de LLEGADA no es una fecha válida";
    respuesta = false;
  }else if (!((fini >= eini) && (fini <= efin))) {
    document.getElementById("errorllegada").innerHTML = "Fecha de LLEGADA debe estar comprendida entre las fechas del evento";
    respuesta = false;
  }

  if (isNaN(ffin.getDate())) {
    document.getElementById("errorsalida").innerHTML = "Fecha de SALIDA no es una fecha válida";
    respuesta = false;
  } else if (!((ffin >= eini) && (ffin <= efin))) {
    document.getElementById("errorsalida").innerHTML = "Fecha de SALIDA debe estar comprendida entre las fechas del evento";
    respuesta = false;
  }

  if (!isNaN(fini.getDate()) && !isNaN(ffin.getDate()) && fini > ffin) {
    document.getElementById("errorllegada").innerHTML = "La Fecha de LLEGADA es posterior a la de SALIDA";
    respuesta = false;
  }

  if(respuesta)
    alert("VALIDACION CORRECTA");

  return respuesta;
}
