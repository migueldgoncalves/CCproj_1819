# Script de Azure CLI

Para el [hito 4](https://github.com/migueldgoncalves/CCproj_1819/milestone/4) he elegido Microsoft Azure como sistema cloud, tal como en el [hito 3](https://github.com/migueldgoncalves/CCproj_1819/milestone/3). Además de estar cubierto por el [contenido](http://jj.github.io/CC/documentos/temas/Automatizando_cloud) de la asignatura con ejemplos de comandos y explicaciones, cada alumno de la asignatura de Cloud Computing ha recibido crédito Azure para usar en la plataforma durante la realización de las prácticas. Además, Azure es un provedor de cloud popular y fiable, con múltiplas regiones en todo el mundo donde ubicar las máquinas virtuales, incluso existindo 5 dentro de la Unión Europea (al menos hasta el Brexit); así como una enorme cantidad de máquintas virtuales y imagenes distintas disponibles para elegir.

El principal objetivo del hito 4 es crear un script de aprovisionamiento que crie en el provedor de cloud elegido una máquina virtual, la configure y la provisione. Para este hito he realizado un script que utiliza comandos de Azure CLI (la versión más actual, no el Azure classic CLI) para hacerlo, así como desplegar en la máquina virtual la aplicación de este repositorio.

Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md) como ejecutar el script, así como los requisitos necesarios.

## Contenido del script

El contenido completo del script está presente [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Imprime para el terminal varias strings que ayudan el usuario a perceber que está ocurriendo en la máquina virtual durante la ejecución del script. Retirando de él todas esas strings, así como los comentários, nos quedamos con estas líneas:

```
#!/bin/bash

REGION='uksouth'
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

az group create -l $REGION -n $GRUPO_NOMBRE
az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO
az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $SSH_PUERTO --priority $SSH_PRIORIDAD
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD
az network public-ip update -g $GRUPO_NOMBRE -n $VM_IP_PUBLICO --dns-name $DNS_NOMBRE --allocation-method $IP_ALOCACION
ansible-playbook $PLAYBOOK_RUTA
```

La primera línea, `#!/bin/bash` indica al sistema que el fichero es un script a correr por la Bash shell.

El segundo bloque de texto consiste en las variables del script, cuya explicación detallada se puede encontrar [aquí]().

El caracter $ indica a la Bash shell que se pretende utilizar el valor de la variable en el comando; `$REGION` será reemplazado por el valor de la variable `REGION`, en este caso `uksouth`, cuando el script sea corrido.

Todos los comandos de Azure CLI empiezan con `az`. Esto permite distinguirlos de los comandos de Azure classic CLI, cuyos comandos empiezan con `azure`.

El primero comando, `az group create -l $REGION -n $GRUPO_NOMBRE`, crea en Azure un grupo de recursos de nombre igual a `GRUPO_NOMBRE` en el centro de datos indicado en `REGION`. La máquina virtual se incluirá en los recursos de ese grupo, así como otros necesarios a su funcionamiento como un disco de datos y una dirección IP pública. `group` indica que el comando gestionará grupos de recursos, y `create` indica que creará uno. La flag `-l` permite indicar la ubicación de la máquina virtual, y la flag `-n` el nombre de dicha máquina.

El segundo comando, `az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO`, crea una máquina virtual en Azure. `vm` indica que el comando gestionará máquinas virtuales de Azure, y `create` indica que se creará una. La flag `-g` permite indicar el grupo de recursos al cual pertenecerá la máquina, en este caso el grupo creado en el comando anterior y cuyo nombre está almacenado en la variable `GRUPO_NOMBRE`. La flag `-n` permite indicar el nombre que se quier atribuir a la máquina virtual. Hay que añadir una imágen a la máquina, lo que se hace indicándola con la flag `--image`, que puede por ejemplo recibir el URN de la imágen. De modo a permitir la conexión vía SSH, la máquina virtual tiene que recibir una llave pública, cuya llave privada correspondiente esté en la máquina local. La flag `--ssh-key-value` permite indicar o la ruta del fichero de dicha llave pública, o todo el contenido de esa llave. La flag `--size` permite especificar un tamaño para la máquina virtual recibiendo una string que identifique el tamaño deseado. Por fin se especifica un tamaño para el disco de datos de la máquina virtual, utilizando la flag `--data-disk-sizes-gb` que recibe un número entero positivo.

Creada la máquina virtual, el tercero comando, `az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE`, arranca la máquina virtual utilizando la palabra `start`. La flag `-g` recibe el nombre del grupo de recursos dónde esté la máquina (pueden existir máquinas virtuales con el mismo nombre dentro de grupos distintos de recursos); la flag `-n` recibe el nombre de la máquina virtual a arrancar.

La máquina virtual por defecto tiene los puertos 22 (SSH) y 80 (HTTP) cerrados, por lo que hay que abrirlos a la Internet con el cuarto comando, `az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $SSH_PUERTO --priority $SSH_PRIORIDAD`, y con el quinto, `az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD`. `open-port` indica que el comando abrirá un puerto a la Internet. La flag `-g` recibe el grupo de recursos donde esté la máquina virtual, la flag `-n` recibe el nombre de la misma. El puerto a abrir se especifica con la flag `--port`, y la prioridad de la nueva regla en el conjunto de reglas de la firewall de la máquina virtual se indica con la flag `--priority`.

En este punto la máquina ya puede recibir tráfico del exterior vía SSH y HTTP, pero su dirección IP cambiará por defecto cada vez que se arranque la máquina. Esto se contorna añadíndole un nombre DNS, con recurso al sexto comando, `az network public-ip update -g $GRUPO_NOMBRE -n $VM_IP_PUBLICO --dns-name $DNS_NOMBRE --allocation-method $IP_ALOCACION`. `network` indica que se gestionarán recursos de red, `public-ip` especifica que se gestionarán direcciones IP públicas; y `update` indica que se actualizará una dirección IP pública. La flag `-g` recibe el grupo de recursos a que pertenence dicha dirección IP, la flag `-n` recibe el nombre de la dirección IP pública, la flag `--dns-name` recibe el nombre DNS a añadir a la dirección IP, y por fin la flag `--allocation-method` recibe el método de alocación de la dirección IP, si será estático o dinámico.

La máquina está ahora lista a ser provisionada, lo que se hace con el último comando, `ansible-playbook $PLAYBOOK_RUTA`, que ejecuta el playbook cuya ruta estará en la variable `PLAYBOOK_RUTA`.
