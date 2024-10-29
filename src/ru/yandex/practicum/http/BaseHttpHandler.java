package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    public Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        sendResponse(exchange, text, statusCode);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendErrorResponse(exchange, "Not found", 404);
    }

    protected void sendHasOverlapping(HttpExchange exchange) throws IOException {
        sendErrorResponse(exchange, "Задача пересекается с текущей задачей", 406);
    }

    void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendErrorResponse(HttpExchange exchange, String errorMessage, int statusCode) throws IOException {
        String errorResponse = String.format("{\"Error\":\"%s\"}", errorMessage);
        sendResponse(exchange, errorResponse, statusCode);
    }

    void sendJsonResponse(HttpExchange exchange, Object responseObject, int statusCode) throws IOException {
        Gson gson = createGson();
        String jsonResponse = gson.toJson(responseObject);
        sendResponse(exchange, jsonResponse, statusCode);
    }

    int extractIdFromPath(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        String[] parts = requestURI.split("/");

        if (parts.length != 3) {
            sendErrorResponse(exchange, "Неверный запрос", 400);
            return -1;
        }

        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException exception) {
            sendErrorResponse(exchange, "Некорректный ID задачи", 400);
            return -1;
        }
    }
}