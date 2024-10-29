package ru.yandex.practicum.http;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryHistoryManager;
import ru.yandex.practicum.managers.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;


    public static void main(String[] args) throws IOException {


        TaskManagers httpTaskManager = new InMemoryTaskManager();
        HistoryManager historyManager = new InMemoryHistoryManager();

        initializeTestData(httpTaskManager, 10);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(httpTaskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(httpTaskManager));
        httpServer.createContext("/epics", new EpicHandler(httpTaskManager));
        httpServer.createContext("/history", new HistoryManagerHandler(historyManager));
        httpServer.setExecutor(null);
        httpServer.start();

        System.out.println(String.format("HTTP-сервер запущен на %s%s ", PORT, " порту!"));
    }

    private static void initializeTestData(TaskManagers httpTaskManager, int numberOfTasks) {
        for (int i = 1; i <= numberOfTasks; i++) {
            String taskName = "Task" + i;
            String taskDescription = "Description" + i;

            // Создание задачи
            LocalDateTime startTime = LocalDateTime.of(2000 + i, 12, 12, 12, 12);
            httpTaskManager.createTask(taskName, taskDescription, Duration.ofHours(1), startTime);
            System.out.println("Создана задача: " + taskName);
        }

        for (int i = 1; i <= numberOfTasks; i++) {
            String subtaskName = "Subtask" + i;
            String subtaskDescription = "Description" + i;
            LocalDateTime startTime = LocalDateTime.of(1970 + i, 12, 12, 12, 12);
            httpTaskManager.createSubtask(subtaskName, subtaskDescription, Duration.ofHours(1), startTime, 1);
        }
        for (int i = 1; i <= numberOfTasks; i++) {
            String subtaskName = "Epic" + i;
            String subtaskDescription = "Description" + i;
            LocalDateTime startTime = LocalDateTime.of(1970 + i, 12, 12, 12, 12);
            httpTaskManager.createEpic(subtaskName, subtaskDescription);

        }
    }
}