# Configuración automática y provisionamiento de una MV, y despliegue en ella de la aplicación

Este tutorial fue realizado en una máquina con el sistema operativo Ubuntu 16.04 LTS. Su objetivo es crear y configurar con recurso a un script una máquina virtual Azure, provisionarla y desplegar en ella la aplicación almacenada en este repositorio. Es así una evolución del tutorial presente [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md), dónde la creación y configuración de la máquina virtual Azure se hace de forma manual.

## Preparación

Antes de ejecutar el script de creación, configuración y provisionamiento de una máquina virtual en Azure, hay que instalar el Azure CLI (su versión más moderna, no el Azure classic CLI) y iniciar sesión en él, elegindo la suscripción que se quiere utilizar para crear la máquina virtual.

En seguida, es necesario instalar Ansible. Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) un tutorial, seguirlo y volver aquí al terminarlo.

Hay que tener un par de llaves SSH ya creadas, incluso es más seguro crear un par de llaves específicamente para este ejercicio. Para este tutorial, se ha utilizado una llave privada ubicada en el fichero `~/.ssh/id_rsa_azure` y una llave pública ubicada en el fichero `~/.ssh/id_rsa_azure.pub`; sin embargo la ubicación puede ser cualquier otra. Si se está realizando este tutorial en Ubuntu 16.04, se tendrá que añadir el siguiente contenido al fichero `~/.ssh/config` (reemplazar `ccazure` por el nombre DNS puesto en el fichero `hosts` de Ansible):

```
Host ccazure.uksouth.cloudapp.azure.com
  StrictHostKeyChecking no
```

dado que en cada arranque o creación de raíz la máquina tendrá una dirección IP diferente, por lo que hay que deshabilitar el strict host key checking con estas líneas para permitir la conexión con la máquina virtual.

## Variables del script

El script de creación, configuración y provisionamiento de la máquina virtual, así como de despliegue en ella de la aplicación, se encuentra en [este fichero](https://github.com/migueldgoncalves/CCproj_1819/acopio.sh). Contiene las siguientes variables, la mayor parte de las cuales puede ser cambiada:

```
REGION='westeurope'
GRUPO_NOMBRE='CCGroup'
VM_NOMBRE='CCazure'
IMAGEN='Canonical:UbuntuServer:16.04-LTS:16.04.201812070'
SSH_LLAVE_RUTA='~/.ssh/id_rsa_azure.pub'
VM_TAMANO='Standard_B1s'
DISCO_DATOS_TAMANO=1
SSH_PUERTO=22
SSH_PRIORIDAD=100
HTTP_PUERTO=80
HTTP_PRIORIDAD=110
SUBSTRING='PublicIp'
VM_IP_PUBLICO="$VM_NOMBRE$SUBSTRING"
DNS_NOMBRE='ccazure'
IP_ALOCACION='Dynamic'
PLAYBOOK_RUTA='/etc/ansible/playbook.yml'
```

Una explicación detallada de los comandos del script se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/script_azure_cli.md).

`REGION` - El centro de datos donde estará la máquina virtual. Para cumplir con los reglamentos de la Unión Europea sobre protección de datos, en particular el reciente Reglamento General de Protección de Datos, hay que instalar la aplicación en una máquina virtual ubicada en un centro de datos de la Unión Europea. Al menos hasta el Brexit, existen 5 opciones: `francecentral`, `westeurope`, `northeurope`, `uksouth` y `ukwest`.

`GRUPO_NOMBRE` - El nombre del grupo de recursos a crear, que incluirá no solo la máquina virtual sino también los recursos que necesitará para funcionar como discos y interfaces de red. El nombre no tiene importancia, y puede ser cambiado libremente.

`VM_NOMBRE` - El nombre de la máquina virtual. Tampoco tiene mucha importancia, y por lo tanto puede ser cambiado libremente.

`IMAGEN` - Una de las variables más importantes del script, consiste en el URN de la imágen a instalar en la máquina virtual. Su proceso de elección es descrito [aquí](). Si embargo puede ser cambiado, desde que el sistema operativo sea el Ubuntu 16.04. Hay que tener en cuenta que ni todas las imagenes son gratuitas.

`SSH_LLAVE_RUTA` - La ruta hasta el fichero que contiene la llave pública a utilizar en la comunicación SSH entre la máquina local y la máquina virtual. Puede ser cambiada libremente.

`VM_TAMANO` - La instancia de la máquina virtual a utilizar. Su proceso de elección es descrito [aquí](). Puede ser cambiada por otra, aunque llevará a costes más grandes una vez que la `Standard_B1s` es la instancia más barata disponible (en el momento de la escrita de este tutorial, 20 de diciembre de 2018).

`DISCO_DATOS_TAMANO` - El tamaño, en GB, del disco de datos a emplear en la máquina virtual. Tiene que ser un número entero. Su proceso de elección es descrito [aquí](). Puede ser cambiado, aunque llevará a costes más grandes una vez que 1 GB es el tamaño mínimo del disco de datos.

`SSH_PUERTO` - Puerto para comunicación SSH. No cambiar.

`SSH_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía SSH con la máquina virtual dentro del conjunto de reglas de la firewall de la MV. Tiene que ser un numero entero entre 100 y 4096, cómo se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `HTTP_PRIORIDAD`, de otro modo el script no funcionará.

`HTTP_PUERTO` - Puerto para comunicación HTTP. No cambiar.

`HTTP_PRIORIDAD` - Prioridad de la regla que permite la comunicación vía HTTP con la máquina virtual dentro del conjunto de reglas de la firewall de la MV. Tiene que ser un numero entero entre 100 y 4096, cómo se puede ver [aquí](https://docs.microsoft.com/en-us/azure/virtual-network/security-overview#security-rules). Dado el reducido número de reglas de seguridad en la máquina virtual, el número es poco importante. Sin embargo, tiene que ser diferente de `SSH_PRIORIDAD`, de otro modo el script no funcionará.

`SUBSTRING` - Una variable auxiliar necesaria para la variable `VM_IP_PUBLICO`. No cambiar.

`VM_IP_PUBLICO` - El nombre del recurso que contiene no solo el IP publico de la máquina virtual, sino también su nombre DNS si creado. Es una concatenación del nombre de la máquina virtual (la variable `VM_NOMBRE`) con la string `PublicIp` (la variable `SUBSTRING`).

`DNS_NOMBRE` - El nombre DNS a atribuir a la máquina virtual para que se pueda comunicar con ella independientemente de su dirección IP. Debe ser igual al nombre que se ha añadido al fichero `hosts` en el [tutorial](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible) de instalación y configuración de Ansible. Dejar la variable como está si se ha elegido el nombre DNS `ccazure`, como viene en ese tutorial.

`IP_ALOCACION` - Tipo de alocación de la dirección IP de la máquina virtual. Puede ser `Dynamic` o `Static`: con alocación dinámica la dirección IP cambia cada vez que se reinicia la máquina; con alocación estática la dirección IP es fija.

`PLAYBOOK_RUTA` - La ruta para el playbook de Ansible a ejecutar. Puede ser cambiada si el directorio de Ansible no es el `/etc/ansible` definido por defecto.

## Creación, configuración y provisionamiento de la MV, y despliegue de la aplicación

Para ejecutar el script, hay que crear un fichero llamado `acopio.sh` (u otro nombre terminado en `.sh`) que esté vacío en una ubicación cualquier y copiar en él el script arriba. Hay que dar permiso de ejecución del fichero como programa, en Ubuntu 16.04 esto se hace pulsando con el botón derecho del ratón en el fichero; seleccionando la pestaña Permisos; y en ella la opción Permitir Ejecución de Fichero enquanto Programa. Mirar una ilustración [aquí](https://github.com/migueldgoncalves/CC_ejercicios/blob/master/Automatizacion/eje2_documentos/Ejercicio2_3.png).

El último paso antes de la ejecución del script es cambiar las variables que sean necesarias; sin embargo el script está preparado para funcionar como está.

Por fin, ejecutar el script abriendo un terminal en el directorio donde esté el script y correr el comando `./acopio.sh` (reemplazar por el nombre elegido para el fichero del script), si se está en Ubuntu 16.04.

Pasarán cerca de 7-8 minutos hasta que la aplicación esté desplegada y lista para ser accedida por un browser. El output inicial de la consola deberá ser parecido a [esto](), y el output final de la ejecución del script deberá tener este [aspecto](). El script no se detiene por sí, sigue ejecutándose y mantenendo la aplicación corriendo en la máquina virtual hasta que se la detenga con CTRL+C, lo que producirá mucho output. También es posible detener la ejecución del script con CTRL+C, presionándolo 1 vez por cada comando del script que quede por ejecutar.

Mirar [aquí]() una captura de pantalla de la página inicial de la aplicación.

Para rearrancar la máquina virtual solo hay que ejecutar el script de nuevo con el mismo comando. Pasarán cerca de 3 minutos hasta que la aplicación esté corriendo de nuevo en la máquina virtual, lista para receber pedidos HTTP. El output final del script cuando ejecutado novamente deberá tener este [aspecto]().

Cuando la máquina virtual no sea más necesaria, eliminar todo su grupo de recursos para no gastar crédito innecesáriamente, lo que eliminará la máquina y recursos asociados. En alternativa, si se está utilizando ese grupo de recursos para más máquinas virtuales, eliminar solo los relacionados con este tutorial.
