package ru.university.schedule.model;

/** * Курс обучения. */
public record Course(int number, String title) {
    /** Минимальный допустимый номер курса. */
    public static final int MIN_COURSE = 1;
    /** Максимальный допустимый номер курса. */
    public static final int MAX_COURSE = 6;

    /** * Проверяет корректность номера и названия курса. */
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
