# Justificación de elecciones

## Sistema cloud

La justificación del sistema cloud utilizado, Microsoft Azure, se encuentra [aquí](), en el primero párrafo.

## Region

Como es dicho [aquí](https://azure.microsoft.com/es-es/global-infrastructure/regions/), una región Azure consiste en un conjunto de centros de datos en una misma región física. Azure tiene docenas de centros de datos por todo el mundo, y elegir uno es una de las decisiones a tomar al crear una máquina virtual.

Para cumplir con los reglamentos de la Unión Europea sobre protección de datos, en particular el reciente Reglamento General de Protección de Datos, hay que instalar la aplicación en una máquina virtual ubicada en un centro de datos de la Unión Europea. La página indicada arriba indica la existencia de 8 regiones en la Unión Europea, todavía solo 5 eran aceptadas por Azure para ubicar una máquina virtual a la hora de realizar este hito, en Diciembre de 2018. Esas regiones son Centro de Francia; Oeste de Reino Unido; Sur de Reino Unido; Norte de Europa; y Europa Occidental.

El siguiente paso ha sido medir la latencia y velocidad de conexión en cada una de las regiones. Para eso se ha utilizado la herramienta [ApacheBench](https://httpd.apache.org/docs/2.4/programs/ab.html), una herramienta de benchmarking de servidores. La herramienta se instala con el comando `sudo apt install apache2-utils`.

En seguida se ha creado una máquina virtual sucesivamente en cada una de las 5 regiones a probar. Después de la creación de cada una, se ha ejecutado en la máquina local el comando `ab -n 5000 -c 500 http://ccazure.<region>.cloudapp.azure.com:80/`, siguiendo el tutorial presente [aquí](https://geekflare.com/web-performance-benchmark/), sección Apache. `ab` indica que es un comando de Apache Bench; la flag `-n` recibe el número de pedidos HTTP que se harán al servidor, en este caso 5000; la flag `-c` recibe el número de pedidos HTTP a enviar en simultáneo, en este caso 500; y el último argumento es el enlace y puerto del servidor a probar. Para cada región se ha creado y provisionado una máquina virtual, así como desplegado en ella la aplicación de este repositorio, utilizando el script de aprovisionamiento como viene [aquí](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh). Solamente se ha cambiado el valor de la variable REGION, que recibe la region Azure donde ubicar la máquina virtual. Así, el enlace de la máquina virtual ha sido siempre de la forma `http://ccazure.<region>.cloudapp.azure.com:80/`, donde <region> fue reemplazado por una de estas strings, `francecentral`, `uksouth`, `ukwest`, `northeurope`, `westeurope`, consoante la región Azure a probar.

Los resultados detallados se pueden mirar en la siguientes imagenes:

[Centro de Francia]()
[Sur de Reino Unido]()
[Oeste de Reino Unido]()
[Norte de Europa]()
[Europa Occidental]()

Se ha considerado el tiempo para contestar a todos los pedidos HTTP como el factor decisivo en la elección de la región. Los resultados se pueden ver en la siguiente tabla:

| Región              | Tiempo (segundos) |
| ------------------- | ----------------- |
| Centro de Francia   | 9.221             |
| Sur de Reino Unido  | 8.288             |
| Oeste de Reino Unido| 11.464            |
| Norte de Europa     | 11.175            |
| Europa Occidental   | 9.118             |

El centro de datos ubicado en el Sur de Reino Unido contestó a los pedidos en menos tiempo, 8.288 segundos, por lo que ha sido elegido para se ubicar la máquina virtual a crear y provisionar en el [hito 4](https://github.com/migueldgoncalves/CCproj_1819/milestone/4).

## Imágen

El [playbook](https://github.com/migueldgoncalves/CCproj_1819/blob/master/provision/hito3.yml) de Ansible a utilizar para provisionar la máquina virtual solo funciona en un sistema operativo Ubuntu 16.04. Ha sido probado, sin éxito, en máquinas con Ubuntu 18.04 y Debian. Así, la imágen a elegir tendría que tener ese sistema operativo.

Para obtener la lista de imagenes disponibles con ese sistema operativo, se ha utilizado el comando `az vm image list | jq '.[] | select( .offer | contains("buntu"))'`, disponible en los [apuntes de la asignatura](http://jj.github.io/CC/documentos/temas/Automatizando_cloud), sección 'CLI de Azure (versión 2.0)', al cual se ha añadido la flag `--all` para obtener todas las imagenes disponibles. El resultado fue una lista de todas las imagenes que contenian 'buntu' (podian tener 'Ubuntu' o 'ubuntu'), la cual fue enviada a un fichero `.txt`, resultando en más de 5000 líneas de texto.

En seguida se ha borrado a mano todas las imagenes que decian tener otras versiones de Ubuntu que no la 16.04, o que simplemente no indicaban la versión que indicaban, o que se destinaban a usos especificos como servidores SQL o de R. El resultado fue un fichero con cerca de 1000 líneas de texto, que se reduciran aún más eliminando todas excepto las versiones más recientes de las imagenes. Se obtuvieron cerca de 15 imagenes así, de las cuales foram excluídas todas las imagenes pagas. El resultado fue una lista de tan solo 3 imagenes:

```
{
  "offer": "UbuntuServer",
  "publisher": "Canonical",
  "sku": "16.04-LTS",
  "urn": "Canonical:UbuntuServer:16.04-LTS:16.04.201812070",
  "version": "16.04.201812070"
}
{
  "offer": "Ubuntu_Core",
  "publisher": "Canonical",
  "sku": "16",
  "urn": "Canonical:Ubuntu_Core:16:2017.0104.0",
  "version": "2017.0104.0"
}
{
  "offer": "ubuntu1605withimdsretryfix",
  "publisher": "MicrosoftTestLinuxPPS",
  "sku": "16.04-LTS",
  "urn": "MicrosoftTestLinuxPPS:ubuntu1605withimdsretryfix:16.04-LTS:16.04.27",
  "version": "16.04.27"
}
```

Al intentar crear una máquina virtual con cada una de las imagenes arriba, las dos últimas no fueron encontradas, como se puede ver [aquí]() para la del medio y [aquí]() para la de abajo. Solo la imágen 'UbuntuServer', de la empresa Canonical, fue encontrada por Azure. De esa forma, ha sido la elegida para las diferentes máquinas virtuales que se crearon en el ámbito del hito 4, y su URN es el valor de la variable `IMAGEN` del [script de aprovisionamiento](https://github.com/migueldgoncalves/CCproj_1819/blob/master/acopio.sh) creado para este hito.

## Tamaño de la máquina virtual

Consultando la oferta de tamaños de máquina virtual a la hora de crear una en Azure, es posible ordenalos por precio mensual como se puede ver [aquí](). Se puede verificar que el tamaño B1s de la oferta Estándar, con 1 virtual CPU y 1 GB de RAM, es la máquina más barata disponible con un coste mensual estimado de 8.16€, muy abajo del segundo tamaño más barato que es el A0 de la oferta Básico, que con 1 virtual CPU y 0,75 GB de RAM tiene un coste mensual estimado de 12.55€.

Dada la reducida complejidad y tráfico de la aplicación en su fase actual, así como el reducido número de programas necesarios para su funcionamiento, el tamaño B1s puede ser considerado suficiente para la máquina virtual a provisionar y donde desplegar la aplicación, por lo que ha sido el tamaño elegido. Por ese motivo, el valor de la variable `VM_TAMANO` del script de aprovisionamiento es `Standard_B1s`, para que sea aplicada la imágen B1s del grupo Estándar a la máquina virtual creada por el script.

## Tamaño del disco de datos

Igualmente debido a la reducida complejidad y tráfico de la aplicación en su fase actual, así como el reducido número de programas necesarios para su funcionamiento, el tamaño mínimo de 1 GB puede ser considerado suficiente para el disco de datos de la máquina virtual. Por ese motivo, la variable `DISCO_DATOS_TAMANO` del script de aprovisionamiento tiene valor 1, para aplicar un disco de datos de 1 GB a la máquina virtual.
