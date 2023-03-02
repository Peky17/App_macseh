<?php

/* Hacemos referencia al documento con la conexion */
 require "conexion.php";
    
 /* Si el dato que llega no esta vacio */   
 if(isset($_GET["id"]))
 {
 	/* Almacenamos el dato entrante */
 	$Id_Usuario = $_GET['id'];
 	/* Realizamos la consulta */
    $sql = "SELECT nombre_lugar, dispositivo, serie_dispositivo FROM dispositivos WHERE id_cliente= '{$Id_Usuario}' AND dispositivo ='Enchufe inteligente premium'";
    /* Ejecutamos esa consulta */
    $query = $mysqli->query($sql);
    /* Creamos un arreglo */
    $datos = array();
    /* Mientras este obteniendo registros */
     while($resultado = $query->fetch_assoc())
      {
		 /* Almaceno los registros en un arreglo */      	
         $datos[] = $resultado;
      }
    
     /* Encapsulamos en un JSON array todos los registros obtenidos */
     echo json_encode(array("Enchufes" => $datos));
}
else
{
  echo "No se ha obtenido un Id valido";	
}

?>
