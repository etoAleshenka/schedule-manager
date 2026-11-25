package ru.university.schedule.model;

import java.util.Objects;

public record Lesson(Group group, Subject subject, Teacher teacher, Room room, TimeSlot slot) {
    public Lesson {
        Objects.requireNonNull(group, "Группа не может быть null");
        Objects.requireNonNull(subject, "Предмет не может быть null");
        Objects.requireNonNull(teacher, "Преподаватель не может быть null");
        Objects.requireNonNull(room, "Аудитория не может быть null");
        Objects.requireNonNull(slot, "Временной слот не может быть null");
    }

    @Override
    public String toString() {
        return slot + " | Группа " + group.getName() + " | " + subject.name()
                + " | " + teacher.getFullName() + " | " + room.number();
    }
}
