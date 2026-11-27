package ru.university.schedule.cli;

import ru.university.schedule.model.CurriculumItem;
import ru.university.schedule.model.Group;
import ru.university.schedule.model.Lesson;
import ru.university.schedule.model.Room;
import ru.university.schedule.model.Schedule;
import ru.university.schedule.model.ScheduleEvent;
import ru.university.schedule.model.ScheduleException;
import ru.university.schedule.model.StudyDay;
import ru.university.schedule.model.Subject;
import ru.university.schedule.model.Teacher;
import ru.university.schedule.model.TimeSlot;
import ru.university.schedule.sim.ScheduleSimulator;
import ru.university.schedule.sim.SimulationReport;
import ru.university.schedule.sim.UniversityDataset;

import java.util.List;
import java.util.Scanner;

/** * Консольное приложение для запуска симуляции и ручного управления расписанием. */
public class ConsoleApp {
    private static final long DEFAULT_SEED = 42L;

    private final UniversityDataset dataset;
    private final SchedulePrinter printer;
    private Schedule currentSchedule;

    /** * Создаёт консольное приложение со стандартным набором данных. */
    public ConsoleApp() {
        this.dataset = UniversityDataset.createDefault();
        this.printer = new SchedulePrinter();
        this.currentSchedule = new Schedule();
    }

    /**
     * Точка входа в программу.
     * Если передан аргумент {@code --auto [seed]}, запускает автосимуляцию без меню.
     */
    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        if (args.length > 0 && "--auto".equals(args[0])) {
            long seed = args.length > 1 ? Long.parseLong(args[1]) : DEFAULT_SEED;
            app.runAutoSimulation(seed);
            return;
        }
        app.runInteractive();
    }

    /** * Запускает автоматическую симуляцию по заданному seed и печатает отчёт. */
    public void runAutoSimulation(long seed) {
        System.out.println("Запуск симуляции с seed = " + seed);
        ScheduleSimulator simulator = new ScheduleSimulator(seed);
        SimulationReport report = simulator.run(dataset);
        this.currentSchedule = report.schedule();
        printer.printSimulationReport(report);
        printer.printScheduleByGroups(currentSchedule, dataset.groups());
        printer.printWindowConflicts(currentSchedule);
    }

    private void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Введите seed (Enter для " + DEFAULT_SEED + "): ");
                    String input = scanner.nextLine().trim();
                    long seed = input.isEmpty() ? DEFAULT_SEED : Long.parseLong(input);
                    runAutoSimulation(seed);
                }
                case "2" -> printer.printScheduleByGroups(currentSchedule, dataset.groups());
                case "3" -> printer.printWindowConflicts(currentSchedule);
                case "4" -> handleManualAdd(scanner);
                case "5" -> handleManualRemove(scanner);
                case "0" -> running = false;
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                --- Меню управления расписанием ---
                1. Запустить автосимуляцию по seed
                2. Показать расписание по группам
                3. Проверить окна в расписании
                4. Добавить занятие вручную
                5. Удалить занятие вручную
                0. Выход""");
        System.out.print("Выберите действие: ");
    }

    private void handleManualAdd(Scanner scanner) {
        try {
            Group group = chooseFromList(scanner, "группу", dataset.groups());
            Subject subject = chooseFromList(scanner, "предмет", dataset.subjects());
            List<Teacher> subjectTeachers = dataset.curriculum().stream()
                    .filter(item -> item.subject().equals(subject))
                    .map(CurriculumItem::teacher)
                    .distinct()
                    .toList();
            if (subjectTeachers.isEmpty()) {
                subjectTeachers = dataset.teachers();
            }
            Teacher teacher = chooseFromList(scanner, "преподавателя", subjectTeachers);
            Room room = chooseFromList(scanner, "аудиторию", dataset.rooms());
            StudyDay day = chooseFromList(scanner, "день", List.of(StudyDay.values()));
            System.out.print("Введите номер пары (1-6): ");
            int pair = Integer.parseInt(scanner.nextLine().trim());

            Lesson lesson = new Lesson(group, subject, teacher, room, new TimeSlot(day, pair));
            ScheduleEvent event = currentSchedule.tryPlaceLesson(lesson);
            System.out.println(event.summary());
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }

    private void handleManualRemove(Scanner scanner) {
        List<Lesson> lessons = currentSchedule.getLessons();
        if (lessons.isEmpty()) {
            System.out.println("Расписание пусто.");
            return;
        }
        try {
            Lesson selected = chooseFromList(scanner, "занятие для удаления", lessons);
            ScheduleEvent event = currentSchedule.removeLesson(selected);
            System.out.println(event.summary());
        } catch (ScheduleException | IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private <T> T chooseFromList(Scanner scanner, String title, List<T> items) {
        System.out.println("Выберите " + title + ":");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
        System.out.print("Номер: ");
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        return items.get(index);
    }
}
