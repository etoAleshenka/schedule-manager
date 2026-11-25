package ru.university.schedule.model;

public record LessonPlacedEvent(Lesson lesson) implements ScheduleEvent {
    @Override
    public String summary() {
        return "ПОСТАВЛЕНО: " + lesson;
    }
}
