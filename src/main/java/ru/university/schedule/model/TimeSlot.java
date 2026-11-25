package ru.university.schedule.model;

import java.util.Comparator;
import java.util.Objects;

public record TimeSlot(StudyDay day, int pairNumber) implements Comparable<TimeSlot> {
    public static final int MIN_PAIR = 1;
    public static final int MAX_PAIR = 6;

    public TimeSlot {
        Objects.requireNonNull(day, "День недели не может быть null");
        if (pairNumber < MIN_PAIR || pairNumber > MAX_PAIR) {
            throw new IllegalArgumentException("Номер пары должен быть от " + MIN_PAIR + " до " + MAX_PAIR);
        }
    }

    @Override
    public int compareTo(TimeSlot other) {
        return Comparator.comparing(TimeSlot::day)
                .thenComparingInt(TimeSlot::pairNumber)
                .compare(this, other);
    }

    @Override
    public String toString() {
        return day.getDisplayName() + ", " + pairNumber + " пара";
    }
}
