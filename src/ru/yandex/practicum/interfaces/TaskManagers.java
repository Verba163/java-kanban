package ru.yandex.practicum.interfaces;


import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TaskManagers {
    int generateTaskId();

    Task createTask(String title, String description, Duration duration, LocalDateTime startTime);

    Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId);

    Epic createEpic(String title, String description);


    List<Epic> getAllEpics();  // Метод для получения всех эпиков

    void addEpic(Epic epic);  // Метод для добавления эпика

    Epic getEpic(int epicId);

    Task getTask(int taskId);

    void updateTask(Task task);

    void deleteTask(int taskId);

    void deleteSubtask(int taskId);

    public List<Task> getAllTasks();

    Subtask getSubtask(int taskId);

    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    void removeAllTasks();

    void updateEpicStatus(Epic epic);

    default boolean allSubtasksCompleted(Epic epic) {
        return false;
    }


    List<Subtask> getAllSubtasks();

    void deleteEpic(int epicId);

    List<String> getTaskOverlapping(Task task);

    List<Subtask> getOverlappingSubtasks(Subtask subtask);
}