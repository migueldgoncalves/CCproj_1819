# Configuración del despliegue

Es posible desplegar automaticamente haciendo push aplicaciones en repositorios Git a Heroku, mediante el uso de un servicio de integración continua. En esta práctica ese servicio es [Codeship](https://codeship.com/), que permite integración y despliegue continuos de forma gratuita con recursos limitados.

Después de crear cuenta en Heroku, se ha creado allí una aplicación. En Codeship se ha creado entonces una pipeline de despliegue, conectada a la branch master del repositorio GitHub del proyecto, que se conectó a la aplicación correspondiente en Heroku mediante la escrita de su nombre y API Key de mi cuenta Heroku.

Con la pipeline configurada, cada push al repositorio GitHub lleva automaticamente a una build del mismo en Codeship. Si la build tiene sucesso, Codeship haz el despliegue de la nueva versión de la aplicación en Heroku.
