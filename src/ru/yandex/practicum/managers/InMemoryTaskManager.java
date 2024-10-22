package ru.yandex.practicum.managers;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManagers {

    private int taskIdCounter;
    public final HashMap<Integer, Task> tasks;
    public final HashMap<Integer, Epic> epics;
    public final HashMap<Integer, ArrayList<Subtask>> subtasksByEpic;
    public final TreeSet<Task> prioritizedTasks = new TreeSet<>();


    public InMemoryTaskManager() {
        this.taskIdCounter = 1;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasksByEpic = new HashMap<>();
    }
    public boolean isTaskOverlapping(Task task) {
        List<Task> sortedTasks = new ArrayList<>(prioritizedTasks);
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = taskStart.plus(task.getDuration());

        for (Task existingTask : sortedTasks) {
            LocalDateTime existingTaskStart = existingTask.getStartTime();
            LocalDateTime existingTaskEnd = existingTaskStart.plus(existingTask.getDuration());
            if (existingTaskStart.isBefore(taskEnd) && taskStart.isBefore(existingTaskEnd)) {
                return true;
            }
        }
        return false;
    }
    public boolean isSubtaskOverlapping(Subtask subtask) {
        return isTaskOverlapping(subtask);
    }

    @Override
    public int generateTaskId() {
        return taskIdCounter++;
    }

    @Override
    public Task createTask(String title, String description, Duration duration, LocalDateTime startTime) {
        int taskId = generateTaskId();
        Task task = new Task(title, description, taskId, duration, startTime);
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей.");
        }
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
        return task;
    }


    @Override
    public Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        int taskId = generateTaskId();

        Subtask subtask = new Subtask(title, description, taskId, duration, startTime);
        if (isSubtaskOverlapping(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей задачей или подзадачей.");
        }


        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
            subtasksByEpic.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtask);
            updateEpicStatus(epic);
            prioritizedTasks.add(epic);
        }
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Epic createEpic(String title, String description) {
        int taskId = generateTaskId();
        Epic epic = new Epic(title, description, taskId);
        epics.put(taskId, epic);
        return epic;
    }

    @Override
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public void updateTask(Task task) {
        Task existingTask = tasks.get(task.getTaskId());
        if (existingTask != null) {
            prioritizedTasks.remove(existingTask);
        }
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Обновленная задача пересекается с существующей задачей.");
        }

        tasks.put(task.getTaskId(), task);
        prioritizedTasks.add(task);
    }


    @Override
    public void deleteTask(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(prioritizedTasks);
           }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? subtasksByEpic.getOrDefault(epicId, new ArrayList<>()) : new ArrayList<>();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
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

