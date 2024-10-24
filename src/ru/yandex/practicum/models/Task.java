package ru.yandex.practicum.models;

import ru.yandex.practicum.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected String title;
    protected String description;
    private final int taskId;
    protected TaskStatus status;
    protected Duration duration;
    private LocalDateTime startTime;


    public Task(String title, String description, int taskId, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.taskId = taskId;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }


    public int getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void updateTask(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public String toCSV() {
        return "TASK," + taskId + "," + getTitle() + "," + getStatus() + "," + getDescription() + "," +
                getDuration().toString() + "," + getStartTime().toString() + "," + getEndTime().toString();
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task other = (Task) obj;
        return taskId == other.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, taskId, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public int compareTo(Task other) {
        return this.startTime.compareTo(other.startTime);
    }
}