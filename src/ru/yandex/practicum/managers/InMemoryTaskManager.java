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
    private final List<Subtask> subtasks = new ArrayList<>();
    private final List<Epic> epicsList = new ArrayList<>();
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
                overlappingTasks.add(String.format("Задача '%s' пересекается с задачей '%s'.", existingTask.getTitle(), task.getTitle()));
            }
        });
        return overlappingTasks;
    }

    public List<Subtask> getOverlappingSubtasks(Subtask newSubtask) {
        List<Subtask> overlappingSubtasks = new ArrayList<>();
        List<Subtask> existingSubtasks = subtasksByEpic.get(newSubtask.getEpicId());

        if (existingSubtasks != null) {
            for (Subtask existingSubtask : existingSubtasks) {
                if (isOverlapping(existingSubtask, newSubtask)) {
                    overlappingSubtasks.add(existingSubtask);
                }
            }
        }

        return overlappingSubtasks;
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
            throw new IllegalArgumentException(String.format("Задача пересекается с существующей задачей: %s", overlappingTasks));
        }
        tasks.put(taskId, task);
        prioritizedTasks.add(task);
        return task;
    }


    @Override
    public Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        int taskId = generateTaskId();
        Subtask subtask = new Subtask(title, description, taskId, duration, startTime);
        subtask.setEpic(epics.get(epicId)); // Убедитесь, что epicId корректно передается

        List<Subtask> overlappingSubtask = getOverlappingSubtasks(subtask);
        if (!overlappingSubtask.isEmpty()) {
            StringBuilder message = new StringBuilder("Подзадача пересекается с существующими задачами или подзадачами: ");
            for (Subtask existingSubtask : overlappingSubtask) {
                message.append(String.format("Подзадача '%s', ", subtask.getTitle()));
            }
            message.setLength(message.length() - 2);
            throw new IllegalArgumentException(message.toString());
        }

        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
            subtasksByEpic.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtask);
            updateEpicStatus(epic);
            prioritizedTasks.add(epic);
        }
        subtasks.add(subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Epic createEpic(String title, String description) {
        int taskId = generateTaskId();
        Epic epic = new Epic(title, description, taskId);
        epics.put(taskId, epic);
        epicsList.add(epic);
        prioritizedTasks.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsList);
    }

    @Override
    public void addEpic(Epic epic) {
        epicsList.add(epic);
    }


    @Override
    public Epic getEpic(int id) {
        for (Epic epic : epicsList) {
            if (epic.getTaskId() == id) {
                return epic;
            }
        }
        return null;
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
    public void deleteSubtask(int subtaskId) {
        Subtask subtaskToRemove = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == subtaskId) {
                subtaskToRemove = subtask;
                break;
            }
        }

        if (subtaskToRemove != null) {
            subtasks.remove(subtaskToRemove);
            prioritizedTasks.remove(subtaskToRemove);
            System.out.println(String.format("Подзадача с ID %d удалена.", subtaskId));
        } else {
            System.out.println(String.format("Подзадача с ID %d не найдена.", subtaskId));
        }

    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Subtask getSubtask(int id) {
        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == id) {
                return subtask;
            }
        }
        return null;
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
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks);
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
            System.out.println(String.format("Статус задачи с ID %d обновлён на %s.", taskId, newStatus));
        } else {
            System.out.println(String.format("Задача с ID %d не найдена.", taskId));
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }
}

