package ru.university.schedule.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    private Course course2;
    private Group group71;
    private Group group72;
    private Teacher teacher1;
    private Teacher teacher2;
    private Subject subjectOop;
    private Subject subjectMath;
    private Room roomSmall;
    private Room roomLarge;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        course2 = new Course(2, "Программная инженерия");
        group71 = new Group("7.1", course2);
        for (int i = 1; i <= 18; i++) {
            group71.addStudent(new Student("S1-" + i, "Студент " + i));
        }

        group72 = new Group("7.2", course2);
        for (int i = 1; i <= 25; i++) {
            group72.addStudent(new Student("S2-" + i, "Студент " + i));
        }

        teacher1 = new Teacher("T1", "Иванов И.И.", "Информатика");
        teacher2 = new Teacher("T2", "Петрова А.С.", "Математика");
        subjectOop = new Subject("CS201", "ООП");
        subjectMath = new Subject("MA202", "Математика");
        roomSmall = new Room("101", 18);
        roomLarge = new Room("201", 30);
        schedule = new Schedule();
    }

    @Test
    void shouldPlaceValidLessonAndReturnPlacedEvent() {
        Lesson lesson = new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1));
        ScheduleEvent event = schedule.tryPlaceLesson(lesson);

        assertInstanceOf(LessonPlacedEvent.class, event);
        assertEquals(1, schedule.getLessons().size());
    }

    @Test
    void shouldAllowExactRoomCapacityMatchBoundary() {
        Lesson lesson = new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1));
        assertTrue(schedule.checkConflicts(lesson).isEmpty());
    }

    @Test
    void shouldDetectRoomCapacityExceeded() {
        Lesson lesson = new Lesson(group72, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1));
        List<Conflict> conflicts = schedule.checkConflicts(lesson);

        assertEquals(1, conflicts.size());
        assertEquals(ConflictType.ROOM_CAPACITY_EXCEEDED, conflicts.getFirst().type());
    }

    @Test
    void shouldDetectTeacherBusyConflict() {
        TimeSlot slot = new TimeSlot(StudyDay.TUESDAY, 2);
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, slot));

        Lesson second = new Lesson(group72, subjectMath, teacher1, roomLarge, slot);
        List<Conflict> conflicts = schedule.checkConflicts(second);

        assertEquals(1, conflicts.size());
        assertEquals(ConflictType.TEACHER_BUSY, conflicts.getFirst().type());
    }

    @Test
    void shouldDetectGroupBusyConflict() {
        TimeSlot slot = new TimeSlot(StudyDay.WEDNESDAY, 3);
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, slot));

        Lesson second = new Lesson(group71, subjectMath, teacher2, roomLarge, slot);
        List<Conflict> conflicts = schedule.checkConflicts(second);

        assertEquals(1, conflicts.size());
        assertEquals(ConflictType.GROUP_BUSY, conflicts.getFirst().type());
    }

    @Test
    void shouldDetectRoomBusyConflict() {
        TimeSlot slot = new TimeSlot(StudyDay.THURSDAY, 1);
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomLarge, slot));

        Lesson second = new Lesson(group72, subjectMath, teacher2, roomLarge, slot);
        List<Conflict> conflicts = schedule.checkConflicts(second);

        assertEquals(1, conflicts.size());
        assertEquals(ConflictType.ROOM_BUSY, conflicts.getFirst().type());
    }

    @Test
    void shouldReturnRejectedEventWithoutAddingLessonOnConflict() {
        TimeSlot slot = new TimeSlot(StudyDay.MONDAY, 2);
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, slot));

        ScheduleEvent event = schedule.tryPlaceLesson(new Lesson(group72, subjectMath, teacher1, roomLarge, slot));
        assertInstanceOf(LessonRejectedEvent.class, event);
        assertEquals(1, schedule.getLessons().size());
    }

    @Test
    void shouldThrowScheduleExceptionOnPlaceLessonOrThrowWhenConflictExists() {
        TimeSlot slot = new TimeSlot(StudyDay.FRIDAY, 1);
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, slot));

        Lesson conflicting = new Lesson(group72, subjectMath, teacher1, roomLarge, slot);
        assertThrows(ScheduleException.class, () -> schedule.placeLessonOrThrow(conflicting));
    }

    @Test
    void shouldRemoveExistingLessonAndReturnRemovedEvent() {
        Lesson lesson = new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1));
        schedule.placeLessonOrThrow(lesson);

        LessonRemovedEvent event = schedule.removeLesson(lesson);
        assertEquals(lesson, event.lesson());
        assertTrue(schedule.getLessons().isEmpty());
    }

    @Test
    void shouldThrowScheduleExceptionWhenRemovingAbsentLesson() {
        Lesson absent = new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1));
        assertThrows(ScheduleException.class, () -> schedule.removeLesson(absent));
    }

    @Test
    void shouldCountZeroWindowsForConsecutiveLessons() {
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1)));
        schedule.placeLessonOrThrow(new Lesson(group71, subjectMath, teacher2, roomLarge, new TimeSlot(StudyDay.MONDAY, 2)));

        assertEquals(0, schedule.countWindowsForGroup(group71, StudyDay.MONDAY));
    }

    @Test
    void shouldCountOneWindowBetweenFirstAndThirdPair() {
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1)));
        schedule.placeLessonOrThrow(new Lesson(group71, subjectMath, teacher2, roomLarge, new TimeSlot(StudyDay.MONDAY, 3)));

        assertEquals(1, schedule.countWindowsForGroup(group71, StudyDay.MONDAY));
        assertEquals(1, schedule.findAllWindowConflicts().size());
        assertEquals(ConflictType.GROUP_WINDOW, schedule.findAllWindowConflicts().getFirst().type());
    }

    @Test
    void shouldCountMultipleWindowsAcrossDays() {
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.MONDAY, 1)));
        schedule.placeLessonOrThrow(new Lesson(group71, subjectMath, teacher2, roomLarge, new TimeSlot(StudyDay.MONDAY, 4)));
        schedule.placeLessonOrThrow(new Lesson(group71, subjectOop, teacher1, roomSmall, new TimeSlot(StudyDay.TUESDAY, 2)));
        schedule.placeLessonOrThrow(new Lesson(group71, subjectMath, teacher2, roomLarge, new TimeSlot(StudyDay.TUESDAY, 4)));

        assertEquals(3, schedule.countWindowsForGroup(group71));
    }

    @Test
    void shouldNotDuplicateSameStudentInGroup() {
        Student duplicate = new Student("S1-1", "Студент 1");
        int sizeBefore = group71.getSize();
        group71.addStudent(duplicate);

        assertEquals(sizeBefore, group71.getSize());
    }

    @Test
    void shouldValidateBoundaryValuesInEntities() {
        assertDoesNotThrow(() -> new Course(Course.MIN_COURSE, "Бакалавриат"));
        assertDoesNotThrow(() -> new Course(Course.MAX_COURSE, "Магистратура"));
        assertThrows(IllegalArgumentException.class, () -> new Course(0, "Неверный курс"));
        assertThrows(IllegalArgumentException.class, () -> new Course(7, "Неверный курс"));

        assertDoesNotThrow(() -> new TimeSlot(StudyDay.SATURDAY, TimeSlot.MIN_PAIR));
        assertDoesNotThrow(() -> new TimeSlot(StudyDay.SATURDAY, TimeSlot.MAX_PAIR));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(StudyDay.MONDAY, 0));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(StudyDay.MONDAY, 7));

        assertThrows(IllegalArgumentException.class, () -> new Room("100", 0));
        assertThrows(IllegalArgumentException.class, () -> new CurriculumItem(group71, subjectOop, teacher1, 0));
    }
}
