package ru.yandex.practicum.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, int taskId, Duration duration, LocalDateTime startTime) {
        super(title, description, taskId, duration, startTime);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toCSV() {
        return "SUBTASK," + getTaskId() + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," +
                getDuration().toString() + "," + getStartTime().toString() + "," + getEndTime().toString() + "," + epic.getTaskId();
    }



    @Override
    public String toString() {
            return "Subtask{id=" + getTaskId() + ", title='" + getTitle() +
                    "', status='" + getStatus() +
                    "', duration=" + getDuration().toMinutes() +
                    ", startTime=" + getStartTime() +
                    ", endTime=" + getEndTime() +
                    "}";

        }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Subtask subtask = (Subtask) obj;
        return Objects.equals(epic, subtask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}


