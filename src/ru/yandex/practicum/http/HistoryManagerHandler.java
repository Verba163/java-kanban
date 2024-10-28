package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HistoryManagerHandler extends BaseHttpHandler implements HttpHandler {

    private final HistoryManager historyManager;
    private final Gson gson;

    public HistoryManagerHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.gson = createGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запросов: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetHistory(exchange);
                break;
            default:
                sendResponse(exchange, "Метод не поддерживается", 405);
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = historyManager.getHistory();
        String response = gson.toJson(history);
        sendResponse(exchange, response, 200);
    }
}