package cl.duoc.ms_barriodigital_rabbit_admin.exception;

/** La cola o el exchange pedido no existe en RabbitMQ (se responde 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
