package ru.university.schedule.model;

public sealed interface ScheduleEvent
        permits LessonPlacedEvent, LessonRejectedEvent, LessonRemovedEvent, SimulationFinishedEvent {

    String summary();
}
