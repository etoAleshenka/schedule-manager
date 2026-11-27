package ru.university.schedule.model;

/** * Типы конфликтов в расписании. */
public enum ConflictType {
    TEACHER_BUSY("Преподаватель занят в это время"),
    GROUP_BUSY("Группа занята в это время"),
    ROOM_BUSY("Аудитория занята в это время"),
    ROOM_CAPACITY_EXCEEDED("Вместимость аудитории меньше размера группы"),
    GROUP_WINDOW("Окно в расписании группы");

    private final String description;

    ConflictType(String description) {
        this.description = description;
    }

    /** * Возвращает человекочитаемое описание типа конфликта. */
    public String getDescription() {
        return description;
    }
}
