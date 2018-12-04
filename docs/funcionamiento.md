# Funcionamiento de la aplicación

La aplicación es un microservicio basado en REST, que almacena datos referentes a viajes y noticias. Los viajes son almacenados en un data object conteniendo su origen, destino, hora de partida, hora de salida y precio. Las noticias son simplemente strings de caracteres.

Los dos tipos de datos son almacenados en arrays por un data access object, el cual suporta operaciones de lectura, escrita y remoción sobre los mismos. Remoción de elementos no elimina su índice, solo limpia su contenido, dejando intactos los restantes elementos y sus índices.

Una clase de servicio web, creada con recurso a la microframework [Javalin](https://javalin.io/), efectua las operaciones REST GET, POST y DELETE sobre esa clase. Pruebas son efectuadas tanto al data access object como al servicio web, estas ultimas con recurso al cliente HTTP [OkHttp](http://square.github.io/okhttp/).
