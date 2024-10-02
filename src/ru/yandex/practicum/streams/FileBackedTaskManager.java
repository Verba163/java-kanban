package ru.yandex.practicum.streams;

import ru.yandex.practicum.enums.TaskStatus;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManagers {
    private File file;
    private String filePath;


    public FileBackedTaskManager(File file) {
        this.file = file;
        this.filePath = file.getAbsolutePath();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Task task : tasks.values()) {
                writer.write("TASK," + task.toCSV() + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write("EPIC," + epic.toCSV() + "\n");
                for (Subtask subtask : subtasksByEpic.getOrDefault(epic.getTaskId(), new ArrayList<>())) {
                    writer.write("SUBTASK," + subtask.toCSV() + "\n");
                }
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
                if (buildText.length < 5) continue;

                String type = buildText[0];
                int id = Integer.parseInt(buildText[1]);
                String name = buildText[2];
                String status = buildText[3];
                String description = buildText[4];

                switch (type) {
                    case "TASK": {
                        Task task = new Task(name, description, id);
                        task.setStatus(TaskStatus.valueOf(status));
                        manager.tasks.put(id, task);
                        break;
                    }
                    case "EPIC": {
                        Epic epic = new Epic(name, description, id);
                        epic.setStatus(TaskStatus.valueOf(status));
                        manager.epics.put(id, epic);
                        manager.tasks.put(id, epic);
                        break;
                    }
                    case "SUBTASK": {
                        int epicId = Integer.parseInt(buildText[5]);
                        Subtask subtask = new Subtask(name, description, id);
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
    public Task createTask(String title, String description) {
        Task task = super.createTask(title, description);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        Subtask subtask = super.createSubtask(title, description, epicId);
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