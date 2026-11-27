package ru.university.schedule.model;

/** * Событие успешного добавления занятия в расписание. */
public record LessonPlacedEvent(Lesson lesson) implements ScheduleEvent {
    @Override
    public String summary() {
        return "ПОСТАВЛЕНО: " + lesson;
    }
}
