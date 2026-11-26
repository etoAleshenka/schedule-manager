package ru.university.schedule.sim;

import ru.university.schedule.model.Course;
import ru.university.schedule.model.CurriculumItem;
import ru.university.schedule.model.Group;
import ru.university.schedule.model.Room;
import ru.university.schedule.model.Student;
import ru.university.schedule.model.StudyDay;
import ru.university.schedule.model.Subject;
import ru.university.schedule.model.Teacher;
import ru.university.schedule.model.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public record UniversityDataset(
        List<Group> groups,
        List<Teacher> teachers,
        List<Subject> subjects,
        List<Room> rooms,
        List<CurriculumItem> curriculum,
        List<TimeSlot> availableSlots
) {
    public static UniversityDataset createDefault() {
        Course course2 = new Course(2, "Программная инженерия");
        Course course3 = new Course(3, "Информационные системы");

        Group g71 = createGroupWithStudents("7.1", course2, 18);
        Group g72 = createGroupWithStudents("7.2", course2, 24);
        Group g81 = createGroupWithStudents("8.1", course3, 27);
        Group g82 = createGroupWithStudents("8.2", course3, 30);

        Teacher tIvanov = new Teacher("T1", "Иванов И.И.", "Кафедра информатики");
        Teacher tPetrova = new Teacher("T2", "Петрова А.С.", "Кафедра математики");
        Teacher tSidorov = new Teacher("T3", "Сидоров П.К.", "Кафедра баз данных");
        Teacher tSmirnova = new Teacher("T4", "Смирнова Е.В.", "Кафедра высшей математики");

        Subject oop = new Subject("CS201", "ООП на Java");
        Subject math = new Subject("MA202", "Дискретная математика");
        Subject algo = new Subject("CS205", "Алгоритмы и структуры данных");
        Subject db = new Subject("DB301", "Базы данных");
        Subject mathAnalysis = new Subject("MA302", "Математический анализ");
        Subject arch = new Subject("SE303", "Архитектура ПО");

        Room r101 = new Room("101", 20);
        Room r102 = new Room("102", 24);
        Room r201 = new Room("201", 25);
        Room r301 = new Room("301", 32);

        List<CurriculumItem> curriculum = List.of(
                new CurriculumItem(g71, oop, tIvanov, 2),
                new CurriculumItem(g71, math, tPetrova, 2),
                new CurriculumItem(g71, algo, tSidorov, 2),

                new CurriculumItem(g72, oop, tIvanov, 2),
                new CurriculumItem(g72, math, tPetrova, 2),
                new CurriculumItem(g72, algo, tSidorov, 2),

                new CurriculumItem(g81, db, tSidorov, 2),
                new CurriculumItem(g81, mathAnalysis, tSmirnova, 3),
                new CurriculumItem(g81, arch, tIvanov, 3),

                new CurriculumItem(g82, db, tSidorov, 2),
                new CurriculumItem(g82, mathAnalysis, tSmirnova, 3),
                new CurriculumItem(g82, arch, tIvanov, 3)
        );

        List<TimeSlot> slots = new ArrayList<>();
        StudyDay[] days = {StudyDay.MONDAY, StudyDay.TUESDAY, StudyDay.WEDNESDAY, StudyDay.THURSDAY, StudyDay.FRIDAY};
        for (StudyDay day : days) {
            for (int pair = 1; pair <= 3; pair++) {
                slots.add(new TimeSlot(day, pair));
            }
        }

        return new UniversityDataset(
                List.of(g71, g72, g81, g82),
                List.of(tIvanov, tPetrova, tSidorov, tSmirnova),
                List.of(oop, math, algo, db, mathAnalysis, arch),
                List.of(r101, r102, r201, r301),
                curriculum,
                List.copyOf(slots)
        );
    }

    private static Group createGroupWithStudents(String name, Course course, int count) {
        Group group = new Group(name, course);
        for (int i = 1; i <= count; i++) {
            group.addStudent(new Student(name + "-S" + i, "Студент " + name + "-" + i));
        }
        return group;
    }
}
