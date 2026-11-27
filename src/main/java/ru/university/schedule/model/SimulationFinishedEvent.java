package ru.university.schedule.model;

/** * Событие завершения симуляции составления расписания. */
public record SimulationFinishedEvent(int placedCount, int unplacedCount, int totalWindows) implements ScheduleEvent {
    @Override
    public String summary() {
        return "ИТОГ: поставлено пар = " + placedCount
                + ", не размещено = " + unplacedCount
                + ", окон у групп = " + totalWindows;
    }
}
