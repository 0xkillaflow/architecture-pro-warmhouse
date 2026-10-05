package ru.warmhouse.device;

import jakarta.validation.constraints.NotBlank;

public record DeviceCreateRequest(
        @NotBlank String houseId,
        @NotBlank String type,
        @NotBlank String name,
        String serialNumber,
        String location) {
}
