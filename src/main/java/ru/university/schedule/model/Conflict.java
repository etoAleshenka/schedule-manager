package ru.university.schedule.model;

import java.util.Objects;

/** * Описание обнаруженного конфликта при размещении занятия или проверке расписания. */
public record Conflict(ConflictType type, String details) {
    /** * Создаёт объект конфликта. */
    public Conflict {
        Objects.requireNonNull(type, "Тип конфликта не может быть null");
        if (details == null || details.isBlank()) {
            details = type.getDescription();
        }
    }

    @Override
    public String toString() {
        return "[" + type.getDescription() + "] " + details;
    }
}
