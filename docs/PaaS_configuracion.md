## Configuración

Heroku, el PaaS elegido para el hito 2 del proyecto, necesita 2 ficheros de configuración para el despliegue del código que esté en un repositório GitHub, el app.json y el Procfile.

```{
  "name": "proyecto",
  "scripts": {
  },
  "env": {
    "PORT": {
      "required": true
    }
  },
  "formation": {
    "web": {
      "quantity": 1
    }
  },
  "addons": [

  ],
  "buildpacks": [
    {
      "url": "heroku/java"
    }
  ]
}
```

El fichero app.json, presentado arriba, se destina a describir aplicaciones web, es decir, variables de configuración, add-ons y cualquier otra información necesaria para que Heroku pueda correr la aplicación.

Esta tiene una única variable de configuración, `PORT`, que por omissión es requerida en el proyecto con `"required": true`. Se indica que se trata de una aplicación web con `"web"`, y en `"quantity": 1` se indica que haberá 1 processo que correrá la aplicación.

No hay add-ons que aplicar a la aplicación, por lo que no se indican ningunos. Por ultimo se indica el buildpack necesario para hacer la build de la aplicación, con el URL `heroku/java`.

`web: java $JAVA_OPTS -cp target/classes:target/dependency/* CC1819.Main`

El Procfile, presentado arriba, indica los comandos a ejecutar por las dynos de Heroku que correrán la aplicación. El tipo de processo `web` permite recibir tráfico HTTP. En este caso existe un solo comando, el necesario para correr la aplicación.
