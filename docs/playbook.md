# Playbook de Ansible

Para realizar el Hito 3 he elegido Ansible como sistema de gestión de configuración. Se trata de una herramienta más sencilla de usar que Salt, y además está cubierta tanto por parte de los apuntes de la clase como por un [seminario](https://www.meetup.com/es-ES/Granada-Geek/events/255973562/?rv=ea1_v2&_xtd=gatlbWFpbF9jbGlja9oAJDViZWU1ZjJmLWViZDAtNGMwMi05ZTI3LTkxODY0M2YwZjYzNw) de la asignatura dedicado a la herramienta.

El microservicio actualmente en desarrollo, el de Información al Cliente, está basado en Java y usará una base de datos MongoDB, aunque no se haya de momento implementado dicho uso de la base de datos. De esa manera, necesitará no sólo de Git y de MongoDB, sino también de Java y Maven, este último para la gestión de las dependencias de la aplicación.

## Playbook

```
---
- hosts: azure
  become: yes
  tasks:
   - name: Install Java 8
     apt:
      name: default-jdk
      state: present

   - name: Install Git
     apt:
      name: git
      state: present

   - name: Install Maven
     apt:
      name: maven
      state: present

   - name: Add MongoDB public GPG key
     apt_key:
      keyserver: hkp://keyserver.ubuntu.com:80
      id: 9DA31620334BD75D9DCB49F368818C72E52529D4
      state: present

   - name: Add MongoDB repository
     lineinfile:
      line: "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/4.0 multiverse"
      dest: /etc/apt/sources.list.d/mongodb-org-4.0.list
      state: present
      create: yes

   - name: Install MongoDB
     apt:
      name: mongodb-org
      state: present
      update_cache: yes

   - name: Start MongoDB service
     service:
      name: mongod
      state: started

   - name: Clone Git project repository
     git:
      dest: Documents/CC1819
      repo: 'https://github.com/migueldgoncalves/CCproj_1819.git'
      version: hito1-reenvio2

   - name: Install application using Maven
     environment:
      PORT: 80
     shell: mvn clean install
     args:
      chdir: Documents/CC1819

   - name: Run application using Maven
     environment:
      PORT: 80
     shell: mvn exec:java -Dexec.mainClass="CC1819.Main"
     args:
      chdir: Documents/CC1819
...
```
Ansible permite especificar las configuraciones y estados a obtener en las máquinas gestionadas utilizando recetas llamadas playbooks, con distintas tareas que indican a Ansible lo que hacer. Arriba se tiene el playbook `playbook.yml`, utilizado para provisionar una máquina virtual Azure con el necesario para correr esta aplicación en su estado actual más su despliegue en dicha máquina.

`---` y `...` delimitan el fichero .yml, el primero empiézalo y el segundo terminalo. Se sigue `- hosts: azure`, que indica que la receta se aplicará a los hosts del grupo `azure`, de acuerdo con el indicado en el fichero `hosts`. En este caso, el grupo `azure` contiene apenas la dirección DNS de la máquina virtual Azure ya creada y en ejecución. Siendo la primera instrucción del playbook, hay que empezarla con un `-`. Después, `become: yes` indica a Azure que hay que tornarse root para ejecutar las tasks. Por fim, `tasks:` delimita la lista de tareas del playbook.

### Instalación del Java 8, Git y Maven

```
- name: Install Java 8
  apt:
   name: default-jdk
   state: present

- name: Install Git
  apt:
   name: git
   state: present

- name: Install Maven
  apt:
   name: maven
   state: present
```
Las primeras tareas del playbook instalam respectivamente el Java 8 (mientras no se actualice el paquete `default-jdk`), Git y Maven. Todas las tareas Ansible son empezadas con un `-`, seguidas de su nombre.
`apt` es un módulo Ansible para la gestión de paquetes `apt`, que en estas tareas recibe 2 parámetros: `name`, que corresponde al nombre del paquete; y `state`, dónde se indica el estado deseado del paquete. `present` es el estado por defecto, se asegura de que el paquete está instalado y una vez instalado no lo actualizará mismo que exista una versión más reciente.

### Instalación y arranque del MongoDB

```
- name: Add MongoDB public GPG key
  apt_key:
   keyserver: hkp://keyserver.ubuntu.com:80
   id: 9DA31620334BD75D9DCB49F368818C72E52529D4
   state: present

- name: Add MongoDB repository
  lineinfile:
   line: "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/4.0 multiverse"
   dest: /etc/apt/sources.list.d/mongodb-org-4.0.list
   state: present
   create: yes

- name: Install MongoDB
  apt:
   name: mongodb-org
   state: present
   update_cache: yes

- name: Start MongoDB service
  service:
   name: mongod
   state: started
```
Una vez que el paquete oficial de MongoDB, `mongodb-org`, no está disponible por defecto en los repositorios Ubuntu, hay que añadir el repositorio oficial antes de instalar MongoDB. Existe un paquete de MongoDB disponible por defecto para Ubuntu, `mongodb`, todavía no es mantenido de forma oficial por MongoDB.

La primera tarea utiliza el módulo `apt-key` para añadir a la máquina la llave pública del repositorio de MongoDB. Todos los repositorios Ubuntu deben estar firmados por una llave privada por razones de seguridad. El módulo recibe como parámetros `keyserver`, que indica al servidor donde obtener la llave; `id`, el id de la llave a obtener, y `state`, que en este caso es igual a `present` para garantizar que la llave está presente en la máquina.

Teniendo ya la llave, la segunda tarea incluí el repositorio en la lista de repositorios de la máquina, creando un fichero en el directorio de fuentes de la máquina y añadiéndole una línea de texto con el enlace del repositorio. `lineinfile` es un módulo Ansible que garantiza que una determinada línea de texto está presente en un fichero. `line` indica la línea a añadir; `dest` el fichero que recibirá la línea; `state: present` garantiza que la línea está presente en el fichero; y `create: yes` crea el fichero si no existe, lo que va a ocurrir en la primera vez que se ejecute el playbook.

La tercera tarea instala entonces el MongoDB en el sistema, utilizando ya el paquete oficial. La diferencia con las instalaciones de Java, Git y Maven es la presencia de `update_cache: yes`, que ejecuta el equivalente a `apt-get update` en el sistema de modo a descargar el paquete antes de la instalación.

La utilización de MongoDB requiere que su servicio sea iniciado previamente; la cuarta tarea se encarga de eso usando el módulo Ansible `service`. `name` indica el nombre del servicio a gestionar, siendo `mongod` el servicio que queremos iniciar; y `state: started` garantiza que el servicio está iniciado o que se inicie si no está.

### Despliegue de la aplicación

```
- name: Clone Git project repository
  git:
   dest: Documents/CC1819
   repo: 'https://github.com/migueldgoncalves/CCproj_1819.git'
   version: hito1-reenvio2

- name: Install application using Maven
  environment:
   PORT: 80
  shell: mvn clean install
  args:
   chdir: Documents/CC1819

- name: Run application using Maven
  environment:
   PORT: 80
  shell: mvn exec:java -Dexec.mainClass="CC1819.Main"
  args:
   chdir: Documents/CC1819
```
Teniendo todos los paquetes necesarios instalados, ya se puede desplegar la aplicación. En la primera tarea, el módulo `git` permite hacer clone del repositorio indicado en `repo` para el directorio indicado en `dest`. `version` es un parámetro opcional, y indica la versión del repositorio a descargar.

La segunda tarea instala la aplicación utilizando la herramienta de gestión de dependencias Maven. Por defecto la aplicación se ejecutará en el puerto 7000; esto se cambia creando una variable de entorno con el puerto deseado, en este caso el 80, lo que se hace con la palabra-llave `environment`, seguida del nombre de la variable `PORT` y de su valor `80`. En seguida se instala la aplicación con el comando `mvn clean install`, que es pasado por una shell `/bin/sh` usando el parámetro `shell`. `chdir: Documents/CC1819` indica que hay que cambiar al directorio `Documents/CC1819`, donde hemos clonado el repositorio de la aplicación, antes de ejecutar el comando. Aunque la ejecución de la aplicación solo se realice en la última tarea, y que `environment` solo cambie variables de entorno dentro de una única tarea, es necesario crear la variable de entorno `PORT` también en la instalación de la aplicación para que los tests de la misma se ejecuten sobre el puerto 80 y no el 7000 por defecto.

Por fin, la tercera tarea ejecuta la aplicación. De nuevo se crea la variable de entorno `PORT` con el valor `80`, y de novo el comando indicado en `shell` se ejecuta en el directorio `Documents/CC1819`. `mvn exec:java` ejecuta la aplicación utilizando Java; `-Dexec.mainClass="CC1819.Main` indica que el método main() de la aplicación se encuentra en el fichero `CC1819.Main`.
