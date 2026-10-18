package cl.duoc.ms_barriodigital_rabbit_admin.exception;

import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Traduce los errores a respuestas HTTP claras en vez de un 500 generico. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Falla alguna anotacion de validacion del DTO (@NotBlank, @Pattern, @NotNull...).
     * Se devuelven todos los errores, campo por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        List<Map<String, String>> errores = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "campo", error.getField(),
                        "mensaje", String.valueOf(error.getDefaultMessage())))
                .toList();

        ResponseEntity<Map<String, Object>> respuesta = error(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        respuesta.getBody().put("errores", errores);
        return respuesta;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** JSON mal formado o tipo de exchange invalido (ej. "type": "OTRO"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> bodyInvalido(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "Body invalido. Revisa el JSON y que type sea DIRECT, TOPIC o FANOUT");
    }

    /** RabbitMQ rechazo la operacion (ej. redeclarar una cola con otros argumentos). */
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<Map<String, Object>> errorRabbit(AmqpException e) {
        return error(HttpStatus.CONFLICT, "RabbitMQ rechazo la operacion: " + causaRaiz(e).getMessage());
    }

    /** No se pudo consultar la API de management (caida o credenciales incorrectas). */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, Object>> errorManagement(RestClientException e) {
        return error(HttpStatus.BAD_GATEWAY, "No se pudo consultar la API de management de RabbitMQ");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        return ResponseEntity.status(status).body(body);
    }

    private Throwable causaRaiz(Throwable e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa;
    }
}
