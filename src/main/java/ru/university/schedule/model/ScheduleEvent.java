package ru.university.schedule.model;

import java.util.List;

/** * Событие, порождаемое моделью расписания или симулятором для отделения логики от вывода. */
public sealed interface ScheduleEvent
        permits LessonPlacedEvent, LessonRejectedEvent, LessonRemovedEvent, SimulationFinishedEvent {

    /** * Возвращает краткое текстовое описание события. */
    String summary();
}
