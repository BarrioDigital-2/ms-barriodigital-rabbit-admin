package cl.duoc.ms_barriodigital_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body de POST /api/admin/queues. */
@Data
@NoArgsConstructor
public class QueueRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Size(max = 255, message = "El nombre de la cola no puede superar los 255 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$",
            message = "El nombre de la cola solo puede tener letras, números, punto, guion y guion bajo")
    @Pattern(regexp = "^(?!amq\\.).*$", message = "Los nombres que empiezan con 'amq.' son reservados de RabbitMQ")
    private String name;

    /** Sobrevive a un reinicio de RabbitMQ. */
    private boolean durable = true;
    /** Se borra sola cuando se desconecta su ultimo consumidor. */
    private boolean autoDelete = false;
}
