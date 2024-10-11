import org.junit.jupiter.api.*;
import ru.yandex.practicum.streams.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;


    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_data", ".cvs");
        manager = new FileBackedTaskManager(tempFile);
    }
    @AfterEach
    void tearDown() {
        tempFile.delete();
    }


    @Test
    void testSaveMultipleTasks() {

        manager.createTask("Task 1", "Description 1");
        manager.createTask("Task 2", "Description 2");
        manager.createEpic("new", "New");
        manager.createSubtask("1", "1", 3);
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        assertEquals(4, loadedManager.getTasks().size());
        assertTrue(loadedManager.getTasks().containsKey(1));
        assertTrue(loadedManager.getTasks().containsKey(2));
    }

    @Test
    void testLoadMultipleTasks() {
        manager.createTask("Task 3", "Description 1");
        manager.createTask("Task 4", "Description 2");
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals("Task 3", loadedManager.getTasks().get(1).getTitle());
        assertEquals("Task 4", loadedManager.getTasks().get(2).getTitle());
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(0, loadedManager.getTasks().size());
    }
}