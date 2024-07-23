package ru.yandex.practicum.models;

import ru.yandex.practicum.enums.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String title, String description, int taskId) {
        super(title, description, taskId);
        this.subtasks = new ArrayList<>();
    }


    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setEpic(this);
        updateStatus(); // обновили статус после добавления задачи
    }


    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


    public boolean checkCompletion() {
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }


    private boolean areAllSubtasksNew() {
        if (subtasks.isEmpty()) {
            return true;
        }

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                return false;
            }
        }

        return true;
    }


    public void updateStatus() {
        if (areAllSubtasksNew()) {
            setStatus(TaskStatus.NEW);
        } else if (checkCompletion()) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void removeSubtasks(Subtask subtask) {
        if (subtasks.remove(subtask)) {
            subtask.setEpic(null);
            updateStatus(); // обновляем статус после удаления подзадачи
        }
    }


    public void clearSubtasks(Subtask subtask) { // обновляем статус после удаления всех подзадач
        subtasks.clear();
        updateStatus();
    }


    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}