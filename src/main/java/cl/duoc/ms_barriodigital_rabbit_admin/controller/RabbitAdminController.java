package cl.duoc.ms_barriodigital_rabbit_admin.controller;

import cl.duoc.ms_barriodigital_rabbit_admin.dto.BindingRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.dto.ExchangeRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.dto.QueueRequest;
import cl.duoc.ms_barriodigital_rabbit_admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @GetMapping("/queues")
    public List<Map<String, Object>> listQueues() {
        return rabbitAdminService.listQueues();
    }

    @PostMapping("/queues")
    public ResponseEntity<Map<String, Object>> createQueue(@Valid @RequestBody QueueRequest request) {
        rabbitAdminService.createQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Cola creada", "name", request.getName()));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> deleteQueue(@PathVariable String name) {
        rabbitAdminService.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exchanges")
    public List<Map<String, Object>> listExchanges() {
        return rabbitAdminService.listExchanges();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, Object>> createExchange(@Valid @RequestBody ExchangeRequest request) {
        rabbitAdminService.createExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Exchange creado", "name", request.getName(), "type", request.getType()));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> deleteExchange(@PathVariable String name) {
        rabbitAdminService.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, Object>> createBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Binding creado", "queue", request.getQueue(), "exchange", request.getExchange()));
    }
}