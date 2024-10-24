import org.junit.jupiter.api.*;

import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.streams.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_data", ".csv"); // Создание временного файла с расширением .csv
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete(); // Удаление временного файла после каждого теста
    }

    @Test
    void testSaveAndLoadTasks() {
        manager.createTask("Task 1", "Description 1", Duration.ofHours(1), LocalDateTime.now().plusHours(1));
        manager.createTask("Task 2", "Description 2", Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        manager.createTask("Task 3", "Description 2", Duration.ofHours(1), LocalDateTime.now().plusHours(30));
        manager.createTask("Task 4", "Description 2", Duration.ofHours(1), LocalDateTime.now().plusHours(4));
        manager.createTask("Task 5", "Description 2", Duration.ofHours(1), LocalDateTime.now().plusHours(5));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(5, loadedManager.getTasks().size(), "Количество задач должно быть 3");
        assertTrue(loadedManager.getTasks().containsKey(1), "Задача с ID 1 не найдена");
        assertTrue(loadedManager.getTasks().containsKey(2), "Задача с ID 2 не найдена");
        assertTrue(loadedManager.getTasks().containsKey(3), "Эпик с ID 3 не найден");
        assertTrue(loadedManager.getTasks().containsKey(4), "Подзадача с ID 4 не найдена");
    }

    @Test
    public void testTasksAreSortedOnSaveAndLoad() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        manager.createTask("Task 1", "Description 1", Duration.ofHours(1), LocalDateTime.of(2024, 10, 22, 10, 0));
        manager.createTask("Task 2", "Description 2", Duration.ofHours(1), LocalDateTime.of(2024, 10, 22, 9, 0));
        manager.createTask("Task 3", "Description 3", Duration.ofHours(1), LocalDateTime.of(2024, 10, 22, 11, 0));

        manager.save();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadedTasks = new ArrayList<>(loadedManager.prioritizedTasks);

        assertEquals(3, loadedTasks.size(), "Должно быть три задачи.");

        for (Task task : loadedTasks) {
            System.out.println(task.getTitle() + " начало в : " + task.getStartTime());
        }

        assertTrue(loadedTasks.get(0).getStartTime().isBefore(loadedTasks.get(1).getStartTime()),
                "Task 2 должно быть перед Task 1");
        assertTrue(loadedTasks.get(1).getStartTime().isBefore(loadedTasks.get(2).getStartTime()),
                "Task 1 должно быть перед Task 3");
    }
}
