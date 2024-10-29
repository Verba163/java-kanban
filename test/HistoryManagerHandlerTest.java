
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.HistoryManagerHandler;
import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.managers.InMemoryHistoryManager;
import ru.yandex.practicum.models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerHandlerTest {

    private HttpServer server;
    private final int PORT = 8080;

    @BeforeEach
    public void setUp() throws IOException {

        HistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Тестовая задача 1", "Описание", 1, Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(task1);

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/history", new HistoryManagerHandler(historyManager));
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void testGetHistory() throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new java.net.URL("http://localhost:" + PORT + "/history").openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        InputStream responseStream = connection.getInputStream();
        String jsonResponse = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(200, responseCode);
    }
}