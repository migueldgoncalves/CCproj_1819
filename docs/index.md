# Documentación del proyecto

## 1-Resúmen

Este proyecto consiste en la infraestructura virtual de una compañía ferroviaria ficticia que gestionará una red de líneas que serán construidas en la Sierra Nevada y alrededores. 

La infraestructura dará servicio tanto a usuarios y pasajeros como a empleados e trenes. Los primeros tendrán acceso a precios, horarios, e avisos; compra e gestión de viajes; así como compras a bordo de los trenes y acceso a contenidos mientras en viaje. Los empleados podrán monitorizar trenes, estaciones y la meteorología, además tendrán la posibilidad de trabajar remotamente. Clientes y empleados podrán autenticarse en el sistema de la compañía, que les asegurará los privilegios y accesos adecuados.

## 2-Arquitectura y características

La aplicación será desarrollada usando la arquitectura basada em microservicios. Esta arquitectura tiene grande popularidad hoy en día, una vez que comparativamente a la arquitectura monolítica trae un aumento en la eficiencia; una mayor facilidad de escalabilidad y de actualización de cada microservicio en separado; así como la posibilidad de usar más que un lenguaje o framework en la aplicación.

Será escrita usando el lenguaje Ruby, adecuado a aplicaciones en la nube y que personalmente no conozco, de modo que encaro este proyecto como una oportunidad de aprenderla. Usará también el framework Ruby on Rails, popular para aplicaciones en web y adecuado para una arquitectura basada en microservicios aunque esté enfocado en la arquitectura monolítica.

La aplicación tendrá 6 microservicios que soportarán sus diferentes funcionalidades: Consulta de informaciones; Autenticación; Compras y reservas; Streaming y información a bordo; Monitorización; y un microservicio de mensajería asíncrona que servirá los restantes microservicios. Sus detalles serán descritos abajo.

Este proyecto no está relacionado con ningún otro, sea de TFG, TFM o de una empresa. Se destina únicamente a la asignatura de Cloud Computing e seré yo su cliente.

Está distribuido con la licencia MIT.

### 2.1-Consulta de informaciones

Destinado a los usuarios de la compañía, permitirá la consulta de horarios de los trenes, precios de los viajes, y noticias e avisos de ámbito ferroviario.

### 2.2-Autenticación

Destinado tanto a usuarios como a empleados, gestionará su autenticación en el sistema de la compañía e garantizará los correctos privilegios de cada usuario del sistema.

### 2.3-Compras y reservas

Destinado a usuarios, permitirá comprar y reservar viajes; hacer compras y encomiendas a bordo; e gestionar sus compras e reservas. Este microservicio accederá a las APIs más adecuadas de Visa y PayPal.

### 2.4-Streaming y información a bordo

Destinado a usuarios, permitirá acceder tanto a streaming da RTVE como a información providenciada por la compañía durante un viaje. Para tal, el microservicio accederá a la API más adecuada de RTVE.

### 2.5-Monitorización

Destinado a empleados, permitirá monitorizar parámetros de los trenes como velocidad y ubicación; así como acceder a cámaras de seguridad tanto en estaciones como en trenes; y además monitorizar la meteorología en toda la red de la compañía. Para esto último, el microservicio accederá a la API más adecuada de AccuWeather.

### 2.6-Servicio de mensajería

Se destina a servir los restantes microservicios, recibiendo en una cola mensajes de todos ellos de forma asíncrona e asegurando que los mensajes llegan al microservicio pretendido.

## 3-Motivo

Se pretende en esta asignatura un proyecto libre basado en la nube, donde se valorará la infraestructura más que el número de líneas de código. Así, escogí este proyecto tanto por mi gusto por los trenes, como por se tratar de un proyecto viable para la asignatura: la virtualización de la infraestructura informática de una empresa de ferrocarril, necesariamente compleja e sirviendo a miles sino a millones de personas.

## 4-Hitos

Tratándose de un proyecto individual destinado exclusivamente a la asignatura, he considerado que los diferentes hitos de la asignatura se adecuaban a mi proyecto y que por tanto no era necesario cambiarlos. Son 7 en total, siendo este documento el punto principal del Hito 1.

Hito 0 - Uso correcto de Git y GitHub (https://github.com/migueldgoncalves/CCproj_1819/milestone/1)

Hito 1 - Definición del proyecto (https://github.com/migueldgoncalves/CCproj_1819/milestone/2)

Hito 2 - Provisionamiento de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/3)

Hito 3 - Automatización de la creación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/4)

Hito 4 - Orquestación de máquinas virtuales (https://github.com/migueldgoncalves/CCproj_1819/milestone/5)

Hito 5 - Contenedores para despliegue en la nube (https://github.com/migueldgoncalves/CCproj_1819/milestone/6)

Hito 6 - Composición de servicios (https://github.com/migueldgoncalves/CCproj_1819/milestone/7)
