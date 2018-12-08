# Arquitectura y características

<img src="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png" alt="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png">

La aplicación está siendo desarrollada usando la arquitectura basada em microservicios. Esta arquitectura tiene grande popularidad hoy en día, una vez que comparativamente a la arquitectura monolítica trae un aumento en la eficiencia; una mayor facilidad de escalabilidad y de actualización de cada microservicio en separado; así como la posibilidad de usar más que un lenguaje o framework en la aplicación.

Tendrá 4 microservicios principales: Información al Cliente; Gestión de Viajes; Autenticación; y Gestión de Equipos. Casi todos serán desarrollados usando el lenguaje Java y el microframework Javalin, e tendrán una base de datos MongoDB. La excepción será el microservicio de Gestión de Equipos, el cual será desarrollado usando el lenguaje Ruby y el microframework Sinatra, además contará con una base de datos Neo4J una vez que tendrá datos en grafos.

Existirán además um broker o servicio de mensajería, el cual será el RabbitMQ por su grande eficiencia; y una API Gateway para dar servicio a todos los clientes de la aplicación, la cual será desarrollada usando Node.js.

Los 4 microservicios principales contarán con una REST API. Recibirán y respondrán a pedidos de la API Gateway. Adicionalmente, el microservicio de autenticación recibirá y respondrá a pedidos de los otros 3 microservicios y de la API Gateway. Los microservicios de Información al Cliente y de Gestión de Viajes tendrán también una conexión HTTP, dada la forte relación entre los datos de los dos microservicios.

Los microservicios de Información al Cliente, Gestión de Viajes y Gestión de Equipos escribirán y leerán mensajes del broker.

Al terminar el Hito 3, se encuentra implementada la funcionalidad básica del microservicio de Información al Cliente, que de momento permite la escrita, lectura y remoción directa de viajes y noticias almacenadas en arrays. No se ha implementado su conexión a la base de datos MongoDB, aunque el playbook de Ansible creado inclua ya la instalación y arranque de MongoDB.

## Información al Cliente

Permitirá a clientes acceder a horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Empleados con los permisos adecuados podrán editar dicha información. Cambios en horarios y precios serán comunicados via HTTP con el microservicio de Gestión de Viajes con el fin de mantener la consistencia; dichos cambios podrán ser recusados por el microservicio, lo que llevará a su cancelamiento.

Su funcionalidad básica se ha implementado en el Hito 2. El microservicio no ha sido alterado en el Hito 3.

## Autenticación

Permitirá tanto a clientes como a empleados crear cuenta y autenticarse en el sistema. El microservicio garantizará los correctos permisos de cada usuario del sistema, de modo a que cada uno sólo tenga accesso a las funciones adecuadas del sistema. Empleados con los permisos correctos podrán atribuir y cambiar permisos a otros usuarios.

## Gestión de Viajes

Permitirá a clientes comprar viajes y gestionar reservas, por ejemplo cambiando el asiento o cancelandolas. Empleados con los permisos adecuados podrán comprar y gestionar viajes en nombre de clientes. Cambios en horarios y precios en el microservicio de Información al Cliente serán comunicados con este microservicio por HTTP; este podrá recusarlos e comunicar eso, o aceptarlos y cambiar también su base de datos.

## Gestión de Equipos

El único de los 4 microservicios sin acceso por parte de los clientes. Empleados con los permisos adecuados podrán gestionar y monitorizar equipos ferroviários en parte o toda la red ferroviária de la compañia; e. g. recibir información de ubicación de trenes en una línea; mover un cambio de agujas o cambiar el color de la sinalización.
