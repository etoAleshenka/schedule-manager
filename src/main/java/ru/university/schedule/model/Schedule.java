package ru.university.schedule.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Расписание занятий с проверкой конфликтов и подсчётом окон у групп.
 * Класс не выполняет вывод в консоль и работает только через состояние и события.
 */
public class Schedule {
    private final List<Lesson> lessons = new ArrayList<>();

    /**
     * Проверяет все жёсткие конфликты для предлагаемого занятия:
     * занятость преподавателя, группы, аудитории и её вместимость.
     */
    public List<Conflict> checkConflicts(Lesson candidate) {
        Objects.requireNonNull(candidate, "Занятие не может быть null");
        List<Conflict> conflicts = new ArrayList<>();

        if (candidate.group().getSize() > candidate.room().capacity()) {
            conflicts.add(new Conflict(
                    ConflictType.ROOM_CAPACITY_EXCEEDED,
                    "Группа " + candidate.group().getName() + " (" + candidate.group().getSize()
                            + " чел.) не помещается в " + candidate.room()
            ));
        }

        for (Lesson existing : lessons) {
            if (!existing.slot().equals(candidate.slot())) {
                continue;
            }
            if (existing.teacher().equals(candidate.teacher())) {
                conflicts.add(new Conflict(
                        ConflictType.TEACHER_BUSY,
                        candidate.teacher().getFullName() + " уже ведёт пару в " + candidate.slot()
                ));
            }
            if (existing.group().equals(candidate.group())) {
                conflicts.add(new Conflict(
                        ConflictType.GROUP_BUSY,
                        "Группа " + candidate.group().getName() + " уже занята в " + candidate.slot()
                ));
            }
            if (existing.room().equals(candidate.room())) {
                conflicts.add(new Conflict(
                        ConflictType.ROOM_BUSY,
                        candidate.room() + " уже занята в " + candidate.slot()
                ));
            }
        }

        return conflicts;
    }

    /** * Пытается добавить занятие в расписание без выбрасывания исключения. */
    public ScheduleEvent tryPlaceLesson(Lesson lesson) {
        List<Conflict> conflicts = checkConflicts(lesson);
        if (!conflicts.isEmpty()) {
            return new LessonRejectedEvent(lesson, conflicts);
        }
        lessons.add(lesson);
        return new LessonPlacedEvent(lesson);
    }

    /** * Добавляет занятие в расписание или выбрасывает исключение при наличии конфликта. */
    public LessonPlacedEvent placeLessonOrThrow(Lesson lesson) {
        List<Conflict> conflicts = checkConflicts(lesson);
        if (!conflicts.isEmpty()) {
            throw new ScheduleException("Невозможно поставить занятие: " + conflicts.getFirst());
        }
        lessons.add(lesson);
        return new LessonPlacedEvent(lesson);
    }

    /** * Удаляет занятие из расписания. */
    public LessonRemovedEvent removeLesson(Lesson lesson) {
        Objects.requireNonNull(lesson, "Занятие не может быть null");
        if (!lessons.remove(lesson)) {
            throw new ScheduleException("Занятие не найдено в расписании: " + lesson);
        }
        return new LessonRemovedEvent(lesson);
    }

    /** * Подсчитывает количество окон в расписании группы за конкретный день. */
    public int countWindowsForGroup(Group group, StudyDay day) {
        Objects.requireNonNull(group, "Группа не может быть null");
        Objects.requireNonNull(day, "День не может быть null");

        List<Integer> pairs = lessons.stream()
                .filter(l -> l.group().equals(group) && l.slot().day() == day)
                .map(l -> l.slot().pairNumber())
                .distinct()
                .sorted()
                .toList();

        if (pairs.size() < 2) {
            return 0;
        }
        int span = pairs.getLast() - pairs.getFirst() + 1;
        return span - pairs.size();
    }

    /** * Подсчитывает общее количество окон в расписании группы за всю неделю. */
    public int countWindowsForGroup(Group group) {
        int total = 0;
        for (StudyDay day : StudyDay.values()) {
            total += countWindowsForGroup(group, day);
        }
        return total;
    }

    /** * Подсчитывает суммарное количество окон по всем группам в расписании. */
    public int countTotalWindows() {
        Set<Group> groups = new LinkedHashSet<>();
        for (Lesson lesson : lessons) {
            groups.add(lesson.group());
        }
        int total = 0;
        for (Group group : groups) {
            total += countWindowsForGroup(group);
        }
        return total;
    }

    /** * Формирует список конфликтов типа {@link ConflictType#GROUP_WINDOW} для всех групп, имеющих окна. */
    public List<Conflict> findAllWindowConflicts() {
        Set<Group> groups = new LinkedHashSet<>();
        for (Lesson lesson : lessons) {
            groups.add(lesson.group());
        }
        List<Conflict> windowConflicts = new ArrayList<>();
        for (Group group : groups) {
            int windows = countWindowsForGroup(group);
            if (windows > 0) {
                windowConflicts.add(new Conflict(
                        ConflictType.GROUP_WINDOW,
                        "У группы " + group.getName() + " обнаружено окон: " + windows
                ));
            }
        }
        return windowConflicts;
    }

    /** * Возвращает все занятия, отсортированные по времени и названию группы. */
    public List<Lesson> getLessons() {
        return lessons.stream()
                .sorted(Comparator.comparing(Lesson::slot)
                        .thenComparing(l -> l.group().getName()))
                .toList();
    }

    /** * Возвращает занятия конкретной группы, отсортированные по времени. */
    public List<Lesson> getLessonsForGroup(Group group) {
        Objects.requireNonNull(group, "Группа не может быть null");
        return lessons.stream()
                .filter(l -> l.group().equals(group))
                .sorted(Comparator.comparing(Lesson::slot))
                .toList();
    }

    /** * Возвращает занятия конкретного преподавателя, отсортированные по времени. */
    public List<Lesson> getLessonsForTeacher(Teacher teacher) {
        Objects.requireNonNull(teacher, "Преподаватель не может быть null");
        return lessons.stream()
                .filter(l -> l.teacher().equals(teacher))
                .sorted(Comparator.comparing(Lesson::slot))
                .toList();
    }
}
