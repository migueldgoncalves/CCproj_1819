# Infraestructura de gestión ferroviária basada en la nube

[Pulse aqui](https://migueldgoncalves.github.io/CCproj_1819/) para acceder a la documentación.

Este proyecto se destina a desplegar en la nube infraestructura virtual de apoyo a una compañia ferroviária fictícia que gestionará una red de ferrocarriles suburbanos centrados en Granada, España. Servirá tanto a clientes de la compañia como a todos sus empleados.

Los clientes podrán acceder a la aplicación para consultar horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Además, podrán comprar viajes y gestionar sus reservas, e. g. cambiar su asiento o cancelarlas.

Los empleados podrán adicionalmente gestionar viajes, e. g. añadir nuevas o cambiar su horario, y información disponible al publico; así como gestionar y monitorizar equipo ferroviário como sinalización y cámaras de seguridad.

La aplicación cuentará con un servicio de autenticación disponible a todos los usuarios que atribui a cada uno los permisos adecuados, asegurando que clientes y empleados con diferentes funciones acceden apenas a las funcionalidades devidas de la aplicación.

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

El fichero app.json, presentado arriba, se destina a describir aplicaciones web, es decir, variables de configuración, add-ons y cualquier otra información necesaria para que Heroku pueda correr la aplicación. La aplicación tiene una única variable de configuración, `PORT`, que por omissión es requerida en el proyecto con `"required": true`. Se indica que se trata de una aplicación web con `"web"`, y en `"quantity": 1` se indica que haberá 1 processo que correrá la aplicación. No hay add-ons que aplicar a la aplicación, por lo que no se indican ningunos. Por ultimo se indica el buildpack necesario para hacer la build de la aplicación, con el URL `heroku/java`.

`web: java $JAVA_OPTS -cp target/classes:target/dependency/* CC1819.Main`

El Procfile, presentado arriba, indica los comandos a ejecutar por las dynos que correrán la aplicación. El tipo de processo `web` permite recibir tráfico HTTP. En este caso existe un solo comando, el necesario para correr la aplicación.

## Rutas

La ruta raiz de la aplicación retorna el JSON {"status":"OK"}. Además, llamadas a la ruta "/viajes/1" retornan el JSON {"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}.

```{
   "status": "OK",
   "ejemplo": { "ruta": "/viajes/1",
                "valor": "{"origen":"Granada","destino":"Maracena","partida":"08h04","llegada":"08h10","precio":1.5}"
              }
}
```

## Enlace del despliegue

Despliegue https://javalinapp.herokuapp.com/

## Elección del PaaS

Existen hoy en día algunas opciones de PaaS disponibles, pagas o gratuitas con recursos limitados. De entre ellas, Heroku y OpenShift son las que soportan más lenguagens, incluyendo Java, Node.js, PHP, Python, Ruby y Perl. Las dos son gratuitas con recursos limitados, suficientes para desarrollar la práctica de la asignatura.

Fui apresentado a Heroku en la charla [De 0 a Cloud](https://www.meetup.com/es-ES/granadagdg/events/255451617/?rv=wm1&_xtd=gatlbWFpbF9jbGlja9oAJDFhZmQzMjkwLWI0OWYtNGUzYy1hNzdmLTNjNjcwODIwYzYyOA&_af=event&_af_eid=255451617), donde me ha gustado su facilidad de uso y despliegue en la cloud. Así, elegi ese PaaS para el hito.

## Funcionamiento de la aplicación

La aplicación es un microservicio basado en REST, que almacena datos referentes a viajes y noticias. Los viajes son almacenados en un data object conteniendo su origen, destino, hora de partida, hora de salida y precio. Las noticias son simplemente strings de caracteres. Los dos tipos de datos son almacenados en arrays por un data access object, el cual suporta operaciones de lectura, escrita y remoción sobre los mismos. Una clase de servicio web, creada con recurso a la microframework Javalin, efectua las ordenes REST GET, POST y DELETE sobre esa clase. Pruebas son efectuadas tanto al data access object como al servicio web, estas ultimas con recurso a la biblioteca OkHttp para implementar un cliente HTTP.

## Creación de la aplicación

Primeiramente, he leído el contenido presente en la materia de [PaaS](http://jj.github.io/CC/documentos/temas/PaaS), y he realizado algunos de los ejercicios. He realizado en Java una pequeña aplicación de prueba, con el objetivo de hacer pruebas con el microframework y buscar la mejor manera de implementar el microservicio. Al hacer los ejercicios más relevantes, y teniendo ya una buena parte del código necesário, empezé a hacer la aplicación de la práctica con base en la de la aplicación de prueba, creando rapidamente el código de las clases y algunas pruebas. Sigui haciendo pruebas, en especial las del servicio web, afinando las clases en conformidad, hasta tener toda la funcionalidad de la aplicación implementada y probada.
