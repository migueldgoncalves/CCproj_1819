# Funcionamiento de la aplicación

Actualmente la aplicación cuenta con 2 microservicios, Información al Cliente y Gestión de Viajes, que comunican entre sí vía HTTP utilizando interfaces REST. Los dos microservicios pueden funcionar individualmente o al mismo tiempo en una sola máquina; o cada uno en una máquina distinta con conexión a la Internet.

Si los dos microservicios se ejecutan, el microservicio de Información al Cliente tendrá que ser el primero a ejecutarse y ponerse listo para contestar a pedidos HTTP.

Los dos microservicios cuentan con una base de datos [MongoDB](https://www.mongodb.com/) presente en la misma máquina del microservicio. La base de datos es compartida entre los dos microservicios si se ejecutan en la misma máquina.

## Información al Cliente

El microservicio almacena datos referentes a viajes y noticias. Los viajes son almacenados en un data object conteniendo su origen, destino, hora de partida, hora de salida y precio. Las noticias son simplemente strings de caracteres.

Los dos tipos de datos son almacenados en la base de datos por un data access object, el cual soporta operaciones de lectura, escrita y remoción sobre los mismos. Operaciones de escrita y remoción son comunicadas al microservicio de Gestión de Viajes, si está funcionando.

Remoción de elementos, sean viajes o noticias, no elimina su índice y sólo limpia su contenido, dejando intactos los restantes elementos y sus índices.

Una clase de servicio web, creada con recurso a la microframework [Javalin](https://javalin.io/), efectúa las operaciones REST GET, POST y DELETE sobre el data access object. Pruebas son efectuadas tanto al data access object como al servicio web, estas últimas con recurso al cliente HTTP [OkHttp](http://square.github.io/okhttp/).

## Gestión de Viajes

El microservicio almacena datos referentes a viajes. El único elemento guardado en un viaje es su asiento, además de su índice, por lo que el microservicio no necesita de un data object.

Los viajes son también almacenados en la base de datos por un data access object, que además de las operaciones de lectura, escrita y remoción soporta operaciones de compra y cancelación del viaje.

Un viaje creado tiene asignado el asiento 0, indicando que existe y no está comprado. Un viaje comprado tiene un asiento entre 1 y 200. Al cancelar un viaje, el número del asiento vuelve a ser 0.

Al pedirse un viaje inexistente se retorna el valor -1. Esto también sucede después de eliminarse un viaje, cuyo índice se mantiene tal como en el microservicio de Información al Cliente.

Las operaciones GET, POST, PUT y DELETE son también ejecutadas con una clase de servicio web creada con recurso a Javalin. Tanto el data access object como la clase de servicio web son probadas, esta última también con recurso a OkHttp.

## Integración

Al arrancar, 3 viajes son creados en el microservicio de Información al Cliente. Sus viajes y noticias pueden en ese momento ser creados y borrados sin restricciones.

Al arrancar, teniendo en cuenta sus variables de entorno, el microservicio de Gestión de Viajes pide el número de viajes existentes en el otro microservicio, esté en la misma máquina u otra. Obtenido el número, crea igual número de viajes en su base de datos y les asigna el número de asiento 0. El microservicio de Información al Cliente pasa así a saber que el otro microservicio está activo.

A partir de ese momento, cada viaje creado en el microservicio de Información al Cliente es comunicado al de Gestión de Viajes. Un viaje solo puede ser borrado en el microservicio de Información al Cliente si no está comprado o si fue cancelado en el microservicio de Gestión de Viajes, de otro modo el cambio no se ejecutará.

## Variables de entorno

Cinco variables de entorno (más 1 secundaria) deciden como funcionará la aplicación al ser desplegada:

* SERVICIO - Decide que servicio o servicios se ejecutarán. Puede tomar los siguientes valores:

  * 0 (SERVICIO_HEROKU) - Ejecuta el microservicio de Información al Cliente sin utilizar la base de datos MongoDB, almacenando los datos en arrays como hacía la aplicación antes de instalarse la conexión con una base de datos MongoDB. Se destina al despliegue en [Heroku](https://www.heroku.com/), donde mi aplicación tiene creada esa variable de entorno con valor 0
  
  * 1 (SERVICIO_INFO) - Es el valor por defecto. Ejecuta el microservicio de Información al Cliente, con la base de datos MongoDB.
  
  * 2 (SERVICIO_VIAJES) - Ejecuta el microservicio de Gestión de Viajes. Si la variable de entorno URL_INFO tiene algún valor, se asume que el microservicio de Información al Cliente está listo para contestar a pedidos HTTP en otra máquina. Si no tiene, se asume que el microservicio de Gestión de Viajes se está ejecutando solo.
  
  * 3 (SERVICIO_TODOS) - Ejecuta los dos microservicios en la misma máquina, utilizando la misma base de datos MongoDB.
  
* PORT_INFO y PORT_VIAJES - Los puertos de los microservicios de Información al Cliente y Gestión de Viajes, respectivamente. Si los dos microservicios se están ejecutando en máquinas distintas los puertos tendrán que ser 80; en caso contrario los puertos son de libre elección contando que no sean iguales. Sus valores por defecto son 7000 y 7001, respectivamente.

* PORT - Variable apenas de interés para Heroku, es necesaria para cambiar el puerto de la aplicación al 80 al desplegársela en Heroku.

* URL_INFO y URL_VIAJES - Los enlaces a los despliegues de los microservicios. Si tienen algún valor, se asume que los dos microservicios se están ejecutando en máquinas distintas. Por defecto son "http://localhost:" más el valor del puerto del microservicio respectivo.