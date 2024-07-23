package ru.yandex.practicum.models;

import ru.yandex.practicum.enums.TaskStatus;

import java.util.Objects;

public class Task {

    private String title;
    private String description;
    private final int taskId;
    private TaskStatus status;

    public Task(String title, String description, int taskId) {
        this.title = title;
        this.description = description;
        this.taskId = taskId;
        this.status = TaskStatus.NEW;

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
        return Objects.hash(title, description, taskId, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status=" + status +
                '}';
    }
}


