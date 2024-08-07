package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.util.ArrayList;

public interface TaskManagers {
    int generateTaskId();

    Task createTask(String title, String description);

    Subtask createSubtask(String title, String description, int epicId);

    Epic createEpic(String title, String description);

    Task getTask(int taskId);

    void updateTask(Task task);

    void deleteTask(int taskId);

    ArrayList<Task> getAllTasks();

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