package ru.university.schedule.model;

public record Subject(String code, String name) {
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
