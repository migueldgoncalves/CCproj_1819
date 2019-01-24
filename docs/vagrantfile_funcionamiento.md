# Ejecución del Vagrantfile

## Requisitos

Ejecutar este [Vagrantfile](https://github.com/migueldgoncalves/CCproj_1819/blob/master/orquestacion/Vagrantfile) requiere que se instale [Vagrant](https://www.vagrantup.com/), [Ansible](https://www.ansible.com/) y [Azure CLI](https://docs.microsoft.com/es-es/cli/azure/install-azure-cli?view=azure-cli-latest), además de crearse una llave RSA con determinadas características.

Seguir este tópico [Preparación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_auto.md#preparaci%C3%B3n) del tutorial creado para el hito 4. El Vagrantfile asume que la llave privada está en el fichero `~/.ssh/id_rsa_azure`, cambiarlo si no es así. No es necesario crear un fichero de hosts ni copiar el playbook, ya están en el mismo directorio del Vagrantfile.

Con Ansible instalado y el par de llaves creado, hay que instalar Vagrant. Es directo: basta acceder a [este](https://www.vagrantup.com/downloads.html) enlace, descargar el fichero de instalación adecuado al sistema operativo y instalarlo.

NOTA: Puede ser necesario cambiar permisos de ficheros y carpetas para que estos comandos de Vagrant se ejecuten sin serse root.

Con Vagrant instalado, hay que instalarle el [plugin](https://github.com/Azure/vagrant-azure) `vagrant-azure` para permitir su integración con Azure. Se hace con el comando `vagrant plugin install vagrant-azure`

Enseguida hay que añadir una 'box dummy' a Vagrant con este comando:

```
vagrant box add <nombre> https://github.com/azure/vagrant-azure/raw/v2.0/dummy.box --provider azure
```

El nombre puede ser uno cualquier. El Vagrantfile espera que el nombre sea `unaboxdummy`, cambiarlo si es otro.

Para que Vagrant se integre con Azure, es necesario crear un [Azure Active Directory service principal](https://docs.microsoft.com/en-us/azure/active-directory/develop/app-objects-and-service-principals) con el comando `az ad sp create-for-rbac`. El output es importante, por lo que no debemos hacer `reset` al terminal de momento.

Hay 4 variables de entorno que hay que asignar antes de ejecutarse el Vagrantfile: `AZURE_TENANT_ID`, `AZURE_CLIENT_ID`, `AZURE_CLIENT_SECRET` y `AZURE_SUBSCRIPTION_ID`. Las tres primeras se obtienen con el comando indicado arriba: son respectivamente los parámetros `tenant`, `appId` y `password` del JSON que sirve de output de ese comando. La cuarta se obtiene con el comando `az account list`: buscase el JSON de la suscrición que elegimos para crear las máquinas virtuales y obtenemos el parámetro `Id`.

Las variables de entorno se pueden crear para una dada sesión con `export <VARIABLE_NOMBRE>=<VARIABLE_VALOR>`, por ejemplo `export A=1`. También se pueden tornar permanentes añadiendo esa misma línea al fichero `~/.profile`, aunque necesiten que se reinicie el ordenador para tornarse efectivas. Su valor se puede obtener con el comando `echo $<VARIABLE_NOMBRE>`, por ejemplo `echo $A`.

## Creación y provisionamiento de las máquinas virtuales

Hacer clone de este repositorio para un cualquier directorio y entrar con un terminal en el directorio `/orquestation`. En él ejecutar el comando `vagrant up info`, que arrancará la máquina virtual destinada al microservicio de Información al Cliente.

NOTA: Me ha dado error 'muy temprano' ('very early') al ejecutar el comando `vagrant up` con `sudo`.

En cerca de 3 minutos la máquina estará creada y el Vagrantfile terminará su ejecución. Ejecutar entonces el comando `vagrant up viajes`, que además de crear la máquina virtual destinada al microservicio de Gestión de Viajes también ejecutará el playbook que provisionará las dos máquinas, ahora que las dos ya están ejecutándose. Las máquinas tardarán un total de 10 minutos a crearse y provisionarse y a ejecutar los microservicios.

Al crearse las dos máquinas el output deberá ser [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Provisionamiento_1.png), al paso que al terminar la ejecución del playbook el output deberá ser [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Provisionamiento_2.png).

## Comprobación del funcionamiento

Los microservicios se quedan ejecutando después de la ejecución del playbook. Una consulta a las rutas raíz de los microservicios de Información al Cliente y Gestión de Viajes (respectivamente `ccazureinfo.uksouth.cloudapp.azure.com` y `ccazureviajes.uksouth.cloudapp.azure.com`) deberá retornar [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Ruta_raiz_info.png) y [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Ruta_raiz_viajes.png) resultados, respectivamente. Una consulta a la ruta `/viajes` de cada microservicio deberá retornar [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Ruta_viajes_info.png) y [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/Ruta_viajes_viajes.png) resultados.

Los microservicios son borrados con el comando `vagrant destroy`. Tardan 5 minutos en borrarse, así como a todos sus recursos.

## Fuentes

Para crear este tutorial, en especial para obtener los comandos, se ha seguido [este](https://blog.scottlowe.org/2017/12/11/using-vagrant-with-azure/) tutorial.