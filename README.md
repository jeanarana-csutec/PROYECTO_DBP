# PROYECTO_DBP
Para poder correr el proyecto es necesario docker, en este caso necesitan crear sus propios .env y modifican el run para que funcione, aquí el .env que estuve usando:
APP_NAME=proyecto_dbp
DB_USERNAME=postgres
DB_PASSWORD=postgres
POSTGRES_DB=proyecto_dbp
DB_HOST=localhost
DB_PORT=5432
DDL_AUTO=create-drop
SHOW_SQL=true
FORMAT_SQL=true
JWT_SECRET=(Aqui le ponen su llave secreta de ustedes)
JWT_EXPIRATION_ACCESS=3600000
JWT_EXPIRATION_REFRESH=3600000
Considero que aun no necesitamos cargarnos de datos reales asi que por eso en DDL_AUTO y en el compose que subi docker crea y destruye la BD para poder testearlo.
