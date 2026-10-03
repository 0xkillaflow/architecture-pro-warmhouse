package ru.warmhouse.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Optional;

/**
 * Клиент монолита Smart Home (Strangler Fig): пока клиенты работают со старым API /sensors,
 * датчики температуры, зарегистрированные в Device Service, дублируются в монолит.
 */
@Component
public class MonolithClient {
    private static final Logger log = LoggerFactory.getLogger(MonolithClient.class);

    private final RestClient client;

    public MonolithClient(@Value("${monolith.url}") String monolithUrl) {
        this.client = RestClient.create(monolithUrl);
    }

    /** Создаёт датчик в монолите и возвращает его id. Если монолит недоступен — пустой результат. */
    public Optional<Long> createSensor(String name, String location) {
        try {
            Map<?, ?> sensor = client.post()
                    .uri("/api/v1/sensors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("name", name, "type", "temperature", "location", location, "unit", "°C"))
                    .retrieve()
                    .body(Map.class);
            return Optional.of(((Number) sensor.get("id")).longValue());
        } catch (RestClientException e) {
            log.warn("Monolith is unavailable, sensor for device '{}' is not created: {}", name, e.getMessage());
            return Optional.empty();
        }
    }
}
