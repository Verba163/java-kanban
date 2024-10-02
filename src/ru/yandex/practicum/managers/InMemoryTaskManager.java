package ru.yandex.practicum.managers;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManagers {

    private int taskIdCounter;
    public final HashMap<Integer, Task> tasks;
    public final HashMap<Integer, Epic> epics;
    public final HashMap<Integer, ArrayList<Subtask>> subtasksByEpic;


    public InMemoryTaskManager() {
        this.taskIdCounter = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasksByEpic = new HashMap<>();

    }


    @Override
    public int generateTaskId() {
        return taskIdCounter++;
    }

    @Override
    public Task createTask(String title, String description) {
        int taskId = generateTaskId();
        Task task = new Task(title, description, taskId);
        tasks.put(taskId, task);
        return task;
    }


    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        int taskId = generateTaskId();
        Subtask subtask = new Subtask(title, description, taskId);
        tasks.put(taskId, subtask);

        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
            subtasksByEpic.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtask);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public Epic createEpic(String title, String description) {
        int taskId = generateTaskId();
        Epic epic = new Epic(title, description, taskId);
        tasks.put(taskId, epic);
        epics.put(taskId, epic);
        return epic;
    }

    @Override
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? subtasksByEpic.getOrDefault(epicId, new ArrayList<>()) : new ArrayList<>();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksCompleted(epic)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = (Epic) tasks.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                tasks.remove(subtask.getTaskId());
            }
        }
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            System.out.println("Статус задачи с ID " + taskId + " обновлён на " + newStatus);
        } else {
            System.out.println("Задача с ID " + taskId + " не найдена.");
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }
}

