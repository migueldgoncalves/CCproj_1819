# Rutas

La ruta raíz de los dos microservicios retorna el JSON `{"status":"OK"}`. Llamadas a la ruta `/viajes/1` del microservicio de Información al Cliente retornan el JSON `{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}`.

```{
   "status": "OK",
   "ejemplo": { "ruta": "/viajes/1",
                "valor": "{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}"
              }
}
```

Llamadas a la ruta `/viajes/1` del microservicio de Gestión de Viajes retornan el número 0.

Los dos microservicios tienen también las rutas `/viajes/2` y `/viajes/3`, que retornan resultados similares a los de arriba. La ruta `/viajes` retorna un JSON conteniendo esos JSONs en el caso del microservicio de Información al Cliente, y un array de ceros en el caso del microservicio de Gestión de Viajes.

El microservicio de Información al Cliente tiene también las rutas `/noticias/1`, `/noticias/2` y `/noticias/3`, que retornan cada una un JSON con una string. La ruta `/noticias` retorna un JSON con esas 3 strings. Este microservicio tiene también la ruta `/numero`, que retorna el número 5.

El microservicio de Gestión de Viajes tiene también la ruta `/viajes/<numero>/disponible`, que retorna `true` independientemente del número puesto.