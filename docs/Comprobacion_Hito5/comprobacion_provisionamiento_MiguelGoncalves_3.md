# Comprobación de la orquestación de Jorge Bonilla (@jabonillab)

La documentación de la comprobación de mi orquestación por Jorge se puede encontrar [aquí]().

## Creación y provisionamiento de la máquina virtual

Como se puede leer en el [repositorio](https://github.com/jabonillab/ProyectoCC2018#pruebas-y-test) de Jorge, su proyecto se despliega en una máquina virtual. Así, he copiado el contenido de su [Vagrantfile]() y de su [playbook de Ansible]() para dos ficheros con los mismos nombres en mi máquina local. He ejecutado entonces el comando `vagrant up` en ese directorio, que creó una máquina virtual en Azure y la provisionó enseguida con dicho playbook de Ansible.

El output de dicha creación y provisionamiento se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves_3/Creacion_provisionamiento.png).

## Arranque del microservicio

Para arrancar el microservicio, he empezado por acceder a la máquina virtual vía SSH. Enseguida he entrado en la carpeta raíz del proyecto `ProyectoCC2018`. Los siguientes comandos se han entonces ejecutado por esta orden:

```
. ~/ProyectoCC2018/venv/bin/activate
```

```
sudo iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 5000
```

```
export DATABASE_URL=postgres://jorgedb:12345678@127.0.0.1:5432/ccdb
```

```
gunicorn --bind 0.0.0.0:5000 wsgi
```

Con este último comando el servidor se arranca y se queda funcionando continuamente, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves_3/Arranque.png).

## Funcionamiento

Con la máquina virtual creada y provisionada y el microservicio arrancado, ya se puede acceder a la ruta raíz del servicio en su dirección IP o dirección DNS. La ruta raíz del microservicio retorna [este](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves_3/Funcionamiento.png) resultado.
