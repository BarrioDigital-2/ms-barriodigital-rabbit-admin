package cl.duoc.ms_barriodigital_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body de POST /api/admin/bindings: enlaza una cola a un exchange. */
@Data
@NoArgsConstructor
public class BindingRequest {

    @NotBlank(message = "La cola es obligatoria")
    private String queue;

    @NotBlank(message = "El exchange es obligatorio")
    private String exchange;

    /** Para Fanout se ignora; puede ir vacia. */
    @Size(max = 255, message = "La routing key no puede superar los 255 caracteres")
    private String routingKey;
}
