package ru.university.schedule.model;

public record Course(int number, String title) {
    public static final int MIN_COURSE = 1;
    public static final int MAX_COURSE = 6;

    public Course {
        if (number < MIN_COURSE || number > MAX_COURSE) {
            throw new IllegalArgumentException("Номер курса должен быть от " + MIN_COURSE + " до " + MAX_COURSE);
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название курса не может быть пустым");
        }
        title = title.trim();
    }
}
