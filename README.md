# Infraestructura de gestión ferroviária basada en la nube

[Pulse aqui](https://migueldgoncalves.github.io/CCproj_1819/) para acceder a la documentación.

Este proyecto se destina a desplegar en la nube infraestructura virtual de apoyo a una compañia ferroviária fictícia que gestionará una red de ferrocarriles suburbanos centrados en Granada, España. Servirá tanto a clientes de la compañia como a todos sus empleados.

Los clientes podrán acceder a la aplicación para consultar horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Además, podrán comprar viajes y gestionar sus reservas, e. g. cambiar su asiento o cancelarlas.

Los empleados podrán adicionalmente gestionar viajes, e. g. añadir nuevas o cambiar su horario, y información disponible al publico; así como gestionar y monitorizar equipo ferroviário como sinalización y cámaras de seguridad.

La aplicación cuentará con un servicio de autenticación disponible a todos los usuarios que atribuirá a cada uno los permisos adecuados, asegurando que clientes y empleados con diferentes funciones acceden apenas a las funcionalidades devidas de la aplicación.

En su estado actual, habendose concluyendo el [Hito 2](https://github.com/migueldgoncalves/CCproj_1819/milestone/8), el proyecto consiste en el microservicio de Información al Cliente implementado y desplegado en la nube.

Este proyecto siguirá siendo desarrollado hasta Enero, cuando se prevé su conclusión.

## Configuración

Heroku, el PaaS elegido para este hito del proyecto, necesita 2 ficheros de configuración para el despliegue del código que esté en un repositório GitHub, el app.json y el Procfile.

```{
  "name": "proyecto",
  "scripts": {
  },
  "env": {
    "PORT": {
      "required": true
    }
  },
  "formation": {
    "web": {
      "quantity": 1
    }
  },
  "addons": [

  ],
  "buildpacks": [
    {
      "url": "heroku/java"
    }
  ]
}
```

El fichero app.json, presentado arriba, se destina a describir aplicaciones web, es decir, variables de configuración, add-ons y cualquier otra información necesaria para que Heroku pueda correr la aplicación.

Esta tiene una única variable de configuración, `PORT`, que por omissión es requerida en el proyecto con `"required": true`. Se indica que se trata de una aplicación web con `"web"`, y en `"quantity": 1` se indica que haberá 1 processo que correrá la aplicación.

No hay add-ons que aplicar a la aplicación, por lo que no se indican ningunos. Por ultimo se indica el buildpack necesario para hacer la build de la aplicación, con el URL `heroku/java`.

`web: java $JAVA_OPTS -cp target/classes:target/dependency/* CC1819.Main`

El Procfile, presentado arriba, indica los comandos a ejecutar por las dynos de Heroku que correrán la aplicación. El tipo de processo `web` permite recibir tráfico HTTP. En este caso existe un solo comando, el necesario para correr la aplicación.

## Rutas

La ruta raiz de la aplicación retorna el JSON `{"status":"OK"}`. Llamadas a la ruta `/viajes/1` retornan el JSON `{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}`.

```{
   "status": "OK",
   "ejemplo": { "ruta": "/viajes/1",
                "valor": "{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}"
              }
}
```

La aplicación tiene también las rutas `/viajes/2` y `/viajes/3`, que retornan un JSON similar. La ruta `/viajes` retorna un JSON conteniendo esos 3 JSONs.

Existen también las rutas `/noticias/1`, `/noticias/2` y `/noticias/3`, que retornan cada uno un JSON con una string. La ruta `/noticias` retorna un JSON con esas 3 strings.

## Despliegue

Despliegue https://javalinapp.herokuapp.com/

[Enlace](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/despliegue.md) a la configuración del despliegue.

## Elección del PaaS

Existen hoy en día algunas opciones de PaaS disponibles, pagas o gratuitas con recursos limitados. Heroku y OpenShift soportan lenguajes como Java, Node.js, PHP, Python, Ruby y Perl. Las dos son gratuitas con recursos limitados, suficientes para desarrollar la práctica de la asignatura.

Fui presentado a Heroku en la charla [De 0 a Cloud](https://www.meetup.com/es-ES/granadagdg/events/255451617/?rv=wm1&_xtd=gatlbWFpbF9jbGlja9oAJDFhZmQzMjkwLWI0OWYtNGUzYy1hNzdmLTNjNjcwODIwYzYyOA&_af=event&_af_eid=255451617), donde me ha gustado su facilidad tanto de uso como de despliegue de aplicaciones en la cloud. Así, elegi ese PaaS para el hito.

## Funcionamiento de la aplicación

La aplicación es un microservicio basado en REST, que almacena datos referentes a viajes y noticias. Los viajes son almacenados en un data object conteniendo su origen, destino, hora de partida, hora de salida y precio. Las noticias son simplemente strings de caracteres.

Los dos tipos de datos son almacenados en arrays por un data access object, el cual suporta operaciones de lectura, escrita y remoción sobre los mismos. Remoción de elementos no elimina su índice, solo limpia su contenido, dejando intactos los restantes elementos y sus índices.

Una clase de servicio web, creada con recurso a la microframework [Javalin](https://javalin.io/), efectua las operaciones REST GET, POST y DELETE sobre esa clase. Pruebas son efectuadas tanto al data access object como al servicio web, estas ultimas con recurso al cliente HTTP [OkHttp](http://square.github.io/okhttp/).

## Creación de la aplicación

Primeiramente, he leído el contenido presente en la materia de [PaaS](http://jj.github.io/CC/documentos/temas/PaaS) y he realizado algunos de los ejercicios.

En seguida he realizado en Java una pequeña aplicación de prueba similar al del microservicio a implementar, con el objetivo de hacer experimentos con el microframework y buscar la mejor manera de implementar el microservicio.

Después de hacer los ejercicios más relevantes, y teniendo entonces una buena parte del código necesário, empezé a hacer la aplicación de la práctica con base en la aplicación de prueba, creando rapidamente el código de las clases y algunas pruebas. Completé primero las pruebas de las clases concretas, después las pruebas del servicio web, corrigindo cuando necesario el código de las clases.
