package ru.university.schedule.cli;

import ru.university.schedule.model.Conflict;
import ru.university.schedule.model.CurriculumItem;
import ru.university.schedule.model.Group;
import ru.university.schedule.model.Lesson;
import ru.university.schedule.model.Schedule;
import ru.university.schedule.model.ScheduleEvent;
import ru.university.schedule.sim.SimulationReport;

import java.util.List;

/** * Отдельный слой вывода расписания и журнала событий в консоль. */
public class SchedulePrinter {

    /** * Печатает отчёт о симуляции: журнал событий и сводку. */
    public void printSimulationReport(SimulationReport report) {
        System.out.println("=== Журнал симуляции ===");
        for (int i = 0; i < report.events().size(); i++) {
            ScheduleEvent event = report.events().get(i);
            System.out.println((i + 1) + ". " + event.summary());
        }
        if (!report.unplacedItems().isEmpty()) {
            System.out.println("\nНе удалось разместить следующие пары:");
            for (CurriculumItem item : report.unplacedItems()) {
                System.out.println("  - Группа " + item.group().getName()
                        + " | " + item.subject().name()
                        + " | " + item.teacher().getFullName());
            }
        }
        System.out.println();
    }

    /** * Печатает расписание для всех указанных групп. */
    public void printScheduleByGroups(Schedule schedule, List<Group> groups) {
        System.out.println("=== Расписание по группам ===");
        for (Group group : groups) {
            System.out.println("\n" + group + " | Окон за неделю: " + schedule.countWindowsForGroup(group));
            List<Lesson> groupLessons = schedule.getLessonsForGroup(group);
            if (groupLessons.isEmpty()) {
                System.out.println("  (занятий нет)");
            } else {
                for (Lesson lesson : groupLessons) {
                    System.out.println("  - " + lesson);
                }
            }
        }
        System.out.println();
    }

    /** * Печатает список конфликтов по окнам в расписании. */
    public void printWindowConflicts(Schedule schedule) {
        List<Conflict> windowConflicts = schedule.findAllWindowConflicts();
        System.out.println("=== Проверка окон в расписании ===");
        if (windowConflicts.isEmpty()) {
            System.out.println("Окон у групп не обнаружено.");
        } else {
            for (Conflict conflict : windowConflicts) {
                System.out.println("  * " + conflict);
            }
        }
        System.out.println();
    }
}
