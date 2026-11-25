package ru.university.schedule.model;

public class Student extends Person {
    private static final String ROLE_NAME = "Студент";

    public Student(String id, String fullName) {
        super(id, fullName);
    }

    @Override
    public String getRole() {
        return ROLE_NAME;
    }
}
