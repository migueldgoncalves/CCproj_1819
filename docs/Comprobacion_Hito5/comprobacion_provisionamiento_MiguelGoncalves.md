# Comprobación de la orquestación de Andrea Morales (@andreamorgar)

El documento de la comprobación por parte de @andreamorgar de mi orquestación se puede leer [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/comprobacion_provisionamiento_AndreaMorales.md).

## Creación de máquinas virtuales y su provisionamiento

Primeramente, he accedido al repositorio de Andrea en [este](https://github.com/andreamorgar/ProyectoCC) enlace e hice clone del mismo en mi Desktop. Enseguida accedí a la documentación de su orquestación presente en [este](https://github.com/andreamorgar/ProyectoCC/blob/master/docs/orquestacion_mv.md) enlace, donde están las instrucciones para crear y provisionar dos máquinas virtuales con el proyecto de Andrea.

Una de las máquinas, `maquinaservicios`, contendrá su servicio principal. Será la primera máquina a ser arrancada y provisionada. La segunda máquina, `maquinamongodb`, contendrá la base de datos MongoDB de dicho servicio, y se creará y provisionará solo cuando la primera máquina esté totalmente funcional.

Para empezar la creación y provisionamiento de las máquinas, he ido con un terminal hasta la carpeta `/ProyectoCC/orquestacion` del proyecto de Andrea y he ejecutado el comando `vagrant up --no-parallel`. Como nos es explicado en el último párrafo de [este](https://github.com/andreamorgar/ProyectoCC/blob/master/docs/orquestacion_mv.md#orquestaci%C3%B3n-de-m%C3%A1quinas-virtuales-1) tópico, la opción `--no-parallel` es necesaria para que las máquinas no sean creadas en paralelo, lo que daría un error al estar en el mismo grupo de recursos.

Al fin de algunos minutos la creación y provisionamiento de las máquinas se completó con éxito. [Aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Creacion_provisionamiento_maquina1.png) se puede ver que la máquina 1, la del servicio principal, se ha creado y provisionado con 2 playbooks de Ansible. [Aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Creacion_provisionamiento_maquina2.png) se puede ver que la máquina 2, la de la base de datos, se ha creado y provisionado posteriormente con un tercero playbook de Ansible.

## Funcionamiento

Continuando con las instrucciones presentes [aquí](https://github.com/andreamorgar/ProyectoCC/blob/master/docs/orquestacion_mv.md#funcionamiento--y-despliegue-del-proyecto), he entrado vía SSH en la máquina principal y ejecutado el servicio, que he mantenido funcionando en ese terminal durante el resto de la comprobación del funcionamiento. Mirar [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina1_1.png) la ejecución del microservicio. Enseguida he entrado vía SSH en la máquina de la base de datos donde he comprobado su correcto funcionamiento, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina2_1.png).

El paso siguiente ha sido comprobar las rutas de la aplicación a partir de mi máquina. Tanto la ruta raíz como la ruta `/predictions` han retornado los resultados esperados de acuerdo con las imágenes del repositorio de Andrea, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina1_2.png). Enseguida he consultado vía SSH los logs de la máquina de la base de datos, que como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina2_2.png) indican que ha aceptado conexiones de la dirección IP `10.0.0.4`. Volviendo a la máquina del servicio principal, he conferido que efectivamente tenía una dirección IP privada `10.0.0.4`, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina1_3.png).

El último paso en la comprobación del funcionamiento ha sido hacer un pedido POST a la máquina del servicio principal a partir de mi máquina, que ha tenido éxito como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina_local.png). Enseguida he accedido a la máquina de la base de datos, donde he comprobado que el contenido enviado en el pedido había sido introducido en la base de datos, como se puede ver [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/docs/Comprobacion_Hito5/images_provision_MiguelGoncalves/Funcionamiento_maquina2_3.png).

Así, he podido comprobar que la orquestación de Andrea estaba debidamente realizada y probada.

## Comentario

Creo que Andrea ha realizado una comprobación de su orquestación no solo detallada sino también muy bien documentada mediante todas las capturas de pantalla que se justificaban, lo que me ha facilitado la labor de realizar la comprobación.

Creo también que es inteligente de su parte separar en máquinas distintas el servicio y la base de datos, una vez que si uno falla el otro no sale afectado. Además, facilita el escalado de uno sin tenerse que escalar el otro por igual, entre otras ventajas.