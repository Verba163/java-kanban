package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.models.Epic;

import java.io.*;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TaskManagers httpTaskManager;

    public EpicHandler(TaskManagers httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
        this.gson = createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запросов: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetEpic(exchange);
                break;
            case "POST":
                handlePostEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpic(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        if (requestURI.equals("/epics")) {
            List<Epic> allEpics = httpTaskManager.getAllEpics();
            sendJsonResponse(exchange, allEpics, 200);
            return;
        } else if (requestURI.startsWith("/epics/")) {
            int epicId = extractIdFromPath(exchange);
            Epic epic = httpTaskManager.getEpic(epicId);
            if (epic == null) {
                sendErrorResponse(exchange, "Эпик не найден", 404);
                return;
            }
            sendJsonResponse(exchange, epic, 200);
            return;
        }
        sendErrorResponse(exchange, "Неверный путь", 404);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        if (requestBody == null || requestBody.isEmpty()) {
            sendErrorResponse(exchange, "Неверные данные эпика", 400);
            return;
        }
        Epic epic;
        try {
            epic = gson.fromJson(requestBody, Epic.class);
        } catch (JsonSyntaxException e) {
            sendErrorResponse(exchange, "Ошибка синтаксиса JSON", 400);
            return;
        } catch (Exception e) {
            sendErrorResponse(exchange, "Неизвестная ошибка при обработке", 500);
            return;
        }
        if (epic == null || epic.getTitle() == null || epic.getDescription() == null) {
            sendErrorResponse(exchange, "Неверные данные эпика", 400);
            return;
        }
        if (epic.getTaskId() > 0) {
            try {
                httpTaskManager.updateTask(epic);
                sendResponse(exchange, "{\"Status\":\"Эпик обновлен\"}", 200);
            } catch (Exception e) {
                sendErrorResponse(exchange, "Произошла ошибка при обновлении эпика", 500);
            }
        } else {
            httpTaskManager.createEpic(epic.getTitle(), epic.getDescription());
            sendResponse(exchange, "{\"Status\":\"Эпик добавлен\"}", 201);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange);
        if (id < 0) {
            sendNotFound(exchange);
            return;
        }
        Epic epicToDelete = httpTaskManager.getEpic(id);
        if (epicToDelete != null) {
            httpTaskManager.deleteEpic(id);
            sendText(exchange, "{\"Status\":\"Эпик удален\"}", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}