package ru.university.schedule.model;

public record LessonRemovedEvent(Lesson lesson) implements ScheduleEvent {
    @Override
    public String summary() {
        return "УДАЛЕНО: " + lesson;
    }
}
