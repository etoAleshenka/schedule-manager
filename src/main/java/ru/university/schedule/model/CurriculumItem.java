package ru.university.schedule.model;

import java.util.Objects;

public record CurriculumItem(Group group, Subject subject, Teacher teacher, int pairsPerWeek) {
    public CurriculumItem {
        Objects.requireNonNull(group, "Группа не может быть null");
        Objects.requireNonNull(subject, "Предмет не может быть null");
        Objects.requireNonNull(teacher, "Преподаватель не может быть null");
        if (pairsPerWeek <= 0) {
            throw new IllegalArgumentException("Количество пар в неделю должно быть больше 0");
        }
    }
}
