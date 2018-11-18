# Documentación del proyecto

## 1-Resúmen

Esta aplicación se trata de mi proyecto de la asignatura de Cloud Computing: Fundamentos e Infraestructuras.

Se destina a desplegar en la nube infraestructura virtual de apoyo a una compañia ferroviária fictícia que gestionará una red de ferrocarriles suburbanos centrados en Granada, España. Servirá tanto a clientes de la compañia como a todos sus empleados.

Los clientes podrán acceder a la aplicación para consultar horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Además, podrán comprar viajes y gestionar sus reservas, e. g. cambiar su asiento o cancelarlas.

Los empleados podrán adicionalmente gestionar viajes, e. g. añadir nuevas o cambiar su horario, y información disponible al publico; así como gestionar y monitorizar equipo ferroviário como sinalización y cámaras de seguridad.

La aplicación cuentará con un servicio de autenticación disponible a todos los usuarios que atribui a cada uno los permisos adecuados, asegurando que clientes y empleados con diferentes funciones acceden apenas a las funcionalidades devidas de la aplicación.

Este proyecto no está relacionado con ningún otro, sea de TFG, TFM o de una empresa. Se destina únicamente a la asignatura de Cloud Computing e seré yo su cliente.

Está distribuido con la licencia MIT.

## 2-Arquitectura y características

<img src="Diagrama.png" alt="https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Diagrama.png">

La aplicación está siendo desarrollada usando la arquitectura basada em microservicios. Esta arquitectura tiene grande popularidad hoy en día, una vez que comparativamente a la arquitectura monolítica trae un aumento en la eficiencia; una mayor facilidad de escalabilidad y de actualización de cada microservicio en separado; así como la posibilidad de usar más que un lenguaje o framework en la aplicación.

Tendrá 4 microservicios principales: Información al Cliente; Gestión de Viajes; Autenticación; y Gestión de Equipos. Casi todos serán desarrollados usando el lenguaje Java y el microframework Javalin, e tendrán una base de datos MongoDB. La excepción será el microservicio de Gestión de Equipos, lo cual será desarrollado usando el lenguaje Ruby y el microframework Sinatra, además contará con una base de datos Neo4J una vez que tendrá datos en grafos.

Existirán además um broker o servicio de mensajería, lo cual será el RabbitMQ por su grande eficiencia; y una API Gateway para dar servicio a todos los clientes de la aplicación, la cual será desarrollada usando Node.js.

Los 4 microservicios principales contarán con una REST API. Recibirán y respondrán a pedidos de la API Gateway. Adicionalmente, el microservicio de autenticación recibirá y respondrá a pedidos de los otros 3 microservicios y de la API Gateway. Los microservicios de Información al Cliente y de Gestión de Viajes tendrán también una conexión HTTP, dada la forte relación entre los datos de los dos microservicios.

Los microservicios de Información al Cliente, Gestión de Viajes y Gestión de Equipos escribirán y leerán mensajes del broker.

Al terminar el Hito 2, se encuentra implementado el microservicio de Información al Cliente, que de momento permite la escrita, lectura y remoción directa de viajes y noticias almacenadas en arrays. No se ha implementado su conexión a la base de datos MongoDB.

### 2.1-Información al Cliente

Permitirá a clientes acceder a horarios de trenes, precios de viajes y noticias de ámbito ferroviário. Empleados con los permisos adecuados podrán editar dicha información. Cambios en horarios y precios serán comunicados via HTTP con el microservicio de Gestión de Viajes con el fin de mantener la consistencia; dichos cambios podrán ser recusados por el microservicio, lo que llevará a su cancelamiento.

Su funcionalidad básica se ha implementado en el Hito 2.

### 2.2-Autenticación

Permitirá tanto a clientes como a empleados crear cuenta y autenticarse en el sistema. El microservicio garantizará los correctos permisos de cada usuario del sistema, de modo a que cada uno sólo tenga accesso a las funciones adecuadas del sistema. Empleados con los permisos correctos podrán atribuir y cambiar permisos a otros usuarios.

### 2.3-Gestión de Viajes

Permitirá a clientes comprar viajes y gestionar reservas, por ejemplo cambiando el asiento o cancelandolas. Empleados con los permisos adecuados podrán comprar y gestionar viajes en nombre de clientes. Cambios en horarios y precios en el microservicio de Información al Cliente serán comunicados con este microservicio por HTTP; este podrá recusarlos e comunicar eso, o aceptarlos y cambiar también su base de datos.

### 2.4-Gestión de equipos

El único de los 4 microservicios sin acceso por parte de los clientes. Empleados con los permisos adecuados podrán gestionar y monitorizar equipos ferroviários en parte o toda la red ferroviária de la compañia; e. g. recibir información de ubicación de trenes en una línea; mover un cambio de agujas o cambiar el color de la sinalización.

## 3-Motivo

Se pretende en esta asignatura un proyecto libre basado en la nube, donde se valorará la infraestructura más que el número de líneas de código. Así, elegi este proyecto tanto por mi gusto por los trenes, como por se tratar de un proyecto viable para la asignatura: la virtualización de la infraestructura informática de una empresa de ferrocarril, necesariamente compleja e sirviendo a miles sino a millones de personas.

## 4-Hitos

Tratándose de un proyecto individual destinado exclusivamente a la asignatura, he considerado que los diferentes hitos de la asignatura se adecuaban a mi proyecto y que por tanto no era necesario cambiarlos. Son hasta ahora 8 en total, siendo este documento el punto principal del Hito 1.

Hito 0 - Uso correcto de Git y GitHub (https://github.com/migueldgoncalves/CCproj_1819/milestone/1)

Hito 1 - Definición del proyecto (https://github.com/migueldgoncalves/CCproj_1819/milestone/2)

Hito 2 - Creación de un microservicio y despliegue en PaaS (https://github.com/migueldgoncalves/CCproj_1819/milestone/8)

Hito 3 - Provisionamiento de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/3)

Hito 4 - Automatización de la creación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/4)

Hito 5 - Orquestación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/5)

Hito 6 - Contenedores para despliegue en la nube (https://github.com/migueldgoncalves/CCproj_1819/milestone/6)

Hito 7 - Composición de servicios (https://github.com/migueldgoncalves/CCproj_1819/milestone/7)
