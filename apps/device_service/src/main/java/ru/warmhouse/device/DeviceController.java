package ru.warmhouse.device;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class DeviceController {
    private final DeviceRepository repository;
    private final MonolithClient monolith;

    public DeviceController(DeviceRepository repository, MonolithClient monolith) {
        this.repository = repository;
        this.monolith = monolith;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/api/v1/devices")
    public List<Device> list() {
        return repository.findAll();
    }

    @PostMapping("/api/v1/devices")
    @ResponseStatus(HttpStatus.CREATED)
    public Device create(@Valid @RequestBody DeviceCreateRequest request) {
        Device device = repository.create(request);
        // Датчик температуры дублируем в монолит, чтобы его видели клиенты старого API /sensors
        if ("temperature".equals(device.getType())) {
            String location = device.getLocation() != null ? device.getLocation() : "Unknown";
            monolith.createSensor(device.getName(), location).ifPresent(device::setLegacySensorId);
        }
        return device;
    }

    @GetMapping("/api/v1/devices/{id}")
    public Device get(@PathVariable long id) {
        return find(id);
    }

    @PutMapping("/api/v1/devices/{id}/properties/{name}")
    public Device setProperty(@PathVariable long id, @PathVariable String name,
                              @Valid @RequestBody PropertyRequest request) {
        Device device = find(id);
        device.setProperty(name, request.value());
        return device;
    }

    private Device find(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));
    }
}
