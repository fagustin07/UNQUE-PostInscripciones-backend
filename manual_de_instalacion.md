# Manual de instalacion.

## Requerimientos
  - [Docker](https://docs.docker.com/get-docker/) 🐳
  - [Docker Compose](https://docs.docker.com/compose/install/) 🐳

## Pasos

1. Clonamos el repositorio y entramos al mismo:

```bash
git clone https://github.com/fagustin07/UNQUE-PostInscripciones-backend
cd UNQUE-PostInscripciones-backend
```

2. Nos aseguramos de estar en la rama de trabajo `main`, ejecutando `git branch`. Deberiamos ver resaltada dicha rama, caso contrario ejecutar `git checkout main`.

3. Ejecutar el comando: 
```bash
cp .env.template .env
```

4. Podremos observar el archivo `.env` en el proyecto, lo editamos para configurar las siguientes variables de entorno:

   - Configuracion de la app:
     - `APP_PORT`: Puerto en el que se expondra la API.
     - `JWT_SECRET`: Clave secreta para segurizar los tokens.
     - `FRONTEND_URLS`: Host en el que se alojan los frontends que tendran permiso para interactuar con la app. Se debe respetar el formato de dejar llaves y comillas para colocar dentro los distintos host. Por ejemplo, con un host `{'http://localhost:4200'}`; o con dos o mas sera lo mismo pero sepandolos entre comas y SIN espacios, ejemplo: `{'http://localhost:3000,http://localhost:4200'}`.
     - `UNQUE_ADMIN_USER`: Usuario con el que el directivo ingresara a la app. Se recomienda colocar un correo real para luego integrar funcionalidades de seguridad. 
     - `UNQUE_ADMIN_PASSWORD`: Contrasenia para el usuario del directivo.

   - Configuracion del correo:
     - `APP_EMAIL_USER`: Correo que enviara codigos de confirmacion de identidad para que los alumnos den de alta su usuario en la app.
     - `APP_EMAIL_PASSWORD`: Contraseña del correo mencionado.
     - `APP_EMAIL_HOST`: Host del correo.
     - `APP_EMAIL_PORT`: Puerto del protocolo que utiliza el correo.

   - Configuracion de la DB:
     - `DB_HOST`: Nombre de la base de datos. Dejar el nombre por defecto ya que este nombre se utiliza dentro de la configuracion de Docker.
     - `DB_PORT`: Puerto que desee exponer la base de datos.
     - `DB_NAME`: Nombre de la base de datos.
     - `DB_ROOT_PASSWORD`: Contraseña del usuario root de la base de datos.

*Aclaracion: todas las variables de entorno poseen valores por defecto.*

5. Una vez configuradas las variables procedemos a correr la app:
```bash
docker-compose up
```

*Aclaracion: La primera vez que ejecutemos este comando tardara un tiempo debido a que debe buildear la imagen de la app*

6. Accedemos a `localhost:${APP_PORT}`, y deberiamos ver una interfaz de Swagger UI, que posee la documentacion de la API.

7. Listo, ya tenemos nuestro backend funcionando.
