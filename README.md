# Infraestructura de gestión ferroviária basada en la nube

## 1-Resúmen

Esta aplicación se trata de mi proyecto de la asignatura de Cloud Computing: Fundamentos e Infraestructuras.

Este proyecto se destina a desplegar en la nube infraestructura virtual de apoyo a una compañia ferroviária fictícia que gestionará una red de ferrocarriles suburbanos centrados en Granada, España. Servirá tanto a clientes de la compañia como a todos sus empleados.

Los clientes podrán acceder a la aplicación para consultar horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Además, podrán comprar viajes y gestionar sus reservas, e. g. cambiar su asiento o cancelarlas.

Los empleados podrán adicionalmente gestionar viajes, e. g. añadir nuevas o cambiar su horario, y información disponible al publico; así como gestionar y monitorizar equipo ferroviário como sinalización y cámaras de seguridad.

La aplicación cuentará con un servicio de autenticación disponible a todos los usuarios que atribuirá a cada uno los permisos adecuados, asegurando que clientes y empleados con diferentes funciones acceden solamente a las funcionalidades devidas de la aplicación.

Este proyecto siguirá siendo desarrollado hasta Enero, cuando se prevé su conclusión.

No está relacionado con ningún otro, sea de TFG, TFM o de una empresa. Se destina únicamente a la asignatura de Cloud Computing e seré yo su cliente.

Está distribuido con la licencia MIT.

Despliegue https://javalinapp.herokuapp.com/

[Enlace](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/despliegue.md) a la configuración del despliegue.

[Enlace](https://migueldgoncalves.github.io/CCproj_1819/) a documentación adicional.

### 1.1-Estado actual

En su estado actual, habendose concluído el [Hito 2](https://github.com/migueldgoncalves/CCproj_1819/milestone/8), el proyecto consiste en la funcionalidad básica del microservicio de Información al Cliente implementada y desplegada en la nube.

## 2-Arquitectura y características

<img src="Diagrama.png" alt="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png">

La aplicación está siendo desarrollada usando la arquitectura basada em microservicios. Esta arquitectura tiene grande popularidad hoy en día, una vez que comparativamente a la arquitectura monolítica trae un aumento en la eficiencia; una mayor facilidad de escalabilidad y de actualización de cada microservicio en separado; así como la posibilidad de usar más que un lenguaje o framework en la aplicación.

Tendrá 4 microservicios principales: Información al Cliente; Gestión de Viajes; Autenticación; y Gestión de Equipos. Casi todos serán desarrollados usando el lenguaje Java y el microframework Javalin, e tendrán una base de datos MongoDB. La excepción será el microservicio de Gestión de Equipos, el cual será desarrollado usando el lenguaje Ruby y el microframework Sinatra, además contará con una base de datos Neo4J una vez que tendrá datos en grafos.

Existirán además um broker o servicio de mensajería, el cual será el RabbitMQ por su grande eficiencia; y una API Gateway para dar servicio a todos los clientes de la aplicación, la cual será desarrollada usando Node.js.

Los 4 microservicios principales contarán con una REST API. Recibirán y respondrán a pedidos de la API Gateway. Adicionalmente, el microservicio de autenticación recibirá y respondrá a pedidos de los otros 3 microservicios y de la API Gateway. Los microservicios de Información al Cliente y de Gestión de Viajes tendrán también una conexión HTTP, dada la forte relación entre los datos de los dos microservicios.

Los microservicios de Información al Cliente, Gestión de Viajes y Gestión de Equipos escribirán y leerán mensajes del broker.

Al terminar el Hito 2, se encuentra implementada la funcionalidad básica del microservicio de Información al Cliente, que de momento permite la escrita, lectura y remoción directa de viajes y noticias almacenadas en arrays. No se ha implementado su conexión a la base de datos MongoDB.

### 2.1-Información al Cliente

Permitirá a clientes acceder a horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Empleados con los permisos adecuados podrán editar dicha información. Cambios en horarios y precios serán comunicados via HTTP con el microservicio de Gestión de Viajes con el fin de mantener la consistencia; dichos cambios podrán ser recusados por el microservicio, lo que llevará a su cancelamiento.

Su funcionalidad básica se ha implementado en el Hito 2.

### 2.2-Autenticación

Permitirá tanto a clientes como a empleados crear cuenta y autenticarse en el sistema. El microservicio garantizará los correctos permisos de cada usuario del sistema, de modo a que cada uno sólo tenga accesso a las funciones adecuadas del sistema. Empleados con los permisos correctos podrán atribuir y cambiar permisos a otros usuarios.

### 2.3-Gestión de Viajes

Permitirá a clientes comprar viajes y gestionar reservas, por ejemplo cambiando el asiento o cancelandolas. Empleados con los permisos adecuados podrán comprar y gestionar viajes en nombre de clientes. Cambios en horarios y precios en el microservicio de Información al Cliente serán comunicados con este microservicio por HTTP; este podrá recusarlos e comunicar eso, o aceptarlos y cambiar también su base de datos.

### 2.4-Gestión de equipos

El único de los 4 microservicios sin acceso por parte de los clientes. Empleados con los permisos adecuados podrán gestionar y monitorizar equipos ferroviários en parte o toda la red ferroviária de la compañia; e. g. recibir información de ubicación de trenes en una línea; mover un cambio de agujas o cambiar el color de la sinalización.

## 3-Configuración

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

## 4-Rutas

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

## 5-Elección del PaaS

Existen hoy en día algunas opciones de PaaS disponibles, pagas o gratuitas con recursos limitados. Heroku y OpenShift soportan lenguajes como Java, Node.js, PHP, Python, Ruby y Perl. Las dos son gratuitas con recursos limitados, suficientes para desarrollar la práctica de la asignatura.

Fui presentado a Heroku en la charla [De 0 a Cloud](https://www.meetup.com/es-ES/granadagdg/events/255451617/?rv=wm1&_xtd=gatlbWFpbF9jbGlja9oAJDFhZmQzMjkwLWI0OWYtNGUzYy1hNzdmLTNjNjcwODIwYzYyOA&_af=event&_af_eid=255451617), donde me ha gustado su facilidad tanto de uso como de despliegue de aplicaciones en la cloud. Así, elegi ese PaaS para el hito.

## 6-Funcionamiento de la aplicación

La aplicación es un microservicio basado en REST, que almacena datos referentes a viajes y noticias. Los viajes son almacenados en un data object conteniendo su origen, destino, hora de partida, hora de salida y precio. Las noticias son simplemente strings de caracteres.

Los dos tipos de datos son almacenados en arrays por un data access object, el cual suporta operaciones de lectura, escrita y remoción sobre los mismos. Remoción de elementos no elimina su índice, solo limpia su contenido, dejando intactos los restantes elementos y sus índices.

Una clase de servicio web, creada con recurso a la microframework [Javalin](https://javalin.io/), efectua las operaciones REST GET, POST y DELETE sobre esa clase. Pruebas son efectuadas tanto al data access object como al servicio web, estas ultimas con recurso al cliente HTTP [OkHttp](http://square.github.io/okhttp/).

## 7-Creación de la aplicación

Primeiramente, he leído el contenido presente en la materia de [PaaS](http://jj.github.io/CC/documentos/temas/PaaS) y he realizado algunos de los ejercicios.

En seguida he realizado en Java una pequeña aplicación de prueba similar al del microservicio a implementar, con el objetivo de hacer experimentos con el microframework y buscar la mejor manera de implementar el microservicio.

Después de hacer los ejercicios más relevantes, y teniendo entonces una buena parte del código necesário, empezé a hacer la aplicación de la práctica con base en la aplicación de prueba, creando rapidamente el código de las clases y algunas pruebas. Completé primero las pruebas de las clases concretas, después las pruebas del servicio web, corrigindo cuando necesario el código de las clases.

## 8-Motivo

Se pretende en esta asignatura un proyecto libre basado en la nube, donde se valorará la infraestructura más que el número de líneas de código. Así, elegi este proyecto tanto por mi gusto por los trenes, como por se tratar de un proyecto viable para la asignatura: la virtualización de la infraestructura informática de una empresa de ferrocarril, necesariamente compleja e sirviendo a miles sino a millones de personas.

## 9-Hitos

Tratándose de un proyecto individual destinado exclusivamente a la asignatura, he considerado que los diferentes hitos de la asignatura se adecuaban a mi proyecto y que por tanto no era necesario cambiarlos. Son hasta ahora 8 en total, siendo este documento el punto principal del Hito 1.

Hito 0 - Uso correcto de Git y GitHub (https://github.com/migueldgoncalves/CCproj_1819/milestone/1)

Hito 1 - Definición del proyecto (https://github.com/migueldgoncalves/CCproj_1819/milestone/2)

Hito 2 - Creación de un microservicio y despliegue en PaaS (https://github.com/migueldgoncalves/CCproj_1819/milestone/8)

Hito 3 - Provisionamiento de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/3)

Hito 4 - Automatización de la creación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/4)

Hito 5 - Orquestación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/5)

Hito 6 - Contenedores para despliegue en la nube (https://github.com/migueldgoncalves/CCproj_1819/milestone/6)

Hito 7 - Composición de servicios (https://github.com/migueldgoncalves/CCproj_1819/milestone/7)
