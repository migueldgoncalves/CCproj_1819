# Script de Azure CLI

El principal objetivo del [hito 4](https://github.com/migueldgoncalves/CCproj_1819/milestone/4) es crear un script de provisionamiento que crie en el proveedor de cloud elegido una máquina virtual, la configure y la provisione. Para este hito se ha realizado un script que utiliza comandos de Azure CLI (la versión más actual, no el Azure classic CLI) para hacerlo, así como para desplegar en la máquina virtual la aplicación de este repositorio.

En el [hito 5](https://github.com/migueldgoncalves/CCproj_1819/milestone/5), el script se ha modificado para provisionar dos máquinas virtuales.

Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md) como ejecutar el script, así como los requisitos necesarios.

## Contenido del script

El contenido completo del script está presente [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Imprime para el terminal varias strings que ayudan quien ejecuta el script a percibir lo que está ocurriendo en las máquinas virtuales durante la ejecución del script. Retirando de él todas esas strings, así como los comentarios, se quedan estas líneas:

```
#!/bin/bash

REGION='uksouth'                                          #Región del centro de datos Azure donde se alojará la aplicación
GRUPO_NOMBRE='CCGroup'                                    #Nombre del grupo de recursos a crear
VM_NOMBRE_INFO='CCazureInfo'                              #Nombre a atribuir a la máquina virtual del microservicio de Información al Cliente
VM_NOMBRE_VIAJES='CCazureViajes'                          #Nombre a atribuir a la máquina virtual del microservicio de Gestión de Viajes
IMAGEN='Canonical:UbuntuServer:16.04-LTS:16.04.201812070' #Imagen a utilizar en la máquina virtual
SSH_LLAVE_RUTA='~/.ssh/id_rsa_azure.pub'                  #Ruta del fichero con la llave pública a utilizar
VM_TAMANO='Standard_B1s'                                  #Tamaño de la máquina virtual
DISCO_DATOS_TAMANO=1                                      #Tamaño del disco de datos de la máquina virtual, en GB
SSH_PUERTO=22                                             #NO CAMBIAR, puerto para comunicación vía SSH
SSH_PRIORIDAD=100                                         #Tiene que ser diferente de HTTP_PRIORIDAD
HTTP_PUERTO=80                                            #NO CAMBIAR, puerto para comunicación vía HTTP
HTTP_PRIORIDAD=110                                        #Tiene que ser diferente de SSH_PRIORIDAD
SUBSTRING='PublicIp'                                      #NO CAMBIAR
VM_INFO_IP_PUBLICO="$VM_NOMBRE_INFO$SUBSTRING"            #NO CAMBIAR, nombre del recurso con la dirección IP pública del microservicio de Información al Cliente
VM_VIAJES_IP_PUBLICO="$VM_NOMBRE_VIAJES$SUBSTRING"        #NO CAMBIAR, nombre del recurso con la dirección IP pública del microservicio de Gestión de Viajes
DNS_NOMBRE_INFO='ccazureinfo'                             #Nombre DNS a atribuir a la máquina virtual del microservicio de Información al Cliente
DNS_NOMBRE_VIAJES='ccazureviajes'                         #Nombre DNS a atribuir a la máquina virtual del microservicio de Gestión de Viajes
IP_ALOCACION='Dynamic'                                    #Puede ser 'Dynamic' o 'Static'
PLAYBOOK_RUTA='/etc/ansible/playbook.yml'                 #Ruta del fichero con el playbook Ansible

az group create -l $REGION -n $GRUPO_NOMBRE

az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO
az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO

az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO
az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --port $SSH_PUERTO --priority $SSH_PRIORIDAD
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --port $SSH_PUERTO --priority $SSH_PRIORIDAD

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD

az network public-ip update -g $GRUPO_NOMBRE -n $VM_INFO_IP_PUBLICO --dns-name $DNS_NOMBRE_INFO --allocation-method $IP_ALOCACION
az network public-ip update -g $GRUPO_NOMBRE -n $VM_VIAJES_IP_PUBLICO --dns-name $DNS_NOMBRE_VIAJES --allocation-method $IP_ALOCACION

ansible-playbook $PLAYBOOK_RUTA
```

## Explicación del script

La primera línea, `#!/bin/bash` indica al sistema que el fichero es un script a correr por la Bash shell.

El primero bloque de texto consiste en las variables del script, cuya explicación detallada se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md#variables-del-script).

El carácter $ indica a la Bash shell que se pretende utilizar el valor de la variable en el comando; `$REGION` será reemplazado por el valor de la variable `REGION`, en este caso `uksouth`, cuando el script sea corrido.

Todos los comandos de Azure CLI empiezan con `az`. Esto permite distinguirlos de los comandos de Azure classic CLI, cuyos comandos empiezan con `azure`.

El primero comando, `az group create -l $REGION -n $GRUPO_NOMBRE`, crea en Azure un grupo de recursos de nombre igual a `GRUPO_NOMBRE` en el centro de datos indicado en `REGION`. Las máquinas virtuales se incluirán en los recursos de ese grupo, así como otros necesarios a su funcionamiento como dos discos de datos y dos direcciones IP públicas. `group` indica que el comando gestionará grupos de recursos, y `create` indica que creará uno. La flag `-l` permite indicar la ubicación de las máquinas virtuales, y la flag `-n` el nombre de dicho grupo.

El segundo grupo de comandos, `az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE_* --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO`, crea dos máquinas virtuales en Azure. `vm` indica que el comando gestionará máquinas virtuales de Azure, y `create` indica que se creará una. La flag `-g` permite indicar el grupo de recursos al cual pertenecerá la máquina, en este caso el grupo creado en el comando anterior y cuyo nombre está almacenado en la variable `GRUPO_NOMBRE`. La flag `-n` permite indicar el nombre que se quiere atribuir a la máquina virtual. Hay que añadir una imagen a la máquina, lo que se hace indicándola con la flag `--image`, que puede por ejemplo recibir el URN de la imagen.

De modo a permitir la conexión vía SSH, cada máquina virtual tiene que recibir una llave pública, cuya llave privada correspondiente esté en la máquina local. La flag `--ssh-key-value` permite indicar o la ruta del fichero de dicha llave pública, o todo el contenido de esa llave. La flag `--size` permite especificar un tamaño para la máquina virtual recibiendo una string que identifique el tamaño deseado. Por fin se especifica un tamaño para el disco de datos de cada máquina virtual, utilizando la flag `--data-disk-sizes-gb` que recibe un número entero positivo.

Creadas las máquinas virtuales, el tercero grupo de comandos, `az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE_*`, arranca las máquinas virtuales utilizando la palabra `start`. La flag `-g` recibe el nombre del grupo de recursos dónde esté la máquina (pueden existir máquinas virtuales con el mismo nombre dentro de grupos distintos de recursos); la flag `-n` recibe el nombre de la máquina virtual a arrancar.

Las máquinas virtuales por defecto tienen los puertos 22 (SSH) y 80 (HTTP) cerrados, por lo que hay que abrirlos a la Internet con el cuarto grupo de comandos, `az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_* --port $SSH_PUERTO --priority $SSH_PRIORIDAD`, y con el quinto, `az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_* --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD`, respectivamente. `open-port` indica que el comando abrirá un puerto a la Internet. La flag `-g` recibe el grupo de recursos donde esté la máquina virtual, la flag `-n` recibe el nombre de la misma. El puerto a abrir se especifica con la flag `--port`, y la prioridad de la nueva regla en el conjunto de reglas de la firewall de la máquina virtual se indica con la flag `--priority`.

En este punto las máquinas ya pueden recibir tráfico del exterior vía SSH y HTTP, pero su dirección IP cambiará por defecto cada vez que se arranquen las máquinas. Esto se contorna añadiéndole un nombre DNS, con recurso al sexto grupo de comandos, `az network public-ip update -g $GRUPO_NOMBRE -n $VM_*_IP_PUBLICO --dns-name $DNS_NOMBRE_* --allocation-method $IP_ALOCACION`. `network` indica que se gestionarán recursos de red, `public-ip` especifica que se gestionarán direcciones IP públicas; y `update` indica que se actualizará una dirección IP pública. La flag `-g` recibe el grupo de recursos a que pertenece dicha dirección IP, la flag `-n` recibe el nombre de la dirección IP pública, la flag `--dns-name` recibe el nombre DNS a añadir a la dirección IP, y por fin la flag `--allocation-method` recibe el método de alocación de la dirección IP, si será estático o dinámico.

Las máquinas están ahora listas a ser provisionadas, lo que se hace con el último comando, `ansible-playbook $PLAYBOOK_RUTA`, que ejecuta el playbook cuya ruta estará en la variable `PLAYBOOK_RUTA`.
