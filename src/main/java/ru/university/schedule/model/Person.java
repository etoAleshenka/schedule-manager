package ru.university.schedule.model;

import java.util.Objects;

/** * Базовый абстрактный класс для участников учебного процесса. */
public abstract class Person {
    private final String id;
    private final String fullName;

    /** * Создаёт участника учебного процесса. */
    protected Person(String id, String fullName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Идентификатор не может быть пустым");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.id = id.trim();
        this.fullName = fullName.trim();
    }

    /** * Возвращает идентификатор участника. */
    public String getId() {
        return id;
    }

    /** * Возвращает полное имя участника. */
    public String getFullName() {
        return fullName;
    }

    /** * Возвращает роль участника в учебном процессе. */
    public abstract String getRole();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getRole() + " " + fullName + " (" + id + ")";
    }
}
