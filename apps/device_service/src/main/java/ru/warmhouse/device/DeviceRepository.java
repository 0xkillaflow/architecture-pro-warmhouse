package ru.warmhouse.device;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory хранилище устройств (упрощение для MVP). */
@Repository
public class DeviceRepository {
    private final Map<Long, Device> devices = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Device create(DeviceCreateRequest request) {
        Device device = new Device(sequence.incrementAndGet(), request.houseId(), request.type(),
                request.name(), request.serialNumber(), request.location());
        devices.put(device.getId(), device);
        return device;
    }

    public List<Device> findAll() {
        return new ArrayList<>(devices.values());
    }

    public Optional<Device> findById(long id) {
        return Optional.ofNullable(devices.get(id));
    }
}
