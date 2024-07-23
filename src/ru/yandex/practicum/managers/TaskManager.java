package ru.yandex.practicum.managers;

import ru.yandex.practicum.enums.TaskStatus;
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

    public Subtask createSubtask(String title, String description, int epicId) {
        int taskId = generateTaskId();
        Subtask subtask = new Subtask(title, description, taskId);
        tasks.put(taskId, subtask);

        Epic epic = (Epic) tasks.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }

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

    public void deleteTask(int taskId) { //удаляем таск по айди
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

    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksCompleted(epic)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private boolean allSubtasksCompleted(Epic epic) {
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }

    public void deleteEpic(int epicId) {
        Epic epic = (Epic) tasks.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                tasks.remove(subtask.getTaskId());
            }
        }
    }
}