package ru.university.schedule.model;

/** * Исключение, возникающее при попытке выполнить недопустимую операцию над расписанием. */
public class ScheduleException extends RuntimeException {
    /** * Создаёт исключение с сообщением об ошибке. */
    public ScheduleException(String message) {
        super(message);
    }
}
