package ru.university.schedule.sim;

import ru.university.schedule.model.CurriculumItem;
import ru.university.schedule.model.Schedule;
import ru.university.schedule.model.ScheduleEvent;

import java.util.List;

public record SimulationReport(
        Schedule schedule,
        List<ScheduleEvent> events,
        int placedCount,
        int unplacedCount,
        int totalWindows,
        List<CurriculumItem> unplacedItems
) {
    public SimulationReport {
        events = List.copyOf(events);
        unplacedItems = List.copyOf(unplacedItems);
    }
}
