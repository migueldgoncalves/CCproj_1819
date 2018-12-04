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

<img src="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png" alt="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png">

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

## 3-Enlaces de interés relacionados con el hito actual



## 4-Enlaces de interés relacionados con los hitos anteriores

[Configuración del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_configuracion.md)

[Elección del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_eleccion.md)

[Creación de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/creacion_aplicacion.md)

[Configuración del despliegue](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/despliegue_PaaS.md)

[Funcionamiento de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/funcionamiento.md)

[Hitos del proyecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/hitos.md)

[Motivo de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/motivo.md)

[Rutas](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/rutas.md)
