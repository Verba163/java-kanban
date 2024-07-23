package ru.yandex.practicum.managers;

import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int taskIdCounter;
    private final HashMap<Integer, Task> tasks;

    public TaskManager() {
        this.taskIdCounter = 1;
        this.tasks = new HashMap<>();
    }

    public int generateTaskId() {
        return taskIdCounter++;
    }

    public Task createTask(String title, String description) {
        int taskId = generateTaskId();
        Task task = new Task(title, description, taskId);
        tasks.put(taskId, task);
        return task;
    }

    public Subtask createSubtask(String title, String description) {
        int taskId = generateTaskId();
        Subtask subtask = new Subtask(title, description, taskId);
        tasks.put(taskId, subtask);
        return subtask;
    }

    public Epic createEpic(String title, String description) {
        int taskId = generateTaskId();
        Epic epic = new Epic(title, description, taskId);
        tasks.put(taskId, epic);
        return epic;
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = (Epic) tasks.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    public void removeAllTasks() {
        tasks.clear();
    }
}