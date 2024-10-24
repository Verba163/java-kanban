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

    public boolean isOverlapping(Task task1, Task task2) {
        LocalDateTime task1Start = task1.getStartTime();
        LocalDateTime task1End = task1Start.plus(task1.getDuration());
        LocalDateTime task2Start = task2.getStartTime();
        LocalDateTime task2End = task2Start.plus(task2.getDuration());

        return task1Start.isBefore(task2End) && task2Start.isBefore(task1End);
    }

    public List<String> getTaskOverlapping(Task task) {
        List<String> overlappingTasks = new ArrayList<>();
        prioritizedTasks.forEach(existingTask -> {
            if (isOverlapping(existingTask, task)) {
                overlappingTasks.add("Задача '" + existingTask.getTitle() + "' пересекается с задачей '" + task.getTitle() + "'.");
            }
        });
        return overlappingTasks;
    }

    public List<String> getOverlappingSubtasks(Subtask subtask) {
        List<String> overlappingSubtaskMessages = new ArrayList<>();
        List<Subtask> existingSubtasks = subtasksByEpic.get(subtask.getEpicId());

        if (existingSubtasks != null) {
            existingSubtasks.forEach(existingSubtask -> {
                if (isOverlapping(existingSubtask, subtask)) {
                    overlappingSubtaskMessages.add("Подзадача '" + existingSubtask.getTitle() + "' пересекается с подзадачей '" + subtask.getTitle() + "'.");
                }
            });
        }

        return overlappingSubtaskMessages;
    }

    @Override
    public int generateTaskId() {
        return taskIdCounter++;
    }

    @Override
    public Task createTask(String title, String description, Duration duration, LocalDateTime startTime) {
        int taskId = generateTaskId();
        Task task = new Task(title, description, taskId, duration, startTime);
        List<String> overlappingTasks = getTaskOverlapping(task);
        if (!overlappingTasks.isEmpty()) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей." + overlappingTasks);
        }
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
        return task;
    }


    @Override
    public Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        int taskId = generateTaskId();

        Subtask subtask = new Subtask(title, description, taskId, duration, startTime);
        subtask.setEpic(epics.get(epicId));
        List<String> overlappingSubtask = getOverlappingSubtasks(subtask);

        if (!getOverlappingSubtasks(subtask).isEmpty()) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей задачей или подзадачей." + overlappingSubtask);
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
        if (!getTaskOverlapping(task).isEmpty()) {
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

