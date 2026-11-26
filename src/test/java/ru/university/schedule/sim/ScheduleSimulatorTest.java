package ru.university.schedule.sim;

import org.junit.jupiter.api.Test;
import ru.university.schedule.model.Lesson;
import ru.university.schedule.model.LessonPlacedEvent;
import ru.university.schedule.model.Room;
import ru.university.schedule.model.ScheduleEvent;
import ru.university.schedule.model.SimulationFinishedEvent;
import ru.university.schedule.model.StudyDay;
import ru.university.schedule.model.TimeSlot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleSimulatorTest {

    @Test
    void shouldBeCompletelyDeterministicForSameSeed() {
        UniversityDataset dataset = UniversityDataset.createDefault();

        SimulationReport firstRun = new ScheduleSimulator(12345L).run(dataset);
        SimulationReport secondRun = new ScheduleSimulator(12345L).run(dataset);

        assertEquals(firstRun.placedCount(), secondRun.placedCount());
        assertEquals(firstRun.unplacedCount(), secondRun.unplacedCount());
        assertEquals(firstRun.totalWindows(), secondRun.totalWindows());
        assertEquals(firstRun.schedule().getLessons(), secondRun.schedule().getLessons());
        assertEquals(firstRun.events(), secondRun.events());
    }

    @Test
    void shouldDetectUnplacedLessonWhenTeacherLoadExceedsAvailableSlotsInDefaultDataset() {
        UniversityDataset dataset = UniversityDataset.createDefault();
        SimulationReport report = new ScheduleSimulator(42L).run(dataset);

        int expectedTotalPairs = dataset.curriculum().stream()
                .mapToInt(c -> c.pairsPerWeek())
                .sum();

        assertTrue(report.unplacedCount() >= 1);
        assertEquals(expectedTotalPairs, report.placedCount() + report.unplacedCount());
        assertEquals(report.placedCount(), report.schedule().getLessons().size());
    }

    @Test
    void shouldProduceDifferentScheduleForDifferentSeeds() {
        UniversityDataset dataset = UniversityDataset.createDefault();

        SimulationReport reportA = new ScheduleSimulator(1L).run(dataset);
        SimulationReport reportB = new ScheduleSimulator(999L).run(dataset);

        assertNotEquals(reportA.schedule().getLessons(), reportB.schedule().getLessons());
    }

    @Test
    void shouldRespectAllHardConstraintsInSimulatedSchedule() {
        UniversityDataset dataset = UniversityDataset.createDefault();
        SimulationReport report = new ScheduleSimulator(77L).run(dataset);

        List<Lesson> lessons = report.schedule().getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            Lesson a = lessons.get(i);
            assertTrue(a.group().getSize() <= a.room().capacity());
            for (int j = i + 1; j < lessons.size(); j++) {
                Lesson b = lessons.get(j);
                if (a.slot().equals(b.slot())) {
                    assertNotEquals(a.teacher(), b.teacher());
                    assertNotEquals(a.group(), b.group());
                    assertNotEquals(a.room(), b.room());
                }
            }
        }
    }

    @Test
    void shouldEndEventLogWithSimulationFinishedEvent() {
        UniversityDataset dataset = UniversityDataset.createDefault();
        SimulationReport report = new ScheduleSimulator(10L).run(dataset);

        ScheduleEvent lastEvent = report.events().getLast();
        assertInstanceOf(SimulationFinishedEvent.class, lastEvent);
        SimulationFinishedEvent finished = (SimulationFinishedEvent) lastEvent;
        assertEquals(report.placedCount(), finished.placedCount());
        assertEquals(report.totalWindows(), finished.totalWindows());
    }

    @Test
    void shouldReportUnplacedWhenNoRoomsAreAvailable() {
        UniversityDataset defaultData = UniversityDataset.createDefault();
        UniversityDataset noRooms = new UniversityDataset(
                defaultData.groups(),
                defaultData.teachers(),
                defaultData.subjects(),
                List.of(),
                defaultData.curriculum(),
                defaultData.availableSlots()
        );

        SimulationReport report = new ScheduleSimulator(5L).run(noRooms);
        assertEquals(0, report.placedCount());
        assertTrue(report.unplacedCount() > 0);
    }

    @Test
    void shouldReportUnplacedWhenAllRoomsAreTooSmallForGroup82() {
        UniversityDataset defaultData = UniversityDataset.createDefault();
        UniversityDataset tinyRooms = new UniversityDataset(
                defaultData.groups(),
                defaultData.teachers(),
                defaultData.subjects(),
                List.of(new Room("Small", 10)),
                defaultData.curriculum(),
                List.of(new TimeSlot(StudyDay.MONDAY, 1))
        );

        SimulationReport report = new ScheduleSimulator(5L).run(tinyRooms);
        assertEquals(0, report.placedCount());
        assertTrue(report.unplacedCount() > 0);
        assertFalse(report.events().stream().anyMatch(e -> e instanceof LessonPlacedEvent));
    }
}
