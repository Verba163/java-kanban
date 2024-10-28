package ru.yandex.practicum.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.models.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TaskManagers httpTaskManager;

    public SubtaskHandler(TaskManagers httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
        this.gson = createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запросов: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetSubtask(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtask(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        if (requestURI.equals("/subtasks")) {
            List<Subtask> allSubtasks = httpTaskManager.getAllSubtasks();
            sendJsonResponse(exchange, allSubtasks, 200);
            return;
        } else if (requestURI.startsWith("/subtasks/")) {
            int subtaskId = extractIdFromPath(exchange);
            Subtask subtask = httpTaskManager.getSubtask(subtaskId);
            if (subtask == null) {
                sendErrorResponse(exchange, "Подзадача не найдена", 404);
                return;
            }
            sendJsonResponse(exchange, subtask, 200);
            return;
        }
        sendErrorResponse(exchange, "Неверный путь", 404);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);

        if (requestBody == null || requestBody.isEmpty()) {
            sendErrorResponse(exchange, "Неверные данные подзадачи", 400);
            return;
        }

        Subtask subtask;
        try {
            subtask = gson.fromJson(requestBody, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendErrorResponse(exchange, "Ошибка синтаксиса JSON", 400);
            return;
        }

        if (subtask == null || subtask.getTitle() == null || subtask.getDescription() == null) {
            sendErrorResponse(exchange, "Неверные данные подзадачи", 400);
            return;
        }

        if (subtask.getTaskId() > 0) {
            List<Subtask> overlappingSubtasks = httpTaskManager.getOverlappingSubtasks(subtask);
            if (!overlappingSubtasks.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Подзадача пересекается с существующими подзадачами: ");
                for (Subtask overlappingSubtask : overlappingSubtasks) {
                    errorMessage.append(overlappingSubtask.getTitle()).append(", ");
                }
                errorMessage.setLength(errorMessage.length() - 2);
                sendErrorResponse(exchange, errorMessage.toString(), 400);
                return;
            }

            if (httpTaskManager.getSubtask(subtask.getTaskId()) == null) {
                sendErrorResponse(exchange, "Подзадача не найдена для обновления", 404);
                return;
            }

            try {
                httpTaskManager.updateTask(subtask);
                sendResponse(exchange, "{\"Status\":\"Подзадача обновлена\"}", 200);
            } catch (Exception e) {
                sendErrorResponse(exchange, "Произошла ошибка при обновлении подзадачи", 500);
            }
        } else {
            try {
                httpTaskManager.createSubtask(subtask.getTitle(), subtask.getDescription(), subtask.getDuration(),
                        subtask.getStartTime(), subtask.getEpicId());
                sendResponse(exchange, "{\"Status\":\"Подзадача добавлена\"}", 201);
            } catch (Exception e) {
                sendErrorResponse(exchange, "Произошла ошибка при создании подзадачи", 500);
            }
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange);
        if (id < 0) {
            sendNotFound(exchange);
            return;
        }

        Subtask subtaskToDelete = httpTaskManager.getSubtask(id);
        if (subtaskToDelete != null) {
            httpTaskManager.deleteSubtask(id);
            sendText(exchange, "{\"Status\":\"Подзадача удалена\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}