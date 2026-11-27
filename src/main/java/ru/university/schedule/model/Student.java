package ru.university.schedule.model;

/** * Студент учебной группы. */
public class Student extends Person {
    private static final String ROLE_NAME = "Студент";

    /** * Создаёт студента. */
    public Student(String id, String fullName) {
        super(id, fullName);
    }

    @Override
    public String getRole() {
        return ROLE_NAME;
    }
}
