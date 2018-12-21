#!/bin/bash

#Script de creación, configuración y provisionamiento de una máquina virtual en Azure

#Variables
REGION='uksouth'                                          #Región del centro de datos Azure donde se alojará la aplicación
GRUPO_NOMBRE='CCGroup'                                    #Nombre del grupo de recursos a crear
VM_NOMBRE='CCazure'                                       #Nombre a atribuir a la máquina virtual
IMAGEN='Canonical:UbuntuServer:16.04-LTS:16.04.201812070' #Imagen a utilizar en la máquina virtual
SSH_LLAVE_RUTA='~/.ssh/id_rsa_azure.pub'                  #Ruta del fichero con la llave pública a utilizar
VM_TAMANO='Standard_B1s'                                  #Tamaño de la máquina virtual
DISCO_DATOS_TAMANO=1                                      #Tamaño del disco de datos de la máquina virtual, en GB
SSH_PUERTO=22                                             #NO CAMBIAR, puerto para comunicación vía SSH
SSH_PRIORIDAD=100                                         #Tiene que ser diferente de HTTP_PRIORIDAD
HTTP_PUERTO=80                                            #NO CAMBIAR, puerto para comunicación vía HTTP
HTTP_PRIORIDAD=110                                        #Tiene que ser diferente de SSH_PRIORIDAD
SUBSTRING='PublicIp'                                      #NO CAMBIAR
VM_IP_PUBLICO="$VM_NOMBRE$SUBSTRING"                      #NO CAMBIAR, nombre del recurso con la dirección IP pública
DNS_NOMBRE='ccazure'                                      #Nombre DNS a atribuir a la máquina virtual
IP_ALOCACION='Dynamic'                                    #Puede ser 'Dynamic' o 'Static'
PLAYBOOK_RUTA='/etc/ansible/playbook.yml'                 #Ruta del fichero con el playbook Ansible

echo "Empezando creación de la máquina virtual..."
echo "Creando grupo de recursos $GRUPO_NOMBRE..."

az group create -l $REGION -n $GRUPO_NOMBRE

echo "Grupo de recursos $GRUPO_NOMBRE creado."
echo "Creando una máquina virtual Azure..."

az vm create -g $GRUPO_NOMBRE -n $VM_NOMBRE --image $IMAGEN --ssh-key-value $SSH_LLAVE_RUTA --size $VM_TAMANO --data-disk-sizes-gb $DISCO_DATOS_TAMANO

echo "Máquina virtual $VM_NOMBRE creada."
echo "Arrancando máquina virtual $VM_NOMBRE..."

az vm start -g $GRUPO_NOMBRE -n $VM_NOMBRE

echo "Máquina virtual $VM_NOMBRE arrancada."
echo "Abriendo puerto $SSH_PUERTO a la Internet..."

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $SSH_PUERTO --priority $SSH_PRIORIDAD

echo "Puerto $SSH_PUERTO abierto a la Internet."
echo "Abriendo puerto $HTTP_PUERTO a la Internet..."

az vm open-port -g $GRUPO_NOMBRE -n $VM_NOMBRE --port $HTTP_PUERTO --priority $HTTP_PRIORIDAD

echo "Puerto $HTTP_PUERTO abierto a la Internet."
echo "Añadiendo nombre DNS $DNS_NOMBRE a la máquina virtual $VM_NOMBRE..."

az network public-ip update -g $GRUPO_NOMBRE -n $VM_IP_PUBLICO --dns-name $DNS_NOMBRE --allocation-method $IP_ALOCACION

echo "Nombre DNS $DNS_NOMBRE añadido a la máquina virtual $VM_NOMBRE"
echo "Provisionando máquina virtual $VM_NOMBRE..."

ansible-playbook $PLAYBOOK_RUTA
