package ru.warmhouse.device;

import jakarta.validation.constraints.NotNull;

/** Новое значение свойства устройства: строка, число или boolean (например, "on" или 22.5). */
public record PropertyRequest(@NotNull Object value) {
}
