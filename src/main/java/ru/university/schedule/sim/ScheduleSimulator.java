package ru.university.schedule.sim;

import ru.university.schedule.model.CurriculumItem;
import ru.university.schedule.model.Lesson;
import ru.university.schedule.model.LessonPlacedEvent;
import ru.university.schedule.model.LessonRejectedEvent;
import ru.university.schedule.model.Room;
import ru.university.schedule.model.Schedule;
import ru.university.schedule.model.ScheduleEvent;
import ru.university.schedule.model.SimulationFinishedEvent;
import ru.university.schedule.model.TimeSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ScheduleSimulator {
    private static final int RANDOM_PROBES_PER_PAIR = 3;

    private final long seed;
    private final Random random;

    public ScheduleSimulator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public long getSeed() {
        return seed;
    }

    public SimulationReport run(UniversityDataset dataset) {
        Objects.requireNonNull(dataset, "Набор данных не может быть null");
        Schedule schedule = new Schedule();
        List<ScheduleEvent> events = new ArrayList<>();

        List<CurriculumItem> expandedTasks = new ArrayList<>();
        for (CurriculumItem item : dataset.curriculum()) {
            for (int i = 0; i < item.pairsPerWeek(); i++) {
                expandedTasks.add(item);
            }
        }
        Collections.shuffle(expandedTasks, random);

        int placed = 0;
        List<CurriculumItem> unplacedItems = new ArrayList<>();

        for (CurriculumItem task : expandedTasks) {
            boolean success = tryPlaceTask(schedule, task, dataset.rooms(), dataset.availableSlots(), events);
            if (success) {
                placed++;
            } else {
                unplacedItems.add(task);
            }
        }

        int totalWindows = schedule.countTotalWindows();
        events.add(new SimulationFinishedEvent(placed, unplacedItems.size(), totalWindows));
        return new SimulationReport(schedule, events, placed, unplacedItems.size(), totalWindows, unplacedItems);
    }

    private boolean tryPlaceTask(
            Schedule schedule,
            CurriculumItem task,
            List<Room> rooms,
            List<TimeSlot> slots,
            List<ScheduleEvent> events
    ) {
        if (rooms.isEmpty() || slots.isEmpty()) {
            return false;
        }

        for (int attempt = 0; attempt < RANDOM_PROBES_PER_PAIR; attempt++) {
            TimeSlot slot = slots.get(random.nextInt(slots.size()));
            Room room = rooms.get(random.nextInt(rooms.size()));
            Lesson candidate = new Lesson(task.group(), task.subject(), task.teacher(), room, slot);
            ScheduleEvent event = schedule.tryPlaceLesson(candidate);
            events.add(event);
            if (event instanceof LessonPlacedEvent) {
                return true;
            }
        }

        List<TimeSlot> shuffledSlots = new ArrayList<>(slots);
        List<Room> shuffledRooms = new ArrayList<>(rooms);
        Collections.shuffle(shuffledSlots, random);
        Collections.shuffle(shuffledRooms, random);

        LessonRejectedEvent lastRejection = null;
        for (TimeSlot slot : shuffledSlots) {
            for (Room room : shuffledRooms) {
                Lesson candidate = new Lesson(task.group(), task.subject(), task.teacher(), room, slot);
                ScheduleEvent event = schedule.tryPlaceLesson(candidate);
                if (event instanceof LessonPlacedEvent) {
                    events.add(event);
                    return true;
                } else if (event instanceof LessonRejectedEvent rejected) {
                    lastRejection = rejected;
                }
            }
        }

        if (lastRejection != null) {
            events.add(lastRejection);
        }
        return false;
    }
}
