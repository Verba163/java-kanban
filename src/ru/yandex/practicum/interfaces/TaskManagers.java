package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public interface TaskManagers {
    int generateTaskId();

    Task createTask(String title, String description, Duration duration, LocalDateTime startTime);

    Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId);

    Epic createEpic(String title, String description);

    Task getTask(int taskId);

    void updateTask(Task task);

    void deleteTask(int taskId);

    public Collection<Task> getAllTasks();


    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    void removeAllTasks();

    void updateEpicStatus(Epic epic);

    default boolean allSubtasksCompleted(Epic epic) {
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }

    void deleteEpic(int epicId);

}