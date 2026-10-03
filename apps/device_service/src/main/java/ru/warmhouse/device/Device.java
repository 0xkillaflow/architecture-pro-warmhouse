package ru.warmhouse.device;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Устройство умного дома. Тип - произвольная строка (heating, light, gate, camera, ...),
 * поэтому новый тип устройства подключается без изменения кода сервиса.
 */
public class Device {
    private final long id;
    private final String houseId;
    private final String type;
    private final String name;
    private final String serialNumber;
    private final String location;
    private Long legacySensorId;
    private final Map<String, Object> properties = new HashMap<>();
    private Instant updatedAt = Instant.now();

    public Device(long id, String houseId, String type, String name, String serialNumber, String location) {
        this.id = id;
        this.houseId = houseId;
        this.type = type;
        this.name = name;
        this.serialNumber = serialNumber;
        this.location = location;
    }

    /** Устанавливает значение свойства. В MVP устройство эмулируется: значение применяется сразу. */
    public void setProperty(String name, Object value) {
        properties.put(name, value);
        updatedAt = Instant.now();
    }

    public long getId() { return id; }
    public String getHouseId() { return houseId; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public String getLocation() { return location; }
    /** id датчика в монолите, если устройство продублировано туда (см. MonolithClient). */
    public Long getLegacySensorId() { return legacySensorId; }
    public void setLegacySensorId(Long legacySensorId) { this.legacySensorId = legacySensorId; }
    public Map<String, Object> getProperties() { return properties; }
    public Instant getUpdatedAt() { return updatedAt; }
}
