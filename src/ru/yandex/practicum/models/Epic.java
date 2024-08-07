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
    public String toString() {
        return "Epic: " + this.title + ", Subtasks: " + this.subtasks.toString();
    }
}