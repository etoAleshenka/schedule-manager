package ru.university.schedule.model;

/** * Учебная аудитория с ограниченной вместимостью. */
public record Room(String number, int capacity) {
    /** * Проверяет корректность номера и вместимости аудитории. */
    public Room {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Номер аудитории не может быть пустым");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость аудитории должна быть положительной");
        }
        number = number.trim();
    }

    @Override
    public String toString() {
        return "ауд. " + number + " (мест: " + capacity + ")";
    }
}
