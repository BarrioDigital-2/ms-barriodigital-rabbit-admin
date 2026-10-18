package cl.duoc.ms_barriodigital_rabbit_admin.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuracion centralizada de las dos herramientas para administrar RabbitMQ:
 * - RabbitAdmin: crea y borra colas, exchanges y bindings (via AMQP).
 * - RestClient: consulta la API HTTP de management para listar colas y exchanges.
 */
@Configuration
public class RabbitAdminConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Cliente HTTP hacia http://localhost:15672/api con autenticacion Basic
     * (mismo usuario y clave de la conexion AMQP).
     */
    @Bean
    public RestClient managementRestClient(@Value("${rabbitmq.management.url}") String managementUrl,
                                           @Value("${spring.rabbitmq.username}") String username,
                                           @Value("${spring.rabbitmq.password}") String password) {
        return RestClient.builder()
                .baseUrl(managementUrl + "/api")
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }
}
