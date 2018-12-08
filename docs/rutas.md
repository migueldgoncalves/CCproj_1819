# Rutas

La ruta raíz de la aplicación retorna el JSON `{"status":"OK"}`. Llamadas a la ruta `/viajes/1` retornan el JSON `{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}`.

```{
   "status": "OK",
   "ejemplo": { "ruta": "/viajes/1",
                "valor": "{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}"
              }
}
```

La aplicación tiene también las rutas `/viajes/2` y `/viajes/3`, que retornan un JSON similar. La ruta `/viajes` retorna un JSON conteniendo esos 3 JSONs.

Existen también las rutas `/noticias/1`, `/noticias/2` y `/noticias/3`, que retornan cada uno un JSON con una string. La ruta `/noticias` retorna un JSON con esas 3 strings.
