import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.EpicHandler;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Epic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicTest {
    private HttpServer server;
    private TaskManagers taskManager;
    private EpicHandler epicHandler;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        epicHandler = new EpicHandler(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/epics", epicHandler);
        server.setExecutor(null);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop(0);
    }

    @Test
    public void testHandleGetAllEpics() throws IOException {
        // Создаем эпик для тестирования
        taskManager.createEpic("Epic 1", "Description of Epic 1");

        // Выполнение GET-запроса
        URI uri = URI.create("http://localhost:8080/epics");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        // Проверка ответа
        String responseBody = new String(connection.getInputStream().readAllBytes());
        List<Epic> epicsFromResponse = List.of(gson.fromJson(responseBody, Epic[].class));
        assertEquals(1, epicsFromResponse.size());
        assertEquals("Epic 1", epicsFromResponse.get(0).getTitle());
    }

    @Test
    public void testHandleGetEpicById() throws IOException {
        // Создаем эпик для тестирования
        taskManager.createEpic("Epic 1", "Description of Epic 1");

        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        String responseBody = new String(connection.getInputStream().readAllBytes());
        Epic epicFromResponse = gson.fromJson(responseBody, Epic.class);
        assertNotNull(epicFromResponse);
        assertEquals("Epic 1", epicFromResponse.getTitle());
    }

    @Test
    public void testHandleGetEpicNotFound() throws IOException {
        URI uri = URI.create("http://localhost:8080/epics/999");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(404, connection.getResponseCode());
    }

    @Test
    public void testHandleInvalidPath() throws IOException {
        URI uri = URI.create("http://localhost:8080/invalid");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(404, connection.getResponseCode());
    }
}