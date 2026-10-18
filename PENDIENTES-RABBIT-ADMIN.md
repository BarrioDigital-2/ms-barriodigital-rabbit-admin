# Pendientes de configuracion: Rabbit Admin

La integracion usa por ahora los valores de la guia: RabbitMQ en `localhost`, AMQP en `5672`, Management en `http://localhost:15672` y valores de respaldo `guest/guest`. No se almacenan credenciales reales en este repositorio.

## Antes de conectar con el cluster de tu entorno

- [ ] Configurar `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME` y `RABBITMQ_PASSWORD` con los valores del cluster.
- [ ] Configurar `RABBITMQ_MANAGEMENT_URL` con la URL accesible desde este servicio, normalmente `http://localhost:15672` en ejecucion local.
- [ ] Definir donde vivira el `.env` local. El comando de la guia lo busca en `../.env` desde la carpeta del servicio; ajustar esa ruta si el archivo queda en otra ubicacion.
- [ ] Mantener las credenciales fuera de Git. No agregar el `.env` con secretos al repositorio.
- [ ] Confirmar que el mismo usuario y clave permiten conectarse por AMQP y autenticarse en la API HTTP de Management.
- [ ] Levantar el cluster y probar los siete endpoints, incluyendo los casos de error descritos en la guia.

Las propiedades aceptan variables de entorno y, si no se definen, usan los valores de respaldo indicados arriba.