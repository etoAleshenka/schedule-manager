package ru.university.schedule.model;

public class Teacher extends Person {
    private static final String ROLE_NAME = "Преподаватель";
    private final String department;

    public Teacher(String id, String fullName, String department) {
        super(id, fullName);
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Кафедра не может быть пустой");
        }
        this.department = department.trim();
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getRole() {
        return ROLE_NAME;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + department + ")";
    }
}
