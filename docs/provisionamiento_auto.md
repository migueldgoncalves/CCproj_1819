# Configuración automática y provisionamiento de una MV, y despliegue en ella de la aplicación

Este tutorial fue realizado en una máquina con el sistema operativo Ubuntu 16.04 LTS. Su objetivo es crear y configurar con recurso a un script una máquina virtual Azure, provisionarla y desplegar en ella la aplicación almacenada en este repositorio. Es así una evolución del tutorial presente [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md), donde la creación y configuración de la máquina virtual Azure se hace de forma manual.

## Preparación

Antes de ejecutar el script de provisionamiento de una máquina virtual en Azure, hay que instalar el Azure CLI (su versión más moderna, no el Azure classic CLI) y iniciar sesión en él, elegindo la suscripción que se quiere utilizar para crear la máquina virtual.

En seguida, es necesario instalar Ansible. Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) un tutorial, seguirlo y volver aquí al terminarlo.

Hay que tener un par de llaves SSH ya creadas, incluso es más seguro crear un par de llaves específicamente para este ejercicio. Azure solo admite pares de llaves RSA con una longitud mínima de 2048 bits, como se puede ver [aquí](https://docs.microsoft.com/es-es/azure/virtual-machines/linux/mac-create-ssh-keys#supported-ssh-key-formats).

Para este tutorial, se ha utilizado una llave privada ubicada en el fichero `~/.ssh/id_rsa_azure` y una llave pública ubicada en el fichero `~/.ssh/id_rsa_azure.pub`; sin embargo la ubicación puede ser cualquier otra. Si se está realizando este tutorial en Ubuntu 16.04, se tendrá que añadir el siguiente contenido al fichero `~/.ssh/config` (reemplazar `ccazure` por el nombre DNS puesto en el fichero `hosts` de Ansible):

```
Host ccazure.uksouth.cloudapp.azure.com
  StrictHostKeyChecking no
```

dado que en cada arranque o creación de raíz la máquina tendrá una dirección IP diferente, por lo que hay que deshabilitar el strict host key checking con estas líneas para permitir la conexión con la máquina virtual. De contrário, la conexión será recusada.

## Variables del script

El script de provisionamiento se encuentra en [este fichero](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Contiene las siguientes variables, la mayor parte de las cuales puede ser cambiada:

```
REGION='uksouth'                                          #Región del centro de datos Azure donde se alojará la aplicación
GRUPO_NOMBRE='CCGroup'                                    #Nombre del grupo de recursos a crear
VM_NOMBRE='CCazure'                                       #Nombre a atribuir a la máquina virtual
IMAGEN='Canonical:UbuntuServer:16.04-LTS:16.04.201812070' #Imágen a utilizar en la máquina virtual
SSH_LLAVE_RUTA='~/.ssh/id_rsa_azure.pub'                  #Ruta del fichero con la llave pública a utilizar
VM_TAMANO='Standard_B1s'                                  #Tamaño de la máquina virtual
DISCO_DATOS_TAMANO=1                                      #Tamaño del disco de datos de la máquina virtual, en GB
SSH_PUERTO=22                                             #NO CAMBIAR, puerto para comunicación vía SSH
SSH_PRIORIDAD=100                                         #Tiene que ser diferente de HTTP_PRIORIDAD
HTTP_PUERTO=80                                            #NO CAMBIAR, puerto para comunicación vía HTTP
HTTP_PRIORIDAD=110                                        #Tiene que ser diferente de SSH_PRIORIDAD
SUBSTRING='PublicIp'                                      #NO CAMBIAR
VM_IP_PUBLICO="$VM_NOMBRE$SUBSTRING"                      #NO CAMBIAR, nombre del recurso con la dirección IP pública
DNS_NOMBRE='ccazure'                                      #Nombre DNS a atribuir a la máquina virtual
IP_ALOCACION='Dynamic'                                    #Puede ser 'Dynamic' o 'Static'
PLAYBOOK_RUTA='/etc/ansible/playbook.yml'                 #Ruta del fichero con el playbook Ansible
```

Una explicación detallada de los comandos del script se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/script_azure_cli.md).

`REGION` - La región del centro de datos donde estará la máquina virtual. Para cumplir con los reglamentos de la Unión Europea sobre protección de datos, en particular el reciente Reglamento General de Protección de Datos, hay que instalar la aplicación en una máquina virtual ubicada en un centro de datos de la Unión Europea. Al menos hasta el Brexit, existen 5 opciones: `francecentral`, `westeurope`, `northeurope`, `uksouth` y `ukwest`. Todas pueden ser elegidas sin costes adicionales.

`GRUPO_NOMBRE` - El nombre del grupo de recursos a crear, que incluirá no solo la máquina virtual sino también los recursos que necesitará para funcionar como discos y interfaces de red. El nombre puede ser cambiado libremente.

`VM_NOMBRE` - El nombre de la máquina virtual. También puede ser cambiado libremente.

`IMAGEN` - Una de las variables más importantes del script, consiste en el URN de la imagen a instalar en la máquina virtual. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#imagen). Sin embargo puede ser cambiada, desde que el sistema operativo sea el Ubuntu 16.04. Hay que tener en cuenta que ni todas las imágenes son gratuitas.

`SSH_LLAVE_RUTA` - La ruta hasta el fichero que contiene la llave pública a utilizar en la comunicación SSH entre la máquina local y la máquina virtual. Puede ser cambiada libremente para apuntar al fichero con la llave pública deseada.

`VM_TAMANO` - El tamaño de la máquina virtual a utilizar. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#tama%C3%B1o-de-la-m%C3%A1quina-virtual). Puede ser cambiado por otro, aunque llevará a costes más grandes una vez que el `Standard_B1s` es el tamaño más barato disponible (en el momento de la escrita de este tutorial, Diciembre de 2018).

`DISCO_DATOS_TAMANO` - El tamaño, en GB, del disco de datos a emplear en la máquina virtual. Tiene que ser un número entero. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#tama%C3%B1o-del-disco-de-datos). Puede ser cambiado, aunque llevará a costes más grandes una vez que 1 GB es el tamaño mínimo del disco de datos.

`SSH_PUERTO` - Puerto para comunicación vía SSH. No cambiar.

`SSH_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía SSH con la máquina virtual dentro del conjunto de reglas de su firewall. Tiene que ser un numero entero entre 100 y 4096, como se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `HTTP_PRIORIDAD`, de otro modo el script no funcionará.

`HTTP_PUERTO` - Puerto para comunicación vía HTTP. No cambiar.

`HTTP_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía HTTP con la máquina virtual dentro del conjunto de reglas de su firewall. Tiene que ser un numero entero entre 100 y 4096, como se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `SSH_PRIORIDAD`, de otro modo el script no funcionará.

`SUBSTRING` - Una variable auxiliar necesaria para la variable `VM_IP_PUBLICO`. No cambiar.

`VM_IP_PUBLICO` - El nombre del recurso que contiene no solo el IP publico de la máquina virtual, sino también su nombre DNS si creado. Es una concatenación del nombre de la máquina virtual (la variable `VM_NOMBRE`) con la string `PublicIp` (la variable `SUBSTRING`).

`DNS_NOMBRE` - El nombre DNS a atribuir a la máquina virtual para que se pueda comunicar con ella independientemente de su dirección IP. Debe ser igual al nombre que se ha añadido al fichero `hosts` en el [tutorial](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) de instalación y configuración de Ansible. Dejar la variable como está si se ha elegido el nombre DNS `ccazure`, como viene en ese tutorial.

`IP_ALOCACION` - Tipo de alocación de la dirección IP de la máquina virtual. Puede ser `Dynamic` o `Static`: con alocación dinámica la dirección IP cambia cada vez que se inicia la máquina; con alocación estática la dirección IP es fija.

`PLAYBOOK_RUTA` - La ruta para el playbook de Ansible a ejecutar. Puede ser cambiada libremente para apuntar al playbook, si no está en el directorio `/etc/ansible` o si no se llama `playbook.yml`.

## Creación, configuración y provisionamiento de la MV, y despliegue de la aplicación

Para ejecutar el script, hay que crear un fichero llamado `acopio.sh` (u otro nombre terminado en `.sh`) que esté vacío en una ubicación cualquier y copiar en él el [script de provisionamiento](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Hay que dar permiso de ejecución del fichero como programa, en Ubuntu 16.04 esto se hace pulsando con el botón derecho del ratón en el fichero; seleccionando Propiedades y la pestaña Permisos; y en ella la opción Permitir Ejecución de Fichero enquanto Programa. Mirar una ilustración [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_permisos.png).

El último paso antes de la ejecución del script es cambiar las variables que sean necesarias; sin embargo el script está preparado para funcionar como está.

Por fin, ejecutar el script abriendo un terminal en el directorio donde esté el script y correr el comando `./acopio.sh` (reemplazar por el nombre elegido para el fichero del script), si se está en Ubuntu 16.04.

Pasarán cerca de 7-8 minutos hasta que la aplicación esté desplegada y lista para ser accedida por un navegador. El output inicial de la consola deberá ser parecido a [esto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_inicio.png), y el output final de la ejecución del script deberá tener este [aspecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_fin.png). El script no se detiene por sí, sigue ejecutándose y manteniendo la aplicación corriendo en la máquina virtual hasta que se lo detenga con CTRL+C, lo que producirá mucho output. También es posible detener la ejecución del script prematuramente con CTRL+C, presionándolo 1 vez por cada comando del script que quede por ejecutar.

Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/aplicacion.png) una captura de pantalla de la página inicial de la aplicación desplegada utilizando el script.

Para rearrancar la máquina virtual solo hay que ejecutar el script de nuevo con el mismo comando `./acopio.sh` (con el nombre reemplazado si necesario). Pasarán cerca de 3 minutos hasta que la aplicación esté corriendo de nuevo en la máquina virtual, lista para receber pedidos HTTP. El output final del script cuando ejecutado novamente deberá tener este [aspecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_reejecucion.png).

Cuando la máquina virtual no sea más necesaria, eliminar todo su grupo de recursos, lo que eliminará la máquina y recursos asociados, para no gastar crédito innecesariamente. En alternativa, si se está utilizando ese grupo de recursos para más máquinas virtuales, eliminar solo los recursos relacionados con este tutorial.
