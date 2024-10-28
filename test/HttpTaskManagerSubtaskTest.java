import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.SubtaskHandler;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Subtask;
import ru.yandex.practicum.models.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtaskTest {
    private HttpServer server;
    private TaskManagers taskManager;
    private SubtaskHandler subtaskHandler;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        subtaskHandler = new SubtaskHandler(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/subtasks", subtaskHandler);
        server.setExecutor(null);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop(0);
    }

    @Test
    public void testHandleGetAllSubtasks() throws IOException {

        taskManager.createSubtask("Subtask 1", "Description", Duration.ofMinutes(10), LocalDateTime.now(), 1);

        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        String responseBody = new String(connection.getInputStream().readAllBytes());
        List<Task> subtaskFromResponse = List.of(gson.fromJson(responseBody, Subtask[].class));
        assertEquals(1, subtaskFromResponse.size());
        assertEquals("Subtask 1", subtaskFromResponse.get(0).getTitle());
    }

    @Test
    public void testHandleGetSubtaskById() throws IOException {

        taskManager.createSubtask("Subtask 1", "Description", Duration.ofMinutes(10), LocalDateTime.now(), 1);

        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        String responseBody = new String(connection.getInputStream().readAllBytes());
        Subtask subtaskFromResponse = gson.fromJson(responseBody, Subtask.class);
        assertNotNull(subtaskFromResponse);
        assertEquals("Subtask 1", subtaskFromResponse.getTitle());
    }

    @Test
    public void testHandleGetSubtaskNotFound() throws IOException {

        URI uri = URI.create("http://localhost:8080/subtasks/999");
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