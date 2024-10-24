package ru.yandex.practicum.streams;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManagers {
    private File file;
    private String filePath;


    public FileBackedTaskManager(File file) {
        this.file = file;
        this.filePath = file.getAbsolutePath();
    }


    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<Task> allTasks = new ArrayList<>(getAllTasks());
            allTasks.addAll(epics.values());
            subtasksByEpic.values().forEach(allTasks::addAll);
            allTasks.sort(Comparator.comparing(Task::getStartTime));
            for (Task task : allTasks) {
                writer.write(task.toCSV() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] buildText = line.split(",", -1);
                if (buildText.length < 8) continue;
                String type = buildText[0];
                int id = Integer.parseInt(buildText[1]);
                if (manager.tasks.containsKey(id) || (type.equals("EPIC") && manager.epics.containsKey(id))) {
                    continue;
                }
                String title = buildText[2];
                String status = buildText[3];
                String description = buildText[4];
                Duration duration = Duration.parse(buildText[5]);
                LocalDateTime startTime = LocalDateTime.parse(buildText[6]);
                LocalDateTime endTime = LocalDateTime.parse(buildText[7]);

                switch (type) {
                    case "TASK": {
                        Task task = new Task(title, description, id, duration, startTime);
                        task.setStatus(TaskStatus.valueOf(status));
                        manager.tasks.put(id, task);
                        manager.prioritizedTasks.add(task);
                        break;
                    }
                    case "EPIC": {
                        Epic epic = new Epic(title, description, id);
                        epic.setStatus(TaskStatus.valueOf(status));
                        manager.epics.put(id, epic);
                        break;
                    }
                    case "SUBTASK": {
                        if (buildText.length < 10) continue;
                        int epicId = Integer.parseInt(buildText[9]);
                        Subtask subtask = new Subtask(title, description, id, duration, startTime);
                        subtask.setStatus(TaskStatus.valueOf(status));
                        manager.tasks.put(id, subtask);
                        Epic epic = manager.epics.get(epicId);
                        if (epic != null) {
                            epic.addSubtask(subtask);
                            manager.subtasksByEpic.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtask);
                            manager.updateEpicStatus(epic);
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public Task createTask(String title, String description, Duration duration, LocalDateTime startTime) {
        Task task = super.createTask(title, description, duration, startTime);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        Subtask subtask = super.createSubtask(title, description, duration, startTime, epicId);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = super.createEpic(title, description);
        save();
        return epic;
    }

}