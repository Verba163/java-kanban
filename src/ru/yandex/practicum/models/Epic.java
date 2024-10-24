package ru.yandex.practicum.models;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String title, String description, int taskId) {
        super(title, description, taskId, Duration.ZERO, LocalDateTime.now());
        this.subtasks = new ArrayList<>();
    }

    @Override
    public int getTaskId() {
        return super.getTaskId();
    }

    @Override
    public Duration getDuration() {
        return subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.MIN);
    }

    @Override
    public LocalDateTime getStartTime() {
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.MIN);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setEpic(this);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtasks(Subtask subtask) {
        if (subtasks.remove(subtask)) {
            subtask.setEpic(null);
        }
    }

    @Override
    public String toCSV() {
        return "EPIC," + getTaskId() + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," +
                getDuration().toString() + "," + getStartTime().toString() + "," + getEndTime().toString();
    }

    @Override
    public String toString() {
        return "Epic: " + this.title + ", Subtasks: " + this.subtasks.toString() +
                ", Duration: " + getDuration() +
                ", StartTime: " + getStartTime() +
                ", EndTime: " + getEndTime();

    }
}
