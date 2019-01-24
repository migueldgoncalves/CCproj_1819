# Configuración automática y provisionamiento de dos MVs, y despliegue en ellas de la aplicación

Este tutorial fue realizado en una máquina con el sistema operativo Ubuntu 16.04 LTS. Su objetivo es crear y configurar con recurso a un script dos máquinas virtuales Azure, provisionarlas y desplegar en ellas la aplicación almacenada en este repositorio. Es así una evolución del tutorial presente [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md), donde la creación y configuración de las máquinas virtuales Azure se hace de forma manual.

## Preparación

Antes de ejecutar el script de provisionamiento de dos máquinas virtuales en Azure, hay que instalar el Azure CLI (su versión más moderna, no el Azure classic CLI) y iniciar sesión en él, elegindo la suscripción que se quiere utilizar para crear las máquinas virtuales.

En seguida, es necesario instalar Ansible. Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) un tutorial, seguirlo y volver aquí al terminarlo.

Hay que tener un par de llaves SSH ya creadas, incluso es más seguro crear un par de llaves específicamente para este ejercicio. Azure solo admite pares de llaves RSA con una longitud mínima de 2048 bits, como se puede ver [aquí](https://docs.microsoft.com/es-es/azure/virtual-machines/linux/mac-create-ssh-keys#supported-ssh-key-formats).

Para este tutorial, se ha utilizado una llave privada ubicada en el fichero `~/.ssh/id_rsa_azure` y una llave pública ubicada en el fichero `~/.ssh/id_rsa_azure.pub`; sin embargo la ubicación puede ser cualquier otra. Si se está realizando este tutorial en Ubuntu 16.04, se tendrá que añadir el siguiente contenido al fichero `~/.ssh/config` (reemplazar `ccazureinfo` y `ccazureviajes` por el nombre DNS puesto en el fichero `hosts` de Ansible):

```
Host ccazureinfo.uksouth.cloudapp.azure.com
  StrictHostKeyChecking no
  
Host ccazureviajes.uksouth.cloudapp.azure.com
  StrictHostKeyChecking no
```

dado que en cada arranque o creación de raíz las máquinas tendrán una dirección IP diferente, por lo que hay que deshabilitar el strict host key checking con estas líneas para permitir la conexión con las máquinas virtuales. De contrário, la conexión será recusada.

## Variables del script

El script de provisionamiento se encuentra en [este fichero](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Contiene las siguientes variables, la mayor parte de las cuales puede ser cambiada:

```
REGION='uksouth'
GRUPO_NOMBRE='CCGroup'
VM_NOMBRE_INFO='CCazureInfo'
VM_NOMBRE_VIAJES='CCazureViajes'
IMAGEN='Canonical:UbuntuServer:16.04-LTS:16.04.201812070'
SSH_LLAVE_RUTA='~/.ssh/id_rsa_azure.pub'
VM_TAMANO='Standard_B1s'
DISCO_DATOS_TAMANO=1
SSH_PUERTO=22
SSH_PRIORIDAD=100
HTTP_PUERTO=80
HTTP_PRIORIDAD=110
SUBSTRING='PublicIp'
VM_INFO_IP_PUBLICO="$VM_NOMBRE_INFO$SUBSTRING"
VM_VIAJES_IP_PUBLICO="$VM_NOMBRE_VIAJES$SUBSTRING"
DNS_NOMBRE_INFO='ccazureinfo'
DNS_NOMBRE_VIAJES='ccazureviajes'
IP_ALOCACION='Dynamic'
PLAYBOOK_RUTA='/etc/ansible/playbook.yml'
```

Una explicación detallada de los comandos del script se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/script_azure_cli.md).

`REGION` - La región del centro de datos donde estarán las máquinas virtuales. Para cumplir con los reglamentos de la Unión Europea sobre protección de datos, en particular el reciente Reglamento General de Protección de Datos, hay que instalar la aplicación en máquinas virtuales ubicadas en un centro de datos de la Unión Europea. Al menos hasta el Brexit, existen 5 opciones: `francecentral`, `westeurope`, `northeurope`, `uksouth` y `ukwest`. Todas pueden ser elegidas sin costes adicionales.

`GRUPO_NOMBRE` - El nombre del grupo de recursos a crear, que incluirá no solo las máquinas virtuales sino también los recursos que necesitarán para funcionar como discos y interfaces de red. El nombre puede ser cambiado libremente.

`VM_NOMBRE_INFO` y `VM_NOMBRE_VIAJES` - El nombre de la máquina virtual del microservicio de Información al Cliente y de la máquina virtual del microservicio de Gestión de Viajes, respectivamente. También pueden ser cambiados librementes.

`IMAGEN` - Una de las variables más importantes del script, consiste en el URN de la imagen a instalar en las máquinas virtuales. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#imagen). Sin embargo puede ser cambiada, desde que el sistema operativo sea el Ubuntu 16.04. Hay que tener en cuenta que ni todas las imágenes son gratuitas.

`SSH_LLAVE_RUTA` - La ruta hasta el fichero que contiene la llave pública a utilizar en la comunicación SSH entre la máquina local y las máquinas virtuales. Puede ser cambiada libremente para apuntar al fichero con la llave pública deseada.

`VM_TAMANO` - El tamaño de la máquina virtual a utilizar. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#tama%C3%B1o-de-la-m%C3%A1quina-virtual). Puede ser cambiado por otro, aunque llevará a costes más grandes una vez que el `Standard_B1s` es el tamaño más barato disponible (en el momento de la escrita de este tutorial, Diciembre de 2018).

`DISCO_DATOS_TAMANO` - El tamaño, en GB, del disco de datos a emplear en la máquina virtual. Tiene que ser un número entero. Su proceso de elección es descrito [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md#tama%C3%B1o-del-disco-de-datos). Puede ser cambiado, aunque llevará a costes más grandes una vez que 1 GB es el tamaño mínimo del disco de datos.

`SSH_PUERTO` - Puerto para comunicación vía SSH. No cambiar.

`SSH_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía SSH con la máquina virtual dentro del conjunto de reglas de su firewall. Tiene que ser un numero entero entre 100 y 4096, como se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `HTTP_PRIORIDAD`, de otro modo el script no funcionará.

`HTTP_PUERTO` - Puerto para comunicación vía HTTP. No cambiar.

`HTTP_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía HTTP con la máquina virtual dentro del conjunto de reglas de su firewall. Tiene que ser un numero entero entre 100 y 4096, como se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `SSH_PRIORIDAD`, de otro modo el script no funcionará.

`SUBSTRING` - Una variable auxiliar necesaria para la variable `VM_IP_PUBLICO`. No cambiar.

`VM_INFO_IP_PUBLICO` y `VM_VIAJES_IP_PUBLICO` - El nombre de los recursos que contiene no solo el IP publico de cada máquina virtual, sino también su nombre DNS si creado. Es una concatenación del nombre de la máquina virtual (las variables `VM_NOMBRE`) con la string `PublicIp` (la variable `SUBSTRING`).

`DNS_NOMBRE_INFO` y `DNS_NOMBRE_VIAJES` - El nombre DNS a atribuir a cada máquina virtual para que se pueda comunicar con ella independientemente de su dirección IP. Debe ser igual al nombre que se ha añadido para cada máquina al fichero `hosts` en el [tutorial](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) de instalación y configuración de Ansible. Dejar las variables como están si se ha elegido los nombres DNS `ccazureinfo` y `ccazureviajes`, como viene en ese tutorial.

`IP_ALOCACION` - Tipo de alocación de la dirección IP de la máquina virtual. Puede ser `Dynamic` o `Static`: con alocación dinámica la dirección IP cambia cada vez que se inicia la máquina; con alocación estática la dirección IP es fija.

`PLAYBOOK_RUTA` - La ruta para el playbook de Ansible a ejecutar. Puede ser cambiada libremente para apuntar al playbook, si no está en el directorio `/etc/ansible` o si no se llama `playbook.yml`.

## Creación, configuración y provisionamiento de las MVs, y despliegue de la aplicación

Para ejecutar el script, hay que crear un fichero llamado `acopio.sh` (u otro nombre terminado en `.sh`) que esté vacío en una ubicación cualquier y copiar en él el [script de provisionamiento](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Hay que dar permiso de ejecución del fichero como programa, en Ubuntu 16.04 esto se hace pulsando con el botón derecho del ratón en el fichero; seleccionando Propiedades y la pestaña Permisos; y en ella la opción Permitir Ejecución de Fichero enquanto Programa. Mirar una ilustración [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_permisos.png).

El último paso antes de la ejecución del script es cambiar las variables que sean necesarias; sin embargo el script está preparado para funcionar como está.

Por fin, ejecutar el script abriendo un terminal en el directorio donde esté el script y correr el comando `./acopio.sh` (reemplazar por el nombre elegido para el fichero del script), si se está en Ubuntu 16.04.

La aplicación estará desplegada y lista para ser accedida por un navegador al terminarse la ejecución del playbook. El output inicial de la consola deberá ser parecido a [esto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_inicio.png), y el output final de la ejecución del script deberá tener este [aspecto](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_fin.png). Tener en cuenta que estas imágenes fueron obtenidas cuando el script se aplicaba a una sola máquina virtual. Es posible detener la ejecución del script prematuramente con CTRL+C, presionándolo 1 vez por cada comando del script que quede por ejecutar.

Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/aplicacion.png) una captura de pantalla de la página inicial del microservicio de Información al Cliente desplegado utilizando el script.

Las máquinas se detienen en el panel de Azure. Para rearrancar las máquinas virtuales solo hay que ejecutar el script de nuevo con el mismo comando `./acopio.sh` (con el nombre reemplazado si necesario). Pasarán pocos minutos hasta que la aplicación esté corriendo de nuevo en las máquinas virtuales, lista para receber pedidos HTTP. El output final del script cuando ejecutado novamente deberá tener un aspecto similar a [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Automatizacion/Script_reejecucion.png), teniendo en cuenta que la imagen fue obtenida cuando el script se aplicaba a una sola máquina virtual.

Cuando las máquinas virtuales no sean más necesarias, eliminar todo su grupo de recursos, lo que eliminará las máquinas y recursos asociados, para no gastar crédito innecesariamente. En alternativa, si se está utilizando ese grupo de recursos para más máquinas virtuales, eliminar solo los recursos relacionados con este tutorial.
