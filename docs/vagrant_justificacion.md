# Justificación del Vagrantfile

El objetivo del [Hito 5](https://github.com/migueldgoncalves/CCproj_1819/milestone/5) ha sido crear un Vagrantfile capaz de reemplazar el [script](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh) creado en el [hito anterior](https://github.com/migueldgoncalves/CCproj_1819/milestone/4). El Vagrantfile creado se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/orquestacion/Vagrantfile).

## Vagrantfile

```
Vagrant.configure('2') do |config|

  config.vm.box = 'unaboxdummy'
  config.ssh.private_key_path = '~/.ssh/id_rsa_azure'
  
  config.vm.provider :azure do |azure, override|
    azure.tenant_id = ENV['AZURE_TENANT_ID']
    azure.client_id = ENV['AZURE_CLIENT_ID']
    azure.client_secret = ENV['AZURE_CLIENT_SECRET']
    azure.subscription_id = ENV['AZURE_SUBSCRIPTION_ID']
    
    azure.resource_group_name = "CCGroup"
    azure.location = "uksouth"
    azure.vm_size = "Standard_B1s"
    azure.vm_image_urn = "Canonical:UbuntuServer:16.04-LTS:16.04.201812070"
    azure.tcp_endpoints = [22, 80]
  end
  
  config.vm.define "info" do |info|
    config.vm.provider :azure do |azureinfo, override|
      azureinfo.vm_name = "info"
      azureinfo.dns_name = "ccazureinfo"
      azureinfo.data_disks = [
         {
           name: "discoinfo", 
           size_gb: 1
         }
      ]
    end
  end
  
  config.vm.define "viajes" do |viajes|
    config.vm.provider :azure do |azureviajes, override|
      azureviajes.vm_name = "viajes"
      azureviajes.dns_name = "ccazureviajes"
      azureviajes.data_disks = [
         {
           name: "discoviajes", 
           size_gb: 1
         }
      ]
    end
    
    config.vm.provision "ansible" do |ansible|
      ansible.playbook = "playbook.yml"
      ansible.inventory_path = "hosts"
      ansible.limit = "all"
    end
  end
end
```

## Justificación

Como se puede leer en los [apuntes de la asignatura](http://jj.github.io/CC/documentos/temas/Orquestacion#orquestaci%C3%B3n-de-m%C3%A1quinas-virtuales), Vagrant no es más que un DSL construido sobre Ruby, por lo que la estructura de un Vagrantfile es idéntica a la de un fichero en Ruby.

### Configuración común

El Vagrantfile empieza con `Vagrant.configure('2') do |config|`, que define un bloque de Ruby `config` dentro del cual está el resto del Vagrantfile. El número 2 corresponde a su versión; [Vagrant 2.0](https://www.hashicorp.com/blog/hashicorp-vagrant-2-0) fue lanzado en Septiembre 2017. En particular, he utilizado Vagrant 2.2.3 para realizar este hito.

Vagrant siempre necesita una [box](https://www.vagrantbox.es/) para crear máquinas virtuales; si se está trabajando con Azure se usarán imágenes de Azure, pero Vagrant sigue necesitando una 'box dummy' para funcionar, en caso contrario dará [error](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Orquestacion/No_box.png). Una box dummy se puede encontrar en https://github.com/azure/vagrant-azure/raw/v2.0/dummy.box. Se añade con la línea `config.vm.box = '<nombre>'`, donde el nombre tendrá que ser el de la box que hayamos añadido previamente. Esa box puede tener cualquier nombre, aquí he elegido `unaboxdummy`. `config` dice respecto al bloque actual, y `vm` indica que estamos lidiando con máquinas virtuales.

Abajo se tiene la línea `config.ssh.private_key_path = '~/.ssh/id_rsa_azure'`, que recibe el path hasta el fichero con la llave privada a utilizar por Vagrant al comunicar con Azure. `ssh` especifica que se está lidiando con SSH, pues es así que Vagrant comunicará con Azure.

La mayor parte del resto del Vagrantfile dice respecto a Azure, donde alguna de su configuración es común a las dos máquinas virtuales y por lo tanto se encuentra más arriba. Existen 3 bloques dentro del bloque principal `config`: `azure`, el bloque con la configuración común a las dos máquinas; `info`, con la configuración específica de la máquina del microservicio de Información al Cliente; y `viajes`, con la configuración específica de la máquina del microservicio de Gestión de Viajes. Todos esos bloques utilizan el argumento `override`, que según la [documentación](https://www.vagrantup.com/docs/providers/configuration.html#overriding-configuration) de HashiCorp sobre Vagrant permite hacer override por un proveedor de configuración no específica de un proveedor como Azure. `provider` permite especificar el proveedor, en este caso Azure.

Para comunicarse con Azure, Vagrant necesita 4 valores en particular: el ID del tenant(`AZURE_TENANT_ID`), el ID de la aplicación cliente(`AZURE_CLIENT_ID`), la contraseña de la aplicación cliente(`AZURE_CLIENT_SECRET`) y el ID de la suscrición Azure a utilizar(`AZURE_SUBSCRIPTION_ID`). Hay dos formas de enviar esos valores a Vagrant: mediante variables de entorno definidas previamente (tienen que tener esos nombres) o introduciendo directamente los valores. Si el Vagrantfile se destina a ser compartido, la primera opción es mejor por razones de seguridad.

El resto del bloque consiste en parámetros que ya se definían en el [script de provisionamiento](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh) creado en el Hito 4: el nombre del grupo de recursos donde crear las máquinas (`resource_group_name`), el centro de datos donde ubicar los recursos (`location`), el tamaño de la máquina virtual (`vm_size`), la imagen a aplicar a las máquinas virtuales (`vm_image_urn`) y los puertos a abrir al público (`tcp_endpoints`).

Esos parámetros tienen los mismos valores que los utilizados en el script de provisionamiento, y por lo tanto su proceso de elección es el mismo y se puede encontrar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md).

### Configuración específica de la MV del microservicio de Información al Cliente

El contenido del bloque `info` es solamente un otro bloque `azureinfo`, ese relacionado con Azure. En él se define el nombre de la máquina virtual (`vm_name`), que tiene que ser distinto de cualquier otro en el mismo grupo; el nombre DNS a asignar a la máquina (`dns_name`), que tiene que ser único en el centro de datos, y un disco de datos a ser utilizado por la máquina virtual (`data_disks`). El disco es definido con un JSON donde, en este Vagrantfile, se define su nombre y tamaño en GB; el tamaño del disco es también igual a los discos creados por el script de provisionamiento y por lo tanto objecto de su [justificación](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/justificacion_hito4.md).

### Configuración específica de la MV del microservicio de Gestión de Viajes

El contenido del bloque `viajes` empieza por ser también un bloque de configuración de Azure `azureviajes` donde los parámetros son exactamente los mismos del bloque `azureinfo`. La diferencia es el bloque de provisionamiento que se sigue, donde se indica que se utilizará Ansible como provisionador.

En ese bloque, con `playbook` se indica el path del playbook a utilizar. Ese [playboook](https://github.com/migueldgoncalves/CCproj_1819/blob/master/orquestacion/playbook.yml) es una copia exacta del [ubicado](https://github.com/migueldgoncalves/CCproj_1819/blob/master/provision/playbook.yml) en `/provision` y está ubicado también en la carpeta `orquestation`, tal como el Vagrantfile.

Como indicado [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/vagrantfile_funcionamiento.md), se espera que esta máquina virtual sea la última a arrancarse, por lo que tiene sentido que el playbook se ejecute después de su creación.

En Vagrant se puede indicar un fichero de hosts de Ansible o dejar que Vagrant cree uno automáticamente; aquí he optado por suministrar el [fichero de hosts](https://github.com/migueldgoncalves/CCproj_1819/blob/master/orquestacion/hosts), que se encuentra en el mismo directorio del Vagrantfile y tiene exactamente los hosts a añadir para ejecutar el playbook de Ansible manualmente, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/provisionamiento_manual.md#instalaci%C3%B3n-y-configuraci%C3%B3n-de-ansible).

Vagrant por defecto provisiona solamente la máquina `default` mismo que el playbook de Ansible tenga otros grupos; ese comportamiento se puede detener con la línea `ansible.limit="all"` para que todas las máquinas en los grupos del playbook de Ansible sean provisionadas.

## Tutoriales y ayudas

Mis primeros experimentos con un Vagrantfile los he hecho basado en los [apuntes de la asignatura](http://jj.github.io/CC/documentos/temas/Orquestacion).

Para integrar Vagrant con Ansible, la propia [documentación](https://www.vagrantup.com/docs/provisioning/ansible.html) de HashiCorp, la desarrolladora de Vagrant, ha sido mí principal tutorial. Excepción para la línea `ansible.limit = "all"`, que he introducido después de encontrar [esta](https://github.com/jlund/mazer-rackham/issues/7) issue de Github.

Para integrar Vagrant con Azure, [este](https://blog.scottlowe.org/2017/12/11/using-vagrant-with-azure/) tutorial y la documentación del [repositorio](https://github.com/Azure/vagrant-azure) del plugin `vagrant-azure` han sido mis principales tutoriales.

Por fin, para ejecutar Vagrant en más de una máquina, también la propia [documentación](https://www.vagrantup.com/docs/multi-machine/) de HashiCorp sobre Vagrant ha sido mi principal tutorial.

Las explicaciones teóricas de este fichero vienen de las fuentes arriba indicadas.