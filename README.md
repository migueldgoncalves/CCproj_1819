# Infraestructura de gestión ferroviaria basada en la nube

## Resúmen

Esta aplicación se trata de mi proyecto de la asignatura de Cloud Computing: Fundamentos e Infraestructuras.

Este proyecto se destina a desplegar en la nube infraestructura virtual de apoyo a una compañía ferroviaria fictícia que gestionará una red de ferrocarriles suburbanos centrados en Granada, España. Servirá tanto a clientes de la compañía como a todos sus empleados.

Los clientes podrán acceder a la aplicación para consultar horarios de trenes, precios de viajes y noticias de ámbito ferroviario. Además, podrán comprar viajes y gestionar sus reservas, e. g. cambiar su asiento o cancelarlas.

Los empleados podrán adicionalmente gestionar viajes, e. g. añadir nuevas o cambiar su horario, y información disponible al público; así como gestionar y monitorizar equipo ferroviario como señalización y cámaras de seguridad.

La aplicación cuentará con un servicio de autenticación disponible a todos los usuarios que atribuirá a cada uno los permisos adecuados, asegurando que clientes y empleados con diferentes funciones acceden solamente a las funcionalidades debidas de la aplicación.

Este proyecto seguirá siendo desarrollado hasta Enero, cuando se prevé su conclusión.

No está relacionado con ningún otro, sea de TFG, TFM o de una empresa. Se destina únicamente a la asignatura de Cloud Computing e seré yo su cliente.

Está distribuido con la licencia MIT.

Despliegue https://javalinapp.herokuapp.com/

MV: 51.140.167.247

MV2: 51.140.167.247

### Estado actual

En su estado actual, habendose concluído el [Hito 4](https://github.com/migueldgoncalves/CCproj_1819/milestone/4), el proyecto consiste en la funcionalidad básica del microservicio de Información al Cliente implementada y desplegada en un dyno Heroku, así como los pasos para desplegarlo en una máquina virtual Azure tanto creándola [manualmente](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md) como con [recurso a un script](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md).

## Enlaces de interés relacionados con el hito actual

[Justificación de elecciones](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md)

Justificación del sistema cloud, región Azure, imagen, tamaño de la máquina virtual y tamaño del disco de datos

[Provisionamiento con Ansible - Creación automática de la máquina virtual](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md)

Tutorial de provisionamiento y despliegue con Ansible utilizando un script

[Script de provisionamiento](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/script_azure_cli.md)

Explicación detallada del script de provisionamiento creado en este hito

## Enlaces de interés relacionados con los hitos anteriores

[Arquitectura y características](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/arquitectura.md)

[Comprobación de playbooks](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/comprobacion.md)

[Configuración del despliegue](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/despliegue_PaaS.md)

[Configuración del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_configuracion.md)

[Creación de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/creacion_aplicacion.md)

[Elección del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_eleccion.md)

[Funcionamiento de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/funcionamiento.md)

[Hitos del proyecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/hitos.md)

[Motivo de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/motivo.md)

[Playbook de Ansible](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/playbook.md)

[Provisionamiento con Ansible - Creación manual de la máquina virtual](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md)

[Rutas](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/rutas.md)
