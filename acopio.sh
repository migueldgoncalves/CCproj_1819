#!/bin/bash

#Script de creación, configuración y provisionamiento de dos máquinas virtuales en Azure

#Variables
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

echo "Empezando creación de la máquina virtual..."
echo "Creando grupo de recursos $GRUPO_NOMBRE..."

az group create -l $REGION -n $GRUPO_NOMBRE

echo "Grupo de recursos $GRUPO_NOMBRE creado."
echo "Creando dos máquinas virtuales Azure..."

az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO
az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO

echo "Máquinas virtuales creadas."
echo "Arrancando máquinas virtuales..."

az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO
az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES

echo "Máquinas virtuales arrancadas."
echo "Abriendo puerto $SSH_PUERTO a la Internet..."

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --port $SSH_PUERTO --priority $SSH_PRIORIDAD
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --port $SSH_PUERTO --priority $SSH_PRIORIDAD

echo "Puerto $SSH_PUERTO abierto a la Internet."
echo "Abriendo puerto $HTTP_PUERTO a la Internet..."

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_INFO --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD
az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE_VIAJES --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD

echo "Puerto $HTTP_PUERTO abierto a la Internet."
echo "Añadiendo nombres DNS a las máquinas virtuales..."

az network public-ip update -g $GRUPO_NOMBRE -n $VM_INFO_IP_PUBLICO --dns-name $DNS_NOMBRE_INFO --allocation-method $IP_ALOCACION
az network public-ip update -g $GRUPO_NOMBRE -n $VM_VIAJES_IP_PUBLICO --dns-name $DNS_NOMBRE_VIAJES --allocation-method $IP_ALOCACION

echo "Nombres DNS añadidos a las máquinas virtuales"
echo "Provisionando máquinas virtuales..."

ansible-playbook $PLAYBOOK_RUTA
