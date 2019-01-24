# Arquitectura y características de la aplicación

<img src="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png" alt="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png">

La aplicación utiliza la arquitectura basada en microservicios. Esta arquitectura tiene gran popularidad hoy en día, una vez que comparativamente a la arquitectura monolítica trae un aumento en la eficiencia; una mayor facilidad de escalabilidad y de actualización de cada microservicio en separado; así como la posibilidad de usar más que un lenguaje o framework en la aplicación.

Tendrá cuando completada 4 microservicios principales: Información al Cliente; Gestión de Viajes; Autenticación; y Gestión de Equipos. Casi todos serán o fueron desarrollados usando el lenguaje Java y el microframework Javalin, y tendrán o tienen una base de datos MongoDB. La excepción es el microservicio de Gestión de Equipos, el cual será desarrollado usando el lenguaje Ruby y el microframework Sinatra, además contará con una base de datos Neo4J una vez que tendrá datos en grafos.

Existirán además un broker o servicio de mensajería, el cual será el RabbitMQ por su grande eficiencia; y una API Gateway para dar servicio a todos los clientes de la aplicación, la cual será desarrollada usando Node.js.

Los 4 microservicios principales contarán o cuentan con una REST API. Recibirán y responderán a pedidos de la API Gateway. Adicionalmente, el microservicio de autenticación recibirá y responderá a pedidos de los otros 3 microservicios y de la API Gateway. Los microservicios de Información al Cliente y de Gestión de Viajes tienen también una conexión HTTP, dada la fuerte relación entre los datos de los dos microservicios.

Los microservicios de Información al Cliente, Gestión de Viajes y Gestión de Equipos escribirán y leerán mensajes del broker.

El funcionamiento en detalle de la aplicación se puede consultar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/funcionamiento.md).

## Información al Cliente

Permite a clientes acceder a horarios de trenes, precios de viajes y noticias de ámbito ferroviario. Empleados con los permisos adecuados podrán editar dicha información. Cambios en horarios y precios son comunicados vía HTTP con el microservicio de Gestión de Viajes con el fin de mantener la consistencia; dichos cambios pueden ser recusados por el microservicio, lo que llevará a su cancelación.

Su funcionalidad básica se ha implementado en el [Hito 2](https://github.com/migueldgoncalves/CCproj_1819/milestone/8). En el [Hito 5](https://github.com/migueldgoncalves/CCproj_1819/milestone/5) se ha añadido la base de datos MongoDB, así como la conexión al microservicio de Gestión de Viajes, creado en ese hito.

## Autenticación

Permitirá tanto a clientes como a empleados crear cuenta y autenticarse en el sistema. El microservicio garantizará los correctos permisos de cada usuario del sistema, de modo a que cada uno sólo tenga acceso a las funciones adecuadas del sistema. Empleados con los permisos correctos podrán atribuir y cambiar permisos a otros usuarios.

## Gestión de Viajes

Permitirá a clientes comprar viajes y gestionar reservas, por ejemplo cambiando el asiento o cancelándolas; en este momento se puede comprar y cancelar viajes. Empleados con los permisos adecuados podrán comprar y gestionar viajes en nombre de clientes. Cambios en horarios y precios en el microservicio de Información al Cliente son comunicados con este microservicio por HTTP; este puede recusarlos y comunicar eso, o aceptarlos y cambiar también su base de datos.

El microservicio y su conexión con el microservicio de Información al Cliente fueron creados en el Hito 5.

## Gestión de Equipos

El único de los 4 microservicios sin acceso por parte de los clientes. Empleados con los permisos adecuados podrán gestionar y monitorizar equipos ferroviarios en parte o toda la red ferroviaria de la compañía; e. g. recibir información de ubicación de trenes en una línea; mover un cambio de agujas o cambiar el color de la señalización.