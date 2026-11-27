package ru.university.schedule.model;

/** * Учебный предмет. */
public record Subject(String code, String name) {
    /** * Проверяет корректность кода и названия предмета. */
    public Subject {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Код предмета не может быть пустым");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название предмета не может быть пустым");
        }
        code = code.trim();
        name = name.trim();
    }

    @Override
    public String toString() {
        return name;
    }
}
