package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.models.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManagers httpTaskManager;

    public TaskHandler(TaskManagers httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запросов: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetTask(exchange);
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        if (requestURI.equals("/tasks")) {
            List<Task> allTasks = httpTaskManager.getAllTasks();
            sendJsonResponse(exchange, allTasks, 200);
            return;
        } else if (requestURI.startsWith("/tasks/")) {
            int taskId = extractIdFromPath(exchange);
            Task task = httpTaskManager.getTask(taskId);
            if (task == null) {
                sendErrorResponse(exchange, "Задача не найдена", 404);
            } else {
                sendJsonResponse(exchange, task, 200);
            }
            return;
        }
        sendErrorResponse(exchange, "Неверный путь", 404);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String requestBody = readRequestBody(exchange);
            if (requestBody == null || requestBody.isEmpty()) {
                sendErrorResponse(exchange, "Неверные данные задачи", 400);
                return;
            }

            Task task = parseTask(requestBody);
            if (task == null || task.getTitle() == null || task.getDescription() == null) {
                sendErrorResponse(exchange, "Неверные данные задачи", 400);
                return;
            }

            if (task.getTaskId() > 0) {
                if (!validateTaskOverlap(task, exchange)) return;
                updateTask(task, exchange);
            } else {
                createTask(task, exchange);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке POST запроса: " + e.getMessage());
            sendErrorResponse(exchange, "Ошибка сервера", 500);
        }
    }

    private Task parseTask(String requestBody) {
        try {
            return gson.fromJson(requestBody, Task.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка синтаксиса JSON: " + e.getMessage());
            return null;
        }
    }

    private boolean validateTaskOverlap(Task task, HttpExchange exchange) throws IOException {
        List<String> overlappingTasks = httpTaskManager.getTaskOverlapping(task);
        overlappingTasks.removeIf(t -> t.contains(task.getTitle()) && task.getTaskId() == task.getTaskId());
        if (!overlappingTasks.isEmpty()) {
            sendErrorResponse(exchange, "Задача пересекается с существующими задачами: " + overlappingTasks, 400);
            return false;
        }
        return true;
    }

    private void updateTask(Task task, HttpExchange exchange) throws IOException {
        try {
            httpTaskManager.updateTask(task);
            sendResponse(exchange, "{\"status\":\"Задача обновлена\"}", 200);
        } catch (Exception e) {
            System.out.println("Ошибка обновления задачи: " + e.getMessage());
            sendErrorResponse(exchange, "Произошла ошибка при обновлении задачи", 500);
        }
    }

    private void createTask(Task task, HttpExchange exchange) throws IOException {
        try {
            httpTaskManager.createTask(task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime());
            sendResponse(exchange, "{\"status\":\"Задача добавлена\"}", 201);
        } catch (Exception e) {
            System.out.println("Ошибка создания задачи: " + e.getMessage());
            sendErrorResponse(exchange, "Произошла ошибка при создании задачи", 500);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange);
        if (id < 0) {
            sendNotFound(exchange);
            return;
        }

        Task taskToDelete = httpTaskManager.getTask(id);
        if (taskToDelete != null) {
            httpTaskManager.deleteTask(id);
            sendText(exchange, "{\"status\":\"Задача удалена\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}