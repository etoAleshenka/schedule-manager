package ru.university.schedule.model;

import java.util.List;

public record LessonRejectedEvent(Lesson lesson, List<Conflict> conflicts) implements ScheduleEvent {
    public LessonRejectedEvent {
        conflicts = List.copyOf(conflicts);
    }

    @Override
    public String summary() {
        return "ОТКЛОНЕНО: " + lesson + " -> " + conflicts;
    }
}
