package cl.duoc.ms_barriodigital_rabbit_admin.service;

import cl.duoc.ms_barriodigital_rabbit_admin.dto.BindingRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.dto.ExchangeRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.dto.QueueRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.exception.RecursoNoEncontradoException;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RabbitAdminService {

    private static final String PREFIJO_RESERVADO = "amq.";

    private final RabbitAdmin rabbitAdmin;
    private final RestClient managementClient;

    public RabbitAdminService(RabbitAdmin rabbitAdmin, RestClient managementRestClient) {
        this.rabbitAdmin = rabbitAdmin;
        this.managementClient = managementRestClient;
    }

    public List<Map<String, Object>> listQueues() {
        return consultarManagement("/queues").stream()
                .map(cola -> resumen(cola, "name", "durable", "auto_delete", "messages", "consumers", "node", "type"))
                .toList();
    }

    public void createQueue(QueueRequest request) {
        validarNombre(request.getName(), "cola");
        rabbitAdmin.declareQueue(new Queue(request.getName(), request.isDurable(), false, request.isAutoDelete()));
    }

    public void deleteQueue(String name) {
        if (rabbitAdmin.getQueueProperties(name) == null) {
            throw new RecursoNoEncontradoException("La cola '" + name + "' no existe");
        }
        rabbitAdmin.deleteQueue(name);
    }

    public List<Map<String, Object>> listExchanges() {
        return consultarManagement("/exchanges").stream()
                .filter(exchange -> !"".equals(exchange.get("name")))
                .map(exchange -> resumen(exchange, "name", "type", "durable", "auto_delete", "internal"))
                .toList();
    }

    public void createExchange(ExchangeRequest request) {
        validarNombre(request.getName(), "exchange");
        if (request.getType() == null) {
            throw new IllegalArgumentException("El tipo es obligatorio: DIRECT, TOPIC o FANOUT");
        }

        AbstractExchange exchange = switch (request.getType()) {
            case DIRECT -> new DirectExchange(request.getName(), request.isDurable(), false);
            case TOPIC -> new TopicExchange(request.getName(), request.isDurable(), false);
            case FANOUT -> new FanoutExchange(request.getName(), request.isDurable(), false);
        };
        rabbitAdmin.declareExchange(exchange);
    }

    public void deleteExchange(String name) {
        validarNombre(name, "exchange");
        verificarExchangeExiste(name);
        rabbitAdmin.deleteExchange(name);
    }

    public void createBinding(BindingRequest request) {
        validarNombre(request.getQueue(), "cola");
        validarNombre(request.getExchange(), "exchange");
        if (rabbitAdmin.getQueueProperties(request.getQueue()) == null) {
            throw new RecursoNoEncontradoException("La cola '" + request.getQueue() + "' no existe");
        }
        verificarExchangeExiste(request.getExchange());

        String routingKey = request.getRoutingKey() == null ? "" : request.getRoutingKey();
        rabbitAdmin.declareBinding(
                new Binding(request.getQueue(), Binding.DestinationType.QUEUE, request.getExchange(), routingKey, null));
    }

    private List<Map<String, Object>> consultarManagement(String ruta) {
        List<Map<String, Object>> respuesta = managementClient.get()
                .uri(ruta)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        return respuesta == null ? List.of() : respuesta;
    }

    private void verificarExchangeExiste(String name) {
        try {
            managementClient.get()
                    .uri("/exchanges/{vhost}/{name}", "/", name)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("El exchange '" + name + "' no existe");
        }
    }

    private void validarNombre(String name, String recurso) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la " + recurso + " es obligatorio");
        }
        if (name.startsWith(PREFIJO_RESERVADO)) {
            throw new IllegalArgumentException("Los nombres que empiezan con 'amq.' son reservados de RabbitMQ");
        }
    }

    private Map<String, Object> resumen(Map<String, Object> origen, String... campos) {
        Map<String, Object> resumen = new LinkedHashMap<>();
        for (String campo : campos) {
            resumen.put(campo, origen.get(campo));
        }
        return resumen;
    }
}