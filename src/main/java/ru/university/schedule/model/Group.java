package ru.university.schedule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Group {
    private final String name;
    private final Course course;
    private final List<Student> students = new ArrayList<>();

    public Group(String name, Course course) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название группы не может быть пустым");
        }
        this.name = name.trim();
        this.course = Objects.requireNonNull(course, "Курс не может быть null");
    }

    public void addStudent(Student student) {
        Objects.requireNonNull(student, "Студент не может быть null");
        if (!students.contains(student)) {
            students.add(student);
        }
    }

    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    public int getSize() {
        return students.size();
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return name.equals(group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Группа " + name + " (" + course.number() + " курс, " + students.size() + " чел.)";
    }
}
