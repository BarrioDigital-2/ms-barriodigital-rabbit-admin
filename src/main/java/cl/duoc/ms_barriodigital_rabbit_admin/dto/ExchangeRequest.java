package cl.duoc.ms_barriodigital_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body de POST /api/admin/exchanges. */
@Data
@NoArgsConstructor
public class ExchangeRequest {

    public enum ExchangeType { DIRECT, TOPIC, FANOUT }

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Size(max = 255, message = "El nombre del exchange no puede superar los 255 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$",
            message = "El nombre del exchange solo puede tener letras, números, punto, guion y guion bajo")
    @Pattern(regexp = "^(?!amq\\.).*$", message = "Los nombres que empiezan con 'amq.' son reservados de RabbitMQ")
    private String name;

    @NotNull(message = "El tipo es obligatorio: DIRECT, TOPIC o FANOUT")
    private ExchangeType type;

    private boolean durable = true;
}
