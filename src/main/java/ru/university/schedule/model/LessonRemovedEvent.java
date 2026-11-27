package ru.university.schedule.model;

/** * Событие удаления занятия из расписания. */
public record LessonRemovedEvent(Lesson lesson) implements ScheduleEvent {
    @Override
    public String summary() {
        return "УДАЛЕНО: " + lesson;
    }
}
