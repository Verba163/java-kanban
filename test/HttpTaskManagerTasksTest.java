import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.TaskHandler;
import ru.yandex.practicum.interfaces.TaskManagers;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private HttpServer server;
    private TaskManagers taskManager;
    private TaskHandler taskHandler;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskHandler = new TaskHandler(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", taskHandler);
        server.setExecutor(null);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop(0);
    }

    @Test
    public void testHandleGetAllTasks() throws IOException {

        taskManager.createTask("Task 1", "Description", Duration.ofMinutes(10), LocalDateTime.now());

        // Выполнение GET-запроса
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        // Проверка ответа
        String responseBody = new String(connection.getInputStream().readAllBytes());
        List<Task> tasksFromResponse = List.of(gson.fromJson(responseBody, Task[].class));
        assertEquals(1, tasksFromResponse.size());
        assertEquals("Task 1", tasksFromResponse.get(0).getTitle());
    }

    @Test
    public void testHandleGetTaskById() throws IOException {

        taskManager.createTask("Task 1", "Description", Duration.ofMinutes(10), LocalDateTime.now());

        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode());

        String responseBody = new String(connection.getInputStream().readAllBytes());
        Task taskFromResponse = gson.fromJson(responseBody, Task.class);
        assertNotNull(taskFromResponse);
        assertEquals("Task 1", taskFromResponse.getTitle());
    }

    @Test
    public void testHandleGetTaskNotFound() throws IOException {

        URI uri = URI.create("http://localhost:8080/tasks/999");
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

    @Test
    public void testHandlePostAddTask() throws IOException {

        String taskJson = gson.toJson(new Task("New Task", "Description", 1, Duration.ofMinutes(10), LocalDateTime.now()));

        // Выполнение POST-запроса
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream os = connection.getOutputStream()) {
            os.write(taskJson.getBytes());
        }

        assertEquals(200, connection.getResponseCode());
        assertEquals(1, taskManager.getAllTasks().size());
    }


}