# PROYECTO_DBP
Backend desarrollado con Spring Boot y PostgreSQL para el proyecto DBP.
## Requisitos
Antes de ejecutar el proyecto asegúrense de tener instalado:
* Java 21
* Docker Desktop
## Configuración del entorno
Es necesario crear un archivo `.env` en la raíz de tu  proyecto local con la siguiente configuración:

```env
APP_NAME=proyecto_dbp

DB_USERNAME=postgres
DB_PASSWORD=postgres
POSTGRES_DB=proyecto_dbp
DB_HOST=localhost
DB_PORT=5432

DDL_AUTO=create-drop
SHOW_SQL=true
FORMAT_SQL=true

JWT_SECRET=(aqui pones tu secret key de 32 caracteres o mas )
JWT_EXPIRATION_ACCESS=3600000
JWT_EXPIRATION_REFRESH=3600000
```
---

## Levantar PostgreSQL con Docker

Ejecutar el siguiente comando en la terminal que provee el propio intellij:
```
docker compose up -d
```

Esto iniciará una instancia de PostgreSQL usando Docker.

---

## Notas

Actualmente el proyecto utiliza:

```env
DDL_AUTO=create-drop
```

Esto significa que la base de datos se crea y destruye automáticamente al iniciar o detener la aplicación.

Por ahora esto facilita las pruebas y el desarrollo inicial, ya que todavía no estamos trabajando con datos reales o persistencia definitiva.

Más adelante probablemente cambiaremos esta configuración a:

```env
DDL_AUTO=update
```

o

```env
DDL_AUTO=validate
```

dependiendo de las necesidades del proyecto.
