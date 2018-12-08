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

MV: hito3.francecentral.cloudapp.azure.com

### Estado actual

En su estado actual, habendose concluído el [Hito 3](https://github.com/migueldgoncalves/CCproj_1819/milestone/3), el proyecto consiste en la funcionalidad básica del microservicio de Información al Cliente implementada y desplegada en un dyno Heroku, así como los pasos para desplegarlo en una máquina virtual Azure.

## Configuración y provisionamiento de una MV, y despliegue en ella de la aplicación

Este tutorial fue realizado en una máquina física con el sistema operativo Ubuntu 16.04.5 LTS (Xenial Xerus).

Se destina a enseñar paso a paso a configurar una máquina virtual en Microsoft Azure, a provisionarla con el necesario para el despliegue de la aplicación de este repositorio, y a desplegarla en esa máquina virtual.

### Instalación y configuración de Ansible

El provisionamiento de la máquina virtual se hará usando Ansible. Para instalarlo en nuestra máquina local (no es necesario instalarlo en la máquina virtual) hay que tener la utilidad de instalación de módulos `pip`. Una vez teniendo `pip` instalado, Ansible se instalará utilizando el comando

`pip install paramiko PyYAML jinja2 httplib2 ansible`

Se puede comprobar la correcta instalación de Ansible con el comando `ansible --version`, que deberá retornar un output similar a este:

```
ansible 2.7.4
config file = /etc/ansible/ansible.cfg
configured module search path = [u'/home/miguel/.ansible/plugins/modules', u'/usr/share/ansible/plugins/modules']
ansible python module location = /home/miguel/.local/lib/python2.7/site-packages/ansible
executable location = /usr/bin/ansible
python version = 2.7.12 (default, Nov 12 2018, 14:36:49) [GCC 5.4.0 20160609]
```

La configuración de Ansible se encuentra por defecto en la directoria `/etc/ansible`, donde se encontrará el fichero `hosts`. Este fichero tendrá una grande cantidad de líneas comentadas que sirven de ejemplo a nuevos usuarios de Ansible; sin embargo se pueden borrar sin problemas. En seguida, hay que añadir al fichero `hosts` las siguientes líneas:

```
[local]
127.0.0.1   ansible_connection=local

[azure]
hito3.francecentral.cloudapp.azure.com
```

Con estas líneas, estamos indicando a Ansible cuales los hosts donde se ejecutará el provisionamiento. Ansible permite agrupar los hosts en grupos; en este caso el grupo `local` contiene la dirección IP del localhost y el grupo `azure` contiene la dirección DNS de la máquina virtual que se creará en Azure. Ansible por defecto se comunica con los hosts via SSH, `ansible_connection=local` indica a Ansible que la conexión al host `127.0.0.1` es local y no via SSH.

Se asume que la máquina virtual a crear se llamará `hito3`, cambie el nombre en la dirección DNS arriba si desea poner otro nombre a la máquina virtual.

En seguida, hay que crear también en la directoria `/etc/ansible` un fichero .yml. El nombre no tiene importancia desde que su nombre termine con `.yml`. Se asumirá que el fichero se llama `hito3.yml`, sin embargo `hito3` puede ser sustituido por cualquier otro nombre.

En el fichero `hito3.yml` (o cualquier que sea su nombre) hay en seguida que copiar y pegar el contenido de este [fichero .yml](https://github.com/migueldgoncalves/CCproj_1819/blob/master/provision/hito3.yml).

Ansible estará así configurado y listo para provisionar máquinas.

### Creación y configuración de la máquina virtual

Para empezar, hay que acceder al [panel de Azure](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/01.png). En él seleccionar "Máquinas virtuales" en el menú de la izquierda y [pulsar](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/02.png) en "Crear máquina virtual".

Aparecerá una pantalla de [Datos Básicos](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/03.png), una de las más importantes. Hay que seleccionar la suscripción Azure que se quiere usar para crear la máquina virtual. Después, elegir un grupo de recursos o crear un nuevo. El nombre de la máquina virtual puede ser cualquier uno, aunque en este tutorial se asume que es `hito3`. Elegir como región "Centro de Francia"; elegir otras regiones cambiará la dirección DNS de la máquina virtual. Es importante elegir una región en la Unión Europea de modo a que se apliquen las leyes europeas. No será necesaria redundancia de la infraestructura.

Como imágen utilizar Ubuntu Server 16.04 LTS, con la cual se ha probado este tutorial. Una máquina virtual B1s estándar con 1 virtual CPU y 1 GB de RAM es suficiente para correr la aplicación; la série B de máquinas virtuales de Azure se destina a ofrecer una forma económica de hacer despliegues que no necesiten de desempeño total del CPU de forma permanente.

La autenticación se hará con Clave pública SSH; para eso es recomendable crear un par de llaves SSH que por defecto se guardan en el directorio `~/.ssh`. Hay que copiar el contenido del fichero <nombre_llave>.pub, donde <nombre_llave> es el nombre atribuido a la llave, al campo Clave pública SSH. Dejar el campo de Azure Active Directory desactivado.

Como puertos de entrada, hay que elegir el HTTP (puerto 80) y el SSH (puerto 22), y apenas esos.

Pasar a la pantalla siguiente, [Discos](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/04.png). El disco del sistema operativo puede ser HDD estándar para ahorrar dinero, no es necesario más para correr la aplicación. Crear y adjuntar un nuevo disco de datos de 1 GB, vacío y de tipo también HDD estándar, con un nombre cualquier. Mantener seleccionada la opción por defecto de Sí en "Usar discos administrados".

No hay necesidad de cambiar las opciones por defecto en las pantallas de [Redes](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/05.png), [Administración](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/06.png), [Configuración de invitado](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/07.png) y [Etiquetas](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/08.png). Si todo está de acuerdo con el esperado en la pantalla de [Revisar y crear](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/09.png), seleccionar Crear. La [implementación de los diferentes recursos](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/10.png) deberá tardar 3-4 minutos.

Acceder al recurso de la máquina virtual creada pulsando en su enlace, y en su [panel](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/11.png) pulsar en Configurar abajo de Nombre DNS. Irá aparecer una dirección IP en el panel, pero por defecto es dinámica y cambiará cada vez que se detenga y inicie la máquina virtual. En la [pantalla de configuración](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/12.png) añadir como Etiqueta de nombre DNS `hito3` (o cualquier que sea el nombre de la máquina virtual), y pulsar en Guardar. Los cambios se harán efectivos en segundos, y entonces ya se podrá proceder al provisionamiento de la máquina virtual y al despliegue de la aplicación.

### Provisionamiento de la máquina virtual y despliegue remoto de la aplicación

El fichero `hito3.yml`, que estará en el directorio `/etc/ansible`, consiste en un playbook de Ansible que no solo provisionará la máquina virtual sino también desplegará la aplicación para que escuche en el puerto 80. Para ejecutarlo hay que entrar con la consola en dicho directorio y ejecutar `ansible-playbook hito3.yml`. Cambiar el nombre del fichero si necesario. Hay que decir que no sería necesario entrar en dicho directorio, sino escribir la ruta completa del fichero en el comando.

El provisionamiento y después el despliegue tardarán cerca de 4 minutos; después de ese tiempo la [consola](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/13.png) hay que estar detenida en la task "Run application using Maven", donde la aplicación estará escuchando en el puerto 80.

Bastará entonces escribir en un browser `hito3.francecentral.cloudapp.azure.com` (asumiendo que la máquina se llama `hito3` y tiene esa dirección DNS) para se acceder a la [ruta raiz](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/16.png) de la aplicación, y desde ahí a todas las otras. Ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/17.png) y [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/18.png) capturas de otras rutas de la aplicación.

Para detener la ejecución de la máquina solo hay que hacer CTRL+C en la consola, lo que detengará la máquina con mucho output. Ejecutar el playbook `hito3.yml` de nuevo ya producirá un [output diferente](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/14.png) una vez que las tasks ya fueron ejecutadas antes. La excepción es la task "Install application using Maven", que siempre retornará `changed` y no `ok` una vez que en esa task se ejecuta un comando directamente en la shell.

### Provisionamiento de la máquina local y despliegue local de la aplicación

El fichero `hito3.yml` puede ser también utilizado para provisionar la máquina local y desplegar en ella la aplicación. Para eso, hay que editar el fichero y cambiar la segunda línea `- hosts: azure` para `- hosts: local`, lo que dirá a Ansible para ejecutar las tasks en los hosts del grupo `local`, en este caso solamente el localhost.

Se ejecuta el playbook de igual manera, usando el comando `ansible-playbook hito3.yml`. Deberá tardar menos de 1 minuto hasta el despliegue de la aplicación, que se puede acceder escribiendo `localhost` en un browser, y detener usando igualmente CTRL+C. El output de la consola deberá ser similar a [esto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/15.png). Ver aqui la [ruta raiz](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/19.png), una [otra](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/20.png) y una [tercera](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Provisionamiento/21.png).

## Enlaces de interés relacionados con el hito actual

Más información sobre el provisionamiento [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento.md).
Incluí la justificación del sistema de gestión de configuración usado, la cobertura del microservicio implementado y una explicación detallada del playbook utilizado.

Información sobre comprobación de playbooks [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/comprobacion.md).
Inclui mi comprobación de los playbooks de @danielbc09 y @jabonillab, así como su comprobación de mi playbook.

## Enlaces de interés relacionados con los hitos anteriores

[Arquitectura y características](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/arquitectura.md)

[Configuración del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_configuracion.md)

[Elección del PaaS](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/PaaS_eleccion.md)

[Creación de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/creacion_aplicacion.md)

[Configuración del despliegue](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/despliegue_PaaS.md)

[Funcionamiento de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/funcionamiento.md)

[Hitos del proyecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/hitos.md)

[Motivo de la aplicación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/motivo.md)

[Rutas](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/rutas.md)
