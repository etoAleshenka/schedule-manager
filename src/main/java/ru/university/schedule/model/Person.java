package ru.university.schedule.model;

import java.util.Objects;

public abstract class Person {
    private final String id;
    private final String fullName;

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

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

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
